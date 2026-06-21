package com.chama.mfuko.ui.features.auth.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.remote.RegisterRequest
import com.chama.mfuko.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val name: String = "",
    val phone: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(RegisterState())
    val state: State<RegisterState> = _state

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EnteredName -> {
                _state.value = _state.value.copy(name = event.value)
            }
            is RegisterEvent.EnteredPhone -> {
                _state.value = _state.value.copy(phone = event.value)
            }
            is RegisterEvent.EnteredPassword -> {
                _state.value = _state.value.copy(password = event.value)
            }
            RegisterEvent.Register -> {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val request = RegisterRequest(
                name = state.value.name,
                phone = state.value.phone,
                password = state.value.password
            )

            when (val result = repository.registerUser(request)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _eventFlow.send(UiEvent.RegisterSuccess)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    sealed class UiEvent {
        object RegisterSuccess : UiEvent()
    }
}