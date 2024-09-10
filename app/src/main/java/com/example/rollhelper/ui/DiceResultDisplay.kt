package com.example.rollhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiceResultsDisplay(diceResults: List<Int>, selectedDice: String, rollTrigger: Boolean) {
    // Display each dice roll result using a FlowRow layout
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Render a dice face for each result
        diceResults.forEach { result ->
            DiceFace(diceType = selectedDice, diceValue = result, rollTrigger = rollTrigger)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Display the sum of all dice roll results
    Text(
        text = "Total: ${diceResults.sum()}",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth()
    )
}
