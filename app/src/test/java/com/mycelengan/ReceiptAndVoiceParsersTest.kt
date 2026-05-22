package com.mycelengan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ReceiptAndVoiceParsersTest {

    @Test
    fun receiptParserReadsTotalDateAndStore() {
        val text = """
            TOKO MAJU
            19/05/2026
            BERAS 25.000
            MINYAK 18.000
            TOTAL BELANJA 43.000
        """.trimIndent()

        val result = ReceiptParser.parse(text, "19 Mei 2026")

        assertNotNull(result)
        assertEquals(43000, result!!.draft.amount)
        assertEquals("expense", result.draft.type)
        assertEquals("groceries", result.draft.icon)
        assertEquals("Belanja - TOKO MAJU", result.draft.desc)
    }

    @Test
    fun voiceParserReadsQuickExpense() {
        val result = VoiceTransactionParser.parse(
            "beli makan 25000 hari ini kategori makanan",
            "19 Mei 2026"
        )

        assertNotNull(result)
        assertEquals(25000, result!!.amount)
        assertEquals("expense", result.type)
        assertEquals("food", result.icon)
    }
}
