package com.app.convocial.data.api

import com.app.convocial.common.Resource
import com.app.convocial.data.datasource.UserDataSource
import com.app.convocial.utils.TokenManager
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
@Inject
constructor(
  private val tokenManager: TokenManager,
  private val userDataSource: Lazy<UserDataSource>,
) : Interceptor {
  private val lock = Any()
  private var refreshTokenScheduled = false

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request().newBuilder()
    val token = tokenManager.getToken()
    if (token != null) {
      request.addHeader("Authorization", "Bearer $token")
    }
    val response = chain.proceed(request.build())
    if (response.code == 401) {
      if (!refreshTokenScheduled) {
        synchronized(lock) {
          refreshTokenScheduled = true
          val newToken = refreshAccessToken()
          if (newToken != null) {
            tokenManager.saveToken(newToken)
            val newRequest =
              chain.request().newBuilder().header("Authorization", "Bearer $newToken").build()
            response.close()
            return chain.proceed(newRequest)
          }
          refreshTokenScheduled = false
        }
      }
    }
    return response
  }

  private fun refreshAccessToken(): String? {
    return try {
      val refreshResponse = runBlocking { userDataSource.get().refreshToken() }
      if (refreshResponse is Resource.Success) {
        val newToken = refreshResponse.data?.token
        if (newToken != null) {
          tokenManager.saveToken(newToken)
          return newToken
        }
      }
      tokenManager.clearTokens()
      null
    } catch (e: Exception) {
      e.printStackTrace()
      tokenManager.clearTokens()
      null
    }
  }
}
