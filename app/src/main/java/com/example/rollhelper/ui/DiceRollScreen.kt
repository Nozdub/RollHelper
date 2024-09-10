package com.example.rollhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollScreen() {
    // State management for user selections, roll results, and UI triggers
    var selectedDice by remember { mutableStateOf("Type of Dice") }
    var diceCount by remember { mutableStateOf(1) }
    var diceResults by remember { mutableStateOf<List<Int>>(emptyList()) }
    var rollHistory by remember { mutableStateOf<List<RollHistoryEntry>>(emptyList()) }
    var rollTrigger by remember { mutableStateOf(false) }

    // Snackbar and coroutine scope for showing user feedback
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Scaffold layout with a custom top bar and content
    Scaffold(
        topBar = {
            // TopAppBar with rounded corners for enhanced visual appeal
            Surface(
                shape = RoundedCornerShape(bottomEnd = 25.dp, bottomStart = 25.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Centered title for the app
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RollHelper",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        },
        content = { paddingValues ->  // Padding to account for the top bar height
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Main content displayed in a scrollable column layout
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card combining dice type selection and count slider
                    item {
                        DiceTypeAndCountCard(
                            selectedDice = selectedDice,
                            onDiceSelected = { dice ->
                                selectedDice = dice
                                diceResults = emptyList()  // Clear results when dice type changes
                            },
                            diceCount = diceCount,
                            onCountChanged = { count ->
                                diceCount = count
                                diceResults = emptyList()  // Clear results when count changes
                            }
                        )
                    }
                    // Button to trigger the dice roll
                    item {
                        RollButton(
                            selectedDice, diceCount, snackbarHostState, coroutineScope
                        ) { results, trigger ->
                            diceResults = results
                            rollTrigger = trigger  // Used to trigger animation
                            val total = diceResults.sum()
                            // Add the latest roll to the history, keeping only the last 10 entries
                            rollHistory = rollHistory.takeLast(9) + RollHistoryEntry(
                                selectedDice,
                                diceCount,
                                total
                            )
                        }
                    }

                    // Display the results if any dice have been rolled
                    if (diceResults.isNotEmpty()) {
                        item {
                            DiceResultsDisplay(diceResults, selectedDice, rollTrigger)
                        }
                    }

                    // Display the roll history
                    item {
                        RollHistorySection(rollHistory)
                    }
                }

                // Snackbar to display messages, positioned at the bottom of the screen
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 64.dp)
                )
            }
        }
    )
}
