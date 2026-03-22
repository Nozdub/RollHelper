package com.chriaasen.rollhelper.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chriaasen.rollhelper.ui.storage.CharacterProfile
import com.chriaasen.rollhelper.ui.storage.DataStoreManager
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.absoluteValue

private const val CAROUSEL_LOOP = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(dataStoreManager: DataStoreManager) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var characters by remember { mutableStateOf<List<CharacterProfile>>(emptyList()) }
    var activeCharacterId by remember { mutableStateOf("") }
    var isAudioEnabled by remember { mutableStateOf(true) }
    var isDragonEnabled by remember { mutableStateOf(true) }
    var isShowModifierValues by remember { mutableStateOf(false) }
    var proficiencyValue by remember { mutableStateOf(0) }
    var hasLoaded by remember { mutableStateOf(false) }
    var initialScrollDone by remember { mutableStateOf(false) }

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showCharacterSheet by remember { mutableStateOf(false) }
    var editingCharacter by remember { mutableStateOf<CharacterProfile?>(null) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { if (characters.isEmpty()) 1 else characters.size * CAROUSEL_LOOP }
    )

    LaunchedEffect(Unit) {
        launch { dataStoreManager.getAudioEnabled().collect { isAudioEnabled = it } }
        launch { dataStoreManager.getDragonEnabled().collect { isDragonEnabled = it } }
        launch { dataStoreManager.getShowModifierValues().collect { isShowModifierValues = it } }
        launch { dataStoreManager.getProficiencyValue().collect { proficiencyValue = it } }
        launch { dataStoreManager.getActiveCharacterId().collect { activeCharacterId = it } }
        launch {
            dataStoreManager.getCharacters().collect {
                characters = it
                hasLoaded = true
            }
        }
    }

    // One-time: position pager on the active character after data loads
    LaunchedEffect(hasLoaded, characters.size, activeCharacterId) {
        if (hasLoaded && characters.isNotEmpty() && activeCharacterId.isNotEmpty() && !initialScrollDone) {
            val idx = characters.indexOfFirst { it.id == activeCharacterId }.coerceAtLeast(0)
            pagerState.scrollToPage(characters.size * (CAROUSEL_LOOP / 2) + idx)
            initialScrollDone = true
            // Sync active character's proficiency to the global key MainRollPage reads
            val char = characters[idx]
            scope.launch { dataStoreManager.saveProficiencyValue(char.proficiencyBonus) }
        }
    }

    // Sync active character as user swipes
    LaunchedEffect(pagerState.currentPage) {
        if (characters.isNotEmpty()) {
            val idx = pagerState.currentPage % characters.size
            val char = characters[idx]
            if (char.id != activeCharacterId) {
                activeCharacterId = char.id
                scope.launch {
                    dataStoreManager.saveActiveCharacterId(char.id)
                    dataStoreManager.saveAbilityModifiers(char.abilityModifiers)
                    dataStoreManager.saveProficiencyValue(char.proficiencyBonus)
                }
            }
        }
    }

    val currentCharIndex = if (characters.isNotEmpty()) pagerState.currentPage % characters.size else 0
    val activeCharacter = characters.getOrNull(currentCharIndex)

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2D2D2D).copy(alpha = 0.7f),
                    contentColor = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            CharacterCarouselCard(
                characters = characters,
                pagerState = pagerState,
                onAddCharacter = {
                    editingCharacter = null
                    showCharacterSheet = true
                },
                onSettingsClick = { showSettingsSheet = true },
                onLongPressCharacter = { char ->
                    editingCharacter = char
                    showCharacterSheet = true
                }
            )

            AbilityModifiersDisplayCard(
                abilityModifiers = activeCharacter?.abilityModifiers ?: emptyMap(),
                hasCharacter = characters.isNotEmpty(),
                onNoCharacterTap = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Click the \"+\" above to set up a character first!",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )

            ProficiencyBonusCard(
                proficiencyBonus = activeCharacter?.proficiencyBonus ?: proficiencyValue,
                onProficiencyBonusChanged = { newValue ->
                    if (activeCharacter != null) {
                        val updated = characters.map { c ->
                            if (c.id == activeCharacterId) c.copy(proficiencyBonus = newValue) else c
                        }
                        characters = updated
                        scope.launch {
                            dataStoreManager.saveCharacters(updated)
                            dataStoreManager.saveProficiencyValue(newValue)
                        }
                    } else {
                        proficiencyValue = newValue
                        scope.launch { dataStoreManager.saveProficiencyValue(newValue) }
                    }
                }
            )
        }
    }

    if (showSettingsSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = sheetState
        ) {
            SettingsSheetContent(
                isAudioEnabled = isAudioEnabled,
                onAudioChanged = {
                    isAudioEnabled = it
                    scope.launch { dataStoreManager.saveAudioEnabled(it) }
                },
                isDragonEnabled = isDragonEnabled,
                onDragonChanged = {
                    isDragonEnabled = it
                    scope.launch { dataStoreManager.saveDragonEnabled(it) }
                },
                isShowModifierValues = isShowModifierValues,
                onShowModifierValuesChanged = {
                    isShowModifierValues = it
                    scope.launch { dataStoreManager.saveShowModifierValues(it) }
                }
            )
        }
    }

    if (showCharacterSheet) {
        CharacterEditSheet(
            character = editingCharacter,
            onSave = { name, modifiers ->
                scope.launch {
                    if (editingCharacter == null) {
                        val newChar = CharacterProfile(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            abilityModifiers = modifiers
                        )
                        val updated = characters + newChar
                        val newIdx = updated.size - 1
                        characters = updated
                        activeCharacterId = newChar.id
                        dataStoreManager.saveCharacters(updated)
                        dataStoreManager.saveActiveCharacterId(newChar.id)
                        dataStoreManager.saveAbilityModifiers(modifiers)
                        val cp = pagerState.currentPage
                        val diff = ((newIdx - cp % updated.size) + updated.size) % updated.size
                        pagerState.animateScrollToPage(cp + diff)
                    } else {
                        val updated = characters.map { char ->
                            if (char.id == editingCharacter!!.id)
                                char.copy(name = name, abilityModifiers = modifiers)
                            else char
                        }
                        characters = updated
                        dataStoreManager.saveCharacters(updated)
                        if (editingCharacter!!.id == activeCharacterId) {
                            dataStoreManager.saveAbilityModifiers(modifiers)
                        }
                    }
                    showCharacterSheet = false
                    editingCharacter = null
                }
            },
            onDelete = if (editingCharacter != null) {
                {
                    scope.launch {
                        val charToDelete = editingCharacter!!
                        val updated = characters.filter { it.id != charToDelete.id }
                        characters = updated
                        dataStoreManager.saveCharacters(updated)
                        if (updated.isEmpty()) {
                            activeCharacterId = ""
                            dataStoreManager.saveActiveCharacterId("")
                            dataStoreManager.saveAbilityModifiers(emptyMap())
                            pagerState.scrollToPage(0)
                        } else {
                            val newIdx = currentCharIndex.coerceIn(0, updated.size - 1)
                            activeCharacterId = updated[newIdx].id
                            dataStoreManager.saveActiveCharacterId(updated[newIdx].id)
                            dataStoreManager.saveAbilityModifiers(updated[newIdx].abilityModifiers)
                            pagerState.scrollToPage(updated.size * (CAROUSEL_LOOP / 2) + newIdx)
                        }
                        showCharacterSheet = false
                        editingCharacter = null
                    }
                }
            } else null,
            onDismiss = {
                showCharacterSheet = false
                editingCharacter = null
            }
        )
    }
}

