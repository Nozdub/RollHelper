package com.example.rollhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DiceRollScreen() {
    // State variables to hold the selected dice type, count, results, and history
    var selectedDice by remember { mutableStateOf("Type of Dice") }
    var diceCount by remember { mutableStateOf(1) }
    var diceResults by remember { mutableStateOf<List<Int>>(emptyList()) }
    var rollHistory by remember { mutableStateOf<List<RollHistoryEntry>>(emptyList()) }
    var rollTrigger by remember { mutableStateOf(false) }

    // State of Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                DiceTypeChips(selectedDice) { dice ->
                    selectedDice = dice
                    diceResults = emptyList()
                }
            }

            item {
                DiceCountSlider(diceCount) {
                    diceCount = it
                    diceResults = emptyList()
                }
            }

            item {
                RollButton(
                    selectedDice, diceCount, snackbarHostState, coroutineScope
                ) { results, trigger ->
                    diceResults = results
                    rollTrigger = trigger
                    val total = diceResults.sum()
                    rollHistory = rollHistory.takeLast(9) + RollHistoryEntry(
                        selectedDice,
                        diceCount,
                        total
                    )
                }
            }

            if (diceResults.isNotEmpty()) {
                item {
                    DiceResultsDisplay(diceResults, selectedDice, rollTrigger)
                }
            }

            item {
                RollHistorySection(rollHistory)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState
        )
    }
}

