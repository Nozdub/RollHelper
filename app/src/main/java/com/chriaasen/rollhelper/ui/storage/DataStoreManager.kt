package com.chriaasen.rollhelper.ui.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

private val lenientJson = Json { ignoreUnknownKeys = true }

class DataStoreManager(private val context: Context) {

    companion object {
        private val AUDIO_ENABLED_KEY = booleanPreferencesKey("audio_enabled")
        private val DRAGON_ENABLED_KEY = booleanPreferencesKey("dragon_enabled")
        private val SHOW_MODIFIER_VALUES_KEY = booleanPreferencesKey("show_modifier_values")
        private val PROFICIENCY_VALUE_KEY = intPreferencesKey("proficiency_value")
        private val ABILITY_MODIFIERS_KEY = stringPreferencesKey("ability_modifiers")
        private val ROLL_HISTORY_KEY = stringPreferencesKey("roll_history")
        private val CHARACTERS_KEY = stringPreferencesKey("characters")
        private val ACTIVE_CHARACTER_ID_KEY = stringPreferencesKey("active_character_id")
    }

    fun getAudioEnabled(): Flow<Boolean> = context.dataStore.data.map {
        it[AUDIO_ENABLED_KEY] ?: true
    }

    fun getDragonEnabled(): Flow<Boolean> = context.dataStore.data.map {
        it[DRAGON_ENABLED_KEY] ?: true
    }

    fun getShowModifierValues(): Flow<Boolean> = context.dataStore.data.map {
        it[SHOW_MODIFIER_VALUES_KEY] ?: false
    }

    fun getProficiencyValue(): Flow<Int> = context.dataStore.data.map {
        it[PROFICIENCY_VALUE_KEY] ?: 0
    }

    fun getAbilityModifiers(): Flow<Map<String, Int>> = context.dataStore.data.map {
        val json = it[ABILITY_MODIFIERS_KEY] ?: "{}"
        lenientJson.decodeFromString(json)
    }

    fun getCharacters(): Flow<List<CharacterProfile>> = context.dataStore.data.map {
        val json = it[CHARACTERS_KEY] ?: "[]"
        lenientJson.decodeFromString(json)
    }

    fun getActiveCharacterId(): Flow<String> = context.dataStore.data.map {
        it[ACTIVE_CHARACTER_ID_KEY] ?: ""
    }

    fun getRollHistory(): Flow<List<Triple<List<Int>, Int, Int>>> = context.dataStore.data.map {
        val json = it[ROLL_HISTORY_KEY] ?: "[]"
        lenientJson.decodeFromString(json)
    }

    suspend fun saveAudioEnabled(enabled: Boolean) {
        context.dataStore.edit { it[AUDIO_ENABLED_KEY] = enabled }
    }

    suspend fun saveDragonEnabled(enabled: Boolean) {
        context.dataStore.edit { it[DRAGON_ENABLED_KEY] = enabled }
    }

    suspend fun saveShowModifierValues(show: Boolean) {
        context.dataStore.edit { it[SHOW_MODIFIER_VALUES_KEY] = show }
    }

    suspend fun saveProficiencyValue(value: Int) {
        context.dataStore.edit { it[PROFICIENCY_VALUE_KEY] = value }
    }

    suspend fun saveAbilityModifiers(modifiers: Map<String, Int>) {
        val json = Json.encodeToString(modifiers)
        context.dataStore.edit { it[ABILITY_MODIFIERS_KEY] = json }
    }

    suspend fun saveCharacters(characters: List<CharacterProfile>) {
        val json = Json.encodeToString(characters)
        context.dataStore.edit { it[CHARACTERS_KEY] = json }
    }

    suspend fun saveActiveCharacterId(id: String) {
        context.dataStore.edit { it[ACTIVE_CHARACTER_ID_KEY] = id }
    }

    suspend fun saveRollHistory(history: List<Triple<List<Int>, Int, Int>>) {
        val json = Json.encodeToString(history)
        context.dataStore.edit { it[ROLL_HISTORY_KEY] = json }
    }
}
