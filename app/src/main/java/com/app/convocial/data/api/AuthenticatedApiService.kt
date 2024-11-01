package com.app.convocial.data.api

import com.app.convocial.data.model.CommentCreateResponse
import com.app.convocial.data.model.CommentRequest
import com.app.convocial.data.model.CommentResponse
import com.app.convocial.data.model.CourseResponse
import com.app.convocial.data.model.EditProfileRequest
import com.app.convocial.data.model.FollowMessage
import com.app.convocial.data.model.FollowerResponse
import com.app.convocial.data.model.FollowingResponse
import com.app.convocial.data.model.NotificationResponse
import com.app.convocial.data.model.PostLikeResponse
import com.app.convocial.data.model.PostRequest
import com.app.convocial.data.model.PostResponse
import com.app.convocial.data.model.ProfileResponse
import com.app.convocial.data.model.SearchResponse
import com.app.convocial.data.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthenticatedApiService {

  @GET("users/profile") suspend fun getUserProfile(): Response<ProfileResponse>

  @GET("users/profile/{id}")
  suspend fun getUserProfileById(@Path("id") id: String): Response<ProfileResponse>

  @GET("posts/users/{id}")
  suspend fun getUserPostsById(
    @Path("id") id: String,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int,
  ): Response<PostResponse>

  @POST("users/follow/{id}") suspend fun followUser(@Path("id") id: String): Response<FollowMessage>

  @GET("users/is-following/{id}")
  suspend fun isFollowingUser(@Path("id") id: String): Response<FollowMessage>

  @GET("users/followers/{id}")
  suspend fun getFollowers(@Path("id") id: String): Response<FollowerResponse>

  @GET("users/followings/{id}")
  suspend fun getFollowings(@Path("id") id: String): Response<FollowingResponse>

  @GET("courses")
  suspend fun getCourses(
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int,
  ): Response<CourseResponse>

  @GET("users/courses") suspend fun getUserCourses(): Response<CourseResponse>

  @GET("users/refresh-token") suspend fun refreshToken(): Response<TokenResponse>

  @GET("posts")
  suspend fun getPosts(
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int,
  ): Response<PostResponse>

  @POST("posts/create") suspend fun createPost(@Body post: PostRequest): Response<PostResponse>

  @POST("posts/{id}/like") suspend fun likePost(@Path("id") id: String): Response<PostLikeResponse>

  @POST("posts/{id}/unlike")
  suspend fun unLikePost(@Path("id") id: String): Response<PostLikeResponse>

  @PUT("users/edit-profile/{id}")
  suspend fun editProfile(
    @Path("id") id: String,
    @Body user: EditProfileRequest,
  ): Response<ProfileResponse>

  @GET("users/search") suspend fun searchUsers(@Query("q") query: String): Response<SearchResponse>

  @GET("posts/{id}/comments")
  suspend fun getComments(
    @Path("id") id: String,
    @Query("page") page: Int,
    @Query("pageSize") pageSize: Int,
  ): CommentResponse

  @POST("posts/{id}/comments")
  suspend fun addComment(
    @Path("id") id: String,
    @Body comment: CommentRequest,
  ): Response<CommentCreateResponse>

  @GET("notifications/{id}")
  suspend fun getNotifications(@Path("id") id: String): List<NotificationResponse>

  @POST("users/device-token")
  suspend fun sendDeviceToken(@Body token: Map<String, String>): Response<String>
}
