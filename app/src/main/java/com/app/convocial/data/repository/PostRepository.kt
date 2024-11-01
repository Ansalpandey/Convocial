package com.app.convocial.data.repository

import com.app.convocial.common.Resource
import com.app.convocial.data.model.CommentCreateResponse
import com.app.convocial.data.model.CommentResponse
import com.app.convocial.data.model.PostLikeResponse
import com.app.convocial.data.model.PostRequest
import com.app.convocial.data.model.PostResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PostRepository {
  suspend fun getPosts(page: Int, pageSize: Int): Response<PostResponse>

  suspend fun createPost(postRequest: PostRequest): Flow<Resource<PostResponse>>

  suspend fun likePost(postId: String): Flow<Resource<PostLikeResponse>>

  suspend fun unLikePost(postId: String): Flow<Resource<PostLikeResponse>>

  suspend fun getUsersPostById(
    id: String,
    page: Int,
    pageSize: Int
  ): Response<PostResponse>

  suspend fun getComments(postId: String, page: Int, pageSize: Int): CommentResponse

  suspend fun addComment(postId: String, content: String): Flow<Resource<CommentCreateResponse>>
}
