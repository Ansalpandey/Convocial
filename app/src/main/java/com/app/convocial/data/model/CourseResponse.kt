package com.app.convocial.data.model

data class CourseResponse(
  val courses: List<Course>?,
  val currentPage: Int?, // 1
  val message: String?, // Courses retrieved successfully
  val totalPages: Int? // 3
)