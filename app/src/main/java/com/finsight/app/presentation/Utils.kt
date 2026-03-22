package com.finsight.app.presentation

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
}