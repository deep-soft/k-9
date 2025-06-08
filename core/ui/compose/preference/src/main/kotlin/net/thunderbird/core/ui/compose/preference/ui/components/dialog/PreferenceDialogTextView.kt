package net.thunderbird.core.ui.compose.preference.ui.components.dialog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import app.k9mail.core.ui.compose.designsystem.atom.text.TextBodyMedium
import app.k9mail.core.ui.compose.designsystem.molecule.input.AdvancedTextInput
import app.k9mail.core.ui.compose.theme2.MainTheme
import kotlinx.coroutines.delay
import net.thunderbird.core.ui.compose.preference.api.PreferenceSetting

// This a workaround for a bug in Compose, preventing the keyboard been show when requesting focus on a dialog,
// see: https://issuetracker.google.com/issues/204502668
private const val EDIT_TEXT_FOCUS_DELAY = 200L

@Composable
internal fun PreferenceDialogTextView(
    preference: PreferenceSetting.Text,
    onConfirmClick: (PreferenceSetting<*>) -> Unit,
    onDismissClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = preference.value,
                selection = TextRange(preference.value.length),
            ),
        )
    }

    LaunchedEffect(Unit) {
        delay(EDIT_TEXT_FOCUS_DELAY)
        focusRequester.requestFocus()
    }

    PreferenceDialogLayout(
        title = preference.title(),
        icon = preference.icon(),
        onConfirmClick = {
            onConfirmClick(preference.copy(value = textFieldValue.text))
        },
        onDismissClick = onDismissClick,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        preference.description()?.let {
            TextBodyMedium(text = it)

            Spacer(modifier = Modifier.height(MainTheme.spacings.default))
        }

        AdvancedTextInput(
            text = textFieldValue,
            contentPadding = PaddingValues(),
            onTextChange = { changedText ->
                textFieldValue = changedText
            },
            modifier = Modifier.focusRequester(focusRequester),
        )
    }
}
