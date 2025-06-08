package com.fsck.k9.mailstore

import com.fsck.k9.Preferences
import com.fsck.k9.mail.FolderType
import net.thunderbird.core.android.account.LegacyAccount
import net.thunderbird.core.common.mail.Protocols
import net.thunderbird.core.logging.legacy.Log
import net.thunderbird.feature.mail.folder.api.SpecialFolderSelection

class SpecialLocalFoldersCreator(
    private val preferences: Preferences,
    private val localStoreProvider: LocalStoreProvider,
) {
    // TODO: When rewriting the account setup code make sure this method is only called once. Until then this can be
    //  called multiple times and we have to make sure folders are only created once.
    fun createSpecialLocalFolders(account: LegacyAccount) {
        Log.d("Creating special local folders")

        val localStore = localStoreProvider.getInstance(account)

        if (account.outboxFolderId == null) {
            account.outboxFolderId = localStore.createLocalFolder(OUTBOX_FOLDER_NAME, FolderType.OUTBOX)
        } else {
            Log.d("Outbox folder was already set up")
        }

        if (account.isPop3()) {
            if (account.draftsFolderId == null) {
                val draftsFolderId = localStore.createLocalFolder(DRAFTS_FOLDER_NAME, FolderType.DRAFTS)
                account.setDraftsFolderId(draftsFolderId, SpecialFolderSelection.MANUAL)
            } else {
                Log.d("Drafts folder was already set up")
            }

            if (account.sentFolderId == null) {
                val sentFolderId = localStore.createLocalFolder(SENT_FOLDER_NAME, FolderType.SENT)
                account.setSentFolderId(sentFolderId, SpecialFolderSelection.MANUAL)
            } else {
                Log.d("Sent folder was already set up")
            }

            if (account.trashFolderId == null) {
                val trashFolderId = localStore.createLocalFolder(TRASH_FOLDER_NAME, FolderType.TRASH)
                account.setTrashFolderId(trashFolderId, SpecialFolderSelection.MANUAL)
            } else {
                Log.d("Trash folder was already set up")
            }
        }

        preferences.saveAccount(account)
    }

    fun createOutbox(account: LegacyAccount): Long {
        Log.d("Creating Outbox folder")

        val localStore = localStoreProvider.getInstance(account)
        val outboxFolderId = localStore.createLocalFolder(OUTBOX_FOLDER_NAME, FolderType.OUTBOX)

        account.outboxFolderId = outboxFolderId
        preferences.saveAccount(account)

        return outboxFolderId
    }

    private fun LegacyAccount.isPop3() = incomingServerSettings.type == Protocols.POP3

    companion object {
        private const val OUTBOX_FOLDER_NAME = LegacyAccount.OUTBOX_NAME
        private const val DRAFTS_FOLDER_NAME = "Drafts"
        private const val SENT_FOLDER_NAME = "Sent"
        private const val TRASH_FOLDER_NAME = "Trash"
    }
}
