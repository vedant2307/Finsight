package com.finsight.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("finsight_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Keys — like column names for DataStore
    companion object {
        val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")
        val USER_NAME = stringPreferencesKey("user_name")
        val MONTHLY_SALARY = doublePreferencesKey("monthly_salary")
        val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
    }

    // ── READ ──────────────────────────────────────────────

    val isOnBoardingComplete: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_ONBOARDING_COMPLETE] ?: false
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }

    val monthlySalary: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[MONTHLY_SALARY] ?: 0.0
    }

    val selectedCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_CURRENCY] ?: ""
    }

    // ── WRITE ──────────────────────────────────────────────

    suspend fun saveOnBoardingData(
        userName: String,
        monthlySalary: Double,
        selectedCurrency: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETE] = true
            preferences[USER_NAME] = userName
            preferences[MONTHLY_SALARY] = monthlySalary
            preferences[SELECTED_CURRENCY] = selectedCurrency
        }
    }

    // Add these after saveOnBoardingData

    suspend fun saveName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun saveSalary(salary: Double) {
        context.dataStore.edit { preferences ->
            preferences[MONTHLY_SALARY] = salary
        }
    }
}