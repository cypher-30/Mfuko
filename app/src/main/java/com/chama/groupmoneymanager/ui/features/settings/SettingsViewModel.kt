package com.chama.groupmoneymanager.ui.features.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.groupmoneymanager.data.local.DemoSeeder
import com.chama.groupmoneymanager.data.local.LocalAuthManager
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.local.dao.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val passwordChangeSuccess: Boolean = false,
    val isDemoAccount: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userDao: UserDao
) : ViewModel() {

    private val _state = mutableStateOf(SettingsState())
    val state: State<SettingsState> = _state

    init {
        viewModelScope.launch {
            val enabled = tokenManager.getNotificationsEnabled().first()
            val userId = tokenManager.getUserId().first()
            val isDemo = userId?.let { userDao.getUserById(it)?.phone == DemoSeeder.DEMO_PHONE } ?: false
            _state.value = _state.value.copy(notificationsEnabled = enabled, isDemoAccount = isDemo)
        }
    }

    fun onNotificationsToggle(enabled: Boolean) {
        _state.value = _state.value.copy(notificationsEnabled = enabled)
        viewModelScope.launch { tokenManager.setNotificationsEnabled(enabled) }
    }

    fun onCurrentPasswordChange(value: String) {
        _state.value = _state.value.copy(currentPassword = value, passwordError = null, passwordChangeSuccess = false)
    }

    fun onNewPasswordChange(value: String) {
        _state.value = _state.value.copy(newPassword = value, passwordError = null, passwordChangeSuccess = false)
    }

    fun onConfirmPasswordChange(value: String) {
        _state.value = _state.value.copy(confirmPassword = value, passwordError = null, passwordChangeSuccess = false)
    }

    fun onChangePasswordClick() {
        viewModelScope.launch {
            val s = _state.value
            val userId = tokenManager.getUserId().first()
            val user = userId?.let { userDao.getUserById(it) }

            if (user == null) {
                _state.value = s.copy(passwordError = "Not logged in.")
                return@launch
            }
            if (user.phone == DemoSeeder.DEMO_PHONE) {
                _state.value = s.copy(passwordError = "The demo account's password can't be changed.")
                return@launch
            }
            if (!LocalAuthManager.verifyPassword(s.currentPassword, user.passwordHash)) {
                _state.value = s.copy(passwordError = "Current password is incorrect.")
                return@launch
            }
            if (s.newPassword.length < 6) {
                _state.value = s.copy(passwordError = "New password must be at least 6 characters.")
                return@launch
            }
            if (s.newPassword != s.confirmPassword) {
                _state.value = s.copy(passwordError = "New passwords don't match.")
                return@launch
            }

            userDao.updatePasswordHash(user.id, LocalAuthManager.hashPassword(s.newPassword))
            _state.value = SettingsState(
                notificationsEnabled = s.notificationsEnabled,
                isDemoAccount = s.isDemoAccount,
                passwordChangeSuccess = true
            )
        }
    }
}
