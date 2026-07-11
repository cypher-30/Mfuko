package com.chama.mfuko.ui.features.nests.create

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.mfuko.ui.components.MfukoCard
import com.chama.mfuko.ui.components.MfukoCardVariant
import com.chama.mfuko.ui.theme.MfukoExtraType
import com.chama.mfuko.ui.theme.MfukoSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNestSuccessScreen(
    viewModel: CreateNestSuccessViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nest Created!") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(MfukoSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Your nest '${viewModel.nestName}' is ready.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = MfukoSpacing.sm)
            )
            Spacer(modifier = Modifier.height(MfukoSpacing.xxxl))
            Text(
                text = "Share this code with your members to let them join:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(MfukoSpacing.lg))
            // Invite-code display — replaces the old hardcoded 48.sp/8.sp one-off
            // with the named displayCode style (DESIGN_SYSTEM.md §4.3, §2 item 10).
            MfukoCard(variant = MfukoCardVariant.Emphasized) {
                Text(
                    text = viewModel.inviteCode,
                    style = MfukoExtraType.displayCode,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = MfukoSpacing.xl, vertical = MfukoSpacing.lg)
                )
            }
            Spacer(modifier = Modifier.height(MfukoSpacing.lg))
            Row(
                horizontalArrangement = Arrangement.spacedBy(MfukoSpacing.md)
            ) {
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(viewModel.inviteCode))
                    }
                ) {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(MfukoSpacing.xs))
                    Text("Copy code")
                }
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Join my nest '${viewModel.nestName}' on Mfuko! Use invite code: ${viewModel.inviteCode}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share invite code"))
                    }
                ) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(MfukoSpacing.xs))
                    Text("Share")
                }
            }
            Spacer(modifier = Modifier.height(MfukoSpacing.xxxl))
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Go to Dashboard")
            }
        }
    }
}