package com.mycelengan.pages.HomeElement

data class FinanceInsight(
    val title: String,
    val message: String,
    val emoji: String
)

fun calculateFinanceInsight(
    income: Int,
    expense: Int
): FinanceInsight {

    if (income <= 0) {

        return FinanceInsight(
            "Belum ada data",
            "Mulai catat transaksi supaya bisa lihat pola keuanganmu.",
            "📈"
        )
    }

    val ratio =
        expense.toFloat() /
                income

    return when {

        ratio >= 0.9f ->

            FinanceInsight(
                "Pengeluaran tinggi",
                "Kamu terlalu boros bulan ini. Coba kurangi pengeluaran kecil dulu.",
                "⚠️"
            )

        ratio >= 0.7f ->

            FinanceInsight(
                "Perlu dijaga",
                "Pengeluaranmu mulai besar. Masih aman tapi tetap hati-hati.",
                "🙂"
            )

        ratio >= 0.4f ->

            FinanceInsight(
                "Bagus",
                "Pengeluaran masih terkontrol. Pertahankan ritmenya.",
                "✨"
            )

        else ->

            FinanceInsight(
                "Kamu hemat",
                "Bulan ini kamu berhasil menabung lebih banyak.",
                "🎉"
            )
    }
}