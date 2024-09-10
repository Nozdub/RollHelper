// Copyright (c) 2024, Nozdub
// All rights reserved.


package com.example.rollhelper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DiceCountSlider(diceCount: Int, onCountChanged: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        // Header text with an outline effect
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            // Shadow layers for outline effect
            Text(
                text = "Select Amount of Dice",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.offset(x = 0.5.dp, y = 0.5.dp) // Bottom-right shadow
            )
            Text(
                text = "Select Amount of Dice",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.offset(x = -0.5.dp, y = -0.5.dp) // Top-left shadow
            )
            Text(
                text = "Select Amount of Dice",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.offset(x = -0.5.dp, y = 0.5.dp) // Bottom-left shadow
            )
            Text(
                text = "Select Amount of Dice",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.offset(x = 0.5.dp, y = -0.5.dp) // Top-right shadow
            )

            // Main header text
            Text(
                text = "Select Amount of Dice",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.tertiary
                )
            )
        }

        Spacer(modifier = Modifier.height(0.dp))

        // Slider for selecting the number of dice
        Slider(
            value = diceCount.toFloat(),
            onValueChange = {
                onCountChanged(it.toInt())
            },
            valueRange = 1f..20f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.tertiary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        // Display selected dice count with an outline effect
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            // Shadow layers for outline effect
            Text(
                text = "$diceCount",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.offset(x = 0.5.dp, y = 0.5.dp)
            )
            Text(
                text = "$diceCount",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.offset(x = -0.5.dp, y = -0.5.dp)
            )
            Text(
                text = "$diceCount",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.offset(x = -0.5.dp, y = 0.5.dp)
            )
            Text(
                text = "$diceCount",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.offset(x = 0.5.dp, y = -0.5.dp)
            )

            // Main text for displaying selected count
            Text(
                text = "$diceCount",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.tertiary
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
    }
}
