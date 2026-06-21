package com.chama.mfuko.ui.features.auth.register

sealed interface RegisterEvent {
    data class EnteredName(val value: String) : RegisterEvent
    data class EnteredPhone(val value: String) : RegisterEvent
    data class EnteredPassword(val value: String) : RegisterEvent
    object Register : RegisterEvent
}