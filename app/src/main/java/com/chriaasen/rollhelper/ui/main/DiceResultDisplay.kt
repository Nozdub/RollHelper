package com.chriaasen.rollhelper.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chriaasen.rollhelper.ui.utils.getAnimationFramesForDiceResourceIds
import com.chriaasen.rollhelper.ui.utils.getStillImageForResultResourceId
import com.chriaasen.rollhelper.ui.utils.maxDiceValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceResultDisplay(
    selectedDiceList: List<Pair<String, Int>>,
    rollResults: List<Int>,
    shouldAnimate: Boolean,
    hasRolled: Boolean,
    rollTotal: Int,
    abilityModTotal: Int,
    proficiencyValue: Int,
    isCritMode: Boolean,
    onCritModeChanged: (Boolean) -> Unit,
    isProfSelected: Boolean,
    onIsProfSelectedChanged: (Boolean) -> Unit,
    isExpertiseSelected: Boolean,
    onIsExpertiseSelectedChanged: (Boolean) -> Unit,
    cols: Int,
    rows: Int,
    modifier: Modifier = Modifier
) {
    var showModifySheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val diceImageResIds = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(selectedDiceList.size) {
        selectedDiceList.forEachIndexed { index, (diceType, _) ->
            if (!diceImageResIds.containsKey(index)) {
                diceImageResIds[index] = getStillImageForResultResourceId(diceType, maxDiceValue(diceType))
            }
        }
        diceImageResIds.keys.filter { it >= selectedDiceList.size }.forEach { diceImageResIds.remove(it) }
    }

    LaunchedEffect(shouldAnimate, rollResults) {
        if (shouldAnimate) {
            val allFrames = selectedDiceList.map { (diceType, _) ->
                getAnimationFramesForDiceResourceIds(diceType)
            }
            val maxFrameCount = allFrames.maxOfOrNull { it.size } ?: 0
            repeat(maxFrameCount) { frameIdx ->
                selectedDiceList.forEachIndexed { index, (diceType, _) ->
                    val frames = allFrames[index]
                    diceImageResIds[index] = frames.getOrElse(frameIdx) {
                        getStillImageForResultResourceId(diceType, rollResults.getOrNull(index) ?: maxDiceValue(diceType))
                    }
                }
                delay(50L)
            }
            selectedDiceList.forEachIndexed { index, (diceType, _) ->
                val result = rollResults.getOrNull(index) ?: maxDiceValue(diceType)
                diceImageResIds[index] = getStillImageForResultResourceId(diceType, result)
            }
        } else if (hasRolled) {
            selectedDiceList.forEachIndexed { index, (diceType, _) ->
                val result = rollResults.getOrNull(index) ?: maxDiceValue(diceType)
                diceImageResIds[index] = getStillImageForResultResourceId(diceType, result)
            }
        } else {
            selectedDiceList.forEachIndexed { index, (diceType, _) ->
                diceImageResIds[index] = getStillImageForResultResourceId(diceType, maxDiceValue(diceType))
            }
        }
    }

    val effectiveDice = if (isCritMode) rollTotal * 2 else rollTotal
    val effectiveProf = when {
        !isProfSelected -> 0
        isExpertiseSelected -> proficiencyValue * 2
        else -> proficiencyValue
    }
    val effectiveMod = abilityModTotal + effectiveProf
    val finalTotal = effectiveDice + effectiveMod

    var resultFontSize by remember(isCritMode, isProfSelected, isExpertiseSelected, rollTotal, abilityModTotal) {
        mutableStateOf(20.sp)
    }

    val resultBarHeight = if (hasRolled) 40.dp else 0.dp
    val gridPad = 8.dp

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableWidth = maxWidth - gridPad * 2
        val availableHeight = maxHeight - gridPad * 2 - resultBarHeight

        val diceSizeByWidth = (availableWidth - 8.dp * (cols - 1)) / cols
        val diceSizeByHeight = (availableHeight - 8.dp * (rows - 1)) / rows
        val diceSize = minOf(diceSizeByWidth, diceSizeByHeight).coerceAtLeast(24.dp)

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gridPad)
                    .padding(bottom = resultBarHeight),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(selectedDiceList.size) { index ->
                    val (diceType, _) = selectedDiceList[index]
                    val currentResult = rollResults.getOrNull(index) ?: maxDiceValue(diceType)
                    val imageResId = diceImageResIds[index]
                        ?: getStillImageForResultResourceId(diceType, maxDiceValue(diceType))

                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Dice $diceType showing $currentResult",
                        modifier = Modifier.size(diceSize)
                    )
                }
            }

            if (hasRolled) {
                val muted = Color(0xFFACACAC)
                val profActive = isProfSelected || isExpertiseSelected
                val modHighlight = MaterialTheme.colorScheme.onTertiaryContainer
                val resultText = buildAnnotatedString {
                    withStyle(SpanStyle(color = muted)) { append("Dice: $effectiveDice  |  Mod: ") }
                    withStyle(SpanStyle(color = if (profActive) modHighlight else muted)) {
                        append("$effectiveMod")
                    }
                    withStyle(SpanStyle(color = muted)) { append("  |  Total: $finalTotal") }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(resultBarHeight)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { showModifySheet = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Modify roll",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = resultFontSize),
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.weight(1f),
                        onTextLayout = { result ->
                            if (result.hasVisualOverflow && resultFontSize > 8.sp) {
                                resultFontSize = (resultFontSize.value * 0.88f).sp
                            }
                        }
                    )

                    // Spacer to balance the icon on the left
                    Spacer(modifier = Modifier.size(32.dp))
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (hasRolled) 48.dp else 8.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2D2D2D).copy(alpha = 0.7f),
                    contentColor = Color.White
                )
            }
        }
    }

    if (showModifySheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showModifySheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "MODIFY ROLL",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 2.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // CRIT toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isCritMode,
                        onClick = {
                            val next = !isCritMode
                            onCritModeChanged(next)
                            if (next) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Crit! Dice results doubled.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Critical Hit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Doubles the dice result",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFACACAC)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // PROF toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (proficiencyValue > 0) 1f else 0.38f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isProfSelected,
                        onClick = {
                            if (proficiencyValue > 0) {
                                val next = !isProfSelected
                                onIsProfSelectedChanged(next)
                                if (!next) onIsExpertiseSelectedChanged(false)
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Proficiency Bonus",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (proficiencyValue > 0) "+$proficiencyValue to total"
                                   else "Set proficiency bonus on the Profile page",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFACACAC)
                        )
                    }
                }

                // EXPT toggle — only shown when PROF is active
                if (isProfSelected && proficiencyValue > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isExpertiseSelected,
                            onClick = { onIsExpertiseSelectedChanged(!isExpertiseSelected) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.tertiary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Expertise",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "+${proficiencyValue * 2} to total (doubles proficiency)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFACACAC)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Live total breakdown
                val sheetMuted = Color(0xFFACACAC)
                val sheetModHighlight = MaterialTheme.colorScheme.onTertiaryContainer
                val sheetProfActive = isProfSelected || isExpertiseSelected
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = sheetMuted)) { append("Dice: $effectiveDice") }
                        if (effectiveMod != 0) {
                            withStyle(SpanStyle(color = sheetMuted)) { append("  |  Mod: ") }
                            withStyle(SpanStyle(color = if (sheetProfActive) sheetModHighlight else sheetMuted)) {
                                append("$effectiveMod")
                            }
                        }
                        withStyle(SpanStyle(color = sheetMuted)) { append("  |  Total: $finalTotal") }
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 1.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
