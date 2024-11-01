package com.app.convocial.data.repository

import com.app.convocial.common.Resource
import com.app.convocial.data.model.CourseResponse
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
  suspend fun getCourses(page: Int, pageSize: Int): Flow<Resource<CourseResponse>>

  suspend fun getUserCourses(): Flow<Resource<CourseResponse>>
}
