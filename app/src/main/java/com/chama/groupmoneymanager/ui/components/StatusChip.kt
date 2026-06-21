package com.chama.groupmoneymanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chama.groupmoneymanager.ui.theme.MfukoFullShape
import com.chama.groupmoneymanager.ui.theme.MfukoStatusColors

/**
 * The semantic states a [StatusChip] can render — DESIGN_SYSTEM.md §6.6.
 * Replaces every plain `Text("Status: …")` instance across the app
 * (MembersScreen.MemberCard, LoanListScreen.LoanRequestCard — §2 item 3).
 */
enum class MfukoStatus(val label: String, val semantic: MfukoSemantic) {
    PAID("Paid", MfukoSemantic.SUCCESS),
    PARTIAL("Partial", MfukoSemantic.WARNING),
    UNPAID("Unpaid", MfukoSemantic.DANGER),
    PENDING("Pending", MfukoSemantic.NEUTRAL),
    ACTIVE("Active", MfukoSemantic.SUCCESS),
    REJECTED("Rejected", MfukoSemantic.DANGER)
}

enum class MfukoSemantic { SUCCESS, WARNING, NEUTRAL, DANGER }

/**
 * Maps the raw lowercase status strings already used throughout the data
 * layer (e.g. "paid", "partial", "pending", "active", "rejected") onto the
 * design system's [MfukoStatus]. Unrecognized values fall back to [MfukoStatus.PENDING]
 * (neutral) rather than crashing or silently rendering nothing.
 */
fun mfukoStatusOf(raw: String?): MfukoStatus = when (raw?.lowercase()) {
    "paid" -> MfukoStatus.PAID
    "partial" -> MfukoStatus.PARTIAL
    "unpaid" -> MfukoStatus.UNPAID
    "active" -> MfukoStatus.ACTIVE
    "rejected", "denied" -> MfukoStatus.REJECTED
    "pending" -> MfukoStatus.PENDING
    else -> MfukoStatus.PENDING
}

/** Shared status pill — DESIGN_SYSTEM.md §3.4, §6.6. */
@Composable
fun StatusChip(status: MfukoStatus, modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()
    val palette = when (status.semantic) {
        MfukoSemantic.SUCCESS -> if (dark) MfukoStatusColors.successDark else MfukoStatusColors.successLight
        MfukoSemantic.WARNING -> if (dark) MfukoStatusColors.warningDark else MfukoStatusColors.warningLight
        MfukoSemantic.NEUTRAL -> if (dark) MfukoStatusColors.neutralDark else MfukoStatusColors.neutralLight
        MfukoSemantic.DANGER -> if (dark) MfukoStatusColors.dangerDark else MfukoStatusColors.dangerLight
    }
    Row(
        modifier = modifier
            .height(24.dp)
            .clip(MfukoFullShape)
            .background(palette.container)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(palette.dot)
        )
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            color = palette.onContainer
        )
    }
}
