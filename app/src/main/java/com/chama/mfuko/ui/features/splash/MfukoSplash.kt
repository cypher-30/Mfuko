package com.chama.mfuko.ui.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chama.mfuko.R
import com.chama.mfuko.ui.theme.Green50
import com.chama.mfuko.ui.theme.Green900
import com.chama.mfuko.ui.theme.MfukoSpacing
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────
// Mfuko launch/splash animation — "coins stacking" sequence, ~4s total
// (deliberately slow — this is a moment meant to be watched, not a snappy
// micro-interaction). Source of truth: APP_REDESIGN_BRIEF.md ("Launch/splash
// animation specs")
// and icon and launch animations/Mfuko Visual Redesign.dc.html §02. Coin
// geometry/palette is identical to ic_launcher_foreground.xml /
// drawable-night/ic_launcher_foreground.xml (same 0..108 viewBox) so the
// splash mark and the home-screen icon read as the same brand object.
// Implemented with Canvas + drawOval — the same primitive HealthGauge
// (ui/components/MfukoGauge.kt) already uses for its arc gauges — and
// Animatable<Float> + spring(DampingRatioMediumBouncy), the same
// micro-interaction spring vocabulary used for the payment-success
// checkmark overshoot. No Lottie / new runtime dependency.
// ─────────────────────────────────────────────────────────────────────────

private data class CoinSpec(
    val edgeCy: Float,
    val faceCy: Float,
    val hlCy: Float,
    val edgeColor: Color,
    val faceColor: Color,
    val hlColor: Color,
    val hlAlpha: Float
)

// Bottom → top, matching ic_launcher_foreground.xml exactly.
private val LightCoins = listOf(
    CoinSpec(73f, 70f, 67.5f, Color(0xFF040F08), Color(0xFF0B3D26), Color(0xFF0F5132), 0.35f),
    CoinSpec(61f, 58f, 55.5f, Color(0xFF06200F), Color(0xFF0F5132), Color(0xFF146647), 0.40f),
    CoinSpec(49f, 46f, 43.5f, Color(0xFF0A2C22), Color(0xFF1F8059), Color(0xFF3FA476), 0.40f),
    CoinSpec(37f, 34f, 31.5f, Color(0xFF7A5F09), Color(0xFFD4A017), Color(0xFFEBCB6E), 0.55f)
)

private val DarkCoins = listOf(
    CoinSpec(73f, 70f, 67.5f, Color(0xFF0A3020), Color(0xFF1F8059), Color(0xFF3FA476), 0.35f),
    CoinSpec(61f, 58f, 55.5f, Color(0xFF1A5C3C), Color(0xFF3FA476), Color(0xFF6CC097), 0.40f),
    CoinSpec(49f, 46f, 43.5f, Color(0xFF357F59), Color(0xFF6CC097), Color(0xFFA0D9BC), 0.40f),
    CoinSpec(37f, 34f, 31.5f, Color(0xFF7A5F09), Color(0xFFD4A017), Color(0xFFEBCB6E), 0.55f)
)

private const val VIEWBOX = 108f
private const val FACE_RX = 26f
private const val FACE_RY = 7.5f
private const val HL_RX = 19f
private const val HL_RY = 2.8f
private const val CX = 54f

// Tuned deliberately slower than a snappy UI micro-interaction — this is the
// one moment in the app meant to be watched, not reacted to instantly.
private const val COIN_STAGGER_MS = 320L
private const val COIN_SPRING_STIFFNESS = 90f // well below Spring.StiffnessLow (200f) for a slow, floaty settle
private const val WORDMARK_DELAY_MS = 2600L // after the slower coin springs below have settled
private const val WORDMARK_DURATION_MS = 500
private const val MIN_HOLD_BEFORE_EXIT_MS = 700L
private const val EXIT_DURATION_MS = 350

private val StackSize = 150.dp
private val EntranceRise = 18.dp
private val WordmarkGap = MfukoSpacing.sm

/**
 * Full-screen launch animation: four coins rise + bounce into a stack
 * bottom-up, the "Mfuko" wordmark settles in beneath it, then the whole
 * thing cross-fades out. Call [onFinished] once to swap to the real app
 * content — see MainActivity for the handoff.
 *
 * [contentReady] gates the exit: the splash will finish its entrance and
 * hold there for at least [MIN_HOLD_BEFORE_EXIT_MS], but won't start the
 * cross-fade out until [contentReady] is also true. This is what's actually
 * behind the app — startDestination is resolved asynchronously from
 * DataStore — so the splash never disappears onto a blank screen while that
 * read is still in flight; it just holds a beat longer.
 */
@Composable
fun MfukoSplash(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    contentReady: Boolean = true,
    onFinished: () -> Unit
) {
    val coins = if (darkTheme) DarkCoins else LightCoins
    val backgroundColor = if (darkTheme) Green900 else Green50
    val wordColor = if (darkTheme) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary

    val coinProgress = remember { coins.map { Animatable(0f) } }
    val wordmarkProgress = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    var entranceComplete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope {
            coinProgress.forEachIndexed { index, anim ->
                launch {
                    delay(COIN_STAGGER_MS * index)
                    anim.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = COIN_SPRING_STIFFNESS
                        )
                    )
                }
            }
            launch {
                delay(WORDMARK_DELAY_MS)
                wordmarkProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(WORDMARK_DURATION_MS, easing = FastOutSlowInEasing)
                )
            }
        }
        entranceComplete = true
    }

    LaunchedEffect(entranceComplete, contentReady) {
        if (entranceComplete && contentReady) {
            delay(MIN_HOLD_BEFORE_EXIT_MS)
            exitAlpha.animateTo(0f, tween(EXIT_DURATION_MS, easing = LinearEasing))
            onFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(exitAlpha.value)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(StackSize)) {
                val scale = size.width / VIEWBOX
                val riseOffsetPx = EntranceRise.toPx()

                coins.forEachIndexed { index, coin ->
                    val progress = coinProgress[index].value
                    val coinAlpha = progress.coerceIn(0f, 1f)
                    val scaleX = progress.coerceAtLeast(0.01f)
                    val pivot = Offset(CX * scale, coin.faceCy * scale)

                    withTransform({
                        translate(top = (1f - progress) * riseOffsetPx)
                        scale(scaleX = scaleX, scaleY = 1f, pivot = pivot)
                    }) {
                        drawOval(
                            color = coin.edgeColor,
                            topLeft = Offset(pivot.x - FACE_RX * scale, coin.edgeCy * scale - FACE_RY * scale),
                            size = Size(FACE_RX * 2 * scale, FACE_RY * 2 * scale),
                            alpha = coinAlpha
                        )
                        drawOval(
                            color = coin.faceColor,
                            topLeft = Offset(pivot.x - FACE_RX * scale, coin.faceCy * scale - FACE_RY * scale),
                            size = Size(FACE_RX * 2 * scale, FACE_RY * 2 * scale),
                            alpha = coinAlpha
                        )
                        drawOval(
                            color = coin.hlColor,
                            topLeft = Offset(pivot.x - HL_RX * scale, coin.hlCy * scale - HL_RY * scale),
                            size = Size(HL_RX * 2 * scale, HL_RY * 2 * scale),
                            alpha = coinAlpha * coin.hlAlpha
                        )
                    }
                }
            }

            Spacer(Modifier.height(WordmarkGap))

            val wordmarkAlpha = wordmarkProgress.value.coerceIn(0f, 1f)
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = wordColor,
                modifier = Modifier
                    .alpha(wordmarkAlpha)
                    .offset(y = ((1f - wordmarkAlpha) * EntranceRise.value).dp)
            )
        }
    }
}
