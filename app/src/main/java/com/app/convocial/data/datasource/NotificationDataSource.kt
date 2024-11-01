package com.app.convocial.data.datasource

import com.app.convocial.data.api.AuthenticatedApiService
import com.app.convocial.data.model.NotificationResponse
import javax.inject.Inject

class NotificationDataSource @Inject constructor(private val apiService: AuthenticatedApiService) {
    suspend fun getNotifications(userId: String) : List<NotificationResponse> = apiService.getNotifications(userId)
}
