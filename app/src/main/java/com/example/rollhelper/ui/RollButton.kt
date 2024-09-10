package com.example.rollhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RollButton(
    selectedDice: String,
    diceCount: Int,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRoll: (List<Int>, Boolean) -> Unit
) {
    // State to trigger re-composition for roll animations
    var rollTrigger by remember { mutableStateOf(false) }

    // ElevatedButton to roll the dice, with logic to handle dice type selection and display results
    ElevatedButton(
        onClick = {
            if (selectedDice == "Type of Dice") {
                // Show a Snackbar if no dice type is selected
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Please select a dice type before rolling.")
                }
            } else {
                // Perform the dice roll operation if a dice type is selected
                val diceResults = rollDice(selectedDice, diceCount)
                rollTrigger = !rollTrigger  // Toggle trigger for animation purposes
                onRoll(diceResults, rollTrigger)  // Pass results to the parent composable
            }
        },
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()  // Button occupies full width
            .padding(vertical = 16.dp)
            .padding(horizontal = 16.dp)
            .height(60.dp)  // Fixed height for consistent button size
    ) {
        // Text label for the button with custom styling
        Text(
            "Roll",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.align(Alignment.CenterVertically)  // Ensure text is vertically centered
        )
    }
}

// Helper function to simulate rolling dice based on the type and count
fun rollDice(diceType: String, diceCount: Int): List<Int> {
    // Determine the number of sides based on the dice type
    val sides = when (diceType) {
        "D4" -> 4
        "D6" -> 6
        "D8" -> 8
        "D10" -> 10
        "D12" -> 12
        "D20" -> 20
        else -> 6  // Default to a 6-sided dice if an unknown type is encountered
    }
    // Generate random roll results for the specified number of dice
    return List(diceCount) { (1..sides).random() }
}
