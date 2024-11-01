package com.app.convocial.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object UserPreferences {
  val USER_ID = stringPreferencesKey("user_id")
  val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
  val USER_NAME = stringPreferencesKey("user_name")
  val USER_EMAIL = stringPreferencesKey("user_email")
  val USER_AGE = intPreferencesKey("user_age")
  val USER_USERNAME = stringPreferencesKey("user_username")
  val USER_BIO = stringPreferencesKey("user_bio")
  val SEARCH_HISTORY = stringPreferencesKey("search_history")
  val DEVICE_TOKEN = stringPreferencesKey("device_token")
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
