package com.chama.mfuko.ui.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Formats a monetary value for display using Kenyan Shillings.
 *
 * Examples:
 *   5000.0   → "KES 5,000.00"
 *   1500.5   → "KES 1,500.50"
 *   0.0      → "KES 0.00"
 */
fun formatKes(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "KES ${formatter.format(amount)}"
}
