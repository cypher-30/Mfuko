package com.chama.groupmoneymanager.ui.features.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.remote.DashboardResponse
import com.chama.groupmoneymanager.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val dashboardData: DashboardResponse? = null,
    val error: String? = null,
    val userIsManager: Boolean = false,
    val isRepayLoanDialogVisible: Boolean = false,
    val isRequestLoanDialogVisible: Boolean = false,
    /**
     * The ID of the current nest — loaded from DataStore.
     * 0L means no nest has been selected yet (freshly registered user).
     * This is the source of truth for navigation actions that need a nestId.
     * Will be replaced by Room membership query in Phase 3.
     */
    val currentNestId: Long = 0L
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                isRepayLoanDialogVisible = false,
                isRequestLoanDialogVisible = false
            )

            // Read the persisted nestId alongside the remote dashboard
            val nestId = tokenManager.getCurrentNestId().first()

            when (val result = userRepository.getDashboard()) {
                is Resource.Success -> {
                    val isManager = result.data?.userRole.equals("manager", ignoreCase = true)
                    _state.value = _state.value.copy(
                        isLoading    = false,
                        dashboardData = result.data,
                        userIsManager = isManager,
                        currentNestId = nestId
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading     = false,
                        error         = result.message,
                        currentNestId = nestId
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onRepayLoanClick() {
        _state.value = _state.value.copy(isRepayLoanDialogVisible = true)
    }

    fun onDismissRepayLoanDialog() {
        _state.value = _state.value.copy(isRepayLoanDialogVisible = false)
    }

    fun onRequestLoanClick() {
        _state.value = _state.value.copy(isRequestLoanDialogVisible = true)
    }

    fun onDismissRequestLoanDialog() {
        _state.value = _state.value.copy(isRequestLoanDialogVisible = false)
    }
}
