package com.app.convocial.data.model

data class ProfileResponse(
  val message: String?, // User profile retrieved successfully!
  val user: UserX?,
  val postCount: Int?,
)
