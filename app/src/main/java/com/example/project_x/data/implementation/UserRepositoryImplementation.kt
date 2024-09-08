package com.example.project_x.data.implementation

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.example.project_x.common.Resource
import com.example.project_x.data.datasource.UserDataSource
import com.example.project_x.data.model.EditProfileRequest
import com.example.project_x.data.model.FollowMessage
import com.example.project_x.data.model.FollowerResponse
import com.example.project_x.data.model.ProfileResponse
import com.example.project_x.data.model.SearchResponse
import com.example.project_x.data.model.TokenResponse
import com.example.project_x.data.model.User
import com.example.project_x.data.model.UserRequest
import com.example.project_x.data.model.UserResponse
import com.example.project_x.data.repository.UserRepository
import com.example.project_x.preferences.UserPreferences
import com.example.project_x.preferences.dataStore
import com.example.project_x.ui.stateholder.UserStateHolder
import com.example.project_x.utils.TokenManager
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class UserRepositoryImplementation
@Inject
constructor(
  private val userDataSource: UserDataSource,
  @ApplicationContext private val context: Context,
  private val tokenManager: TokenManager,
) : UserRepository {
  private val dataStore = context.dataStore

  override val userStateHolder: Flow<UserStateHolder> =
    dataStore.data.map { preferences ->
      val isLoggedIn = preferences[UserPreferences.IS_LOGGED_IN] ?: false
      if (isLoggedIn) {
        UserStateHolder(
          isLoading = false,
          data =
            flowOf(
              UserResponse(
                message = "",
                user =
                  User(
                    id = preferences[UserPreferences.USER_ID] ?: "",
                    name = preferences[UserPreferences.USER_NAME] ?: "",
                    email = preferences[UserPreferences.USER_EMAIL] ?: "",
                    age = preferences[UserPreferences.USER_AGE] ?: 0,
                    username = preferences[UserPreferences.USER_USERNAME] ?: "",
                    bio = preferences[UserPreferences.USER_BIO] ?: "",
                  ),
              )
            ),
          error = "",
          isLoggedIn = isLoggedIn,
        )
      } else {
        UserStateHolder()
      }
    }

  override suspend fun registerUser(user: UserRequest): Flow<Resource<UserResponse>> {
    return userDataSource.registerUser(user)
  }

  override suspend fun loginUser(user: UserRequest): Flow<Resource<UserResponse>> {
    return userDataSource.loginUser(user).onEach { resource ->
      if (resource is Resource.Success) {
        setUserPreferences(resource.data!!, true)
        if (resource.data.token != tokenManager.getToken()) {
          tokenManager.saveToken(resource.data.token!!)
        }
      }
    }
  }

  private suspend fun setUserPreferences(user: UserResponse, isLoggedIn: Boolean) {
    dataStore.edit { preferences ->
      preferences[UserPreferences.IS_LOGGED_IN] = isLoggedIn
      preferences[UserPreferences.USER_NAME] = user.user.name
      preferences[UserPreferences.USER_ID] = user.user.id
      preferences[UserPreferences.USER_EMAIL] = user.user.email
      preferences[UserPreferences.USER_AGE] = user.user.age
      preferences[UserPreferences.USER_USERNAME] = user.user.username
      preferences[UserPreferences.USER_BIO] = user.user.bio
      preferences[UserPreferences.SEARCH_HISTORY] = ""
    }
  }

  override suspend fun logoutUser() {
    userDataSource.logoutUser()
    clearUserPreferences()
  }

  private suspend fun clearUserPreferences() {
    dataStore.edit { preferences -> preferences.clear() }
  }

  override suspend fun getUserProfile(): Flow<Resource<ProfileResponse>> = flow {
    userDataSource.getUserProfile().collect { resource -> emit(resource) }
  }

  override suspend fun getUserProfileById(id: String): Flow<Resource<ProfileResponse>> = flow {
    userDataSource.getUserProfileById(id).collect { resource -> emit(resource) }
  }

  override suspend fun editProfile(
    id: String,
    user: EditProfileRequest,
  ): Flow<Resource<ProfileResponse>> = flow {
    userDataSource.editProfile(id, user).collect { resource -> emit(resource) }
  }

  override suspend fun followUser(id: String): Flow<Resource<FollowMessage>> = flow {
    userDataSource.followUser(id).collect { resource -> emit(resource) }
  }

  override suspend fun isFollowingUser(id: String): Flow<Resource<FollowMessage>> = flow {
    userDataSource.isFollowingUser(id).collect { resource -> emit(resource) }
  }

  override suspend fun refreshToken(): Resource<TokenResponse> {
    return userDataSource.refreshToken()
  }

  override suspend fun searchUsers(query: String): Flow<Resource<SearchResponse>> {
    return userDataSource.searchUsers(query).onEach { resource ->
      if (resource is Resource.Success) {
        try {
          // Retrieve the existing search history or default to an empty list
          val existingHistoryJson = dataStore.data.first()[UserPreferences.SEARCH_HISTORY] ?: "[]"
          val existingHistory =
            Gson().fromJson(existingHistoryJson, Array<String>::class.java)?.toMutableList()
              ?: mutableListOf()

          // Add new query to history if it's not already present
          if (!existingHistory.contains(query)) {
            existingHistory.add(query)
          }

          val updatedHistoryJson = Gson().toJson(existingHistory)
          dataStore.edit { preferences ->
            preferences[UserPreferences.SEARCH_HISTORY] = updatedHistoryJson
          }
        } catch (e: Exception) {
          // Log the error or handle it as needed
          Log.e("SearchUsers", "Failed to update search history", e)
        }
      }
    }
  }

  override suspend fun getFollowers(id: String): Flow<Resource<FollowerResponse>> = flow {
    userDataSource.getFollowers(id).collect { resource -> emit(resource) }
  }
}
