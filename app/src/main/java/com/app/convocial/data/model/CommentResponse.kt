package com.app.convocial.data.model

data class CommentResponse(
  val comments: List<Comment>?,
  val currentPage: Int?, // 1
  val message: String?, // Comments retrieved successfully
  val totalComments: Int?, // 1
  val totalPages: Int? // 1
)