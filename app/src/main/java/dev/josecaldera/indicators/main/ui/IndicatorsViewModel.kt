package dev.josecaldera.indicators.main.ui

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.josecaldera.indicators.args.IndicatorArg
import dev.josecaldera.indicators.args.toParcelable
import dev.josecaldera.indicators.core.Error
import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.main.domain.IndicatorsRepository
import dev.josecaldera.indicators.main.domain.model.Indicator
import dev.josecaldera.indicators.main.ui.adapter.RecyclerViewItem
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndicatorsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val indicatorsRepository: IndicatorsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val ARG_QUERY = "ARG_QUERY"
    }

    private val uiEvents = BroadcastChannel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = uiEvents.asFlow()

    private val inputs = BroadcastChannel<SearchEvent>(Channel.BUFFERED)

    private val _items = MutableLiveData<List<RecyclerViewItem>>(
        listOf(LogoutItem { onLogoutClicked() })
    )
    val items: LiveData<List<RecyclerViewItem>> = _items

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _loading

    val savedQuery = savedStateHandle.getLiveData<String>(ARG_QUERY, null)

    init {
        inputs
            .asFlow()
            .debounce(500)
            .onEach { event ->
                when (event) {
                    is SearchEvent.OnQueryTextChange -> {
                        fetIndicatorsForCode(event.query)
                    }
                    is SearchEvent.OnQueryTextSubmit -> {
                        fetIndicatorsForCode(event.query)

                        sendEvent(Event.HideSoftKeyboard)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onIndicatorClicked(indicator: Indicator) {
        sendEvent(Event.NavigateToDetails(indicator.toParcelable()))
    }

    fun onQueryChanged(query: String) {
        inputs.offer(SearchEvent.OnQueryTextChange(query))
    }

    fun onQuerySubmitted(query: String) {
        inputs.offer(SearchEvent.OnQueryTextSubmit(query))
    }

    private fun onLogoutClicked() {
        viewModelScope.launch {
            authRepository.logOut()
            sendEvent(Event.Logout)
        }
    }

    fun fetchIndicators() {

        // we already fetched indicators
        if (items.value!!.size > 1) return

        viewModelScope.launch {
            _loading.value = true
            val result = indicatorsRepository.getIndicators()

            _loading.value = false
            handleResult(result)
        }
    }

    private fun fetIndicatorsForCode(query: String) {
        savedQuery.value = query

        viewModelScope.launch {
            val result = indicatorsRepository.getIndicatorsForCode(query)
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
        object HideSoftKeyboard : Event()
    }

    sealed class SearchEvent {
        class OnQueryTextChange(val query: String) : SearchEvent()
        class OnQueryTextSubmit(val query: String) : SearchEvent()
    }
}

data class IndicatorItem(
    val indicator: Indicator,
    val onClick: (Indicator) -> Unit
) : RecyclerViewItem

data class LogoutItem(
    val onClick: () -> Unit
) : RecyclerViewItem
