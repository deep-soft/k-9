package com.fsck.k9.job

import android.content.ContentResolver
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fsck.k9.K9
import com.fsck.k9.Preferences
import com.fsck.k9.controller.MessagingController
import com.fsck.k9.mail.AuthType
import net.thunderbird.core.android.account.LegacyAccount
import net.thunderbird.core.logging.legacy.Log

// IMPORTANT: Update K9WorkerFactory when moving this class and the FQCN no longer starts with "com.fsck.k9".
class MailSyncWorker(
    private val messagingController: MessagingController,
    private val preferences: Preferences,
    context: Context,
    parameters: WorkerParameters,
) : Worker(context, parameters) {

    override fun doWork(): Result {
        val accountUuid = inputData.getString(EXTRA_ACCOUNT_UUID)
        requireNotNull(accountUuid)

        Log.d("Executing periodic mail sync for account %s", accountUuid)

        if (isBackgroundSyncDisabled()) {
            Log.d("Background sync is disabled. Skipping mail sync.")
            return Result.success()
        }

        val account = preferences.getAccount(accountUuid)
        if (account == null) {
            Log.e("Account %s not found. Can't perform mail sync.", accountUuid)
            return Result.failure()
        }

        if (account.isPeriodicMailSyncDisabled) {
            Log.d("Periodic mail sync has been disabled for this account. Skipping mail sync.")
            return Result.success()
        }

        if (account.incomingServerSettings.isMissingCredentials) {
            Log.d("Password for this account is missing. Skipping mail sync.")
            return Result.success()
        }

        if (account.incomingServerSettings.authenticationType == AuthType.XOAUTH2 && account.oAuthState == null) {
            Log.d("Account requires sign-in. Skipping mail sync.")
            return Result.success()
        }

        val success = messagingController.performPeriodicMailSync(account)

        return if (success) Result.success() else Result.retry()
    }

    private fun isBackgroundSyncDisabled(): Boolean {
        return when (K9.backgroundOps) {
            K9.BACKGROUND_OPS.NEVER -> true
            K9.BACKGROUND_OPS.ALWAYS -> false
            K9.BACKGROUND_OPS.WHEN_CHECKED_AUTO_SYNC -> !ContentResolver.getMasterSyncAutomatically()
        }
    }

    private val LegacyAccount.isPeriodicMailSyncDisabled
        get() = automaticCheckIntervalMinutes <= 0

    companion object {
        const val EXTRA_ACCOUNT_UUID = "accountUuid"
    }
}
