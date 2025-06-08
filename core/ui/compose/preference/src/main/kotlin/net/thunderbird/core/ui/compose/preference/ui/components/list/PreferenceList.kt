package net.thunderbird.core.ui.compose.preference.ui.components.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import net.thunderbird.core.ui.compose.preference.api.Preference
import net.thunderbird.core.ui.compose.preference.api.PreferenceSetting

@Composable
internal fun PreferenceList(
    preferences: ImmutableList<Preference>,
    onItemClick: (index: Int, item: Preference) -> Unit,
    onPreferenceChange: (PreferenceSetting<*>) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(preferences) { index, item ->
            PreferenceItem(
                preference = item,
                onClick = {
                    onItemClick(index, item)
                },
                onPreferenceChange = onPreferenceChange,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
