package com.chama.mfuko.ui.features.nests.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.mfuko.data.local.dao.NestDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NestSettingsState(
    val isLoading: Boolean = true,
    val nestName: String = "",
    val contributionAmount: String = "",
    val cycleDurationDays: String = "",
    val interestRate: String = "",
    val interestType: String = "flat",
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NestSettingsViewModel @Inject constructor(
    private val nestDao: NestDao
) : ViewModel() {

    private val _state = mutableStateOf(NestSettingsState())
    val state: State<NestSettingsState> = _state

    private var nestId: Long = 0L

    fun load(nestId: Long) {
        this.nestId = nestId
        viewModelScope.launch {
            val nest = nestDao.getNestById(nestId).first()
            if (nest == null) {
                _state.value = _state.value.copy(isLoading = false, error = "Nest not found.")
                return@launch
            }
            _state.value = NestSettingsState(
                isLoading = false,
                nestName = nest.name,
                contributionAmount = nest.contributionAmount.toString(),
                cycleDurationDays = nest.cycleDurationDays.toString(),
                interestRate = nest.interestRate.toString(),
                interestType = nest.interestType
            )
        }
    }

    fun onContributionAmountChange(value: String) {
        _state.value = _state.value.copy(contributionAmount = value, isSaved = false)
    }

    fun onCycleDurationDaysChange(value: String) {
        _state.value = _state.value.copy(cycleDurationDays = value, isSaved = false)
    }

    fun onInterestRateChange(value: String) {
        _state.value = _state.value.copy(interestRate = value, isSaved = false)
    }

    fun onInterestTypeChange(value: String) {
        _state.value = _state.value.copy(interestType = value, isSaved = false)
    }

    fun save() {
        val amount = _state.value.contributionAmount.toDoubleOrNull()
        val rate = _state.value.interestRate.toDoubleOrNull()
        val cycleDays = _state.value.cycleDurationDays.toIntOrNull()
        if (amount == null || rate == null || cycleDays == null || cycleDays <= 0) {
            _state.value = _state.value.copy(error = "Please enter valid numbers.")
            return
        }
        viewModelScope.launch {
            val nest = nestDao.getNestById(nestId).first() ?: return@launch
            nestDao.updateNest(
                nest.copy(
                    contributionAmount = amount,
                    cycleDurationDays = cycleDays,
                    interestRate = rate,
                    interestType = _state.value.interestType
                )
            )
            _state.value = _state.value.copy(isSaved = true, error = null)
        }
    }
}
