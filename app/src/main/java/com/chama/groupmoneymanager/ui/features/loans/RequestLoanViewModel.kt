package com.chama.groupmoneymanager.ui.features.loans

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestLoanState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RequestLoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _state = mutableStateOf(RequestLoanState())
    val state: State<RequestLoanState> = _state

    fun requestLoan(nestId: Long, amount: Double, term: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = loanRepository.requestLoan(nestId, amount, term)) {
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