package com.mycelengan

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

data class ReceiptScanResult(
    val draft: TransactionDraft,
    val rawText: String,
    val itemLines: List<String>
)

object ReceiptParser {
    private val totalKeywords = listOf("grand total", "total belanja", "jumlah", "total", "tunai")
    private val dateRegexes = listOf(
        Regex("""\b(\d{1,2})[/-](\d{1,2})[/-](\d{2,4})\b"""),
        Regex("""\b(\d{1,2})\s+(jan|feb|mar|apr|mei|jun|jul|agu|ags|sep|okt|nov|des)[a-z]*\s+(\d{2,4})\b""", RegexOption.IGNORE_CASE)
    )

    fun parse(text: String, fallbackDate: String): ReceiptScanResult? {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return null

        val total = findTotal(lines) ?: return null
        val storeName = lines.firstOrNull { line ->
            line.length >= 3 && !line.any { it.isDigit() } && !line.contains("struk", ignoreCase = true)
        } ?: lines.first()
        val itemLines = lines.filter { line ->
            extractAmounts(line).isNotEmpty() && totalKeywords.none { line.contains(it, ignoreCase = true) }
        }.take(8)

        return ReceiptScanResult(
            draft = TransactionDraft(
                type = "expense",
                amount = total,
                desc = "Belanja - $storeName",
                date = findDate(lines) ?: fallbackDate,
                icon = "groceries"
            ),
            rawText = text,
            itemLines = itemLines
        )
    }

    private fun findTotal(lines: List<String>): Int? {
        val keywordTotals = lines.mapNotNull { line ->
            if (totalKeywords.any { line.contains(it, ignoreCase = true) }) {
                extractAmounts(line).maxOrNull()
            } else {
                null
            }
        }
        return keywordTotals.maxOrNull()
            ?: lines.flatMap(::extractAmounts).filter { it >= 1000 }.maxOrNull()
    }

    private fun findDate(lines: List<String>): String? {
        lines.forEach { line ->
            dateRegexes.forEach { regex ->
                val match = regex.find(line)
                if (match != null) return normalizeDate(match.value)
            }
        }
        return null
    }

    private fun normalizeDate(raw: String): String {
        val numberMatch = Regex("""\b(\d{1,2})[/-](\d{1,2})[/-](\d{2,4})\b""").find(raw)
        if (numberMatch != null) {
            return formatCalendar(
                day = numberMatch.groupValues[1].toInt(),
                month = numberMatch.groupValues[2].toInt() - 1,
                year = normalizeYear(numberMatch.groupValues[3].toInt())
            )
        }

        val monthNames = mapOf(
            "jan" to 0, "feb" to 1, "mar" to 2, "apr" to 3, "mei" to 4, "jun" to 5,
            "jul" to 6, "agu" to 7, "ags" to 7, "sep" to 8, "okt" to 9, "nov" to 10, "des" to 11
        )
        val wordMatch = Regex("""\b(\d{1,2})\s+([A-Za-z]+)\s+(\d{2,4})\b""", RegexOption.IGNORE_CASE).find(raw)
        if (wordMatch != null) {
            return formatCalendar(
                day = wordMatch.groupValues[1].toInt(),
                month = monthNames[wordMatch.groupValues[2].lowercase(Locale("id", "ID")).take(3)] ?: 0,
                year = normalizeYear(wordMatch.groupValues[3].toInt())
            )
        }

        return raw
    }

    private fun normalizeYear(year: Int): Int = if (year < 100) 2000 + year else year

    private fun formatCalendar(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        return SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(calendar.time)
    }

    private fun extractAmounts(line: String): List<Int> {
        return Regex("""(?:rp\s*)?(\d{1,3}(?:[.,]\d{3})+|\d{4,})""", RegexOption.IGNORE_CASE)
            .findAll(line)
            .mapNotNull { it.groupValues[1].replace(".", "").replace(",", "").toIntOrNull() }
            .filter { it > 0 }
            .toList()
    }
}

object VoiceTransactionParser {
    private val simpleNumbers = mapOf(
        "satu" to 1, "dua" to 2, "tiga" to 3, "empat" to 4, "lima" to 5,
        "enam" to 6, "tujuh" to 7, "delapan" to 8, "sembilan" to 9,
        "sepuluh" to 10, "sebelas" to 11
    )

    fun parse(text: String, fallbackDate: String): TransactionDraft? {
        val normalized = text.lowercase(Locale("id", "ID"))
        val amount = parseNumericAmount(normalized) ?: parseWordAmount(normalized) ?: return null
        val type = if (listOf("gaji", "pemasukan", "masuk", "terima").any { normalized.contains(it) }) "income" else "expense"
        return TransactionDraft(
            type = type,
            amount = amount,
            desc = cleanedDescription(text, amount),
            date = if (normalized.contains("kemarin")) formatRelativeDate(-1) else fallbackDate,
            icon = guessCategory(normalized, type)
        )
    }

    private fun parseNumericAmount(text: String): Int? {
        val match = Regex("""\b(\d{1,3}(?:[.,]\d{3})+|\d+)\s*(ribu|rb|juta)?\b""").find(text) ?: return null
        val base = match.groupValues[1].replace(".", "").replace(",", "").toIntOrNull() ?: return null
        return when (match.groupValues.getOrNull(2)) {
            "ribu", "rb" -> base * 1000
            "juta" -> base * 1_000_000
            else -> base
        }
    }

    private fun parseWordAmount(text: String): Int? {
        val tokens = text.split(Regex("""\s+"""))
        var best = 0
        tokens.forEachIndexed { index, token ->
            val base = simpleNumbers[token] ?: return@forEachIndexed
            val next = tokens.getOrNull(index + 1)
            val amount = when (next) {
                "ribu" -> base * 1000
                "puluh" -> base * 10
                "ratus" -> base * 100
                "juta" -> base * 1_000_000
                else -> base
            }
            best = max(best, amount)
        }
        return best.takeIf { it >= 1000 }
    }

    private fun cleanedDescription(text: String, amount: Int): String {
        return text
            .replace(Regex("""\b(hari ini|kemarin|kategori)\b""", RegexOption.IGNORE_CASE), "")
            .replace(amount.toString(), "")
            .trim()
            .ifBlank { "Transaksi voice" }
            .replaceFirstChar { it.uppercase() }
    }

    private fun guessCategory(text: String, type: String): String {
        if (type == "income") return if (text.contains("tabung")) "saving" else "salary"
        return when {
            listOf("makan", "minum", "kopi", "ayam", "nasi").any { text.contains(it) } -> "food"
            listOf("belanja", "market", "indomaret", "alfamart", "sayur").any { text.contains(it) } -> "groceries"
            listOf("ojek", "bensin", "parkir", "transport").any { text.contains(it) } -> "transport"
            listOf("listrik", "air", "internet", "tagihan").any { text.contains(it) } -> "bills"
            listOf("obat", "dokter", "klinik").any { text.contains(it) } -> "health"
            listOf("buku", "sekolah", "kuliah").any { text.contains(it) } -> "education"
            listOf("baju", "sepatu", "shopping").any { text.contains(it) } -> "shopping"
            else -> "other"
        }
    }

    private fun formatRelativeDate(offsetDays: Int): String {
        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, offsetDays) }
        return SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(calendar.timeInMillis))
    }
}
