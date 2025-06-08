package com.fsck.k9.ui.messagelist

import app.k9mail.legacy.message.controller.MessageReference
import net.thunderbird.core.android.account.SortType
import net.thunderbird.feature.search.LocalSearch

data class MessageListConfig(
    val search: LocalSearch,
    val showingThreadedList: Boolean,
    val sortType: SortType,
    val sortAscending: Boolean,
    val sortDateAscending: Boolean,
    val activeMessage: MessageReference?,
    val sortOverrides: Map<MessageReference, MessageSortOverride>,
)

data class MessageSortOverride(
    val isRead: Boolean,
    val isStarred: Boolean,
)
