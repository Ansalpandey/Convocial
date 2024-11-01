package com.app.convocial.data.repository

import com.app.convocial.data.model.NotificationResponse

interface NotificationRepository {
    suspend fun getNotifications(userId: String): List<NotificationResponse>
}
