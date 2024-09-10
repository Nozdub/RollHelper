package com.example.rollhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DiceTypeAndCountCard(
    selectedDice: String,
    onDiceSelected: (String) -> Unit,
    diceCount: Int,
    onCountChanged: (Int) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Provides slight elevation for visual layering
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Use surface color for a unified look
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp) // Margins around the card to separate it from other UI elements
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(0.dp)
        ) {
            // Component for selecting the type of dice
            // Allows users to choose from available dice types using chips
            DiceTypeChips(
                selectedDice = selectedDice,
                onDiceSelected = onDiceSelected
            )

            Spacer(modifier = Modifier.height(0.dp))  // Minimal space to avoid cramped layout

            // Component for selecting the number of dice
            // Provides a slider to adjust the number of dice to roll
            DiceCountSlider(
                diceCount = diceCount,
                onCountChanged = onCountChanged
            )
        }
    }
}