// ── Character carousel card ───────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterCarouselCard(
    characters: List<CharacterProfile>,
    pagerState: PagerState,
    onAddCharacter: () -> Unit,
    onSettingsClick: () -> Unit,
    onLongPressCharacter: (CharacterProfile) -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 14.dp, bottom = 8.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // + | SETUP YOUR CHARACTER | ⚙️
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onAddCharacter) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New character",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                Text(
                    text = "SETUP YOUR CHARACTER",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.5.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Empty state hint when no characters exist yet
            if (characters.isEmpty()) {
                Text(
                    text = "Tap \"+\" to create your first character\nand set your ability modifiers",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFACACAC),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 12.dp)
                )
            }

            // Infinite smooth scroll carousel — only shown when characters exist
            if (characters.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = if (characters.size > 1) 72.dp else 0.dp),
                        pageSpacing = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val charIdx = page % characters.size
                        val char = characters[charIdx]
                        val isCurrent = page == pagerState.currentPage

                        // Alpha: only animate when there are multiple characters
                        val pageAlpha = if (characters.size > 1) {
                            val rawOffset = (pagerState.currentPage - page).toFloat() +
                                    pagerState.currentPageOffsetFraction
                            (1f - rawOffset.absoluteValue * (1f - 0.30f)).coerceIn(0.30f, 1f)
                        } else 1f

                        Text(
                            text = char.name.uppercase(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (isCurrent) TextDecoration.Underline
                                                 else TextDecoration.None,
                                letterSpacing = 1.sp
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .alpha(pageAlpha)
                                .then(
                                    if (isCurrent) Modifier.combinedClickable(
                                        onClick = {},
                                        onLongClick = { onLongPressCharacter(char) }
                                    ) else Modifier
                                )
                        )
                    }

                    // Edge gradient — only when multiple characters.
                    // Fade zone kept tight (15%→22%) so it stays within the 72dp
                    // content-padding area and never overlaps the current page's name.
                    if (characters.size > 1) {
                        val cardSurface = MaterialTheme.colorScheme.surface
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.horizontalGradient(
                                        0f to cardSurface,
                                        0.15f to cardSurface.copy(alpha = 0.9f),
                                        0.23f to Color.Transparent,
                                        0.77f to Color.Transparent,
                                        0.85f to cardSurface.copy(alpha = 0.9f),
                                        1f to cardSurface
                                    )
                                )
                        )

                        // Amber chevrons on top of the gradient
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous character",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 10.dp)
                                .size(26.dp)
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next character",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 10.dp)
                                .size(26.dp)
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

