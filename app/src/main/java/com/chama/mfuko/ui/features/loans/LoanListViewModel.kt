package com.chama.mfuko.ui.features.loans

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.remote.LoanDetailsResponse
import com.chama.mfuko.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanListState(
    val isLoading: Boolean = false,
    val loans: List<LoanDetailsResponse> = emptyList(),
    val error: String? = null,
    /** Set when an approve or reject action fails; surfaced as a Snackbar by the UI. */
    val actionError: String? = null
)

@HiltViewModel
class LoanListViewModel @Inject constructor(
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoanListState())
    val state: State<LoanListState> = _state

    fun loadLoans(nestId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = loanRepository.getNestLoans(nestId)) {
                is Resource.Success -> _state.value = _state.value.copy(
                    loans     = result.data ?: emptyList(),
                    isLoading = false
                )
                is Resource.Error   -> _state.value = _state.value.copy(
                    error     = result.message,
                    isLoading = false
                )
                is Resource.Loading -> {}
            }
        }
    }

    fun approveLoan(loanId: Long, nestId: Long) {
        viewModelScope.launch {
            when (val result = loanRepository.approveLoan(loanId)) {
                is Resource.Success -> loadLoans(nestId)
                is Resource.Error   -> _state.value = _state.value.copy(
                    actionError = result.message ?: "Failed to approve loan"
                )
                is Resource.Loading -> {}
            }
        }
    }

    fun rejectLoan(loanId: Long, nestId: Long) {
        viewModelScope.launch {
            when (val result = loanRepository.rejectLoan(loanId)) {
                is Resource.Success -> loadLoans(nestId)
                is Resource.Error   -> _state.value = _state.value.copy(
                    actionError = result.message ?: "Failed to reject loan"
                )
                is Resource.Loading -> {}
            }
        }
    }

    /** Called by the UI after the Snackbar has been shown. */
    fun clearActionError() {
        _state.value = _state.value.copy(actionError = null)
    }
}
