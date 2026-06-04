package com.mycelengan.pages.HomeElement

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycelengan.ui.theme.bluelogo

@Composable
fun FinanceChart(
    modifier: Modifier = Modifier,
    values: List<Float> = emptyList(),
    labels: List<String> = emptyList(),
    income: Int = 0,
    expense: Int = 0
) {
    val chartValues = values.map { it.coerceIn(0f, 1f) }
    val progress by animateFloatAsState(
        targetValue = if (chartValues.isEmpty()) 0f else 1f,
        animationSpec = tween(durationMillis = 700),
        label = "finance_chart_progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                )
        ) {

            Text(
                text = "Grafik Transaksi",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                Modifier.height(18.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {

                if (chartValues.isEmpty()) {
                    Text(
                        text = "Belum ada data transaksi",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Canvas(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {
                        val width = size.width
                        val height = size.height
                        val step =
                            if (chartValues.size <= 1) 0f
                            else width / (chartValues.size - 1)

                        val path = Path()

                        chartValues.forEachIndexed { index, rawValue ->
                            val value = rawValue * progress
                            val x =
                                if (chartValues.size == 1) width / 2
                                else index * step

                            val y =
                                height -
                                        (height * value)

                            if (index == 0)
                                path.moveTo(
                                    x,
                                    y
                                )
                            else
                                path.lineTo(
                                    x,
                                    y
                                )
                        }

                        drawPath(
                            path = path,
                            color = bluelogo,
                            style = Stroke(
                                width = 7f,
                                cap = StrokeCap.Round
                            )
                        )

                        chartValues.forEachIndexed { index, rawValue ->
                            val value = rawValue * progress
                            val x =
                                if (chartValues.size == 1) width / 2
                                else index * step

                            val y =
                                height -
                                        (height * value)

                            drawCircle(
                                color = bluelogo,
                                radius = 10f,
                                center = Offset(x, y)
                            )

                            drawCircle(
                                color = Color.White,
                                radius = 4f,
                                center = Offset(x, y)
                            )
                        }
                    }
                }
            }

            if (chartValues.isNotEmpty()) {
                Spacer(
                    Modifier.height(12.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    chartValues.forEachIndexed { index, _ ->
                        ChartLabel(
                            text = labels.getOrNull(index) ?: "${index + 1}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartLabel(
    text: String,
    modifier: Modifier = Modifier
) {

    Text(
        modifier = modifier,
        text = text,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        color =
            MaterialTheme.colorScheme
                .onSurfaceVariant
    )
}
