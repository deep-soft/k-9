package app.k9mail.feature.account.server.config.ui.outgoing.fake

import app.k9mail.core.ui.compose.common.mvi.BaseViewModel
import app.k9mail.feature.account.server.config.ui.outgoing.AccountOutgoingConfigContract.Effect
import app.k9mail.feature.account.server.config.ui.outgoing.AccountOutgoingConfigContract.Event
import app.k9mail.feature.account.server.config.ui.outgoing.AccountOutgoingConfigContract.State
import app.k9mail.feature.account.server.config.ui.outgoing.AccountOutgoingConfigContract.ViewModel

class FakeAccountOutgoingConfigViewModel(
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
