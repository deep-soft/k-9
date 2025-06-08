package net.thunderbird.feature.mail.folder.api

import net.thunderbird.feature.mail.account.api.BaseAccount

// TODO move to ???
fun interface SpecialFolderUpdater {
    /**
     * Updates all account's special folders. If POP3, only Inbox is updated.
     */
    fun updateSpecialFolders()

    interface Factory<TAccount : BaseAccount> {
        fun create(account: TAccount): SpecialFolderUpdater
    }
}
