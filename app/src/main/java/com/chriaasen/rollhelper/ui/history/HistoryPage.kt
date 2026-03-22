package com.chriaasen.rollhelper.ui.history

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Fixed chrome height inside the card (title + subtitle + legend + all padding)
private val FIXED_CHROME_DP = 168f
// Approximate height of each history row (text + vertical padding + divider)
private val ROW_HEIGHT_DP = 34f

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    rollHistory: List<Triple<List<Int>, Int, Int>>
) {
    var selectedEntry by remember { mutableStateOf<Triple<List<Int>, Int, Int>?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0),
        content = { paddingValues ->
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val maxItems = ((maxHeight.value - FIXED_CHROME_DP) / ROW_HEIGHT_DP)
                        .toInt().coerceAtLeast(3)
                    val visibleEntries = rollHistory.reversed().take(maxItems)

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .shadow(6.dp, RoundedCornerShape(20.dp))
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "ROLL HISTORY",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        letterSpacing = 2.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = if (rollHistory.isEmpty()) "You have no rolls yet" else "Tap a row to see the full roll breakdown",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFACACAC),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp, bottom = 10.dp)
                                )

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    visibleEntries.forEach { entry ->
                                        val (rolls, abilityMod, proficiencyMod) = entry
                                        val diceTotal = rolls.sum()
                                        val total = diceTotal + abilityMod + proficiencyMod

                                        val subtextColor = Color(0xFFACACAC)
                                        val summaryText = buildAnnotatedString {
                                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                                                append("$diceTotal")
                                            }
                                            if (abilityMod != 0) {
                                                withStyle(SpanStyle(color = subtextColor)) { append("  +  ") }
                                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiaryContainer)) {
                                                    append("$abilityMod")
                                                }
                                            }
                                            if (proficiencyMod != 0) {
                                                withStyle(SpanStyle(color = subtextColor)) { append("  +  ") }
                                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onTertiaryContainer)) {
                                                    append("$proficiencyMod")
                                                }
                                            }
                                            withStyle(SpanStyle(color = subtextColor)) { append("  =  ") }
                                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                                append("$total")
                                            }
                                        }

                                        Text(
                                            text = summaryText,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedEntry = entry }
                                                .padding(vertical = 6.dp),
                                            textAlign = TextAlign.Start
                                        )

                                        HorizontalDivider(
                                            color = Color(0xFFACACAC).copy(alpha = 0.2f),
                                            thickness = 0.5.dp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    LegendItem(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        label = "Dice"
                                    )
                                    LegendItem(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        label = "Ability Mod"
                                    )
                                    LegendItem(
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        label = "Proficiency"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    // Detail modal
    selectedEntry?.let { (rolls, abilityMod, proficiencyMod) ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { selectedEntry = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ROLL BREAKDOWN",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 2.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                val subtextColor = Color(0xFFACACAC)
                val diceText = buildAnnotatedString {
                    withStyle(SpanStyle(color = subtextColor)) { append("Dice:  ") }
                    rolls.forEachIndexed { index, roll ->
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                            append("$roll")
                        }
                        if (index < rolls.size - 1) withStyle(SpanStyle(color = subtextColor)) { append("  +  ") }
                    }
                    withStyle(SpanStyle(color = subtextColor)) { append("  =  ") }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                        append("${rolls.sum()}")
                    }
                }
                Text(
                    text = diceText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                if (abilityMod != 0) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = subtextColor)) { append("Ability Mod:  ") }
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiaryContainer)) {
                                append("$abilityMod")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                if (proficiencyMod != 0) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = subtextColor)) { append("Proficiency:  ") }
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onTertiaryContainer)) {
                                append("$proficiencyMod")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFACACAC).copy(alpha = 0.3f)
                )

                val grandTotal = rolls.sum() + abilityMod + proficiencyMod
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = subtextColor)) { append("Total:  ") }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                            append("$grandTotal")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
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
