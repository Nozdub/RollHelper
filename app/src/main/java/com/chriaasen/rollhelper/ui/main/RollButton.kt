package com.chriaasen.rollhelper.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chriaasen.rollhelper.ui.utils.maxDiceValue
import kotlinx.coroutines.launch

@Composable
fun RollButton(
    selectedDiceList: List<Pair<String, Int>>,
    onRoll: (List<Int>) -> Unit,
    snackbarHostState: SnackbarHostState, // Pass the snackbar host state
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope() // For showing snackbar messages

    ElevatedButton(
        onClick = {
            if (selectedDiceList.isEmpty()) {
                // Show snackbar if no dice are selected
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Select some dice first", // Snackbar message
                        duration = SnackbarDuration.Short // Short duration
                    )
                }
            } else {
                // Roll the dice if selections are available
                val results = selectedDiceList.map { (diceType, _) ->
                    (1..maxDiceValue(diceType)).random() // Roll based on dice type
                }
                onRoll(results)
            }
        },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        elevation = ButtonDefaults.elevatedButtonElevation( // Customize elevation
            defaultElevation = 12.dp,
            pressedElevation = 18.dp,
            focusedElevation = 10.dp,
            hoveredElevation = 10.dp
        )
    ) {
        Text(
            text = "Roll Dice",
            style = MaterialTheme.typography.headlineSmall.copy(
                letterSpacing = 2.sp
            )
        )
    }
}
