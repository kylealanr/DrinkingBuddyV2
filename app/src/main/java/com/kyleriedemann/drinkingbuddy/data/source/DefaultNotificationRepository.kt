package com.kyleriedemann.drinkingbuddy.data.source

import androidx.lifecycle.LiveData
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import javax.inject.Inject

class DefaultNotificationRepository @Inject constructor(private val notificationDataSource: NotificationDataSource): NotificationRepository {
    override suspend fun getNotifications() = notificationDataSource.getNotifications()

    override fun getLiveNotifications(): LiveData<List<Notification>> = notificationDataSource.getNotificationsLive()

    override suspend fun getNotificationById(notificationId: String) = notificationDataSource.getNotificationById(notificationId)

    override suspend fun insertNotification(notification: Notification) = notificationDataSource.insertNotification(notification)

    override suspend fun updateNotification(notification: Notification) = notificationDataSource.updateNotification(notification)

    override suspend fun markRead(notificationId: String, read: Boolean) = notificationDataSource.markRead(notificationId, read)

    override suspend fun deleteNotification(notification: Notification) = notificationDataSource.deleteNotification(notification)

    override suspend fun deleteNotificationById(notificationId: String) = notificationDataSource.deleteNotificationById(notificationId)

    override suspend fun clearNotifications() = notificationDataSource.clearNotifications()
}