package com.chama.mfuko.ui.features.nests.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateNestSuccessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val nestName: String = savedStateHandle.get<String>("nestName") ?: "Your Nest"
    val inviteCode: String = savedStateHandle.get<String>("inviteCode") ?: "------"
}