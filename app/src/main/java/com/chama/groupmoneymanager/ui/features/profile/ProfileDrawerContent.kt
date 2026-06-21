package com.chama.groupmoneymanager.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.groupmoneymanager.ui.theme.MfukoSpacing

@Composable
fun ProfileDrawerContent(
    viewModel: ProfileViewModel = hiltViewModel(),
    onSwitchNest: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val state = viewModel.state.value

    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(modifier = Modifier.padding(MfukoSpacing.lg)) {
            // Avatar initial — DESIGN_SYSTEM.md §6.5, replaces the generic Person icon.
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.size(MfukoSpacing.sm))
            Text(
                text = state.userName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            state.nestName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            state.userRole?.let {
                Text(
                    text  = it.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        NavigationDrawerItem(
            label    = { Text("Join / Create / Switch Nest") },
            selected = false,
            icon     = { Icon(Icons.Default.SwapHoriz, contentDescription = null) },
            onClick  = onSwitchNest,
            colors   = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MfukoSpacing.md)
        )

        NavigationDrawerItem(
            label    = { Text("Settings") },
            selected = false,
            icon     = { Icon(Icons.Default.Settings, contentDescription = null) },
            onClick  = onSettings,
            colors   = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MfukoSpacing.md)
        )

        NavigationDrawerItem(
            label    = { Text("Log out") },
            selected = false,
            icon     = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
            onClick  = onLogout,
            colors   = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MfukoSpacing.md)
        )
    }
}
