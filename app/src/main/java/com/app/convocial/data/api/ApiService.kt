package com.app.convocial.data.api

import com.app.convocial.data.model.UserRequest
import com.app.convocial.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
  @POST("users/register") suspend fun registerUser(@Body user: UserRequest): Response<UserResponse>

  @POST("users/login") suspend fun loginUser(@Body user: UserRequest): Response<UserResponse>

  @POST("users/logout") suspend fun logoutUser(): Response<UserResponse>
}
