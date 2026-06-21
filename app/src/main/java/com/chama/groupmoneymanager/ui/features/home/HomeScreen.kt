package com.chama.groupmoneymanager.ui.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.groupmoneymanager.data.remote.ContributionStatusDto
import com.chama.groupmoneymanager.data.remote.LoanStatusDto
import com.chama.groupmoneymanager.data.remote.PenaltyStatusDto
import com.chama.groupmoneymanager.ui.components.ContributionRing
import com.chama.groupmoneymanager.ui.components.HealthGauge
import com.chama.groupmoneymanager.ui.components.MfukoCard
import com.chama.groupmoneymanager.ui.components.MfukoCardVariant
import com.chama.groupmoneymanager.ui.components.MoneyText
import com.chama.groupmoneymanager.ui.components.MfukoStatus
import com.chama.groupmoneymanager.ui.components.StatusChip
import com.chama.groupmoneymanager.ui.features.loans.RepayLoanDialog
import com.chama.groupmoneymanager.ui.features.loans.RequestLoanDialog
import com.chama.groupmoneymanager.ui.theme.MfukoExtraType
import com.chama.groupmoneymanager.ui.theme.MfukoSpacing
import com.chama.groupmoneymanager.ui.util.EmptyStateView
import com.chama.groupmoneymanager.ui.util.ErrorView
import com.chama.groupmoneymanager.ui.util.LoadingView
import com.chama.groupmoneymanager.ui.util.formatKes

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToManageLoans: (Long) -> Unit
) {
    val state = viewModel.state.value
    val dashboardData = state.dashboardData
    val nestId = state.currentNestId

    if (state.isRepayLoanDialogVisible && dashboardData?.loanStatus != null) {
        RepayLoanDialog(
            loanId             = dashboardData.loanStatus.loanId,
            outstandingBalance = dashboardData.loanStatus.outstandingBalance,
            onDismiss          = viewModel::onDismissRepayLoanDialog,
            onSuccess          = viewModel::loadDashboard
        )
    }

    if (state.isRequestLoanDialogVisible) {
        RequestLoanDialog(
            nestId    = nestId,
            onDismiss = viewModel::onDismissRequestLoanDialog,
            onSuccess = viewModel::loadDashboard
        )
    }

    when {
        state.isLoading -> LoadingView()

        state.error != null -> ErrorView(message = state.error, onRetry = viewModel::loadDashboard)

        dashboardData != null -> Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MfukoSpacing.lg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MfukoSpacing.xl)
        ) {
            dashboardData.contributionStatus?.let { ContributionHeroCard(it) }
            dashboardData.financialHealthScore?.let { HealthScoreCard(it) }
            dashboardData.loanStatus?.let {
                LoanProgressCard(it, onRepayClick = viewModel::onRepayLoanClick)
            }
            dashboardData.penaltyStatus?.let {
                if (it.totalUnpaid > 0) PenaltyCard(it)
            }

            if (state.userIsManager) {
                Button(
                    onClick  = { onNavigateToManageLoans(nestId) },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Manage Loan Requests") }
            } else {
                Button(
                    onClick  = viewModel::onRequestLoanClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled  = dashboardData.loanStatus == null
                ) { Text("Request a Loan") }
            }
        }

        else -> EmptyStateView("No dashboard data available.")
    }
}

/**
 * Dashboard anchor element — DESIGN_SYSTEM.md §6.4 "Hero" + the
 * contribution-progress ring from §6.7. The ring's colors are overridden to
 * `onPrimary`/translucent-`onPrimary` since it now sits on the Hero card's
 * primary-filled background instead of the page background.
 */
@Composable
fun ContributionHeroCard(status: ContributionStatusDto) {
    val rawProgress = if (status.amountDue > 0.0) {
        (status.amountPaid / status.amountDue).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    MfukoCard(variant = MfukoCardVariant.Hero, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MfukoSpacing.xl),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Contribution Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
                Spacer(Modifier.height(MfukoSpacing.sm))
                MoneyText(
                    amount = status.amountPaid,
                    style = MfukoExtraType.moneyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "of ${formatKes(status.amountDue)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                )
                Spacer(Modifier.height(MfukoSpacing.sm))
                Text(
                    text = "Due: ${status.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.width(MfukoSpacing.lg))
            ContributionRing(
                progress = rawProgress,
                ringColor = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f),
                textColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun HealthScoreCard(score: Int) {
    MfukoCard(variant = MfukoCardVariant.Emphasized, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MfukoSpacing.lg)) {
            Text(text = "Financial Health", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(MfukoSpacing.sm))
            HealthGauge(score = score)
        }
    }
}

/**
 * Active-loan summary — DESIGN_SYSTEM.md §6.8. Note: the literal repaid /
 * total-repayable progress bar specified in §6.8 needs data
 * (`totalRepayableAmount`) that [LoanStatusDto] does not currently expose
 * (only `outstandingBalance`); since this pass changes visuals only and not
 * the data layer, the progress bar is omitted here rather than fabricated.
 */
@Composable
fun LoanProgressCard(status: LoanStatusDto, onRepayClick: () -> Unit) {
    MfukoCard(variant = MfukoCardVariant.Emphasized, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MfukoSpacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Active Loan", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(MfukoSpacing.sm))
                StatusChip(MfukoStatus.ACTIVE)
            }
            Spacer(modifier = Modifier.height(MfukoSpacing.sm))
            MoneyText(amount = status.outstandingBalance, style = MfukoExtraType.moneyLarge)
            Text(
                text = "Outstanding Balance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            status.nextDueDate?.let {
                Spacer(modifier = Modifier.height(MfukoSpacing.xs))
                Text(
                    text = "Next due: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(MfukoSpacing.md))
            Button(
                onClick  = onRepayClick,
                modifier = Modifier.align(Alignment.End)
            ) { Text("Make a Payment") }
        }
    }
}

@Composable
fun PenaltyCard(status: PenaltyStatusDto) {
    MfukoCard(variant = MfukoCardVariant.Alert, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MfukoSpacing.lg)) {
            Text(
                text  = "Unpaid Penalties",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(MfukoSpacing.sm))
            MoneyText(
                amount = status.totalUnpaid,
                style = MfukoExtraType.moneyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
