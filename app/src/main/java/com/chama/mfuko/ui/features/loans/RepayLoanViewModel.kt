package com.chama.mfuko.ui.features.loans

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepayLoanState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RepayLoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _state = mutableStateOf(RepayLoanState())
    val state: State<RepayLoanState> = _state

    fun repayLoan(loanId: Long, amount: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // ✅ FIX: This now passes the correct arguments
            when (val result = loanRepository.repayLoan(loanId, amount)) {
                is Resource.Success -> {
                    onSuccess()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }
}