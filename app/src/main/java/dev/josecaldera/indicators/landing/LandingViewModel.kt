package dev.josecaldera.indicators.landing

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.data.session.SessionStorage
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val uiEvents = BroadcastChannel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = uiEvents.asFlow()

    fun checkUserStatus() {
        when (val result = sessionStorage.getUser()) {
            is Result.OnError -> sendEvent(Event.NavigateToLogin)
            is Result.OnSuccess -> sendEvent(Event.NavigateToIndicators(result.data.name))
        }
    }

    private fun sendEvent(event: Event) {
        uiEvents.offer(event)
    }

    sealed class Event {
        object NavigateToLogin : Event()
        class NavigateToIndicators(val name: String) : Event()
    }
}
