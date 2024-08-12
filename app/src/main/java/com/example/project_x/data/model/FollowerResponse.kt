package com.example.project_x.data.model

data class FollowerResponse(
  val currentPage: Int?, // 1
  val followers: List<Follower?>?,
  val message: String?, // User followers retrieved successfully
  val totalPages: Int?, // 1
)
