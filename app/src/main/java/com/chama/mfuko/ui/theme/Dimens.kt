package com.chama.mfuko.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing scale — DESIGN_SYSTEM.md §5.1. */
object MfukoSpacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
    val xxxl: Dp = 48.dp

    /** Standard screen horizontal padding (§5.1). */
    val screenHorizontal: Dp = lg
    /** Extra breathing room around hero/dashboard sections. */
    val screenHorizontalHero: Dp = xl
    /** Minimum tappable target size for every interactive element (§5.1). */
    val minTouchTarget: Dp = 48.dp
}

/** Elevation scale — DESIGN_SYSTEM.md §5.3. */
object MfukoElevation {
    val level0: Dp = 0.dp
    val level1: Dp = 1.dp
    val level2: Dp = 3.dp
    val level3: Dp = 6.dp
    val level4: Dp = 8.dp
}
