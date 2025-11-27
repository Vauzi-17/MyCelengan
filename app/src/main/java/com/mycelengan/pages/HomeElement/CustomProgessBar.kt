package com.mycelengan.pages.HomeElement

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mycelengan.ui.theme.bg_progress
import com.mycelengan.ui.theme.fg_progress

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun CustomProgressBar() {

    var progress by remember { mutableStateOf(0f) }

    // Animasi progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 120,
            easing = LinearOutSlowInEasing
        )
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp)
    ) {

        Spacer(Modifier.height(6.dp))

        // Wrapper progress bar
        Box(
            modifier = Modifier
                .height(17.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(9.dp))
        ) {

            // Background bar
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(bg_progress)
            )

            // Fill progress
            Box(
                modifier = Modifier
                    .fillMaxHeight()                 // tinggi tetap
                    .fillMaxWidth(animatedProgress)  // lebar mengikuti progress
                    .clip(RoundedCornerShape(9.dp))
                    .background(fg_progress)
                    .animateContentSize()
            )
        }
    }

    // Trigger animasi pertama kali
    LaunchedEffect(Unit) {
        progress = 0.7f
    }
}
