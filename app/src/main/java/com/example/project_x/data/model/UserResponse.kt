package com.example.project_x.data.model

data class UserResponse(
  val message: String,
  val token: String? = null,
  val user: User
)
