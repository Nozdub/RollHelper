package com.chriaasen.rollhelper.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    val gradientBrush = if (isSystemInDarkTheme()) {
        Brush.verticalGradient(colors = listOf(gradientStartDark, gradientEndDark))
    } else {
        Brush.verticalGradient(colors = listOf(gradientStartLight, gradientEndLight))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        content()
    }
}
