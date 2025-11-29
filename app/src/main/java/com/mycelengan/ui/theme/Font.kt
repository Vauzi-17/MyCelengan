package com.mycelengan.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.mycelengan.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Poppins
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins
    ),
    bodySmall = TextStyle(
        fontFamily = Poppins
    ),
    titleLarge = TextStyle(
        fontFamily = Poppins
    ),
    titleMedium = TextStyle(
        fontFamily = Poppins
    ),
    titleSmall = TextStyle(
        fontFamily = Poppins
    ),
    labelLarge = TextStyle(
        fontFamily = Poppins
    ),
    labelMedium = TextStyle(
        fontFamily = Poppins
    ),
    labelSmall = TextStyle(
        fontFamily = Poppins
    ),
    headlineLarge = TextStyle(
        fontFamily = Poppins
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins
    ),
    headlineSmall = TextStyle(
        fontFamily = Poppins
    )
)