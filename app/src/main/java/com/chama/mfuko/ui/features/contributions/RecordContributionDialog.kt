package com.chama.mfuko.ui.features.contributions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.mfuko.data.remote.MemberStatusDto
import com.chama.mfuko.ui.theme.MfukoSpacing

@Composable
fun RecordContributionDialog(
    member: MemberStatusDto,
    nestId: Long,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ContributionViewModel = hiltViewModel()
) {
    var amount by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }
    val state = viewModel.state.value

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = {
            Text("Record for ${member.name}", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                OutlinedTextField(
                    value         = amount,
                    onValueChange = {
                        amount      = it
                        amountError = null   // clear validation error on change
                    },
                    label           = { Text("Amount Paid") },
                    prefix          = {
                        Text(
                            "KES ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    isError         = amountError != null,
                    supportingText  = amountError?.let { error ->
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine      = true,
                    modifier        = Modifier.fillMaxWidth()
                )
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(MfukoSpacing.sm))
                    Text(
                        state.error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    when {
                        amountDouble == null || amount.isBlank() ->
                            amountError = "Please enter a valid amount"
                        amountDouble <= 0 ->
                            amountError = "Amount must be greater than zero"
                        else ->
                            viewModel.recordContribution(nestId, member.userId, amountDouble, onSuccess)
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
                    Text("Confirm")
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