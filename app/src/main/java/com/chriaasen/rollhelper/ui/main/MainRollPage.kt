package com.chriaasen.rollhelper.ui.main

import android.annotation.SuppressLint
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chriaasen.rollhelper.R
import com.chriaasen.rollhelper.ui.components.DragonAnimation
import com.chriaasen.rollhelper.ui.storage.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(35)
@SuppressLint("CoroutineCreationDuringComposition", "UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRollPage(
    modifier: Modifier = Modifier,
    rollHistory: MutableList<Triple<List<Int>, Int, Int>>,
    selectedDiceList: MutableList<Pair<String, Int>>,
    rollResults: List<Int>,
    hasRolled: Boolean,
    onHasRolledUpdated: (Boolean) -> Unit,
    isFocused: Boolean,
    onRollResultsUpdated: (List<Int>) -> Unit,
    shouldAnimate: Boolean,
    dataStoreManager: DataStoreManager,
    selectedModifiers: Set<String>,
    onSelectedModifiersChanged: (Set<String>) -> Unit,
    showModifiers: Boolean,
    onShowModifiersChanged: (Boolean) -> Unit,
    isCritMode: Boolean,
    onCritModeChanged: (Boolean) -> Unit,
    isProfSelected: Boolean,
    onIsProfSelectedChanged: (Boolean) -> Unit,
    isExpertiseSelected: Boolean,
    onIsExpertiseSelectedChanged: (Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val characterModifiers = remember { mutableStateMapOf<String, Int>() }
    var proficiencyValue by remember { mutableStateOf(0) }
    var isDragonEnabled by remember { mutableStateOf(true) }
    var showModifierValues by remember { mutableStateOf(false) }

    var showDragonAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch { dataStoreManager.getAbilityModifiers().collect { characterModifiers.putAll(it) } }
        launch { dataStoreManager.getProficiencyValue().collect { proficiencyValue = it } }
        launch { dataStoreManager.getDragonEnabled().collect { isDragonEnabled = it } }
        launch { dataStoreManager.getShowModifierValues().collect { showModifierValues = it } }
    }

    // Skip the first fire on each composition — proficiencyValue is still 0 at that point
    // because the DataStore flow hasn't delivered its value yet. Without this guard,
    // navigating away and back would wipe the prof value from the last history entry.
    var profEffectInitialized by remember { mutableStateOf(false) }

    // When prof/expertise toggles change after a roll, update only the most recent history entry
    LaunchedEffect(isProfSelected, isExpertiseSelected) {
        if (!profEffectInitialized) {
            profEffectInitialized = true
            return@LaunchedEffect
        }
        if (hasRolled && rollHistory.isNotEmpty()) {
            val effectiveProf = when {
                !isProfSelected -> 0
                isExpertiseSelected -> proficiencyValue * 2
                else -> proficiencyValue
            }
            val last = rollHistory.last()
            rollHistory[rollHistory.lastIndex] = Triple(last.first, last.second, effectiveProf)
            dataStoreManager.saveRollHistory(rollHistory)
        }
    }

    val rollTotal = rollResults.sum()
    val selectedAbilityModTotal = selectedModifiers.sumOf { key -> characterModifiers[key] ?: 0 }

    fun clearAll() {
        selectedDiceList.clear()
        onRollResultsUpdated(emptyList())
        onHasRolledUpdated(false)
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            Box(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.Center)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF2D2D2D).copy(alpha = 0.7f),
                        contentColor = Color.White
                    )
                }
            }
        }
    ) { _ ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isCompact = maxHeight < 700.dp
            val outerPad = if (isCompact) 8.dp else 16.dp
            val innerPad = if (isCompact) 8.dp else 12.dp

            // Grid dimensions: cols fixed at 5, rows derived from screen height
            val gridSpacing = 8.dp
            val availableWidth = maxWidth - outerPad * 2
            val cols = 5
            // Estimate overhead: collapsed panel (~140dp) + roll button (~90dp) + result bar (~40dp)
            val targetDiceSize = (availableWidth - gridSpacing * (cols - 1)) / cols
            val rows = ((maxHeight.value - 270f + gridSpacing.value) / (targetDiceSize.value + gridSpacing.value))
                .toInt().coerceIn(3, 10)
            val maxDice = if (isDragonEnabled) 25 else cols * rows

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                ) {
                    // Selection panel
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = outerPad)
                            .padding(top = 14.dp, bottom = 4.dp)
                            .shadow(6.dp, RoundedCornerShape(20.dp))
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(start = innerPad, end = innerPad, top = innerPad, bottom = 0.dp)) {
                            // Title
                            Text(
                                text = "PICK YOUR DICE!",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = 2.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            )

                            // Dice type chips — single row, fills width
                            DiceTypeChips(
                                onDiceSelected = { diceType ->
                                    if (hasRolled) {
                                        selectedDiceList.clear()
                                        onRollResultsUpdated(emptyList())
                                        onHasRolledUpdated(false)
                                    }
                                    if (isDragonEnabled) {
                                        when {
                                            selectedDiceList.size < 25 -> {
                                                selectedDiceList.add(diceType to 1)
                                                if (selectedDiceList.size == 20) {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            message = "Careful, you're poking the dragon",
                                                            duration = SnackbarDuration.Short
                                                        )
                                                    }
                                                }
                                            }
                                            selectedDiceList.size == 25 -> showDragonAnimation = true
                                        }
                                    } else {
                                        if (selectedDiceList.size < maxDice) {
                                            selectedDiceList.add(diceType to 1)
                                        } else {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Maximum of $maxDice dice reached.",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    }
                                }
                            )

                            // Expandable modifier section
                            AnimatedVisibility(visible = showModifiers) {
                                Column {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    Text(
                                        text = "ADD YOUR MODIFIERS",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            letterSpacing = 1.5.sp
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp)
                                    )
                                    ModifierChips(
                                        characterModifiers = characterModifiers,
                                        selectedModifiers = selectedModifiers,
                                        showValues = showModifierValues,
                                        onModifierToggled = { key ->
                                            onSelectedModifiersChanged(
                                                if (key in selectedModifiers) selectedModifiers - key
                                                else selectedModifiers + key
                                            )
                                        }
                                    )
                                }
                            }

                            // Undo | expand-handle pill | clear
                            val diceEmpty = selectedDiceList.isEmpty()
                            val clearInactive = diceEmpty && !hasRolled
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp, bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                IconButton(
                                    onClick = { if (!diceEmpty) selectedDiceList.removeLast() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.undo_arrow_icon),
                                        contentDescription = "Undo",
                                        tint = if (diceEmpty) Color(0xFFACACAC) else MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onShowModifiersChanged(!showModifiers) }
                                        .padding(bottom = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (!showModifiers) {
                                        Text(
                                            text = "MODIFIERS",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFACACAC),
                                                letterSpacing = 1.sp
                                            ),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(48.dp)
                                            .height(4.dp)
                                            .background(
                                                color = Color(0xFFACACAC),
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                                IconButton(
                                    onClick = { if (!clearInactive) clearAll() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.brush_icon),
                                        contentDescription = "Clear",
                                        tint = if (clearInactive) Color(0xFFACACAC) else MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Dice display — gets all remaining space
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = outerPad, vertical = 4.dp)
                    ) {
                        // First-time user hint — only shown if they've never rolled before
                        if (selectedDiceList.isEmpty() && rollHistory.isEmpty()) {
                            Text(
                                text = "Tap a die above to add it to your roll",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFACACAC)
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 32.dp)
                            )
                        }
                        DiceResultDisplay(
                            selectedDiceList = selectedDiceList,
                            rollResults = rollResults,
                            shouldAnimate = hasRolled && isFocused && shouldAnimate,
                            hasRolled = hasRolled,
                            rollTotal = rollTotal,
                            abilityModTotal = selectedAbilityModTotal,
                            proficiencyValue = proficiencyValue,
                            isCritMode = isCritMode,
                            onCritModeChanged = onCritModeChanged,
                            isProfSelected = isProfSelected,
                            onIsProfSelectedChanged = onIsProfSelectedChanged,
                            isExpertiseSelected = isExpertiseSelected,
                            onIsExpertiseSelectedChanged = onIsExpertiseSelectedChanged,
                            cols = cols,
                            rows = rows,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Roll button — standalone at the bottom
                    RollButton(
                        selectedDiceList = selectedDiceList,
                        onRoll = { rolledResults ->
                            coroutineScope.launch {
                                onRollResultsUpdated(rolledResults)
                                onHasRolledUpdated(true)
                                if (rolledResults.isNotEmpty()) {
                                    rollHistory.add(Triple(rolledResults, selectedAbilityModTotal, 0))
                                    if (rollHistory.size > 25) rollHistory.removeFirst()
                                    dataStoreManager.saveRollHistory(rollHistory)
                                }
                            }
                        },
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = outerPad)
                            .padding(bottom = 36.dp)
                    )
                }

                if (showDragonAnimation) {
                    DragonAnimation(
                        context = LocalContext.current,
                        isTriggered = showDragonAnimation,
                        onFrameUpdate = { frameIndex ->
                            coroutineScope.launch {
                                if (frameIndex % 2 == 0 && selectedDiceList.isNotEmpty()) {
                                    delay(1100L)
                                    val clearCount = minOf(5, selectedDiceList.size)
                                    delay(270L)
                                    repeat(clearCount) {
                                        if (selectedDiceList.isNotEmpty()) selectedDiceList.removeLast()
                                    }
                                }
                            }
                        },
                        onAnimationEnd = {
                            showDragonAnimation = false
                            onRollResultsUpdated(emptyList())
                        },
                        dataStoreManager = dataStoreManager
                    )
                }
            }
        }
    }
}
