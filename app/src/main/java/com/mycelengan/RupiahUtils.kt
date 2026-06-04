package com.mycelengan

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import java.text.NumberFormat
import java.util.Locale

private val rupiahLocale = Locale.forLanguageTag("id-ID")

fun rupiahDigits(value: Any?): String {
    val text = value
        ?.toString()
        .orEmpty()

    val normalized = if (text.matches(Regex("""\d+\.\d{1,2}"""))) {
        text.substringBefore(".")
    } else {
        text
    }

    return normalized.filter(Char::isDigit)
}

fun parseRupiah(value: Any?): Int {
    return rupiahDigits(value).toIntOrNull() ?: 0
}

fun formatRupiahInput(value: Any?): String {
    val digits = rupiahDigits(value)
    if (digits.isBlank()) return ""
    return NumberFormat.getInstance(rupiahLocale).format(digits.toLong())
}

fun formatRupiah(value: Any?): String {
    val isNegative = when (value) {
        is Number -> value.toLong() < 0
        else -> value?.toString()?.trim()?.startsWith("-") == true
    }
    val amount = parseRupiah(value)
    val prefix = if (isNegative && amount > 0) "-" else ""
    return "${prefix}Rp${NumberFormat.getInstance(rupiahLocale).format(amount.toLong())}"
}

fun formatSignedRupiah(type: String, value: Any?): String {
    val prefix = if (type == "income" || type == "add") "+" else "-"
    return "$prefix${formatRupiah(value)}"
}

@Composable
fun RupiahIcon(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = "Rp",
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}
