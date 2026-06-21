package com.chama.mfuko.ui.features.nests.create

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.repository.NestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WelcomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val nestName: String = "",
    val contributionAmount: String = "",
    val inviteCode: String = ""
)

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val nestRepository: NestRepository
) : ViewModel() {

    private val _state = mutableStateOf(WelcomeState())
    val state: State<WelcomeState> = _state

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    fun onNestNameChange(name: String) {
        _state.value = _state.value.copy(nestName = name)
    }

    fun onContributionAmountChange(amount: String) {
        _state.value = _state.value.copy(contributionAmount = amount)
    }

    fun onInviteCodeChange(code: String) {
        _state.value = _state.value.copy(inviteCode = code)
    }

    fun onCreateNestClick() {
        viewModelScope.launch {
            val name   = _state.value.nestName
            val amount = _state.value.contributionAmount.toDoubleOrNull()

            if (name.isBlank() || amount == null) {
                _eventFlow.send(UiEvent.ShowToast("Please enter a valid name and amount."))
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = nestRepository.createNest(name, amount)) {
                is Resource.Success -> {
                    // Navigate to the success screen so the user can copy their invite code.
                    val nestName   = result.data?.nestName   ?: name
                    val inviteCode = result.data?.inviteCode ?: ""
                    _eventFlow.send(UiEvent.NavigateToNestCreated(nestName, inviteCode))
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                is Resource.Loading -> {}
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun onJoinNestClick() {
        viewModelScope.launch {
            val code = _state.value.inviteCode
            if (code.isBlank()) {
                _eventFlow.send(UiEvent.ShowToast("Please enter an invite code."))
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = nestRepository.joinNest(code)) {
                is Resource.Success -> {
                    _eventFlow.send(UiEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                is Resource.Loading -> {}
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        /** User joined an existing nest — go straight to the dashboard. */
        object NavigateToHome : UiEvent()
        /** User created a new nest — show them the invite code first. */
        data class NavigateToNestCreated(val nestName: String, val inviteCode: String) : UiEvent()
    }
}
