package com.chama.mfuko.ui.features.loans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.mfuko.ui.components.MoneyText
import com.chama.mfuko.ui.theme.MfukoExtraType
import com.chama.mfuko.ui.theme.MfukoSpacing

@Composable
fun RepayLoanDialog(
    loanId: Long,
    outstandingBalance: Double,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: RepayLoanViewModel = hiltViewModel()
) {
    var amount by remember { mutableStateOf("") }
    val state = viewModel.state.value

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text("Repay Loan", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                Row {
                    Text(
                        "Outstanding balance: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    MoneyText(amount = outstandingBalance, style = MfukoExtraType.moneySmall)
                }
                Spacer(modifier = Modifier.height(MfukoSpacing.sm))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount to pay") },
                    prefix = {
                        Text(
                            "KES ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.error != null) {
                    Text(
                        state.error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = MfukoSpacing.sm)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (amountDouble != null) {
                        viewModel.repayLoan(loanId, amountDouble, onSuccess)
                    }
                },
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Confirm Payment")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}