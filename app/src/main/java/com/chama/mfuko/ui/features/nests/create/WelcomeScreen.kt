package com.chama.mfuko.ui.features.nests.create

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.mfuko.ui.theme.MfukoSpacing
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = hiltViewModel(),
    /** Called when the user joins an existing nest — navigate to the dashboard. */
    onSuccess: () -> Unit,
    /** Called when the user creates a new nest — navigate to the invite-code screen. */
    onNestCreated: (nestName: String, inviteCode: String) -> Unit,
    /**
     * Non-null only when this screen was reached from an existing dashboard
     * (drawer "Switch Nest") and there's somewhere to safely return to.
     * Null for the first-run flow right after login/register, where no nest
     * exists yet and there's nothing to go back to.
     */
    onNavigateBack: (() -> Unit)? = null
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Create a Nest", "Join a Nest")

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is WelcomeViewModel.UiEvent.NavigateToHome ->
                    onSuccess()
                is WelcomeViewModel.UiEvent.NavigateToNestCreated ->
                    onNestCreated(event.nestName, event.inviteCode)
                is WelcomeViewModel.UiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        // Unified TopAppBar treatment (DESIGN_SYSTEM.md §6.5) — fixes the app-bar
        // inconsistency where this screen alone used a primaryContainer bar.
        topBar = {
            TopAppBar(
                title = { Text("Welcome to Mfuko", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    onNavigateBack?.let { back ->
                        IconButton(onClick = back) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(MfukoSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick  = { selectedTab = index },
                        text     = { Text(title, style = MaterialTheme.typography.labelLarge) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(MfukoSpacing.xl))

            if (selectedTab == 0) {
                CreateNestForm(viewModel = viewModel)
            } else {
                JoinNestForm(viewModel = viewModel)
            }

            if (state.error != null) {
                Text(
                    text     = state.error,
                    color    = MaterialTheme.colorScheme.error,
                    style    = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = MfukoSpacing.lg)
                )
            }
        }
    }
}

@Composable
fun CreateNestForm(viewModel: WelcomeViewModel) {
    val state = viewModel.state.value
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MfukoSpacing.sm)
    ) {
        OutlinedTextField(
            value         = state.nestName,
            onValueChange = viewModel::onNestNameChange,
            label         = { Text("Group name (e.g. 'Family Savings')") },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value          = state.contributionAmount,
            onValueChange  = viewModel::onContributionAmountChange,
            label          = { Text("Monthly contribution (KES)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine     = true,
            modifier       = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(MfukoSpacing.sm))
        Button(
            onClick  = viewModel::onCreateNestClick,
            enabled  = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Create Nest")
            }
        }
    }
}

@Composable
fun JoinNestForm(viewModel: WelcomeViewModel) {
    val state = viewModel.state.value
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MfukoSpacing.sm)
    ) {
        OutlinedTextField(
            value          = state.inviteCode,
            onValueChange  = viewModel::onInviteCodeChange,
            label          = { Text("6-digit invite code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine     = true,
            modifier       = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(MfukoSpacing.sm))
        Button(
            onClick  = viewModel::onJoinNestClick,
            enabled  = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Join Nest")
            }
        }
    }
}
