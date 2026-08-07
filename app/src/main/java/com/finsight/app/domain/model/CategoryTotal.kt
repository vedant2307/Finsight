package com.finsight.app.domain.model

import androidx.compose.ui.graphics.Color

data class CategoryTotal(
    val categoryName: String,
    val emoji: String,
    val totalAmount: Double,
    val percentage: Float,
    val color: Color = Color.Gray
)
