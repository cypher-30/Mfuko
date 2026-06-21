package com.chama.groupmoneymanager.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────
// Mfuko brand palette — see DESIGN_SYSTEM.md §3 for the full specification.
// Three tonal ramps (50 lightest → 900 deepest) plus a dedicated neutral
// "slate" ramp for the Pending status. Every value here is sourced directly
// from the design doc — do not edit a hex here without updating §3 there.
// ─────────────────────────────────────────────────────────────────────────

// ── Primary — Mfuko Forest (savings green) ────────────────────────────────
val Green50  = Color(0xFFEAF6EF)
val Green100 = Color(0xFFCDEBDA)
val Green200 = Color(0xFFA0D9BC)
val Green300 = Color(0xFF6CC097)
val Green400 = Color(0xFF3FA476)
val Green500 = Color(0xFF1F8059)
val Green600 = Color(0xFF146647)
val Green700 = Color(0xFF0F5132)
val Green800 = Color(0xFF0B3D26)
val Green900 = Color(0xFF082B1B)

// ── Secondary — Mfuko Gold (wealth / growth) ───────────────────────────────
val Gold50  = Color(0xFFFBF3DC)
val Gold100 = Color(0xFFF5E3AE)
val Gold200 = Color(0xFFEBCB6E)
val Gold300 = Color(0xFFDDB13D)
val Gold400 = Color(0xFFD4A017)
val Gold500 = Color(0xFFB98510)
val Gold600 = Color(0xFF96690C)
val Gold700 = Color(0xFF714F09)
val Gold800 = Color(0xFF4D3506)
val Gold900 = Color(0xFF2B1D03)

// ── Tertiary — Mfuko Clay (terracotta, warm accent) ────────────────────────
val Clay50  = Color(0xFFFBEAE2)
val Clay100 = Color(0xFFF3CAB6)
val Clay200 = Color(0xFFE8A480)
val Clay300 = Color(0xFFD87D52)
val Clay400 = Color(0xFFB35A2E)
val Clay500 = Color(0xFF954824)
val Clay700 = Color(0xFF5B2B14)
val Clay900 = Color(0xFF221007)

// ── Neutral — surfaces (warm green-tinted) ─────────────────────────────────
val NeutralSurfaceLight            = Color(0xFFFAFDF6)
val NeutralOnSurfaceLight          = Color(0xFF1A1C18)
val NeutralSurfaceVariantLight     = Color(0xFFE3E9DE)
val NeutralOnSurfaceVariantLight   = Color(0xFF44483D)
val NeutralSurfaceContainerLowestLight  = Color(0xFFFFFFFF)
val NeutralSurfaceContainerLowLight     = Color(0xFFF4F8EF)
val NeutralSurfaceContainerLight        = Color(0xFFEEF2E9)
val NeutralSurfaceContainerHighLight    = Color(0xFFE8ECE2)
val NeutralSurfaceContainerHighestLight = Color(0xFFE2E6DC)
val NeutralOutlineLight            = Color(0xFF74796E)
val NeutralOutlineVariantLight     = Color(0xFFC4CABB)
val NeutralInverseSurfaceLight     = Color(0xFF2F312B)
val NeutralInverseOnSurfaceLight   = Color(0xFFF1F2EA)

val NeutralSurfaceDark             = Color(0xFF12150F)
val NeutralOnSurfaceDark           = Color(0xFFE3E4DC)
val NeutralSurfaceVariantDark      = Color(0xFF44483D)
val NeutralOnSurfaceVariantDark    = Color(0xFFC4C8BA)
val NeutralSurfaceContainerLowestDark  = Color(0xFF0C0F0A)
val NeutralSurfaceContainerLowDark     = Color(0xFF1A1D16)
val NeutralSurfaceContainerDark        = Color(0xFF1E211A)
val NeutralSurfaceContainerHighDark    = Color(0xFF292C24)
val NeutralSurfaceContainerHighestDark = Color(0xFF34372E)
val NeutralOutlineDark              = Color(0xFF8E9388)
val NeutralOutlineVariantDark       = Color(0xFF44483D)
val NeutralInverseSurfaceDark       = Color(0xFFE3E4DC)
val NeutralInverseOnSurfaceDark     = Color(0xFF2F312B)

// ── Error (M3 role, kept distinct from the Danger status semantic below) ──
val ErrorLight             = Color(0xFFB3261E)
val OnErrorLight           = Color(0xFFFFFFFF)
val ErrorContainerLight    = Color(0xFFF9DEDC)
val OnErrorContainerLight  = Color(0xFF410E0B)

val ErrorDark              = Color(0xFFFFB4AB)
val OnErrorDark            = Color(0xFF690005)
val ErrorContainerDark     = Color(0xFF93000A)
val OnErrorContainerDark   = Color(0xFFFFDAD6)

// ── Slate — neutral status ramp (Pending) ──────────────────────────────────
val Slate50  = Color(0xFFEEF1F2)
val Slate100 = Color(0xFFD7DEE1)
val Slate400 = Color(0xFF5C6B73)
val Slate700 = Color(0xFF2E3A40)
val SlatePendingDotDark = Color(0xFF9FB0B6)

// ── Semantic status colors — back the shared StatusChip (DESIGN_SYSTEM §3.4,§6.6) ──
// Success reuses the primary (green) ramp; Warning/Neutral are dedicated; Danger reuses error.
data class StatusPalette(
    val container: Color,
    val onContainer: Color,
    val dot: Color
)

object MfukoStatusColors {
    val successLight = StatusPalette(container = Green100, onContainer = Green900, dot = Green600)
    val successDark  = StatusPalette(container = Green800, onContainer = Green100, dot = Green300)

    val warningLight = StatusPalette(container = Gold100, onContainer = Gold900, dot = Gold500)
    val warningDark  = StatusPalette(container = Gold800, onContainer = Gold100, dot = Gold300)

    val neutralLight = StatusPalette(container = Slate100, onContainer = Slate700, dot = Slate400)
    val neutralDark  = StatusPalette(container = Slate700, onContainer = Slate100, dot = SlatePendingDotDark)

    val dangerLight = StatusPalette(container = ErrorContainerLight, onContainer = OnErrorContainerLight, dot = ErrorLight)
    val dangerDark  = StatusPalette(container = ErrorContainerDark, onContainer = OnErrorContainerDark, dot = ErrorDark)
}
