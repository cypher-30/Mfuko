package com.chama.mfuko.core.util

import kotlin.math.pow

data class InterestBreakdown(
    val totalInterestAmount: Double,
    val totalRepayableAmount: Double
)

/**
 * Computes total interest for a loan at approval time.
 *
 * "flat": interest charged on the full principal for the whole term, regardless of repayments.
 * "reducing": interest computed on the amortizing (declining) balance — the standard
 * equal-installment formula, which charges less total interest than flat for the same nominal rate.
 */
object LoanInterestCalculator {

    fun calculate(principal: Double, annualRatePercent: Double, termMonths: Int, interestType: String): InterestBreakdown {
        return when (interestType) {
            "reducing" -> reducingBalance(principal, annualRatePercent, termMonths)
            else -> flat(principal, annualRatePercent, termMonths)
        }
    }

    private fun flat(principal: Double, annualRatePercent: Double, termMonths: Int): InterestBreakdown {
        val totalInterest = principal * (annualRatePercent / 100.0) * (termMonths / 12.0)
        return InterestBreakdown(totalInterest, principal + totalInterest)
    }

    private fun reducingBalance(principal: Double, annualRatePercent: Double, termMonths: Int): InterestBreakdown {
        val monthlyRate = annualRatePercent / 100.0 / 12.0
        val totalRepayable = if (monthlyRate == 0.0 || termMonths <= 0) {
            principal
        } else {
            val factor = (1 + monthlyRate).pow(termMonths)
            val emi = principal * monthlyRate * factor / (factor - 1)
            emi * termMonths
        }
        return InterestBreakdown(totalRepayable - principal, totalRepayable)
    }
}
