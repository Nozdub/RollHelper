package com.example.rollhelper.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.rollhelper.R

// Ads font family for custom font
val KoulenFontFamily = FontFamily(
    Font(R.font.koulen_regular, FontWeight.Normal)
)

// Defined custom font sizes to use in project.
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = KoulenFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = KoulenFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = KoulenFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = KoulenFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),

)
