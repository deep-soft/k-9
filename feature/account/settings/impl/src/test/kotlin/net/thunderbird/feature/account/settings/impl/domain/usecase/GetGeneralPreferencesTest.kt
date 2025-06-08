package net.thunderbird.feature.account.settings.impl.domain.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.core.ui.compose.preference.api.PreferenceDisplay
import net.thunderbird.core.ui.compose.preference.api.PreferenceSetting
import net.thunderbird.feature.account.api.AccountId
import net.thunderbird.feature.account.api.profile.AccountProfile
import net.thunderbird.feature.account.settings.impl.domain.AccountSettingsDomainContract.ResourceProvider
import net.thunderbird.feature.account.settings.impl.domain.AccountSettingsDomainContract.SettingsError
import net.thunderbird.feature.account.settings.impl.domain.AccountSettingsDomainContract.UseCase

internal class GetGeneralPreferencesTest {

    @Test
    fun `should emit preferences when account profile present`() = runTest {
        // Arrange
        val accountId = AccountId.create()
        val accountProfile = AccountProfile(
            accountId = accountId,
            name = "Test Account",
            color = 0xFF0000,
        )
        val resourceProvider = FakeGeneralResourceProvider()
        val testSubject = createTestSubject(accountProfile)

        // Act & Assert
        testSubject(accountId).test {
            val outcome = awaitItem()
            assertThat(outcome).isInstanceOf(Outcome.Success::class)

            val success = outcome as Outcome.Success
            assertThat(success.data).isEqualTo(
                persistentListOf(
                    PreferenceDisplay.Custom(
                        id = "${accountId.value}-general-profile",
                        customUi = resourceProvider.profileUi(
                            name = accountProfile.name,
                            color = accountProfile.color,
                        ),
                    ),
                    PreferenceSetting.Text(
                        id = "${accountId.value}-general-name",
                        title = resourceProvider.nameTitle,
                        description = resourceProvider.nameDescription,
                        icon = resourceProvider.nameIcon,
                        value = accountProfile.name,
                    ),
                    PreferenceSetting.Color(
                        id = "${accountId.value}-general-color",
                        title = resourceProvider.colorTitle,
                        description = resourceProvider.colorDescription,
                        icon = resourceProvider.colorIcon,
                        value = accountProfile.color,
                        colors = resourceProvider.colors,
                    ),
                ),
            )
        }
    }

    @Test
    fun `should emit NotFound when account profile not found`() = runTest {
        // Arrange
        val accountId = AccountId.create()
        val testSubject = createTestSubject()

        // Act & Assert
        testSubject(accountId).test {
            assertThat(awaitItem()).isEqualTo(
                Outcome.failure(
                    SettingsError.NotFound(
                        message = "Account profile not found for accountId: ${accountId.value}",
                    ),
                ),
            )
        }
    }

    private fun createTestSubject(
        accountProfile: AccountProfile? = null,
        resourceProvider: ResourceProvider.GeneralResourceProvider = FakeGeneralResourceProvider(),
    ): UseCase.GetGeneralPreferences {
        return GetGeneralPreferences(
            repository = FakeAccountProfileRepository(accountProfile),
            resourceProvider = resourceProvider,
        )
    }
}
