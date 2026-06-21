package com.chama.mfuko.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.mfuko.data.local.entities.NotificationEntity
import com.chama.mfuko.ui.components.MfukoCard
import com.chama.mfuko.ui.components.MfukoCardVariant
import com.chama.mfuko.ui.theme.MfukoSpacing
import com.chama.mfuko.ui.util.EmptyStateView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel = hiltViewModel()) {
    val notifications by viewModel.notifications.collectAsState()

    if (notifications.isEmpty()) {
        EmptyStateView("No notifications yet")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(MfukoSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(MfukoSpacing.sm)
    ) {
        items(notifications) { notification ->
            NotificationCard(
                notification = notification,
                onClick = { viewModel.onNotificationClick(notification) }
            )
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationEntity, onClick: () -> Unit) {
    MfukoCard(
        variant = MfukoCardVariant.Standard,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(MfukoSpacing.lg),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (notification.isRead) Color.Transparent
                        else MaterialTheme.colorScheme.primary
                    )
            )
            Spacer(modifier = Modifier.width(MfukoSpacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                if (notification.type == "announcement") {
                    Text(
                        text = "ANNOUNCEMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(MfukoSpacing.xs))
                }
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(MfukoSpacing.xs))
                Text(
                    text = formatTimestamp(notification.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTimestamp(epochMillis: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(epochMillis))
