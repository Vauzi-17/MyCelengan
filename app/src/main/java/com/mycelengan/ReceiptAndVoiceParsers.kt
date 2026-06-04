package com.mycelengan

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ReceiptScanResult(
    val draft: TransactionDraft,
    val rawText: String,
    val itemLines: List<String>
)

object ReceiptParser {

    private val strictTotalKeywords = listOf(
        "grand total", "total belanja", "total bayar",
        "total harga", "totalharga", "total pembelian",
        "netto", "net total", "total"
    )
    private val secondaryKeywords = listOf("jumlah", "tagihan")
    private val cashKeywords = listOf("tunai", "cash", "bayar")

    private val avoidKeywords = listOf(
        "kembali", "kembalian", "change",
        "diskon", "disc", "promo", "potongan", "voucher",
        "pajak", "tax", "ppn", "pbb", "vat",
        "subtotal", "sub total",
        "service", "servis", "biaya", "fee",
        "tipping", "tip", "rounding", "pembulatan",
        "poin", "point", "loyalty"
    )

    private val dateRegexes = listOf(
        Regex("""\b(\d{1,2})[/-](\d{1,2})[/-](\d{2,4})\b"""),
        Regex(
            """\b(\d{1,2})\s+(jan|feb|mar|apr|mei|jun|jul|agu|ags|sep|okt|nov|des)[a-z]*\s+(\d{2,4})\b""",
            RegexOption.IGNORE_CASE
        )
    )

    fun parse(text: String, fallbackDate: String): ReceiptScanResult? {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return null

        val total = findTotal(lines)
        android.util.Log.d("RCP", "RESULT total=$total")

        if (total == null) return null

        val storeName = lines.firstOrNull { line ->
            line.length >= 3 &&
                    !line.any { it.isDigit() } &&
                    !line.contains("struk", ignoreCase = true)
        } ?: lines.first()

        val allTotalKeywords = strictTotalKeywords + secondaryKeywords + cashKeywords
        val itemLines = lines.filter { line ->
            extractAmounts(line).isNotEmpty() &&
                    allTotalKeywords.none { line.contains(it, ignoreCase = true) }
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

        fun extractFromLineOrNext(index: Int): Int? {
            val line = lines[index]
            val same = extractAmounts(line).filter { it >= 1000 }
            if (same.isNotEmpty()) return same.max()

            val nextIndex = index + 1
            if (nextIndex < lines.size) {
                val nextLine = lines[nextIndex]
                val nextLower = nextLine.lowercase()
                val isKeyword = (strictTotalKeywords + secondaryKeywords + cashKeywords + avoidKeywords)
                    .any { nextLower.contains(it) }
                if (!isKeyword) {
                    val next = extractAmounts(nextLine).filter { it >= 1000 }
                    if (next.isNotEmpty()) return next.max()
                }
            }
            return null
        }

        // KASTA 1
        for (keyword in strictTotalKeywords.sortedByDescending { it.length }) {
            for ((index, line) in lines.withIndex()) {
                val lowerLine = line.lowercase()
                if (avoidKeywords.any { lowerLine.contains(it) }) continue
                val matched = if (keyword.length <= 5) {
                    Regex("""\b${Regex.escape(keyword)}\b""").containsMatchIn(lowerLine)
                } else {
                    lowerLine.contains(keyword)
                }
                if (matched) {
                    android.util.Log.d("RCP", "Kasta1 keyword=\"$keyword\" line=\"$line\"")
                    val result = extractFromLineOrNext(index)
                    android.util.Log.d("RCP", "Kasta1 result=$result")
                    if (result != null) return result
                }
            }
        }

        // KASTA 2
        for ((index, line) in lines.withIndex()) {
            val lowerLine = line.lowercase()
            if (avoidKeywords.any { lowerLine.contains(it) }) continue
            if (secondaryKeywords.any { lowerLine.contains(it) }) {
                android.util.Log.d("RCP", "Kasta2 line=\"$line\"")
                val result = extractFromLineOrNext(index)
                if (result != null) return result
            }
        }

        // KASTA 3 — hanya aktif jika tidak ada baris "total" sama sekali
        val hasTotalLine = lines.any { line ->
            val lower = line.lowercase()
            avoidKeywords.none { lower.contains(it) } &&
                    strictTotalKeywords.any { kw ->
                        if (kw.length <= 5) Regex("""\b${Regex.escape(kw)}\b""").containsMatchIn(lower)
                        else lower.contains(kw)
                    }
        }
        android.util.Log.d("RCP", "hasTotalLine=$hasTotalLine")

        if (!hasTotalLine) {
            for ((index, line) in lines.withIndex()) {
                val lowerLine = line.lowercase()
                if (avoidKeywords.any { lowerLine.contains(it) }) continue
                if (cashKeywords.any { lowerLine.contains(it) }) {
                    android.util.Log.d("RCP", "Kasta3 line=\"$line\"")
                    val result = extractFromLineOrNext(index)
                    if (result != null) return result
                }
            }
        }

        // KASTA 4 — fallback angka terbesar
        android.util.Log.d("RCP", "Kasta4 fallback")
        val fallbackAmounts = lines
            .filter { line ->
                val lower = line.lowercase()
                avoidKeywords.none { lower.contains(it) } &&
                        cashKeywords.none { lower.contains(it) }
            }
            .flatMap { extractAmounts(it) }
            .filter { it >= 1000 }

        return fallbackAmounts.maxOrNull()
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
            "jan" to 0, "feb" to 1, "mar" to 2, "apr" to 3,
            "mei" to 4, "jun" to 5, "jul" to 6, "agu" to 7,
            "ags" to 7, "sep" to 8, "okt" to 9, "nov" to 10, "des" to 11
        )
        val wordMatch = Regex(
            """\b(\d{1,2})\s+([A-Za-z]+)\s+(\d{2,4})\b""",
            RegexOption.IGNORE_CASE
        ).find(raw)
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
        val cleanLine = line.replace(Regex("""[.,]00\b"""), "")
        return Regex(
            """(?:rp\.?\s*)?(\d{1,3}(?:[.,]\d{3})+|\d{4,})""",
            RegexOption.IGNORE_CASE
        )
            .findAll(cleanLine)
            .mapNotNull { match ->
                match.groupValues[1]
                    .replace(".", "")
                    .replace(",", "")
                    .toIntOrNull()
            }
            .filter { it > 0 }
            .toList()
    }
}

