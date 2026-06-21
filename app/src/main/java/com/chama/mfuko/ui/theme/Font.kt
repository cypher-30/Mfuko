package com.chama.mfuko.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.chama.mfuko.R

// ─────────────────────────────────────────────────────────────────────────
// Mfuko brand typefaces — DESIGN_SYSTEM.md §4.1.
//
// Both families are bundled as single variable-font files (OFL-1.1,
// sourced from github.com/google/fonts — see
// app/src/main/assets/font_licenses/) since the app is offline-first and
// cannot rely on a runtime/downloadable-font provider. Each weight we use
// is declared as a separate `Font()` entry against the same .ttf, selected
// via `FontVariation.Settings(FontVariation.weight(...))`. Variable-font
// axis selection requires API 26+; on API 24–25 the system renders the
// font's default static instance for every weight (graceful degradation,
// not a crash) since `minSdk = 24`.
// ─────────────────────────────────────────────────────────────────────────

/** Display / headline / title-large typeface — DESIGN_SYSTEM.md §4.2. */
@OptIn(ExperimentalTextApi::class)
val BricolageGrotesque = FontFamily(
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    )
)

/** Body / label / money typeface — DESIGN_SYSTEM.md §4.2–§4.3 (tabular numerals). */
@OptIn(ExperimentalTextApi::class)
val Inter = FontFamily(
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    )
)
