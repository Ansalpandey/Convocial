package com.app.convocial.data.implementation

import com.app.convocial.data.datasource.NotificationDataSource
import com.app.convocial.data.model.NotificationResponse
import com.app.convocial.data.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImplementation
@Inject
constructor(private val notificationDataSource: NotificationDataSource) : NotificationRepository {
  override suspend fun getNotifications(userId: String): List<NotificationResponse> {
    return notificationDataSource.getNotifications(userId)
  }
}
