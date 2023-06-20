package app.k9mail.autodiscovery.providersxml

import androidx.test.core.app.ApplicationProvider
import app.k9mail.core.android.testing.RobolectricTest
import app.k9mail.core.common.oauth.OAuthConfiguration
import app.k9mail.core.common.oauth.OAuthConfigurationProvider
import app.k9mail.core.common.oauth.OAuthProvider
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.fsck.k9.mail.AuthType
import com.fsck.k9.mail.ConnectionSecurity
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub

class ProvidersXmlDiscoveryTest : RobolectricTest() {
    private val xmlProvider = ProvidersXmlProvider(ApplicationProvider.getApplicationContext())
    private val oAuthConfigurationProvider = mock<OAuthConfigurationProvider>()
    private val providersXmlDiscovery = ProvidersXmlDiscovery(xmlProvider, oAuthConfigurationProvider)

    @Test
    fun discover_withGmailDomain_shouldReturnCorrectSettings() {
        oAuthConfigurationProvider.stub {
            on { getConfiguration("imap.gmail.com") } doReturn createOAuthConfiguration()
            on { getConfiguration("smtp.gmail.com") } doReturn createOAuthConfiguration()
        }

        val connectionSettings = providersXmlDiscovery.discover("user@gmail.com")

        assertThat(connectionSettings).isNotNull()
        with(connectionSettings!!.incoming.first()) {
            assertThat(host).isEqualTo("imap.gmail.com")
            assertThat(security).isEqualTo(ConnectionSecurity.SSL_TLS_REQUIRED)
            assertThat(authType).isEqualTo(AuthType.XOAUTH2)
            assertThat(username).isEqualTo("user@gmail.com")
        }
        with(connectionSettings.outgoing.first()) {
            assertThat(host).isEqualTo("smtp.gmail.com")
            assertThat(security).isEqualTo(ConnectionSecurity.SSL_TLS_REQUIRED)
            assertThat(authType).isEqualTo(AuthType.XOAUTH2)
            assertThat(username).isEqualTo("user@gmail.com")
        }
    }

    @Test
    fun discover_withUnknownDomain_shouldReturnNull() {
        val connectionSettings = providersXmlDiscovery.discover(
            "user@not.present.in.providers.xml.example",
        )

        assertThat(connectionSettings).isNull()
    }

    private fun createOAuthConfiguration(): OAuthConfiguration {
        return OAuthConfiguration(
            provider = OAuthProvider.GMAIL,
            clientId = "irrelevant",
            scopes = listOf("irrelevant"),
            authorizationEndpoint = "irrelevant",
            tokenEndpoint = "irrelevant",
            redirectUri = "irrelevant",
        )
    }
}
