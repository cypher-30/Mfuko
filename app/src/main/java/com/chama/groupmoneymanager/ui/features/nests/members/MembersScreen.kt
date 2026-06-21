package com.chama.groupmoneymanager.ui.features.nests.members

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.groupmoneymanager.data.remote.MemberStatusDto
import com.chama.groupmoneymanager.ui.components.MfukoCard
import com.chama.groupmoneymanager.ui.components.MfukoCardVariant
import com.chama.groupmoneymanager.ui.components.MoneyText
import com.chama.groupmoneymanager.ui.components.StatusChip
import com.chama.groupmoneymanager.ui.components.mfukoStatusOf
import com.chama.groupmoneymanager.ui.features.contributions.RecordContributionDialog
import com.chama.groupmoneymanager.ui.theme.MfukoExtraType
import com.chama.groupmoneymanager.ui.theme.MfukoSpacing
import com.chama.groupmoneymanager.ui.util.EmptyStateView
import com.chama.groupmoneymanager.ui.util.ErrorView
import com.chama.groupmoneymanager.ui.util.LoadingView
import com.chama.groupmoneymanager.ui.util.formatKes
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    viewModel: MembersViewModel = hiltViewModel(),
    onNavigateToNestSettings: (Long) -> Unit = {}
) {
    val state = viewModel.state.value
    val isManager = state.currentUserRole.equals("manager", ignoreCase = true)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is MembersViewModel.UiEvent.ShareReport -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, event.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share nest report"))
                }
            }
        }
    }

    if (state.selectedMemberForPayment != null) {
        RecordContributionDialog(
            member    = state.selectedMemberForPayment,
            nestId    = state.nestId,
            onDismiss = { viewModel.onDismissDialog() },
            onSuccess = {
                viewModel.onDismissDialog()
                viewModel.loadMembers()
            }
        )
    }

    if (state.showAnnouncementDialog) {
        AnnouncementDialog(
            onDismiss = viewModel::onDismissAnnouncementDialog,
            onSend = viewModel::sendAnnouncement
        )
    }

    state.memberPendingRemoval?.let { member ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissRemoveDialog,
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text("Remove ${member.name}?", style = MaterialTheme.typography.headlineSmall) },
            text = { Text("They will lose access to this nest's contributions and loans.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmRemoveMember) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissRemoveDialog) { Text("Cancel") }
            }
        )
    }

    when {
        state.isLoading -> LoadingView()

        state.error != null -> ErrorView(message = state.error, onRetry = viewModel::loadMembers)

        state.members.isEmpty() -> EmptyStateView("No members found.")

        else -> LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(MfukoSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(MfukoSpacing.md)
        ) {
            if (isManager) {
                item {
                    ManagerOverviewCard(
                        members              = state.members,
                        inviteCode           = state.inviteCode,
                        announcementSent     = state.announcementSent,
                        onNestSettingsClick  = { onNavigateToNestSettings(state.nestId) },
                        onExportClick        = viewModel::onExportReportClick,
                        onAnnouncementClick  = viewModel::onAnnouncementIconClick
                    )
                }
            }
            items(state.members) { member ->
                MemberCard(
                    member               = member,
                    isManager            = isManager,
                    onRecordPaymentClick = { viewModel.onRecordPaymentClick(member) },
                    onRemoveClick        = { viewModel.onRemoveMemberClick(member) }
                )
            }
        }
    }
}

@Composable
fun ManagerOverviewCard(
    members: List<MemberStatusDto>,
    inviteCode: String,
    announcementSent: Boolean,
    onNestSettingsClick: () -> Unit,
    onExportClick: () -> Unit,
    onAnnouncementClick: () -> Unit
) {
    val totalCollected = members.sumOf { it.amountPaid }
    val totalDue = members.sumOf { it.totalDue }
    val paidCount = members.count { it.status == "paid" }
    val clipboardManager = LocalClipboardManager.current

    MfukoCard(variant = MfukoCardVariant.Emphasized, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MfukoSpacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Contribution Overview", style = MaterialTheme.typography.titleMedium)
                Row {
                    IconButton(onClick = onAnnouncementClick) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = "Send Announcement",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onExportClick) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Export Report",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNestSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Nest Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (announcementSent) {
                Text(
                    text = "Announcement sent to all members.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(MfukoSpacing.sm))
            Row(verticalAlignment = Alignment.Bottom) {
                MoneyText(amount = totalCollected, style = MfukoExtraType.moneyMedium)
                Text(
                    text = " of ${formatKes(totalDue)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$paidCount of ${members.size} members fully paid this cycle",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MfukoSpacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Invite code: $inviteCode",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(inviteCode))
                }) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy invite code",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun MemberCard(
    member: MemberStatusDto,
    isManager: Boolean,
    onRecordPaymentClick: () -> Unit,
    onRemoveClick: () -> Unit = {}
) {
    MfukoCard(variant = MfukoCardVariant.Standard, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier             = Modifier
                .fillMaxWidth()
                .padding(MfukoSpacing.lg),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = member.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text  = "Paid: ${formatKes(member.amountPaid)} / ${formatKes(member.totalDue)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(MfukoSpacing.xs))
                StatusChip(mfukoStatusOf(member.status))
            }
            if (isManager) {
                IconButton(onClick = onRecordPaymentClick) {
                    Icon(
                        imageVector        = Icons.Default.AddCard,
                        contentDescription = "Record Payment",
                        tint               = MaterialTheme.colorScheme.primary
                    )
                }
                if (!member.role.equals("manager", ignoreCase = true)) {
                    IconButton(onClick = onRemoveClick) {
                        Icon(
                            imageVector        = Icons.Default.PersonRemove,
                            contentDescription = "Remove Member",
                            tint               = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text("Send Announcement", style = MaterialTheme.typography.headlineSmall) },
        text = {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSend(message.trim()) },
                enabled = message.isNotBlank()
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
