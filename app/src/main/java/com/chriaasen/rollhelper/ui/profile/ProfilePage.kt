package com.chriaasen.rollhelper.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chriaasen.rollhelper.ui.components.AppTopBar
import com.chriaasen.rollhelper.ui.storage.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun ProfilePage(dataStoreManager: DataStoreManager) {
    val scope = rememberCoroutineScope()

    // Local states for ability modifiers and enabled states
    val abilityModifiers = remember {
        mutableStateMapOf(
            "Strength" to 0,
            "Dexterity" to 0,
            "Constitution" to 0,
            "Intelligence" to 0,
            "Wisdom" to 0,
            "Charisma" to 0
        )
    }
    val enabledState = remember {
        mutableStateMapOf(
            "Strength" to false,
            "Dexterity" to false,
            "Constitution" to false,
            "Intelligence" to false,
            "Wisdom" to false,
            "Charisma" to false
        )
    }

    // Local states for settings and proficiency
    var isAudioEnabled by remember { mutableStateOf(true) }
    var isProficiencyEnabled by remember { mutableStateOf(false) }
    var isExpertiseEnabled by remember { mutableStateOf(false) }
    var proficiencyValue by remember { mutableStateOf(0) }

    // Sync states with DataStore
    LaunchedEffect(Unit) {
        launch {
            dataStoreManager.getAbilityEnabledState().collect { savedState ->
                enabledState.putAll(savedState)
            }
        }
        launch {
            dataStoreManager.getAbilityModifiers().collect { savedModifiers ->
                abilityModifiers.putAll(savedModifiers)
            }
        }
        launch {
            dataStoreManager.getAudioEnabled().collect { isAudioEnabled = it }
        }
        launch {
            dataStoreManager.getProficiencyEnabled().collect { isProficiencyEnabled = it }
        }
        launch {
            dataStoreManager.getExpertiseEnabled().collect { isExpertiseEnabled = it }
        }
        launch {
            dataStoreManager.getProficiencyValue().collect { proficiencyValue = it }
        }
    }

    Scaffold(
        topBar = { AppTopBar() },
        containerColor = Color.Transparent,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Add the SettingsCard
                SettingsCard(
                    isAudioEnabled = isAudioEnabled,
                    onAudioChanged = {
                        isAudioEnabled = it
                        scope.launch { dataStoreManager.saveAudioEnabled(it) }
                    }
                )

                // Add the AbilityModifiersSection
                AbilityModifiersSection(
                    abilityModifiers,
                    enabledState,
                    onStateChanged = { updatedModifiers, updatedEnabledState ->
                        abilityModifiers.putAll(updatedModifiers)
                        enabledState.putAll(updatedEnabledState)
                        scope.launch {
                            dataStoreManager.saveAbilityModifiers(updatedModifiers)
                            dataStoreManager.saveAbilityEnabledState(updatedEnabledState)
                        }
                    }
                )

                // Add the ProficiencyBonusSection
                ProficiencyBonusSection(
                    isProficiencyEnabled = isProficiencyEnabled,
                    isExpertiseEnabled = isExpertiseEnabled,
                    proficiencyValue = proficiencyValue,
                    onProficiencyEnabledUpdated = {
                        isProficiencyEnabled = it
                        scope.launch { dataStoreManager.saveProficiencyEnabled(it) }
                    },
                    onExpertiseEnabledUpdated = {
                        isExpertiseEnabled = it
                        scope.launch { dataStoreManager.saveExpertiseEnabled(it) }
                    },
                    onProficiencyValueUpdated = {
                        proficiencyValue = it
                        scope.launch { dataStoreManager.saveProficiencyValue(it) }
                    }
                )
            }
        }
    )
}



@Composable
fun SettingsCard(isAudioEnabled: Boolean, onAudioChanged: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Enable Audio",
                style = MaterialTheme.typography.bodyLarge.copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                color = MaterialTheme.colorScheme.tertiary,
            )
            Switch(
                checked = isAudioEnabled,
                onCheckedChange = { onAudioChanged(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    }
}

@Composable
fun AbilityModifiersSection(
    abilityModifiers: Map<String, Int>,
    enabledState: Map<String, Boolean>,
    onStateChanged: (Map<String, Int>, Map<String, Boolean>) -> Unit
) {
    val abilityOrder = listOf("Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom", "Charisma")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ability Modifiers",
                style = MaterialTheme.typography.headlineSmall.copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                color = MaterialTheme.colorScheme.tertiary
            )

            abilityOrder.forEach { key ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    val isChecked = enabledState[key] ?: false

                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            val updatedEnabledState = enabledState.toMutableMap()
                            val updatedModifiers = abilityModifiers.toMutableMap()

                            updatedEnabledState[key] = checked
                            if (!checked) updatedModifiers[key] = 0 // Reset dropdown value if unchecked

                            onStateChanged(updatedModifiers, updatedEnabledState)
                        },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium.copy(

                        ),
                        color = MaterialTheme.colorScheme.scrim,
                        modifier = Modifier.weight(1f)
                    )

                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Text(
                            text = abilityModifiers[key].toString(),
                            modifier = Modifier
                                .clickable(enabled = isChecked) { expanded = true }
                                .padding(8.dp)
                                .width(50.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isChecked) MaterialTheme.colorScheme.scrim else Color.Gray
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            (-5..10).forEach { value ->
                                DropdownMenuItem(
                                    text = { Text(value.toString()) },
                                    onClick = {
                                        val updatedModifiers = abilityModifiers.toMutableMap()
                                        updatedModifiers[key] = value
                                        onStateChanged(updatedModifiers, enabledState)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProficiencyBonusSection(
    isProficiencyEnabled: Boolean,
    isExpertiseEnabled: Boolean,
    proficiencyValue: Int,
    onProficiencyEnabledUpdated: (Boolean) -> Unit,
    onExpertiseEnabledUpdated: (Boolean) -> Unit,
    onProficiencyValueUpdated: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Proficiency Bonus",
                style = MaterialTheme.typography.headlineSmall.copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                color = MaterialTheme.colorScheme.tertiary
            )

            // Checkbox for enabling/disabling Proficiency Bonus
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isProficiencyEnabled,
                    onCheckedChange = { checked ->
                        onProficiencyEnabledUpdated(checked)
                        if (!checked) {
                            onProficiencyValueUpdated(0) // Reset the proficiency value
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.tertiary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Proficiency Bonus",
                    style = MaterialTheme.typography.bodyMedium.copy(

                    ),
                    color = MaterialTheme.colorScheme.scrim,
                    modifier = Modifier.weight(1f)
                )

                // Dropdown for Proficiency Value
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Text(
                        text = proficiencyValue.toString(),
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding(8.dp)
                            .width(50.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isProficiencyEnabled) MaterialTheme.colorScheme.scrim else Color.Gray
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        (0..10).forEach { value ->
                            DropdownMenuItem(
                                text = { Text(value.toString()) },
                                onClick = {
                                    if (isProficiencyEnabled) {
                                        onProficiencyValueUpdated(value)
                                    }
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Expertise Section
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isExpertiseEnabled,
                    onClick = { onExpertiseEnabledUpdated(!isExpertiseEnabled) },
                    enabled = isProficiencyEnabled
                )
                Text(
                    text = "Expertise",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}


