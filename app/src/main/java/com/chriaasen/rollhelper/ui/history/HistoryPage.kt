package com.chriaasen.rollhelper.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chriaasen.rollhelper.ui.components.AppTopBar

@Composable
fun HistoryPage(
    rollHistory: List<Triple<List<Int>, Int, Int>> // Matches the Triple structure
)
 {
    Scaffold(
        topBar = { AppTopBar() },
        containerColor = Color.Transparent,
        content = { paddingValues ->
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Center the Roll History text
                            Text(
                                text = "Roll History",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.tertiary
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 8.dp)
                            )

                            // Display newest rolls at the top
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(rollHistory.reversed()) { (rolls, abilityMod, proficiencyMod) ->
                                    val total = rolls.sum() + abilityMod + proficiencyMod

                                    // Constructing the formatted roll
                                    val rollText = buildAnnotatedString {
                                        append("Roll :  ")
                                        // Add dice rolls

                                        rolls.forEachIndexed { index, roll ->
                                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                                                append("$roll")
                                            }
                                            if (index < rolls.size - 1) append(" + ")
                                        }


                                        // Add ability modifier
                                        if (abilityMod != 0) {
                                            append(" + ")
                                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiaryContainer)) {
                                                append("$abilityMod")
                                            }
                                        }

                                        // Add proficiency modifier
                                        if (proficiencyMod != 0) {
                                            append(" + ")
                                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onTertiaryContainer)) {
                                                append("$proficiencyMod")
                                            }
                                        }

                                        // Add total
                                        append("  =  ")
                                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.scrim)) {
                                            append("$total")
                                        }
                                    }


                                    // Display the roll history with styled text for dice rolls, modifiers, and total
                                    Text(
                                        text = rollText,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.scrim
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }

                            // Legend for colors
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                LegendItem(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    label = "Dice Rolls"
                                )
                                LegendItem(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    label = "Ability Modifier"
                                )
                                LegendItem(
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    label = "Proficiency Modifier"
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
        )
    }
}
