package com.chama.groupmoneymanager.ui.features.activity

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.repository.ContributionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

data class MpesaReceipt(
    val amount: Double,
    val reference: String,
    val timestamp: String
)

sealed class PaymentStage {
    object Idle : PaymentStage()
    object SendingStkPush : PaymentStage()
    data class Success(val receipt: MpesaReceipt) : PaymentStage()
    data class Failed(val message: String) : PaymentStage()
}

data class ActivityState(
    val amount: String = "",
    val amountError: String? = null,
    val stage: PaymentStage = PaymentStage.Idle
)

/**
 * Simulated M-Pesa contribution flow (Phase 5). No real Daraja API call — fakes an STK push
 * delay and a ~85% success rate, then records the contribution locally on success.
 * Real M-Pesa integration is deferred to Phase 7.
 */
@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val contributionRepository: ContributionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = mutableStateOf(ActivityState())
    val state: State<ActivityState> = _state

    fun onAmountChange(value: String) {
        _state.value = _state.value.copy(amount = value, amountError = null)
    }

    fun onPayClick() {
        val amount = _state.value.amount.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _state.value = _state.value.copy(amountError = "Enter a valid amount")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(stage = PaymentStage.SendingStkPush)
            delay(2000) // simulate the STK push round-trip

            if (Random.nextInt(100) < 15) {
                _state.value = _state.value.copy(
                    stage = PaymentStage.Failed("Payment was not completed on your phone. Please try again.")
                )
                return@launch
            }

            val nestId = tokenManager.getCurrentNestId().first()
            val userId = tokenManager.getUserId().first()
            if (userId == null) {
                _state.value = _state.value.copy(stage = PaymentStage.Failed("Not logged in."))
                return@launch
            }

            when (val result = contributionRepository.recordContribution(nestId, userId, amount)) {
                is Resource.Success -> {
                    val receipt = MpesaReceipt(
                        amount = amount,
                        reference = generateReference(),
                        timestamp = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
                    )
                    _state.value = _state.value.copy(stage = PaymentStage.Success(receipt))
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(stage = PaymentStage.Failed(result.message ?: "Payment failed."))
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun reset() {
        _state.value = ActivityState()
    }

    private fun generateReference(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..10).map { chars.random() }.joinToString("")
    }
}
