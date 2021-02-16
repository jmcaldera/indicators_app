package dev.josecaldera.indicators.landing

import androidx.lifecycle.ViewModel
import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.data.session.SessionStorage
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class LandingViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val uiEvents = BroadcastChannel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = uiEvents.asFlow()

    fun checkUserStatus() {
        when (sessionStorage.getUser()) {
            is Result.OnError -> sendEvent(Event.NavigateToLogin)
            is Result.OnSuccess -> sendEvent(Event.NavigateToIndicators)
        }
    }

    private fun sendEvent(event: Event) {
        uiEvents.offer(event)
    }

    sealed class Event {
        object NavigateToLogin : Event()
        object NavigateToIndicators : Event()
    }
}
