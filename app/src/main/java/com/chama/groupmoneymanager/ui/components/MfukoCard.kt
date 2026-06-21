package com.chama.groupmoneymanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chama.groupmoneymanager.ui.theme.MfukoElevation

/** The four card treatments defined in DESIGN_SYSTEM.md §6.4. */
enum class MfukoCardVariant { Standard, Emphasized, Hero, Alert }

/**
 * Shared card primitive — DESIGN_SYSTEM.md §6.4. Collapses the previous
 * inconsistent elevation values (2dp / 4dp / default, no shape — §2 items
 * 5 & 9) into four named, reusable variants.
 */
@Composable
fun MfukoCard(
    modifier: Modifier = Modifier,
    variant: MfukoCardVariant = MfukoCardVariant.Standard,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = when (variant) {
        MfukoCardVariant.Standard, MfukoCardVariant.Alert -> MaterialTheme.shapes.medium
        MfukoCardVariant.Emphasized, MfukoCardVariant.Hero -> MaterialTheme.shapes.large
    }
    val elevation = when (variant) {
        MfukoCardVariant.Standard, MfukoCardVariant.Alert -> MfukoElevation.level1
        MfukoCardVariant.Emphasized -> MfukoElevation.level2
        MfukoCardVariant.Hero -> MfukoElevation.level3
    }
    val containerColor = when (variant) {
        MfukoCardVariant.Standard -> MaterialTheme.colorScheme.surfaceContainerLow
        MfukoCardVariant.Emphasized -> MaterialTheme.colorScheme.surfaceContainer
        MfukoCardVariant.Hero -> MaterialTheme.colorScheme.primary
        MfukoCardVariant.Alert -> MaterialTheme.colorScheme.errorContainer
    }

    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        if (variant == MfukoCardVariant.Hero) {
            // Soft gold corner glow, replacing the earlier flat top-edge rule
            // (which clashed with the card's rounded corners). Clipped to the
            // card's own shape, so it reads as a gentle highlight rather than
            // a hard line.
            Box {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 32.dp, y = (-32).dp)
                        .size(140.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.30f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                Column(content = content)
            }
        } else {
            Column(content = content)
        }
    }
}

/** Section title — DESIGN_SYSTEM.md §6 (titleLarge / Bricolage). */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(text = title, style = MaterialTheme.typography.titleLarge, color = color, modifier = modifier)
}
