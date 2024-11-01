package com.app.convocial.data.repository

import com.app.convocial.common.Resource
import com.app.convocial.data.model.EditProfileRequest
import com.app.convocial.data.model.FollowMessage
import com.app.convocial.data.model.FollowerResponse
import com.app.convocial.data.model.FollowingResponse
import com.app.convocial.data.model.ProfileResponse
import com.app.convocial.data.model.SearchResponse
import com.app.convocial.data.model.TokenResponse
import com.app.convocial.data.model.UserRequest
import com.app.convocial.data.model.UserResponse
import com.app.convocial.ui.stateholder.UserStateHolder
import kotlinx.coroutines.flow.Flow

interface UserRepository {
  val userStateHolder: Flow<UserStateHolder>

  suspend fun registerUser(user: UserRequest): Flow<Resource<UserResponse>>

  suspend fun loginUser(user: UserRequest): Flow<Resource<UserResponse>>

  suspend fun logoutUser()

  suspend fun getUserProfile(): Flow<Resource<ProfileResponse>>

  suspend fun getUserProfileById(id: String): Flow<Resource<ProfileResponse>>

  suspend fun followUser(id: String): Flow<Resource<FollowMessage>>

  suspend fun isFollowingUser(id: String): Flow<Resource<FollowMessage>>

  suspend fun refreshToken(): Resource<TokenResponse>

  suspend fun editProfile(id: String, user: EditProfileRequest): Flow<Resource<ProfileResponse>>

  suspend fun searchUsers(query: String): Flow<Resource<SearchResponse>>

  suspend fun getFollowers(id: String): Flow<Resource<FollowerResponse>>

  suspend fun getFollowings(id: String): Flow<Resource<FollowingResponse>>

  suspend fun sendDeviceToken(token: String) : Flow<Resource<String>>
}
