package com.chama.mfuko.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────
// Mfuko shape scale — DESIGN_SYSTEM.md §5.2. Generously rounded, soft
// geometry (Principle 4: "communal, not corporate") replacing the
// unmodified M3 default shapes used everywhere in the previous UI.
// ─────────────────────────────────────────────────────────────────────────
val MfukoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/** The "full" (pill / circle) shape from §5.2 — not part of [androidx.compose.material3.Shapes],
 *  applied directly where used (status chips, avatars, FABs, nav indicators). */
val MfukoFullShape = CircleShape
