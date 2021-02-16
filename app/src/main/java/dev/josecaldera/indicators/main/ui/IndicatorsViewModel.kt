package dev.josecaldera.indicators.main.ui

import androidx.lifecycle.*
import dev.josecaldera.indicators.args.IndicatorArg
import dev.josecaldera.indicators.args.toParcelable
import dev.josecaldera.indicators.core.Error
import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.main.domain.IndicatorsRepository
import dev.josecaldera.indicators.main.domain.model.Indicator
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class IndicatorsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val indicatorsRepository: IndicatorsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val uiEvents = BroadcastChannel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = uiEvents.asFlow()

    private val _items = MutableLiveData<List<Indicator>>(emptyList())
    val items: LiveData<List<Indicator>> = _items

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _loading

    fun onIndicatorClicked(indicator: Indicator) {
        sendEvent(Event.NavigateToDetails(indicator.toParcelable()))
    }

    fun onLogoutClicked() {
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
