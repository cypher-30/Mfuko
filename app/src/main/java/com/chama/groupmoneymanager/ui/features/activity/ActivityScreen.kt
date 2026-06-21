package com.chama.groupmoneymanager.ui.features.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.groupmoneymanager.ui.components.MfukoCard
import com.chama.groupmoneymanager.ui.components.MfukoCardVariant
import com.chama.groupmoneymanager.ui.components.MoneyText
import com.chama.groupmoneymanager.ui.theme.MfukoExtraType
import com.chama.groupmoneymanager.ui.theme.MfukoSpacing

@Composable
fun ActivityScreen(viewModel: ActivityViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    Box(modifier = Modifier.fillMaxSize().padding(MfukoSpacing.lg)) {
        when (val stage = state.stage) {
            is PaymentStage.Idle -> MakeContributionForm(
                amount = state.amount,
                amountError = state.amountError,
                onAmountChange = viewModel::onAmountChange,
                onPayClick = viewModel::onPayClick
            )

            is PaymentStage.SendingStkPush -> SendingStkPushView()

            is PaymentStage.Success -> ReceiptView(receipt = stage.receipt, onDone = viewModel::reset)

            is PaymentStage.Failed -> FailedView(message = stage.message, onRetry = viewModel::onPayClick, onCancel = viewModel::reset)
        }
    }
}

@Composable
private fun MakeContributionForm(
    amount: String,
    amountError: String?,
    onAmountChange: (String) -> Unit,
    onPayClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(MfukoSpacing.lg)) {
        Text("Make a Contribution", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text("Amount (KES)") },
            isError = amountError != null,
            supportingText = amountError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onPayClick, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Pay now (M-Pesa)")
        }
    }
}

@Composable
private fun SendingStkPushView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        Text(
            text = "STK push sent — check your phone to complete payment...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = MfukoSpacing.lg)
        )
    }
}

@Composable
private fun ReceiptView(receipt: MpesaReceipt, onDone: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = MfukoSpacing.md).size(48.dp)
        )
        Text("Payment Successful", style = MaterialTheme.typography.headlineSmall)
        MfukoCard(
            variant = MfukoCardVariant.Standard,
            modifier = Modifier.fillMaxWidth().padding(top = MfukoSpacing.lg)
        ) {
            Column(
                modifier = Modifier.padding(MfukoSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(MfukoSpacing.sm)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Amount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    MoneyText(amount = receipt.amount, style = MfukoExtraType.moneySmall)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "M-Pesa Ref",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(receipt.reference, style = MaterialTheme.typography.bodyMedium)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(receipt.timestamp, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Button(
            onClick = onDone,
            modifier = Modifier.padding(top = MfukoSpacing.xl).fillMaxWidth().height(52.dp)
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun FailedView(message: String, onRetry: () -> Unit, onCancel: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = MfukoSpacing.md).size(48.dp)
        )
        Text("Payment Failed", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = MfukoSpacing.sm)
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = MfukoSpacing.xl).fillMaxWidth().height(52.dp)
        ) {
            Text("Try Again")
        }
        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().padding(top = MfukoSpacing.sm)
        ) {
            Text("Cancel")
        }
    }
}
