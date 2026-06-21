package com.chama.groupmoneymanager.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Custom arc gauge primitive — DESIGN_SYSTEM.md §6.7. Replaces the stock
 * `CircularProgressIndicator` previously repurposed as a data visualization
 * for the contribution-progress ring and the financial-health score (§2,
 * item 7). A 270° ring with a 90° gap centered at the bottom (start angle
 * 135°, sweeping 270° clockwise to 45°).
 */
@Composable
fun MfukoArcGauge(
    progress: Float,
    diameter: Dp,
    strokeWidth: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable () -> Unit = {}
) {
    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = strokeWidth.toPx()
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            val startAngle = 135f
            val sweepAngle = 270f

            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        content()
    }
}

/** Score band thresholds — unchanged from the pre-existing HealthScoreCard logic. */
enum class HealthBand(val label: String) {
    GOOD("Good"),
    FAIR("Fair"),
    NEEDS_ATTENTION("Needs attention")
}

fun healthBandOf(score: Int): HealthBand = when {
    score >= 70 -> HealthBand.GOOD
    score >= 40 -> HealthBand.FAIR
    else -> HealthBand.NEEDS_ATTENTION
}

/**
 * Financial-health gauge — DESIGN_SYSTEM.md §6.7 and micro-interaction §9.2.
 * The ring sweep and the centered score count-up are driven by the *same*
 * animated float, so they always arrive in lockstep.
 */
@Composable
fun HealthGauge(
    score: Int,
    modifier: Modifier = Modifier,
    diameter: Dp = 64.dp
) {
    val band = healthBandOf(score)
    val bandColor = when (band) {
        HealthBand.GOOD -> MaterialTheme.colorScheme.primary
        HealthBand.FAIR -> MaterialTheme.colorScheme.secondary
        HealthBand.NEEDS_ATTENTION -> MaterialTheme.colorScheme.error
    }
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "HealthGaugeScore"
    )

    MfukoArcGauge(
        progress = animatedScore / 100f,
        diameter = diameter,
        strokeWidth = 8.dp,
        color = bandColor,
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = animatedScore.roundToInt().toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = bandColor
            )
            Text(
                text = band.label,
                style = MaterialTheme.typography.labelSmall,
                color = bandColor
            )
        }
    }
}

/**
 * Contribution-progress ring — DESIGN_SYSTEM.md §6.7 ("uses the same
 * HealthGauge primitive at 96dp diameter, always in primary"). [progress]
 * is 0f..1f (amountPaid / amountDue, already divide-by-zero guarded by the
 * caller as today). Colors default to on-`surface` usage but can be
 * overridden (e.g. to `onPrimary`/translucent-`onPrimary`) when the ring is
 * placed on a primary-filled Hero card, so the ring stays legible against
 * its background.
 */
@Composable
fun ContributionRing(
    progress: Float,
    modifier: Modifier = Modifier,
    diameter: Dp = 96.dp,
    ringColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "ContributionRingProgress"
    )
    MfukoArcGauge(
        progress = animatedProgress,
        diameter = diameter,
        strokeWidth = 10.dp,
        color = ringColor,
        trackColor = trackColor,
        modifier = modifier
    ) {
        Text(
            text = "${(animatedProgress * 100).roundToInt()}%",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
