package com.fsck.k9.notification

import app.k9mail.legacy.mailstore.MessageStoreManager
import app.k9mail.legacy.message.controller.MessageReference
import com.fsck.k9.mailstore.LocalStoreProvider
import net.thunderbird.core.android.account.LegacyAccount

internal class NotificationRepository(
    private val notificationStoreProvider: NotificationStoreProvider,
    private val localStoreProvider: LocalStoreProvider,
    private val messageStoreManager: MessageStoreManager,
    private val notificationContentCreator: NotificationContentCreator,
) {
    private val notificationDataStore = NotificationDataStore()

    @Synchronized
    fun restoreNotifications(account: LegacyAccount): NotificationData? {
        if (notificationDataStore.isAccountInitialized(account)) return null

        val localStore = localStoreProvider.getInstance(account)

        val (activeNotificationMessages, inactiveNotificationMessages) = localStore.notificationMessages.partition {
            it.notificationId != null
        }

        val activeNotifications = activeNotificationMessages.map { notificationMessage ->
            val content = notificationContentCreator.createFromMessage(account, notificationMessage.message)
            NotificationHolder(notificationMessage.notificationId!!, notificationMessage.timestamp, content)
        }

        val inactiveNotifications = inactiveNotificationMessages.map { notificationMessage ->
            val content = notificationContentCreator.createFromMessage(account, notificationMessage.message)
            InactiveNotificationHolder(notificationMessage.timestamp, content)
        }

        val notificationData = notificationDataStore.initializeAccount(
            account,
            activeNotifications,
            inactiveNotifications,
        )

        return if (notificationData.activeNotifications.isNotEmpty()) notificationData else null
    }

    @Synchronized
    fun addNotification(account: LegacyAccount, content: NotificationContent, timestamp: Long): AddNotificationResult? {
        restoreNotifications(account)

        return notificationDataStore.addNotification(account, content, timestamp)?.also { result ->
            persistNotificationDataStoreChanges(
                account = account,
                operations = result.notificationStoreOperations,
                updateNewMessageState = true,
            )
        }
    }

    @Synchronized
    fun removeNotifications(
        account: LegacyAccount,
        clearNewMessageState: Boolean = true,
        selector: (List<MessageReference>) -> List<MessageReference>,
    ): RemoveNotificationsResult? {
        restoreNotifications(account)

        return notificationDataStore.removeNotifications(account, selector)?.also { result ->
            persistNotificationDataStoreChanges(
                account = account,
                operations = result.notificationStoreOperations,
                updateNewMessageState = clearNewMessageState,
            )
        }
    }

    @Synchronized
    fun clearNotifications(account: LegacyAccount, clearNewMessageState: Boolean) {
        notificationDataStore.clearNotifications(account)
        clearNotificationStore(account)

        if (clearNewMessageState) {
            clearNewMessageState(account)
        }
    }

    private fun persistNotificationDataStoreChanges(
        account: LegacyAccount,
        operations: List<NotificationStoreOperation>,
        updateNewMessageState: Boolean,
    ) {
        val notificationStore = notificationStoreProvider.getNotificationStore(account)
        notificationStore.persistNotificationChanges(operations)

        if (updateNewMessageState) {
            setNewMessageState(account, operations)
        }
    }

    private fun setNewMessageState(account: LegacyAccount, operations: List<NotificationStoreOperation>) {
        val messageStore = messageStoreManager.getMessageStore(account)

        for (operation in operations) {
            when (operation) {
                is NotificationStoreOperation.Add -> {
                    val messageReference = operation.messageReference
                    messageStore.setNewMessageState(
                        folderId = messageReference.folderId,
                        messageServerId = messageReference.uid,
                        newMessage = true,
                    )
                }
                is NotificationStoreOperation.Remove -> {
                    val messageReference = operation.messageReference
                    messageStore.setNewMessageState(
                        folderId = messageReference.folderId,
                        messageServerId = messageReference.uid,
                        newMessage = false,
                    )
                }
                else -> Unit
            }
        }
    }

    private fun clearNewMessageState(account: LegacyAccount) {
        val messageStore = messageStoreManager.getMessageStore(account)
        messageStore.clearNewMessageState()
    }

    private fun clearNotificationStore(account: LegacyAccount) {
        val notificationStore = notificationStoreProvider.getNotificationStore(account)
        notificationStore.clearNotifications()
    }
}
