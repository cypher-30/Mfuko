package com.chama.groupmoneymanager.ui.features.contributions

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.repository.ContributionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContributionState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ContributionViewModel @Inject constructor(
    private val repository: ContributionRepository
) : ViewModel() {

    private val _state = mutableStateOf(ContributionState())
    val state: State<ContributionState> = _state

    fun recordContribution(nestId: Long, userId: Long, amount: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.recordContribution(nestId, userId, amount)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    onSuccess()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                else -> {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }
}