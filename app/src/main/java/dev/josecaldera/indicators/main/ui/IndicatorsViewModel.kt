package dev.josecaldera.indicators.main.ui

import androidx.lifecycle.*
import dev.josecaldera.indicators.args.IndicatorArg
import dev.josecaldera.indicators.args.toParcelable
import dev.josecaldera.indicators.core.Error
import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.data.session.SessionStorage
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.main.domain.IndicatorsRepository
import dev.josecaldera.indicators.main.domain.model.Indicator
import dev.josecaldera.indicators.main.ui.adapter.RecyclerViewItem
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class IndicatorsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val indicatorsRepository: IndicatorsRepository,
    private val authRepository: AuthRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val uiEvents = BroadcastChannel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = uiEvents.asFlow()

    private val _items = MutableLiveData<List<RecyclerViewItem>>(emptyList())
    val items: LiveData<List<RecyclerViewItem>> = _items

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _loading

    val userName = sessionStorage.getUser().getOrNull()?.name
        ?: throw IllegalStateException("Indicators can't be shown to a logged out user")

    private fun onIndicatorClicked(indicator: Indicator) {
        sendEvent(Event.NavigateToDetails(indicator.toParcelable()))
    }

    private fun onLogoutClicked() {
        viewModelScope.launch {
            authRepository.logOut()
            sendEvent(Event.Logout)
        }
    }

    fun fetchIndicators() {
        viewModelScope.launch {
            _loading.value = true
            val result = indicatorsRepository.getIndicators()

            _loading.value = false
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<List<Indicator>>) {
        when (result) {
            is Result.OnError -> sendEvent(
                Event.Failure(result.error) { fetchIndicators() }
            )
            is Result.OnSuccess -> {
                _items.value = result.data
                    .map {
                        IndicatorItem(it) { item -> onIndicatorClicked(item) } as RecyclerViewItem
                    }
                    .toMutableList()
                    .also { it.add(LogoutItem { onLogoutClicked() } as RecyclerViewItem) }
                    .toList()
            }
        }
    }

    private fun sendEvent(event: Event) {
        uiEvents.offer(event)
    }

    sealed class Event {
        data class NavigateToDetails(val indicator: IndicatorArg) : Event()
        data class Failure(val error: Error, val onRetry: () -> Unit) : Event()
        object Logout : Event()
    }
}

data class IndicatorItem(
    val indicator: Indicator,
    val onClick: (Indicator) -> Unit
) : RecyclerViewItem

data class LogoutItem(
    val onClick: () -> Unit
) : RecyclerViewItem
