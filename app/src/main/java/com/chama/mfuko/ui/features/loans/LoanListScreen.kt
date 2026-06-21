package com.chama.mfuko.ui.features.loans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.mfuko.data.remote.LoanDetailsResponse
import com.chama.mfuko.ui.components.MfukoCard
import com.chama.mfuko.ui.components.MfukoCardVariant
import com.chama.mfuko.ui.components.MoneyText
import com.chama.mfuko.ui.components.StatusChip
import com.chama.mfuko.ui.components.mfukoStatusOf
import com.chama.mfuko.ui.theme.MfukoExtraType
import com.chama.mfuko.ui.theme.MfukoSpacing
import com.chama.mfuko.ui.util.EmptyStateView
import com.chama.mfuko.ui.util.ErrorView
import com.chama.mfuko.ui.util.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanListScreen(
    nestId: Long,
    viewModel: LoanListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    // Load loans when screen opens or nestId changes
    LaunchedEffect(nestId) {
        viewModel.loadLoans(nestId)
    }

    // Surface approve / reject errors via Snackbar
    LaunchedEffect(state.actionError) {
        state.actionError?.let { error ->
            snackbarHostState.showSnackbar(
                message  = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearActionError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Loan Requests", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                state.isLoading -> LoadingView()

                state.error != null -> ErrorView(
                    message = state.error,
                    onRetry = { viewModel.loadLoans(nestId) }
                )

                state.loans.isEmpty() -> EmptyStateView("No loan requests found.")

                else -> LazyColumn(
                    modifier        = Modifier.fillMaxSize(),
                    contentPadding  = PaddingValues(MfukoSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(MfukoSpacing.md)
                ) {
                    items(state.loans) { loan ->
                        LoanRequestCard(
                            loan           = loan,
                            onApproveClick = { viewModel.approveLoan(loan.loanId, nestId) },
                            onRejectClick  = { viewModel.rejectLoan(loan.loanId, nestId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoanRequestCard(
    loan: LoanDetailsResponse,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    MfukoCard(variant = MfukoCardVariant.Standard, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MfukoSpacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text  = "Member ID: ${loan.userId}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(mfukoStatusOf(loan.status))
            }
            Spacer(modifier = Modifier.height(MfukoSpacing.sm))
            MoneyText(amount = loan.principalAmount, style = MfukoExtraType.moneyMedium)
            Text(
                text = "Term: ${loan.termMonths} months",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (loan.status.equals("pending", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(MfukoSpacing.md))
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment    = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onRejectClick) {
                        Text("Reject", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(MfukoSpacing.sm))
                    Button(onClick = onApproveClick) {
                        Text("Approve")
                    }
                }
            }
        }
    }
}
