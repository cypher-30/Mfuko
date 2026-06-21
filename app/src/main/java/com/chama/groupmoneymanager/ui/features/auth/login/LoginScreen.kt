package com.chama.groupmoneymanager.ui.features.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.groupmoneymanager.ui.theme.MfukoSpacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToWelcome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state = viewModel.state.value

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoginViewModel.UiEvent.NavigateToHome    -> onNavigateToHome()
                is LoginViewModel.UiEvent.NavigateToWelcome -> onNavigateToWelcome()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MfukoSpacing.xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MfukoSpacing.md),
            modifier = Modifier.fillMaxWidth()
        ) {
            // ── Header — wordmark (DESIGN_SYSTEM.md §8) ─────────────────────────
            Text(
                text  = "Mfuko",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text  = "Your group savings, simplified.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(MfukoSpacing.sm))

            // ── Phone & Password ──────────────────────────────────────────────
            OutlinedTextField(
                value         = state.phone,
                onValueChange = { viewModel.onEvent(LoginEvent.EnteredPhone(it)) },
                label         = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value                  = state.password,
                onValueChange          = { viewModel.onEvent(LoginEvent.EnteredPassword(it)) },
                label                  = { Text("Password") },
                visualTransformation   = PasswordVisualTransformation(),
                keyboardOptions        = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine             = true,
                modifier               = Modifier.fillMaxWidth()
            )

            // ── Error message ─────────────────────────────────────────────────
            if (state.error != null) {
                Text(
                    text  = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(MfukoSpacing.xs))

            // ── Login button ──────────────────────────────────────────────────
            Button(
                onClick  = { viewModel.onEvent(LoginEvent.Login) },
                enabled  = !state.isLoading && !state.isDemoLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color  = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Log In")
                }
            }

            // ── Register link ─────────────────────────────────────────────────
            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }

            // ── Divider ───────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    "  or  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            // ── Demo button ───────────────────────────────────────────────────
            OutlinedButton(
                onClick  = { viewModel.loginDemo() },
                enabled  = !state.isLoading && !state.isDemoLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isDemoLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Continue as Demo")
                }
            }
        }
    }
}
