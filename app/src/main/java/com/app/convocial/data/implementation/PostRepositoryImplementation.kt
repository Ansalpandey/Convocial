package com.app.convocial.data.implementation

import com.app.convocial.common.Resource
import com.app.convocial.data.datasource.PostDataSource
import com.app.convocial.data.model.CommentCreateResponse
import com.app.convocial.data.model.CommentResponse
import com.app.convocial.data.model.PostLikeResponse
import com.app.convocial.data.model.PostRequest
import com.app.convocial.data.model.PostResponse
import com.app.convocial.data.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class PostRepositoryImplementation @Inject constructor(private val postDataSource: PostDataSource) :
  PostRepository {
  override suspend fun getPosts(page: Int, pageSize: Int): Response<PostResponse> {
    return postDataSource.getPosts(page, pageSize)
  }

  override suspend fun createPost(postRequest: PostRequest): Flow<Resource<PostResponse>> {
    return postDataSource.createPost(postRequest)
  }

  override suspend fun likePost(postId: String): Flow<Resource<PostLikeResponse>> {
    return postDataSource.likeUserPost(postId)
  }

  override suspend fun unLikePost(postId: String): Flow<Resource<PostLikeResponse>> {
    return postDataSource.unLikePost(postId)
  }

  override suspend fun getUsersPostById(
    id: String,
    page: Int,
    pageSize: Int,
  ): Response<PostResponse> {
    val response = postDataSource.getUserPostsById(id, page, pageSize)
    return response
  }

  override suspend fun getComments(postId: String, page: Int, pageSize: Int): CommentResponse {
    return postDataSource.getComments(postId, page, pageSize)
  }

  override suspend fun addComment(
    postId: String,
    content: String,
  ): Flow<Resource<CommentCreateResponse>> {
    return postDataSource.addComment(postId, content)
  }
}