object VoiceTransactionParser {
    fun parseAmount(text: String): Int? {
        val normalized = text.lowercase(Locale("id", "ID")).trim()
        return (parseNumericAmount(normalized) ?: parseWordAmount(normalized))?.first
    }

    fun parse(text: String, fallbackDate: String): TransactionDraft? {
        val normalized = text.lowercase(Locale("id", "ID")).trim()

        val parsedResult = parseNumericAmount(normalized) ?: parseWordAmount(normalized) ?: return null
        val (amount, textToRemove) = parsedResult

        val type = if (listOf("gaji", "pemasukan", "masuk", "terima").any { normalized.contains(it) }) "income" else "expense"

        return TransactionDraft(
            type = type,
            amount = amount,
            desc = cleanedDescription(text, textToRemove),
            date = if (normalized.contains("kemarin")) formatRelativeDate(-1) else fallbackDate,
            icon = guessCategory(normalized, type)
        )
    }

    private fun parseNumericAmount(text: String): Pair<Int, String>? {
        val regex = Regex("""\b(?:rp\.?\s*)?(\d{1,3}(?:[.,]\d{3})+|\d+)\s*(ribu|rb|juta)?\b""", RegexOption.IGNORE_CASE)
        val candidates = regex.findAll(text).mapNotNull { match ->
            val base = match.groupValues[1].replace(".", "").replace(",", "").toIntOrNull() ?: return@mapNotNull null
            val amount = when (match.groupValues.getOrNull(2)?.lowercase(Locale.ROOT)) {
                "ribu", "rb" -> base * 1000
                "juta" -> base * 1_000_000
                else -> base
            }
            Pair(amount, match.value)
        }.toList()

        if (candidates.isEmpty()) return null
        val validTransactions = candidates.filter { it.first >= 1000 }
        val bestCandidate = if (validTransactions.isNotEmpty()) {
            validTransactions.maxByOrNull { it.first }
        } else {
            candidates.maxByOrNull { it.first }
        }
        return bestCandidate?.takeIf { it.first >= 1000 }
    }

