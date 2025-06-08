package net.thunderbird.feature.account.settings.impl.ui.general

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.k9mail.core.ui.compose.designsystem.PreviewWithTheme
import net.thunderbird.feature.account.settings.impl.ui.fake.FakePreferenceData

@Composable
@Preview(showBackground = true)
internal fun GeneralSettingsContentPreview() {
    PreviewWithTheme {
        GeneralSettingsContent(
            state = GeneralSettingsContract.State(
                subtitle = "Subtitle",
                preferences = FakePreferenceData.preferences,
            ),
            onEvent = {},
        )
    }
}
