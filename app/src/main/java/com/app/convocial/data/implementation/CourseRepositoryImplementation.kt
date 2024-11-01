package com.app.convocial.data.implementation

import com.app.convocial.common.Resource
import com.app.convocial.data.datasource.CourseDataSource
import com.app.convocial.data.model.CourseResponse
import com.app.convocial.data.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CourseRepositoryImplementation
@Inject
constructor(private val courseDataSource: CourseDataSource) : CourseRepository {
  // Implementation of CourseRepository
  override suspend fun getCourses(page: Int, pageSize: Int): Flow<Resource<CourseResponse>> {
    return courseDataSource.getCourses(page, pageSize)
  }

  override suspend fun getUserCourses(): Flow<Resource<CourseResponse>> {
    return courseDataSource.getUserCourses()
  }
}
