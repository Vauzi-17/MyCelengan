package com.mycelengan.pages.HomeElement

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycelengan.ui.theme.bluelogo

@Composable
fun FinanceChart(
    modifier: Modifier = Modifier,
    values: List<Float> = emptyList(),
    income: Int = 0,
    expense: Int = 0
) {

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
                text = "Ringkasan Keuangan",
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
                    .height(120.dp)
            ) {

                Canvas(
                    modifier =
                        Modifier.fillMaxSize()
                ) {
                    val chartValues =

                        values.ifEmpty {

                            List(6) {
                                0.5f
                            }
                        }

                    val width = size.width
                    val height = size.height

                    val step =
                        width /
                                (values.size - 1)

                    val path = Path()

                    chartValues.forEachIndexed { index, value ->

                        val x =
                            index * step

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

                    chartValues.forEachIndexed { index, value ->

                        val x =
                            index * step

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

            Spacer(
                Modifier.height(12.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                ChartLabel("Jan")
                ChartLabel("Feb")
                ChartLabel("Mar")
                ChartLabel("Apr")
                ChartLabel("Mei")
                ChartLabel("Jun")
            }
        }
    }
}

@Composable
private fun ChartLabel(
    text: String
) {

    Text(
        text = text,
        fontSize = 11.sp,
        color =
            MaterialTheme.colorScheme
                .onSurfaceVariant
    )
}