package com.chama.mfuko.ui.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.mfuko.data.local.TokenManager
import com.chama.mfuko.data.local.dao.NotificationDao
import com.chama.mfuko.data.local.entities.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val notificationDao: NotificationDao
) : ViewModel() {

    val notifications: StateFlow<List<NotificationEntity>> =
        tokenManager.getUserId()
            .flatMapLatest { userId ->
                if (userId == null) kotlinx.coroutines.flow.flowOf(emptyList())
                else notificationDao.getNotificationsForUser(userId)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onNotificationClick(notification: NotificationEntity) {
        if (notification.isRead) return
        viewModelScope.launch {
            notificationDao.markRead(notification.id)
        }
    }
}
