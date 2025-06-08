package com.fsck.k9.backends

import com.fsck.k9.backend.BackendManager
import com.fsck.k9.backend.imap.BackendIdleRefreshManager
import com.fsck.k9.backend.imap.SystemAlarmManager
import com.fsck.k9.mail.oauth.OAuth2TokenProviderFactory
import com.fsck.k9.mail.store.imap.IdleRefreshManager
import net.thunderbird.backend.api.BackendFactory
import net.thunderbird.backend.api.folder.RemoteFolderCreator
import net.thunderbird.backend.imap.ImapRemoteFolderCreatorFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.fsck.k9.backend.BackendFactory as LegacyBackendFactory

val backendsModule = module {
    single {
        val developmentBackends = get<Map<String, LegacyBackendFactory>>(named("developmentBackends"))
        BackendManager(
            mapOf(
                "imap" to get<ImapBackendFactory>(),
                "pop3" to get<Pop3BackendFactory>(),
            ) + developmentBackends,
        )
    }
    single {
        ImapBackendFactory(
            accountManager = get(),
            powerManager = get(),
            idleRefreshManager = get(),
            backendStorageFactory = get(),
            trustedSocketFactory = get(),
            context = get(),
            clientInfoAppName = get(named("ClientInfoAppName")),
            clientInfoAppVersion = get(named("ClientInfoAppVersion")),
        )
    }

    single<BackendFactory<*>>(named("imap")) {
        get<ImapBackendFactory>()
    }

    single<RemoteFolderCreator.Factory>(named("imap")) {
        ImapRemoteFolderCreatorFactory(
            logger = get(),
            backendFactory = get(named("imap")),
        )
    }

    single<SystemAlarmManager> { AndroidAlarmManager(context = get(), alarmManager = get()) }
    single<IdleRefreshManager> { BackendIdleRefreshManager(alarmManager = get()) }
    single { Pop3BackendFactory(get(), get()) }
    single<OAuth2TokenProviderFactory> { RealOAuth2TokenProviderFactory(context = get()) }
}
