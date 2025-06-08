package net.thunderbird.core.android.account

import kotlinx.coroutines.flow.Flow

interface LegacyAccountWrapperManager {
    fun getAll(): Flow<List<LegacyAccountWrapper>>

    fun getById(id: String): Flow<LegacyAccountWrapper?>

    suspend fun update(account: LegacyAccountWrapper)
}
