package com.chama.groupmoneymanager.ui.features.auth.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.remote.AuthRequest
import com.chama.groupmoneymanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val phone: String    = "",
    val password: String = "",
    val isLoading: Boolean  = false,
    val isDemoLoading: Boolean = false,
    val error: String?  = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EnteredPhone    -> _state.value = _state.value.copy(phone    = event.value, error = null)
            is LoginEvent.EnteredPassword -> _state.value = _state.value.copy(password = event.value, error = null)
            LoginEvent.Login              -> loginUser()
        }
    }

    // ── Normal login ──────────────────────────────────────────────────────────

    private fun loginUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            when (val result = authRepository.loginUser(
                AuthRequest(phone = state.value.phone, password = state.value.password)
            )) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    navigateAfterLogin()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    // ── Demo login ────────────────────────────────────────────────────────────

    fun loginDemo() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDemoLoading = true, error = null)

            when (val result = authRepository.loginDemo()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isDemoLoading = false)
                    // Demo always has a nest (seeded), so go straight to Home.
                    _eventFlow.send(UiEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isDemoLoading = false, error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    // ── Shared post-login navigation ──────────────────────────────────────────

    private suspend fun navigateAfterLogin() {
        val nestId = tokenManager.getCurrentNestId().first()
        if (nestId > 0L) {
            _eventFlow.send(UiEvent.NavigateToHome)
        } else {
            _eventFlow.send(UiEvent.NavigateToWelcome)
        }
    }

    // ── Events ────────────────────────────────────────────────────────────────

    sealed class UiEvent {
        object NavigateToHome    : UiEvent()
        object NavigateToWelcome : UiEvent()
    }
}
