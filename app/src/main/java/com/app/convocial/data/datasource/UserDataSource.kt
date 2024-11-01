package com.app.convocial.data.datasource

import android.util.Log
import com.app.convocial.common.Resource
import com.app.convocial.data.api.ApiService
import com.app.convocial.data.api.AuthenticatedApiService
import com.app.convocial.data.model.EditProfileRequest
import com.app.convocial.data.model.FollowMessage
import com.app.convocial.data.model.FollowerResponse
import com.app.convocial.data.model.FollowingResponse
import com.app.convocial.data.model.ProfileResponse
import com.app.convocial.data.model.SearchResponse
import com.app.convocial.data.model.TokenResponse
import com.app.convocial.data.model.UserRequest
import com.app.convocial.data.model.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserDataSource
@Inject
constructor(
  private val apiService: ApiService,
  private val authenticatedApiService: AuthenticatedApiService,
) {

  suspend fun registerUser(user: UserRequest): Flow<Resource<UserResponse>> = flow {
    emit(Resource.Loading())
    try {
      val response = apiService.registerUser(user)
      if (response.isSuccessful && response.body() != null) {
        emit(Resource.Success(response.body()!!))
      } else {
        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
        emit(Resource.Error("Registration failed: $errorMessage"))
      }
    } catch (e: Exception) {
      Log.e("RegisterUser", "Error registering user", e)
      emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }
  }

  suspend fun loginUser(user: UserRequest): Flow<Resource<UserResponse>> = flow {
    emit(Resource.Loading())
    try {
      val response = apiService.loginUser(user)
      if (response.isSuccessful) {
        val userResponse = response.body()
        emit(Resource.Success(userResponse))
      } else {
        emit(Resource.Error("Login failed"))
      }
    } catch (e: Exception) {
      emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }
  }

  suspend fun editProfile(id: String, user: EditProfileRequest): Flow<Resource<ProfileResponse>> =
    flow {
      emit(Resource.Loading())
      try {
        val response = authenticatedApiService.editProfile(id, user)
        if (response.isSuccessful) {
          emit(Resource.Success(response.body()))
        } else {
          emit(Resource.Error("Edit profile failed"))
        }
      } catch (e: Exception) {
        emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
      }
    }

  suspend fun logoutUser(): Flow<Resource<UserResponse>> = flow {
    emit(Resource.Loading())
    try {
      val response = apiService.logoutUser()
      if (response.isSuccessful) {
        emit(Resource.Success(response.body()))
      } else {
        emit(Resource.Error("Logout failed"))
      }
    } catch (e: Exception) {
      emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }
  }

  suspend fun getUserProfile(): Flow<Resource<ProfileResponse>> = flow {
    try {
      val response = authenticatedApiService.getUserProfile()
      if (response.isSuccessful) {
        response.body()?.let { emit(Resource.Success(it)) }
          ?: run { emit(Resource.Error("Failed to fetch user profile: Empty response body")) }
      } else {
        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
        emit(Resource.Error("Failed to fetch user profile: $errorMessage"))
      }
    } catch (e: Exception) {
      emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }
  }

  suspend fun getUserProfileById(id: String): Flow<Resource<ProfileResponse>> = flow {
    emit(Resource.Loading())
    try {
      val response = authenticatedApiService.getUserProfileById(id)
      if (response.isSuccessful) {
        response.body()?.let { emit(Resource.Success(it)) }
          ?: run { emit(Resource.Error("Failed to fetch user profile: Empty response body")) }
      } else {
        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
        emit(Resource.Error("Failed to fetch user profile: $errorMessage"))
      }
    } catch (e: Exception) {
      emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }
  }

  suspend fun followUser(id: String): Flow<Resource<FollowMessage>> = flow {
    emit(Resource.Loading())
    try {
      val response = authenticatedApiService.followUser(id)
      if (response.isSuccessful) {
        response.body()?.let { emit(Resource.Success(it)) }
          ?: run { emit(Resource.Error("Failed to follow user: Empty response body")) }
      } else {
        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
        emit(Resource.Error("Failed to follow user: $errorMessage"))
      }
    } catch (e: Exception) {
      emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }
  }

  suspend fun isFollowingUser(id: String): Flow<Resource<FollowMessage>> = flow {
    emit(Resource.Loading())
    try {
      val response = authenticatedApiService.isFollowingUser(id)
      if (response.isSuccessful) {
        response.body()?.let { emit(Resource.Success(it)) }
          ?: run { emit(Resource.Error("Failed to follow user: Empty response body")) }
      } else {
        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
        emit(Resource.Error("Failed to follow user: $errorMessage"))
      }
    } catch (e: Exception) {
      emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }
  }

  suspend fun refreshToken(): Resource<TokenResponse> {
    return try {
      val response = authenticatedApiService.refreshToken()
      if (response.isSuccessful) {
        Resource.Success(response.body()!!)
      } else {
        Resource.Error("Failed to refresh token")
      }
    } catch (e: Exception) {
      Resource.Error(e.localizedMessage ?: "Unknown error")
    }
  }

  suspend fun searchUsers(query: String): Flow<Resource<SearchResponse>> = flow {
    emit(Resource.Loading())
    try {
      val response = authenticatedApiService.searchUsers(query)
      if (response.isSuccessful) {
        emit(Resource.Success(response.body()))
      } else {
        emit(Resource.Error("Search failed"))
      }
    } catch (e: Exception) {
      emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }
  }

  suspend fun getFollowers(id: String): Flow<Resource<FollowerResponse>> = flow {
    emit(Resource.Loading())

    val response = authenticatedApiService.getFollowers(id)
    if (response.isSuccessful) {
      emit(Resource.Success(response.body()))
    } else {
      emit(Resource.Error("Search failed"))
    }
  }


  fun getFollowings(id: String): Flow<Resource<FollowingResponse>> = flow {
    emit(Resource.Loading())

    val response = authenticatedApiService.getFollowings(id)
    if (response.isSuccessful) {
      emit(Resource.Success(response.body()))
    } else {
      emit(Resource.Error("Search failed"))
    }
  }

  fun sendDeviceToken(token: String): Flow<Resource<String>> =
    flow {
        try {
          emit(Resource.Loading()) // Emit loading state

          // Create a map to wrap the token as JSON
          val tokenMap = mapOf("token" to token)

          // Make API call
          val response = authenticatedApiService.sendDeviceToken(tokenMap)

          if (response.isSuccessful && response.body() != null) {
            emit(Resource.Success(response.body()!!)) // Emit success state
          } else {
            emit(Resource.Error(response.message() ?: "Failed to send device token"))
          }
        } catch (e: Exception) {
          emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
      }
      .flowOn(Dispatchers.IO)
}
