// Copyright (c) 2024, Nozdub
// All rights reserved.

package com.example.rollhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiceTypeChips(selectedDice: String, onDiceSelected: (String) -> Unit) {
    // Main container for the dice type selection UI
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        // Header text with an outline effect to give a 3D-like appearance
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            // Creating a shadow-like outline by overlaying multiple texts with slight offsets
            Text(
                text = "Select Dice Type",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier
                    .offset(x = 0.5.dp, y = 0.5.dp)
            )
            Text(
                text = "Select Dice Type",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier
                    .offset(x = -0.5.dp, y = -0.5.dp)
            )
            Text(
                text = "Select Dice Type",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier
                    .offset(x = -0.5.dp, y = 0.5.dp)
            )
            Text(
                text = "Select Dice Type",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier
                    .offset(x = 0.5.dp, y = -0.5.dp)
            )

            // Main text in the center without any offset
            Text(
                text = "Select Dice Type",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.tertiary
                )
            )
        }

        // Dice selection chips arranged in a flow layout to support multiple rows
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 3,  // Ensures chips are organized into two rows with three items each
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            // List of available dice types
            val diceOptions = listOf("D4", "D6", "D8", "D10", "D12", "D20")
            val chipWidth = 80.dp // Standard width for all chips to maintain uniformity

            diceOptions.forEach { dice ->
                // FilterChip allows users to select a dice type
                FilterChip(
                    selected = selectedDice == dice,  // Marks the chip as selected if it matches the current selection
                    onClick = { onDiceSelected(dice) },  // Updates the selected dice type on click
                    label = {
                        // Centers the text within the chip to make it visually consistent
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dice,  // Displays the dice type
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(end = 8.dp)  // Ensures text is visually centered
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .width(chipWidth)  // Ensures all chips have a consistent width
                )
            }
        }
    }
}
