// Copyright (c) 2024, Nozdub
// All rights reserved.

package com.example.rollhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class RollHistoryEntry(val diceType: String, val diceCount: Int, val total: Int)

@Composable
fun RollHistorySection(rollHistory: List<RollHistoryEntry>) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),  // Adjust elevation as needed
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            // Outline Text Effect for Roll History Header
            Box(
                contentAlignment = Alignment.Center,  // Ensure text is centered
                modifier = Modifier
                    .fillMaxWidth()  // Make the Box fill the width
                    .padding(bottom = 8.dp)
            ) {
                // Outline Text
                Text(
                    text = "Roll History",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onTertiary  // Outline color
                    ),
                    modifier = Modifier
                        .offset(x = 0.5.dp, y = 0.5.dp)  // Bottom-right shadow
                )
                Text(
                    text = "Roll History",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onTertiary  // Outline color
                    ),
                    modifier = Modifier
                        .offset(x = -0.5.dp, y = -0.5.dp)  // Top-left shadow
                )
                Text(
                    text = "Roll History",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onTertiary  // Outline color
                    ),
                    modifier = Modifier
                        .offset(x = -0.5.dp, y = 0.5.dp)  // Bottom-left shadow
                )
                Text(
                    text = "Roll History",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onTertiary  // Outline color
                    ),
                    modifier = Modifier
                        .offset(x = 0.5.dp, y = -0.5.dp)  // Top-right shadow
                )

                // Main Text on Top
                Text(
                    text = "Roll History",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.tertiary  // Main text color
                    )
                )
            }
            RollHistoryList(rollHistory.reversed()) // Allows the most recent roll to appear on top.
        }
    }
}

@Composable
fun RollHistoryList(rollHistory: List<RollHistoryEntry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        rollHistory.forEach { entry ->
            RollHistoryItem(entry)
        }
    }
}

@Composable
fun RollHistoryItem(entry: RollHistoryEntry) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
        Column {
            Text(
                text = "${entry.diceType} x${entry.diceCount}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Total: ${entry.total}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
