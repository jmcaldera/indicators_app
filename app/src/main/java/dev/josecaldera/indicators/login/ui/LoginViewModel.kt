package dev.josecaldera.indicators.login.ui

import androidx.lifecycle.*
import dev.josecaldera.indicators.core.Result
import dev.josecaldera.indicators.login.domain.AuthRepository
import dev.josecaldera.indicators.login.domain.model.User
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val uiEvents = BroadcastChannel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = uiEvents.asFlow()

    val email: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")
    val emailError: MutableLiveData<String> = MutableLiveData("")

    private val emailValid = email.map {
        val emailMatches = it.matches(Regex(PATTERN_EMAIL))
        if (!emailMatches && it.isNotBlank()) {
            emailError.value =
                "Your Email Address must be entered in this format: jane@mail.com. Please try again."
        } else {
            emailError.value = ""
        }
        emailMatches
    }

    private val passwordValid = password.map {
        it.isNotBlank()
    }

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _loading

    val isLogInButtonEnabled: LiveData<Boolean> = combineLatest(
        emailValid,
        passwordValid,
        isLoading
    )

    fun onPasswordFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) sendEvent(Event.HideSoftKeyboard)
    }

    fun onLoginClicked() {
        // view can only call this method if email and password are valid
        viewModelScope.launch {
            _loading.value = true

            val result = authRepository.logIn(
                request = AuthRepository.AuthRequest(
                    email = email.value!!,
                    password = password.value!!
                )
            )

            _loading.value = false
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<User>) {
        when (result) {
            is Result.OnSuccess -> sendEvent(Event.LoginSuccess(result.data))
            is Result.OnError -> sendEvent(Event.LoginError)
        }
    }

    private fun sendEvent(event: Event) {
        uiEvents.offer(event)
    }

    private fun combineLatest(
        validEmail: LiveData<Boolean>,
        validPassword: LiveData<Boolean>,
        loading: LiveData<Boolean>
    ): LiveData<Boolean> {

        fun combine(
            validEmail: LiveData<Boolean>,
            validPassword: LiveData<Boolean>,
            loading: LiveData<Boolean>
        ): Boolean {
            val isValidEmail = validEmail.value
            val isValidPassword = validPassword.value
            val isLoading = loading.value

            return isValidEmail == true && isValidPassword == true && isLoading == false
        }

        val finalLiveData: MediatorLiveData<Boolean> = MediatorLiveData()

        finalLiveData.addSource(validEmail) {
            finalLiveData.value = combine(validEmail, validPassword, loading)
        }

        finalLiveData.addSource(validPassword) {
            finalLiveData.value = combine(validEmail, validPassword, loading)
        }

        finalLiveData.addSource(loading) {
            finalLiveData.value = combine(validEmail, validPassword, loading)
        }

        return finalLiveData
    }

    sealed class Event {
        object HideSoftKeyboard : Event()

        object LoginError : Event()

        data class LoginSuccess(val user: User) : Event()
    }

    companion object {

        // Simple pattern for demonstration purposes
        private const val PATTERN_EMAIL = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{2,64}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,254}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" +
                ")+"

    }
}
