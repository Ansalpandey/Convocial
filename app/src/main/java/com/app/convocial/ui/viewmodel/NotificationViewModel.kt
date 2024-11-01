package com.app.convocial.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.app.convocial.data.model.NotificationResponse
import com.app.convocial.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class NotificationViewModel
@Inject
constructor(private val notificationRepository: NotificationRepository) : ViewModel() {
  private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
  val notifications: StateFlow<List<NotificationResponse>> = _notifications.asStateFlow()

  suspend fun getNotifications(userId: String) {
    val notifications = notificationRepository.getNotifications(userId)
    _notifications.value = notifications
  }
}
