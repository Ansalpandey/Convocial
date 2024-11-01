package com.app.convocial.data.model

data class PostResponse(
  val currentPage: Int?, // 1
  val message: String?, // Posts retrieved successfully
  val posts: List<Post>?,
  val totalPages: Int? // 1
)