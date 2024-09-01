package com.example.rollhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    var rollTrigger by remember { mutableStateOf(false) }

    ElevatedButton(
        onClick = {
            if (selectedDice == "Type of Dice") {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Please select a dice type before rolling.")
                }
            } else {
                val diceResults = rollDice(selectedDice, diceCount)
                rollTrigger = !rollTrigger // Toggle the trigger (for animation)
                onRoll(diceResults, rollTrigger)
            }
        },
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(horizontal = 16.dp)
            .height(60.dp)
    ) {
        Text("Roll", style = MaterialTheme.typography.headlineSmall)
    }
}

fun rollDice(diceType: String, diceCount: Int): List<Int> {
    val sides = when (diceType) {
        "D4" -> 4
        "D6" -> 6
        "D8" -> 8
        "D10" -> 10
        "D12" -> 12
        "D20" -> 20
        else -> 6
    }
    return List(diceCount) { (1..sides).random() }
}
