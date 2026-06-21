package com.chama.mfuko.ui.features.auth.login

sealed interface LoginEvent {
    data class EnteredPhone(val value: String) : LoginEvent
    data class EnteredPassword(val value: String) : LoginEvent
    object Login : LoginEvent
}