// ── Character create / edit sheet ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterEditSheet(
    character: CharacterProfile?,
    onSave: (String, Map<String, Int>) -> Unit,
    onDelete: (() -> Unit)?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val abilityOrder = listOf(
        "Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom", "Charisma"
    )

    var name by remember { mutableStateOf(character?.name ?: "") }
    val modifiers = remember {
        mutableStateMapOf<String, Int>().also { map ->
            abilityOrder.forEach { key ->
                map[key] = character?.abilityModifiers?.get(key) ?: 0
            }
        }
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title row with optional delete icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (character == null) "NEW CHARACTER" else "EDIT CHARACTER",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (onDelete != null) {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete character",
                            tint = Color(0xFFCC3333)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Character name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFACACAC),
                    unfocusedTextColor = Color(0xFFACACAC)
                )
            )

            HorizontalDivider()

            Text(
                text = "ABILITY MODIFIERS",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.5.sp
                )
            )

            abilityOrder.forEach { key ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    ValueStepper(
                        value = modifiers[key] ?: 0,
                        min = -5,
                        max = 10,
                        onValueChange = { modifiers[key] = it }
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "Tip: Long press your character's name to edit these values later.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFACACAC)
            )

            Button(
                onClick = { if (name.isNotBlank()) onSave(name.trim(), modifiers.toMap()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Text(
                    text = "Save Character",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    text = "DELETE CHARACTER?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 2.sp
                    )
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ${character?.name}? This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete?.invoke()
                    }
                ) {
                    Text("Delete", color = Color(0xFFCC3333))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}

// ── Ability modifiers display (read-only) ─────────────────────────────────────

@Composable
fun AbilityModifiersDisplayCard(
    abilityModifiers: Map<String, Int>,
    hasCharacter: Boolean = true,
    onNoCharacterTap: (() -> Unit)? = null
) {
    val abilityOrder = listOf(
        "Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom", "Charisma"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ABILITY MODIFIERS",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 2.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            abilityOrder.forEachIndexed { index, key ->
                val value = abilityModifiers[key] ?: 0
                val sign = if (value >= 0) "+" else ""

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .then(
                            if (!hasCharacter && onNoCharacterTap != null)
                                Modifier.clickable { onNoCharacterTap() }
                            else Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key.uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hasCharacter) MaterialTheme.colorScheme.onSurface else Color(0xFFACACAC)
                    )
                    Text(
                        text = "$sign$value",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hasCharacter) MaterialTheme.colorScheme.onSurface else Color(0xFFACACAC)
                    )
                }

                if (index < abilityOrder.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                }
            }
        }
    }
}

// ── Proficiency card ──────────────────────────────────────────────────────────

@Composable
fun ProficiencyBonusCard(
    proficiencyBonus: Int,
    onProficiencyBonusChanged: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 16.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "PROFICIENCY BONUS",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 2.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Bonus",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Toggle on the roll screen after rolling",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFACACAC)
                    )
                }
                ValueStepper(
                    value = proficiencyBonus,
                    min = 0,
                    max = 10,
                    valueColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    onValueChange = onProficiencyBonusChanged
                )
            }
        }
    }
}

// ── Settings sheet ────────────────────────────────────────────────────────────

@Composable
fun SettingsSheetContent(
    isAudioEnabled: Boolean,
    onAudioChanged: (Boolean) -> Unit,
    isDragonEnabled: Boolean,
    onDragonChanged: (Boolean) -> Unit,
    isShowModifierValues: Boolean,
    onShowModifierValuesChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 2.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Enable Audio", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Dragon roar sound effect",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isAudioEnabled,
                onCheckedChange = onAudioChanged,
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.tertiary)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Dragon Easter Egg", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (isDragonEnabled) "Caps dice at 25" else "As many as fit on your screen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isDragonEnabled,
                onCheckedChange = onDragonChanged,
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.tertiary)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Show Modifier Values", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (isShowModifierValues) "Shows e.g. STR +2" else "Shows e.g. STR",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isShowModifierValues,
                onCheckedChange = onShowModifierValuesChanged,
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.tertiary)
            )
        }
    }
}

// ── Value stepper ─────────────────────────────────────────────────────────────

@Composable
fun ValueStepper(
    value: Int,
    min: Int,
    max: Int,
    enabled: Boolean = true,
    valueColor: Color? = null,
    onValueChange: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onValueChange(value - 1) },
            enabled = enabled && value > min,
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "−",
                style = MaterialTheme.typography.titleLarge,
                color = if (enabled && value > min) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor ?: if (enabled) MaterialTheme.colorScheme.onSurface
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(36.dp)
        )
        IconButton(
            onClick = { onValueChange(value + 1) },
            enabled = enabled && value < max,
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.titleLarge,
                color = if (enabled && value < max) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
