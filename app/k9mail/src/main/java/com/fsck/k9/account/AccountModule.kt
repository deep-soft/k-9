package com.fsck.k9.account

import app.k9mail.feature.account.setup.AccountSetupExternalContract
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val newAccountModule = module {
    factory<AccountSetupExternalContract.AccountOwnerNameProvider> {
        AccountOwnerNameProvider(
            preferences = get(),
        )
    }

    factory<AccountSetupExternalContract.AccountCreator> {
        AccountCreator(
            accountCreatorHelper = get(),
            localFoldersCreator = get(),
            preferences = get(),
            context = androidApplication(),
        )
    }
}
