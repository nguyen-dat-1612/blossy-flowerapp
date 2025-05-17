package com.blossy.flowerstore.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {
    fun formatVND(amount: Double): String {
        val vnLocale = Locale("vi", "VN")
        val currencyFormat = NumberFormat.getCurrencyInstance(vnLocale).apply {
            maximumFractionDigits = 0
            currency = Currency.getInstance("VND")
        }
        return currencyFormat.format(amount)
    }

    fun formatVNDWithoutSymbol(amount: Double): String {
        val vnLocale = Locale("vi", "VN")
        val numberFormat = NumberFormat.getNumberInstance(vnLocale).apply {
            maximumFractionDigits = 0
        }
        return numberFormat.format(amount)
    }

    fun formatVNDWithCustomSymbol(amount: Double): String {
        val vnLocale = Locale("vi", "VN")
        val numberFormat = NumberFormat.getNumberInstance(vnLocale).apply {
            maximumFractionDigits = 0
        }
        return "${numberFormat.format(amount)}đ"
    }
}
