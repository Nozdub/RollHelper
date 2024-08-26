package com.example.rollhelper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun DiceRollScreen() {
    // State variables to hold the selected dice type, count, results, and history
    var selectedDice by remember { mutableStateOf("Type of Dice") }
    var diceCount by remember { mutableStateOf(1) }
    var diceResults by remember { mutableStateOf<List<Int>>(emptyList()) }
    var rollHistory by remember { mutableStateOf<List<RollHistoryEntry>>(emptyList()) }

    // State of Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Use background color from the theme
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
                // Roll button
                ElevatedButton(
                    onClick = {
                        if (selectedDice == "Type of Dice") {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please select a dice type before rolling.")
                            }
                        } else {
                            diceResults = rollDice(selectedDice, diceCount)
                            val total = diceResults.sum()
                            rollHistory = rollHistory.takeLast(9) + RollHistoryEntry(
                                selectedDice,
                                diceCount,
                                total
                            )
                        }
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(60.dp)
                ) {
                    Text("Roll", style = MaterialTheme.typography.headlineSmall)
                }
            }

            // Display the dice results under the Roll button
            if (diceResults.isNotEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        diceResults.forEach { result ->
                            Text(
                                text = result.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total: ${diceResults.sum()}",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Roll history section
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.Center)
                    ) {
                        Text(
                            text = "Roll History",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        RollHistoryList(rollHistory)
                    }
                }
            }
        }

        // Snackbar Host to show messages
        SnackbarHost(
            hostState = snackbarHostState
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiceTypeChips(selectedDice: String, onDiceSelected: (String) -> Unit) {
    // Elevated card to contain the chip selection
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp) // Inner padding for the card content
        ) {
            // Header Text for the Chips
            Text(
                text = "Select Dice Type",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Display chips for each dice option using FlowRow
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center, // Center align items horizontally
                verticalArrangement = Arrangement.Center   // Center align items vertically within each row
            ) {
                val diceOptions = listOf("D4", "D6", "D8", "D10", "D12", "D20", "D100")
                diceOptions.forEach { dice ->
                    FilterChip(
                        selected = selectedDice == dice,
                        onClick = { onDiceSelected(dice) },
                        label = { Text(dice) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DiceCountSlider(diceCount: Int, onCountChanged: (Int) -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp) // Inner padding for the card content
        ) {
            // Add the header above the slider
            Text(
                text = "Select Amount of Dice",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Display the current value centered above the slider with a background box
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "$diceCount",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Slider(
                value = diceCount.toFloat(),
                onValueChange = {
                    onCountChanged(it.toInt())
                },
                valueRange = 1f..20f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
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
        "D100" -> 100
        else -> 6
    }
    return List(diceCount) { (1..sides).random()}
}

data class RollHistoryEntry(val diceType: String, val diceCount: Int, val total: Int)

@Composable
fun RollHistoryList(rollHistory: List<RollHistoryEntry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp) // Add a small padding to connect with the header
    ) {
        rollHistory.forEach { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp) // Add some vertical padding for each row
            ) {
                Text(
                    text = entry.diceType,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "x${entry.diceCount}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Total: ${entry.total}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}