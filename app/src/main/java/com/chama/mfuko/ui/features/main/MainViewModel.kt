package com.chama.mfuko.ui.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.mfuko.data.local.TokenManager
import com.chama.mfuko.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Decides the app's initial navigation destination using local DataStore state only.
 * No network calls are made — fast, offline-safe, deterministic.
 *
 * Decision tree:
 *   No token   → Login
 *   Token + nestId > 0 → Home  (has an active nest)
 *   Token + nestId == 0 → Welcome  (registered but not in a nest yet)
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    init {
        viewModelScope.launch {
            val token  = tokenManager.getToken().first()
            val nestId = tokenManager.getCurrentNestId().first()

            _startDestination.value = when {
                token.isNullOrEmpty() -> Screen.Login.route
                nestId > 0L           -> Screen.Home.route
                else                  -> Screen.Welcome.route
            }
        }
    }
}
