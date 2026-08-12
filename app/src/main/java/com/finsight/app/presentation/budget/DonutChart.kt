package com.finsight.app.presentation.budget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finsight.app.domain.model.CategoryTotal
import com.finsight.app.presentation.Utils

/**
 * A custom animated Donut Chart component using Jetpack Compose Canvas.
 * It visualizes expense breakdown by categories with a smooth entry animation.
 *
 * @param categoryTotals List of categories with their respective percentages and colors.
 * @param totalExpense The total amount spent, displayed in the center of the donut.
 * @param modifier Modifier for sizing and layout of the chart container.
 */
@Composable
fun DonutChart(
    categoryTotals: List<CategoryTotal>,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    // Animation state for the drawing progress (0f to 1f)
    val animatedProgress = remember { Animatable(0f) }

    // Trigger animation whenever the data changes
    LaunchedEffect(categoryTotals) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = EaseInOutCubic)
        )
    }

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 40.dp.toPx()
            // Subtract strokeWidth to ensure the entire arc stays within the Canvas bounds
            val radius = (size.minDimension - strokeWidth) / 2
            
            // Calculate the top-left corner of the square bounding box for the arc
            val topLeft = Offset(
                x = center.x - radius,
                y = center.y - radius
            )
            val arcSize = Size(radius * 2, radius * 2)

            // Start angle: -90f corresponds to the 12 o'clock (top) position
            var startAngle = -90f

            categoryTotals.forEach { category ->
                // Calculate how much of the circle this category takes (scaled by animation)
                val sweepAngle = (category.percentage / 100f) * 360f * animatedProgress.value

                drawArc(
                    color = category.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false, // false creates the "donut" or "ring" look
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Butt
                    )
                )
                // Increment start angle for the next category segment
                startAngle += sweepAngle
            }
        }

        // Display the total expense amount in the center of the donut
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = Utils.formatAmount(totalExpense),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "total spent",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}