    private fun parseWordAmount(text: String): Pair<Int, String>? {
        val tokens = text.split(Regex("""\s+"""))
        var total = 0
        var currentGroup = 0
        var tempDigit = 0
        val currentWords = mutableListOf<String>()
        var bestAmount = 0
        var bestTextToRemove = ""

        val units = mapOf(
            "satu" to 1, "dua" to 2, "tiga" to 3, "empat" to 4, "lima" to 5,
            "enam" to 6, "tujuh" to 7, "delapan" to 8, "sembilan" to 9,
            "sepuluh" to 10, "sebelas" to 11, "seratus" to 100, "seribu" to 1000
        )

        val flushCandidate = {
            val groupTotal = currentGroup + tempDigit
            val finalTotal = total + groupTotal
            if (finalTotal >= 1000 && finalTotal > bestAmount) {
                bestAmount = finalTotal
                bestTextToRemove = currentWords.joinToString(" ")
            }
            total = 0; currentGroup = 0; tempDigit = 0; currentWords.clear()
        }

        for (token in tokens) {
            when (token) {
                "juta" -> { if (currentWords.isNotEmpty()) { currentWords.add(token); currentGroup += tempDigit; total += (if (currentGroup == 0) 1 else currentGroup) * 1_000_000; currentGroup = 0; tempDigit = 0 } }
                "ribu", "rb" -> { if (currentWords.isNotEmpty()) { currentWords.add(token); currentGroup += tempDigit; total += (if (currentGroup == 0) 1 else currentGroup) * 1000; currentGroup = 0; tempDigit = 0 } }
                "ratus" -> { if (currentWords.isNotEmpty()) { currentWords.add(token); currentGroup += (if (tempDigit == 0) 1 else tempDigit) * 100; tempDigit = 0 } }
                "puluh" -> { if (currentWords.isNotEmpty()) { currentWords.add(token); currentGroup += (if (tempDigit == 0) 1 else tempDigit) * 10; tempDigit = 0 } }
                "belas" -> { if (currentWords.isNotEmpty()) { currentWords.add(token); currentGroup += tempDigit + 10; tempDigit = 0 } }
                else -> {
                    val v = units[token]
                    if (v != null) {
                        if (tempDigit != 0) flushCandidate()
                        currentWords.add(token)
                        if (v == 100 || v == 1000) currentGroup += v else tempDigit = v
                    } else {
                        if (currentWords.isNotEmpty()) flushCandidate()
                    }
                }
            }
        }
        flushCandidate()
        return if (bestAmount >= 1000) Pair(bestAmount, bestTextToRemove) else null
    }

    private fun cleanedDescription(text: String, textToRemove: String): String {
        var cleaned = text
        if (textToRemove.isNotEmpty()) {
            cleaned = cleaned.replace(Regex(Regex.escape(textToRemove), RegexOption.IGNORE_CASE), "")
        }
        return cleaned
            .replace(Regex("""\b(hari ini|kemarin|kategori)\b""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\s+"""), " ")
            .trim()
            .ifBlank { "Transaksi voice" }
            .replaceFirstChar { it.uppercase() }
    }

    private fun guessCategory(text: String, type: String): String {
        if (type == "income") return if (text.contains("tabung")) "saving" else "salary"
        return when {
            listOf("makan", "minum", "kopi", "ayam", "nasi", "bakso", "bubur", "telor", "telur").any { text.contains(it) } -> "food"
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
