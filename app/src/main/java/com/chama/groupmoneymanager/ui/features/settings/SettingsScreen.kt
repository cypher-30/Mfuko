package com.chama.groupmoneymanager.ui.features.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.groupmoneymanager.ui.components.MfukoCard
import com.chama.groupmoneymanager.ui.components.MfukoCardVariant
import com.chama.groupmoneymanager.ui.theme.MfukoSpacing
import com.chama.groupmoneymanager.ui.theme.MfukoStatusColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(MfukoSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(MfukoSpacing.lg)
        ) {
            MfukoCard(variant = MfukoCardVariant.Standard, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MfukoSpacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Notifications", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Get alerts for contributions, loans, and approvals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(MfukoSpacing.md))
                    Switch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = { viewModel.onNotificationsToggle(it) }
                    )
                }
            }

            MfukoCard(variant = MfukoCardVariant.Standard, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(MfukoSpacing.lg)) {
                    Text("Change Password", style = MaterialTheme.typography.titleMedium)

                    if (state.isDemoAccount) {
                        Text(
                            "The demo account's password can't be changed.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = MfukoSpacing.sm)
                        )
                    } else {
                        OutlinedTextField(
                            value = state.currentPassword,
                            onValueChange = viewModel::onCurrentPasswordChange,
                            label = { Text("Current password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = MfukoSpacing.sm)
                        )
                        OutlinedTextField(
                            value = state.newPassword,
                            onValueChange = viewModel::onNewPasswordChange,
                            label = { Text("New password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = MfukoSpacing.sm)
                        )
                        OutlinedTextField(
                            value = state.confirmPassword,
                            onValueChange = viewModel::onConfirmPasswordChange,
                            label = { Text("Confirm new password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = MfukoSpacing.sm)
                        )

                        state.passwordError?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = MfukoSpacing.sm)
                            )
                        }

                        if (state.passwordChangeSuccess) {
                            val successColor = if (isSystemInDarkTheme()) {
                                MfukoStatusColors.successDark.dot
                            } else {
                                MfukoStatusColors.successLight.dot
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = MfukoSpacing.sm)
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = successColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    "Password updated.",
                                    color = successColor,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = MfukoSpacing.xs)
                                )
                            }
                        }

                        Button(
                            onClick = viewModel::onChangePasswordClick,
                            modifier = Modifier.padding(top = MfukoSpacing.md).fillMaxWidth().height(52.dp)
                        ) {
                            Text("Update Password")
                        }
                    }
                }
            }
        }
    }
}
