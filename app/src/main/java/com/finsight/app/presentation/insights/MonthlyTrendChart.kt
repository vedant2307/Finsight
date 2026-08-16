package com.finsight.app.presentation.insights

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.finsight.app.domain.model.MonthlySpend

@Composable
fun MonthlyTrendChart(monthlySpends: List<MonthlySpend>) {
    val maxAmount = monthlySpends.maxOfOrNull { it.amount } ?: 0.0
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(monthlySpends) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = EaseInOutCubic)
        )
    }

    val barColor = Color(0xFF4A9E8F)
    val zeroBarColor = Color(0xFFE8F5F3)
    val labelColor = Color.Gray
    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 8.dp)
    ) {
        val chartHeight = size.height - 40.dp.toPx() // reserve bottom for labels
        val barWidth = size.width / (monthlySpends.size * 2f)
        val gap = barWidth
        val totalBlockWidth = barWidth + gap

        monthlySpends.forEachIndexed { index, monthly ->
            val barHeightRatio = if (maxAmount > 0) {
                (monthly.amount / maxAmount).toFloat()
            } else 0f

            val animatedHeight = chartHeight * barHeightRatio * animatedProgress.value
            val left = index * totalBlockWidth + gap / 2
            val top = chartHeight - animatedHeight

            // Draw bar
            drawRoundRect(
                color = if (monthly.amount > 0) barColor else zeroBarColor,
                topLeft = Offset(left, top),
                size = Size(barWidth, animatedHeight.coerceAtLeast(4.dp.toPx())),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            )

            // Draw month label below
            drawContext.canvas.nativeCanvas.drawText(
                monthly.month,
                left + barWidth / 2,
                size.height - 4.dp.toPx(),
                textPaint
            )
        }
    }

}