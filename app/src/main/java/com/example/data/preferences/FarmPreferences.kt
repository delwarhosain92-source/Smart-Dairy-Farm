package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smart_dairy_prefs")

enum class UserRole(val key: String) {
  ADMIN("ADMIN"),
  FARMER("FARMER");

  companion object {
    fun fromKey(key: String?): UserRole =
      entries.firstOrNull { it.key == key } ?: ADMIN
  }
}

enum class CurrencyOption(val code: String, val symbol: String) {
  BDT("BDT", "৳"),
  USD("USD", "$");

  companion object {
    fun fromCode(code: String?): CurrencyOption =
      entries.firstOrNull { it.code == code } ?: BDT
  }
}

data class FarmSettings(
  val language: AppLanguage = AppLanguage.ENGLISH,
  val userRole: UserRole = UserRole.ADMIN,
  val currency: CurrencyOption = CurrencyOption.BDT,
  val farmName: String = "Green Valley Dairy",
  val hasSeededDemo: Boolean = false,
)

class FarmPreferences(private val context: Context) {
  private object Keys {
    val LANGUAGE = stringPreferencesKey("language")
    val USER_ROLE = stringPreferencesKey("user_role")
    val CURRENCY = stringPreferencesKey("currency")
    val FARM_NAME = stringPreferencesKey("farm_name")
    val HAS_SEEDED_DEMO = booleanPreferencesKey("has_seeded_demo")
  }

  val settingsFlow: Flow<FarmSettings> = context.dataStore.data.map { prefs ->
    FarmSettings(
      language = AppLanguage.fromCode(prefs[Keys.LANGUAGE]),
      userRole = UserRole.fromKey(prefs[Keys.USER_ROLE]),
      currency = CurrencyOption.fromCode(prefs[Keys.CURRENCY]),
      farmName = prefs[Keys.FARM_NAME] ?: "Green Valley Dairy",
      hasSeededDemo = prefs[Keys.HAS_SEEDED_DEMO] ?: false,
    )
  }

  suspend fun setLanguage(language: AppLanguage) {
    context.dataStore.edit { it[Keys.LANGUAGE] = language.code }
  }

  suspend fun setUserRole(role: UserRole) {
    context.dataStore.edit { it[Keys.USER_ROLE] = role.key }
  }

  suspend fun setCurrency(currency: CurrencyOption) {
    context.dataStore.edit { it[Keys.CURRENCY] = currency.code }
  }

  suspend fun setFarmName(name: String) {
    context.dataStore.edit { it[Keys.FARM_NAME] = name.trim().ifEmpty { "Green Valley Dairy" } }
  }

  suspend fun markDemoSeeded(seeded: Boolean = true) {
    context.dataStore.edit { it[Keys.HAS_SEEDED_DEMO] = seeded }
  }
}
