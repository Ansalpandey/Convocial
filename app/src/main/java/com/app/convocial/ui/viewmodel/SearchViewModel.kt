package com.app.convocial.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.convocial.common.Resource
import com.app.convocial.data.model.SearchResponse
import com.app.convocial.data.model.SearchUserResponse
import com.app.convocial.data.repository.UserRepository
import com.app.convocial.preferences.UserPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel
@Inject
constructor(
  private val userRepository: UserRepository,
  private val dataStore: DataStore<Preferences>,
  private val gson: Gson,
) : ViewModel() {

  // StateFlow for search results
  private val _searchResults = MutableStateFlow<Resource<SearchResponse>>(Resource.Loading())
  val searchResults: StateFlow<Resource<SearchResponse>> = _searchResults.asStateFlow()

  // StateFlow for search history
  private val _searchHistory = MutableStateFlow<List<SearchUserResponse>>(emptyList())
  val searchHistory: StateFlow<List<SearchUserResponse>> = _searchHistory.asStateFlow()

  init {
    // Load initial search history with error handling
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val historyJson = dataStore.data.first()[UserPreferences.SEARCH_HISTORY] ?: "[]"
        val historyArray = gson.fromJson(historyJson, Array<SearchUserResponse>::class.java)
        _searchHistory.value = historyArray.toList()
      } catch (e: JsonSyntaxException) {
        // Handle the error by clearing the malformed data
        clearSearchHistory()
      }
    }
  }

  fun searchUsers(query: String) {
    viewModelScope.launch(Dispatchers.IO) {
      userRepository.searchUsers(query).collect { resource -> _searchResults.value = resource }
    }
  }

  fun addUserToHistory(user: SearchUserResponse) {
    viewModelScope.launch(Dispatchers.IO) {
      val existingHistoryJson = dataStore.data.first()[UserPreferences.SEARCH_HISTORY] ?: "[]"
      val existingHistory =
        try {
          gson.fromJson(existingHistoryJson, Array<SearchUserResponse>::class.java).toMutableList()
        } catch (e: JsonSyntaxException) {
          mutableListOf()
        }

      // Add new user to history if not already present
      if (existingHistory.none { it._id == user._id }) {
        existingHistory.add(user)
      }

      val updatedHistoryJson = gson.toJson(existingHistory)
      dataStore.edit { preferences ->
        preferences[UserPreferences.SEARCH_HISTORY] = updatedHistoryJson
      }

      _searchHistory.value = existingHistory
    }
  }

  fun clearSearchHistory() {
    viewModelScope.launch(Dispatchers.IO) {
      // Clear search history in DataStore
      dataStore.edit { preferences -> preferences[UserPreferences.SEARCH_HISTORY] = "[]" }

      // Update the StateFlow to reflect the cleared history
      _searchHistory.value = emptyList()
    }
  }
}
