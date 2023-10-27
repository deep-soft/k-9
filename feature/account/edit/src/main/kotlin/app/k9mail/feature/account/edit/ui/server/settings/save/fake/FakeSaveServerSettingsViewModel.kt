package app.k9mail.feature.account.edit.ui.server.settings.save.fake

import app.k9mail.core.ui.compose.common.mvi.BaseViewModel
import app.k9mail.feature.account.edit.ui.server.settings.save.SaveServerSettingsContract.Effect
import app.k9mail.feature.account.edit.ui.server.settings.save.SaveServerSettingsContract.Event
import app.k9mail.feature.account.edit.ui.server.settings.save.SaveServerSettingsContract.State
import app.k9mail.feature.account.edit.ui.server.settings.save.SaveServerSettingsContract.ViewModel

class FakeSaveServerSettingsViewModel(
    override val isIncoming: Boolean,
    initialState: State = State(),
) : BaseViewModel<State, Event, Effect>(initialState), ViewModel {

    val events = mutableListOf<Event>()

    override fun event(event: Event) {
        events.add(event)
    }

    fun effect(effect: Effect) {
        emitEffect(effect)
    }
}
