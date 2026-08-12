package com.finsight.app.presentation

import java.text.NumberFormat
import java.util.Locale

object Utils {
    fun getCategoryEmoji(category: String): String {
        return when (category.lowercase()) {
            "food"          -> "🍔"
            "transport"     -> "🚗"
            "shopping"      -> "🛍️"
            "groceries"     -> "🛒"
            "bills"         -> "💡"
            "health"        -> "💊"
            "rent"          -> "🏠"
            "entertainment" -> "🎬"
            "travel"        -> "✈️"
            "education"     -> "📚"
            "salary"        -> "💰"
            "freelance"     -> "💻"
            "investment"    -> "📈"
            else            -> "📦"
        }
    }

    fun formatAmount(amount: Double, currencySymbol: String = "₹"): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2
        return "$currencySymbol${formatter.format(amount)}"
    }
}