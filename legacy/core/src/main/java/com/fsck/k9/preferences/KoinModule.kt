package com.fsck.k9.preferences

import com.fsck.k9.Preferences
import kotlin.time.ExperimentalTime
import net.thunderbird.core.preference.DefaultPreferenceChangeBroker
import net.thunderbird.core.preference.GeneralSettingsManager
import net.thunderbird.core.preference.PreferenceChangeBroker
import net.thunderbird.core.preference.PreferenceChangePublisher
import net.thunderbird.core.preference.display.DefaultDisplaySettingsPreferenceManager
import net.thunderbird.core.preference.display.DisplaySettingsPreferenceManager
import net.thunderbird.core.preference.network.DefaultNetworkSettingsPreferenceManager
import net.thunderbird.core.preference.network.NetworkSettingsPreferenceManager
import net.thunderbird.core.preference.notification.DefaultNotificationPreferenceManager
import net.thunderbird.core.preference.notification.NotificationPreferenceManager
import net.thunderbird.core.preference.privacy.DefaultPrivacySettingsPreferenceManager
import net.thunderbird.core.preference.privacy.PrivacySettingsPreferenceManager
import net.thunderbird.feature.mail.account.api.AccountManager
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import net.thunderbird.core.android.account.AccountManager as LegacyAccountManager

val preferencesModule = module {
    factory {
        SettingsExporter(
            contentResolver = get(),
            preferences = get(),
            folderSettingsProvider = get(),
            folderRepository = get(),
            notificationSettingsUpdater = get(),
            filePrefixProvider = get(),
        )
    }
    factory { FolderSettingsProvider(folderRepository = get()) }
    factory<LegacyAccountManager> { get<Preferences>() }
    factory<AccountManager<*>> { get<LegacyAccountManager>() }
    single<PrivacySettingsPreferenceManager> {
        DefaultPrivacySettingsPreferenceManager(
            logger = get(),
            storage = get<Preferences>().storage,
            storageEditor = get<Preferences>().createStorageEditor(),
        )
    }
    single<NotificationPreferenceManager> {
        DefaultNotificationPreferenceManager(
            logger = get(),
            storage = get<Preferences>().storage,
            storageEditor = get<Preferences>().createStorageEditor(),
        )
    }
    single<DisplaySettingsPreferenceManager> {
        DefaultDisplaySettingsPreferenceManager(
            logger = get(),
            storage = get<Preferences>().storage,
            storageEditor = get<Preferences>().createStorageEditor(),
        )
    }
    single<NetworkSettingsPreferenceManager> {
        DefaultNetworkSettingsPreferenceManager(
            logger = get(),
            storage = get<Preferences>().storage,
            storageEditor = get<Preferences>().createStorageEditor(),
        )
    }
    single {
        DefaultGeneralSettingsManager(
            preferences = get(),
            coroutineScope = get(named("AppCoroutineScope")),
            changePublisher = get(),
            privacySettingsPreferenceManager = get(),
            notificationPreferenceManager = get(),
            displaySettingsSettingsPreferenceManager = get(),
            networkSettingsPreferenceManager = get(),
        )
    } bind GeneralSettingsManager::class
    single {
        DefaultDrawerConfigManager(
            preferences = get(),
            coroutineScope = get(named("AppCoroutineScope")),
            displaySettingsPreferenceManager = get(),
        )
    } bind DrawerConfigManager::class

    factory { SettingsFileParser() }

    factory { GeneralSettingsValidator() }
    factory { GeneralSettingsUpgrader() }
    factory { GeneralSettingsWriter(preferences = get(), generalSettingsManager = get()) }

    factory { AccountSettingsValidator() }

    factory { IdentitySettingsUpgrader() }
    factory { FolderSettingsUpgrader() }
    factory { ServerSettingsUpgrader() }

    factory {
        AccountSettingsUpgrader(
            identitySettingsUpgrader = get(),
            folderSettingsUpgrader = get(),
            serverSettingsUpgrader = get(),
        )
    }

    factory {
        @OptIn(ExperimentalTime::class)
        AccountSettingsWriter(
            preferences = get(),
            localFoldersCreator = get(),
            clock = get(),
            serverSettingsDtoSerializer = get(),
            context = get(),
        )
    }

    factory { UnifiedInboxConfigurator(accountManager = get(), generalSettingsManager = get()) }

    factory {
        SettingsImporter(
            settingsFileParser = get(),
            generalSettingsValidator = get(),
            accountSettingsValidator = get(),
            generalSettingsUpgrader = get(),
            accountSettingsUpgrader = get(),
            generalSettingsWriter = get(),
            accountSettingsWriter = get(),
            unifiedInboxConfigurator = get(),
        )
    }

    single { DefaultPreferenceChangeBroker() }
        .binds(
            arrayOf(
                PreferenceChangePublisher::class,
                PreferenceChangeBroker::class,
            ),
        )
}
