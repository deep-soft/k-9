package app.k9mail.feature.account.server.certificate

import app.k9mail.feature.account.server.certificate.ui.ServerCertificateErrorContract
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class ServerCertificateModuleKtTest : KoinTest {

    @Test
    fun `should have a valid di module`() {
        featureAccountServerCertificateModule.verify(
            extraTypes = listOf(
                ServerCertificateErrorContract.State::class,
            ),
        )
    }
}
