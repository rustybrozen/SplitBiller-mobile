package com.example.anhpansplitter.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToLong



object MoneyUtil {

    fun parseSmartMoney(raw: String): Double {
        if (raw.isBlank()) return 0.0
        var str = raw.trim().lowercase(Locale.ROOT)

        val isNegative = str.startsWith("-")
        if (isNegative || str.startsWith("+")) {
            str = str.substring(1).trim()
        }

        var multiplier = 1.0
        when {
            str.endsWith("k") -> {
                multiplier = 1_000.0
                str = str.removeSuffix("k").trim()
            }
            str.endsWith("tr") -> {
                multiplier = 1_000_000.0
                str = str.removeSuffix("tr").trim()
            }
            str.endsWith("m") -> {
                multiplier = 1_000_000.0
                str = str.removeSuffix("m").trim()
            }
            str.endsWith("cu") || str.endsWith("củ") -> {
                multiplier = 1_000_000.0
                str = str.replace(Regex("(cu|củ)$"), "").trim()
            }
            str.endsWith("lit") || str.endsWith("lít") -> {
                multiplier = 100_000.0
                str = str.replace(Regex("(lit|lít)$"), "").trim()
            }
        }

        if (multiplier > 1.0) {
            str = str.replace(',', '.')
            val num = str.toDoubleOrNull() ?: return 0.0
            val res = (num * multiplier).roundToLong().toDouble()
            return if (isNegative) -res else res
        }

        var finalStr = str.replace(",", "").replace(" ", "")
        val dotCount = str.count { it == '.' }
        if (dotCount > 0) {
            if (Regex("\\.\\d{3}(\\.|$)").containsMatchIn(str)) {
                finalStr = str.replace(".", "").replace(",", "").replace(" ", "")
            } else {
                finalStr = str.replace(',', '.')
            }
        }

        val parsed = finalStr.toDoubleOrNull() ?: return 0.0
        return if (isNegative) -parsed else parsed
    }



    fun parseAdjustmentString(valStr: String): Double? {
        if (valStr.isBlank()) return 0.0
        val parts = valStr.trim().split(Regex("\\s+"))
        var sum = 0.0
        for (p in parts) {
            val num = parseSmartMoney(p)
            sum += num
        }
        return sum
    }



    fun formatVnd(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = '.'
        }
        val formatter = DecimalFormat("#,### đ", symbols)
        return formatter.format(amount.roundToLong())
    }



    fun formatNumberOnly(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = '.'
        }
        val formatter = DecimalFormat("#,###", symbols)
        return formatter.format(amount.roundToLong())
    }



    fun formatCurrency(amount: Double): String = formatVnd(amount)
}
