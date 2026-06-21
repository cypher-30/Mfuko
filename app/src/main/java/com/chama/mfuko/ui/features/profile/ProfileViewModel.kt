package com.chama.mfuko.ui.features.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.mfuko.data.local.TokenManager
import com.chama.mfuko.data.local.dao.MembershipDao
import com.chama.mfuko.data.local.dao.NestDao
import com.chama.mfuko.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val userName: String = "",
    val nestName: String? = null,
    val userRole: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val nestDao: NestDao,
    private val membershipDao: MembershipDao
) : ViewModel() {

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val userName = tokenManager.getUserName().first() ?: ""
            val userId   = tokenManager.getUserId().first()
            val nestId   = tokenManager.getCurrentNestId().first()

            val nestName = if (nestId > 0L) nestDao.getNestById(nestId).first()?.name else null
            val role     = if (nestId > 0L && userId != null) {
                membershipDao.getUserRole(userId, nestId)
            } else null

            _state.value = ProfileState(userName = userName, nestName = nestName, userRole = role)
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            authRepository.logout()
            _eventFlow.send(UiEvent.NavigateToLogin)
        }
    }

    sealed class UiEvent {
        object NavigateToLogin : UiEvent()
        object NavigateToSwitchNest : UiEvent()
    }

    fun onSwitchNestClick() {
        viewModelScope.launch {
            _eventFlow.send(UiEvent.NavigateToSwitchNest)
        }
    }
}
