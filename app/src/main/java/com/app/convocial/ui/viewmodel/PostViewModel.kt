package com.app.convocial.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.convocial.common.Resource
import com.app.convocial.data.model.CommentCreateResponse
import com.app.convocial.data.model.CommentResponse
import com.app.convocial.data.model.Post
import com.app.convocial.data.model.PostLikeResponse
import com.app.convocial.data.model.PostRequest
import com.app.convocial.data.model.PostResponse
import com.app.convocial.data.pagination.PostPagingSource
import com.app.convocial.data.pagination.UserPostsPagingSource
import com.app.convocial.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PostViewModel
@Inject
constructor(
  private val postRepository: PostRepository,
  private val postPagingSource: PostPagingSource
) : ViewModel() {
  private val _posts = MutableStateFlow<PagingData<Post>>(PagingData.empty())
  val posts: StateFlow<PagingData<Post>> = _posts.asStateFlow()
  private val _userPosts = MutableStateFlow<PagingData<Post>>(PagingData.empty())
  val userPosts: StateFlow<PagingData<Post>> = _userPosts.asStateFlow()
  private val _isPostsFetched = MutableStateFlow(false)
  private val _likePost = MutableStateFlow<Resource<PostLikeResponse>>(Resource.Loading())

  private val _post = MutableStateFlow<Resource<PostResponse>>(Resource.Loading())
  val post: StateFlow<Resource<PostResponse>> = _post
  private var isPostCreated by mutableStateOf(false)

  private val _postLikes = mutableStateMapOf<String, Boolean>()

  private val _comments = MutableStateFlow<CommentResponse?>(null)

  private val _createComment = MutableStateFlow<Resource<CommentCreateResponse>>(Resource.Loading())

  init {
    if (!_isPostsFetched.value) {

      getPosts()
    }
  }

  fun getPosts() {
    viewModelScope.launch(Dispatchers.IO) {
      Pager(
        config =
        PagingConfig(
          pageSize = 100, // Larger page size to reduce the number of API calls
          enablePlaceholders = false,
          initialLoadSize = 30, // Load a larger initial page size
        )
      ) {
        postPagingSource
      }
        .flow
        .cachedIn(viewModelScope)
        .collect {
          _posts.value = it
          _isPostsFetched.value = true // Set flag to true after fetching
        }
    }
  }

  fun getUsersPostsById(id: String) {
    viewModelScope.launch(Dispatchers.IO) {
      Pager(
        config =
        PagingConfig(
          pageSize = 100, // Larger page size to reduce the number of API calls
          enablePlaceholders = false,
          initialLoadSize = 30, // Load a larger initial page size
        )
      ) {
        UserPostsPagingSource(postRepository, id)
      }
        .flow
        .cachedIn(viewModelScope)
        .collect { _userPosts.value = it }
    }
  }

  fun createPost(postRequest: PostRequest) {
    viewModelScope.launch(Dispatchers.IO) {
      _post.value = Resource.Loading() // Set loading state
      postRepository.createPost(postRequest).collect { resource ->
        _post.value = resource
        // Notify that a new post was created
        if (resource is Resource.Success) {
          isPostCreated = true
          refreshPosts()
        }
      }
    }
  }

  fun resetPostCreationState() {
    isPostCreated = false
  }

  fun likePost(postId: String) {
    viewModelScope.launch(Dispatchers.IO) {
      postRepository.likePost(postId).collect { resource ->
        _likePost.value = resource
        if (resource is Resource.Success) {
          _postLikes[postId] = true
        }
      }
    }
  }

  fun unLikePost(postId: String) {
    viewModelScope.launch(Dispatchers.IO) {
      postRepository.unLikePost(postId).collect { resource ->
        _likePost.value = resource
        if (resource is Resource.Success) {
          _postLikes[postId] = false
        }
      }
    }
  }

  fun refreshPosts() {
    viewModelScope.launch(Dispatchers.IO) {
      _isPostsFetched.value = false // Reset the flag when refreshing posts
      // Re-fetch posts to include the new post at the top
      getPosts()
    }
  }

  fun getComments(postId: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val fetchedComments = postRepository.getComments(postId, page = 1, pageSize = 60)
      _comments.value = fetchedComments
    }
  }

  fun addComment(postId: String, content: String) {
    viewModelScope.launch(Dispatchers.IO) {
      postRepository.addComment(postId, content).collect { _createComment.value = it }
    }
  }
}