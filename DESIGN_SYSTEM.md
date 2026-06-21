# Mfuko Design System

**Version 1.0 — 2026-06-20**
A complete, decision-complete visual specification for Mfuko, a Kenyan chama (group savings &
lending) Android app. This document defines the look and feel for the full app re-skin. It is a
design specification only — no code lives here, but every value below is final and ready to be
implemented exactly as written.

---

## 1. Design principles & brand voice

Mfuko exists to make a deeply social, trust-based financial practice — the chama — feel as
credible on a phone as it does around a table of people who know each other's names. The visual
system is built on five principles:

1. **Money is legible first.** Every number that represents currency is the most visually
   confident thing on its screen: largest weight available in context, tabular figures, never
   competing with decoration. If a user has to squint at an amount, the design has failed.
2. **Warmth over coldness.** Generic fintech reaches for clinical blue and stark white because
   it signals "neutral system." Mfuko reaches for **deep green, raw gold, and sun-baked
   terracotta** — the colors of growth, harvest, and earth — because a chama is not a neutral
   system, it is a circle of people building something together.
3. **Trust through restraint.** Premium does not mean loud. One accent per screen, generous
   whitespace, confident typography, and motion that is felt rather than noticed. No gradients
   for their own sake, no decorative illustration that doesn't carry information.
4. **Communal, not corporate.** Rounded, soft geometry (large card radii, pill-shaped chips and
   nav indicators) over sharp institutional rectangles. The "nest" motif — woven, layered,
   protective — recurs quietly in the brand mark and empty states, never as a gimmick.
5. **Status at a glance.** A chama is run on social accountability — everyone needs to instantly
   see who has paid, who hasn't, and whose loan is in good standing. Status is never just a word;
   it always carries a color and a shape (the `StatusChip`).

**What "not generic fintech" means here, concretely:** no system-default sans-serif, no flat
single-tone brand color, no plain-text status labels, no stock spinner standing in for a
financial-health visualization, and no screen-by-screen inconsistency in app bars, elevation, or
currency formatting. Every one of those is a specific, named defect in the current build (Section
2) and every one has a specific fix in this document.

---

## 2. What the current UI gets wrong, and why

| # | Problem (current code) | Why it undermines trust / premium feel | Fix in this system |
|---|---|---|---|
| 1 | `ui/theme/Type.kt` sets every text style to `FontFamily.Default` (the OS system font). | The system font is the single strongest "this is a default Android app" signal there is. It reads as unfinished, not premium. | Two bundled brand typefaces — **Bricolage Grotesque** (display/headline) + **Inter** (body/data) — see §4. |
| 2 | `ui/theme/Color.kt` defines exactly one shade per role (`MfukoGreen40`, `MfukoGold40`, `MfukoClay40` + one dark-mode tint each). No tonal ramps. | Without a ramp, every container, hover/disabled state, and "light version of the brand color" has to be improvised or borrowed from raw M3 defaults — which is exactly how inconsistency (see #4, #5) creeps in. | Full 50→900 tonal ramps for green, gold, and terracotta, plus dedicated semantic ramps — see §3. |
| 3 | Contribution status (`MembersScreen.MemberCard`) and loan status (`LoanListScreen.LoanRequestCard`) are rendered as plain `Text("Status: Paid")` / `Text("Status: ${status.uppercase()}")`, distinguished only by bold weight or button visibility. | A chama's entire value proposition is "see who has and hasn't paid" at a glance. Burying that in plain text with no color coding forces the user to *read* every row instead of *scanning* it — the opposite of what a status system should do. | A single shared `StatusChip` component with a fixed color+icon per state, used everywhere status appears — see §6.6. |
| 4 | Currency formatting is inconsistent: `formatKes()` everywhere except `RepayLoanDialog.kt`, which uses a raw `"KES ${outstandingBalance.toInt()}"` (drops decimals, no thousands separator). | A savings app that can't consistently format money in its own repayment dialog reads as unfinished and, worse, untrustworthy with the one thing it exists to handle carefully. | One mandatory `MoneyText` component (wrapping `formatKes`) used for every monetary value, no exceptions — see §6.1, §10. |
| 5 | `WelcomeScreen` and `LoanListScreen` color their `TopAppBar` with `primaryContainer`; every other screen (`Settings`, `CreateNestSuccess`, `BottomNavScaffold`) uses default `TopAppBar` colors. | Inconsistent chrome makes the app feel like several different apps stitched together — small, but it's the kind of detail that erodes "premium" fastest because it's visible on *every* screen transition. | One unified `TopAppBar` treatment, defined once — see §6.5. |
| 6 | `SettingsScreen.kt` hardcodes `Color(0xFF2E7D32)` for its "Password updated." success state instead of using a theme token. | A hardcoded color can't adapt to dark mode, can't be retuned for contrast, and is invisible to anyone auditing the theme — it's a silent crack in an otherwise themed app. | A semantic `success` token in the color scheme, used everywhere a success state needs green — see §3.4. |
| 7 | The "Contribution Progress" ring on Home and the "Financial Health" gauge are both the stock Material 3 `CircularProgressIndicator` — a generic loading-spinner shape repurposed as a data visualization. | A financial-health score is the single most important piece of information in the app; presenting it in the same shape Android uses for "please wait" actively undersells it. | A custom-drawn `HealthGauge` (Canvas arc, banded color, animated sweep + count-up) purpose-built as a data visualization, not a borrowed spinner — see §6.7. |
| 8 | There is no brand mark anywhere except the word "Mfuko" set in `displaySmall` bold on the login screen only (not on Register, not in the app bar). | A financial product without a consistent mark looks unbranded and provisional — exactly wrong for something asking people to trust it with their group's money. | A defined wordmark + simple symbol, with usage rules — see §8. |
| 9 | No `RoundedCornerShape` is set anywhere in the codebase — every card, button, chip, and dialog uses the unmodified Material 3 default shape. | Default shapes are correct but anonymous — they don't carry any of the "soft, communal, woven" character the brand needs (see Principle 4). | A defined shape scale, generously rounded — see §5.2. |
| 10 | The invite-code display on `CreateNestSuccessScreen` hardcodes `fontSize = 48.sp, letterSpacing = 8.sp` directly on a `Text`, bypassing the type scale entirely — the only "custom" type treatment in the app, and it isn't part of any system. | A one-off magic-number style is impossible to keep consistent or reuse, and signals nothing was designed on purpose — it just happened. | A named `displayCode` style, defined once and used for every code-like display — see §4.3. |

---

## 3. Color palette

All ramps below run light → dark as **50 (lightest tint) → 900 (deepest tone)**. Hex values are
exact and final.

### 3.1 Primary — Mfuko Forest (savings green)

| Token | Hex | Use |
|---|---|---|
| green-50 | `#EAF6EF` | Lightest container tint |
| green-100 | `#CDEBDA` | Light-theme `primaryContainer` |
| green-200 | `#A0D9BC` | Hover/disabled tints |
| green-300 | `#6CC097` | **Dark-theme `primary`**, `inversePrimary` (light) |
| green-400 | `#3FA476` | Mid accent |
| green-500 | `#1F8059` | Brand mid-tone (illustration, charts) |
| green-600 | `#146647` | Status-success icon/dot color |
| green-700 | `#0F5132` | **Light-theme `primary`** |
| green-800 | `#0B3D26` | **Dark-theme `primaryContainer`**, `inversePrimary` (dark) |
| green-900 | `#082B1B` | Light-theme `onPrimaryContainer`, dark-theme `onPrimary` |

### 3.2 Secondary — Mfuko Gold (wealth / growth)

| Token | Hex | Use |
|---|---|---|
| gold-50 | `#FBF3DC` | Lightest container tint |
| gold-100 | `#F5E3AE` | Light-theme `secondaryContainer` |
| gold-200 | `#EBCB6E` | Hover/disabled tints |
| gold-300 | `#DDB13D` | **Dark-theme `secondary`** |
| gold-400 | `#D4A017` | Brand mid-tone (legacy brand gold, kept for illustration use) |
| gold-500 | `#B98510` | Status-warning icon/dot color |
| gold-600 | `#96690C` | **Light-theme `secondary`** |
| gold-700 | `#714F09` | **Dark-theme `secondaryContainer`** |
| gold-800 | `#4D3506` | Status-warning container (dark) |
| gold-900 | `#2B1D03` | Light-theme `onSecondaryContainer`, dark-theme `onSecondary` |

### 3.3 Tertiary — Mfuko Clay (terracotta, warm accent)

| Token | Hex | Use |
|---|---|---|
| clay-50 | `#FBEAE2` | Lightest container tint |
| clay-100 | `#F3CAB6` | Light-theme `tertiaryContainer` |
| clay-200 | `#E8A480` | **Dark-theme `tertiary`** |
| clay-300 | `#D87D52` | Mid accent |
| clay-400 | `#B35A2E` | Brand mid-tone (legacy brand clay) |
| clay-500 | `#954824` | **Light-theme `tertiary`** |
| clay-700 | `#5B2B14` | **Dark-theme `tertiaryContainer`** |
| clay-900 | `#221007` | Light-theme `onTertiaryContainer`, dark-theme `onTertiary` |

### 3.4 Semantic status colors

These four pairs back every `StatusChip` (§6.6) and any other state-coded UI. Success deliberately
reuses the primary ramp (savings green *is* the brand's "good" color); the other three are
dedicated.

| State | Meaning | Light container / on-container | Dark container / on-container | Dot/icon (light / dark) |
|---|---|---|---|---|
| **Success** | paid, active loan, approved | `#CDEBDA` (green-100) / `#082B1B` (green-900) | `#0B3D26` (green-800) / `#CDEBDA` (green-100) | `#146647` / `#6CC097` |
| **Warning** | partial payment | `#F5E3AE` (gold-100) / `#2B1D03` (gold-900) | `#4D3506` (gold-800) / `#F5E3AE` (gold-100) | `#B98510` / `#DDB13D` |
| **Neutral** | pending (loan awaiting decision) | `#D7DEE1` (slate-100) / `#2E3A40` (slate-700) | `#2E3A40` (slate-700) / `#D7DEE1` (slate-100) | `#5C6B73` / `#9FB0B6` |
| **Danger** | overdue, rejected, unpaid | `#F9DEDC` (= `errorContainer`) / `#410E0B` (= `onErrorContainer`) | `#93000A` (= `errorContainer`) / `#FFDAD6` (= `onErrorContainer`) | `#B3261E` / `#FFB4AB` |

Slate (neutral) ramp, defined for completeness: `slate-50 #EEF1F2`, `slate-100 #D7DEE1`,
`slate-400 #5C6B73`, `slate-700 #2E3A40`.

### 3.5 Full Material 3 ColorScheme mapping

**Light scheme**

| Role | Hex | Role | Hex |
|---|---|---|---|
| primary | `#0F5132` | onPrimary | `#FFFFFF` |
| primaryContainer | `#CDEBDA` | onPrimaryContainer | `#082B1B` |
| secondary | `#96690C` | onSecondary | `#FFFFFF` |
| secondaryContainer | `#F5E3AE` | onSecondaryContainer | `#2B1D03` |
| tertiary | `#954824` | onTertiary | `#FFFFFF` |
| tertiaryContainer | `#F3CAB6` | onTertiaryContainer | `#221007` |
| background | `#FAFDF6` | onBackground | `#1A1C18` |
| surface | `#FAFDF6` | onSurface | `#1A1C18` |
| surfaceVariant | `#E3E9DE` | onSurfaceVariant | `#44483D` |
| surfaceContainerLowest | `#FFFFFF` | surfaceContainerLow | `#F4F8EF` |
| surfaceContainer | `#EEF2E9` | surfaceContainerHigh | `#E8ECE2` |
| surfaceContainerHighest | `#E2E6DC` | outline | `#74796E` |
| outlineVariant | `#C4CABB` | inverseSurface | `#2F312B` |
| inverseOnSurface | `#F1F2EA` | inversePrimary | `#6CC097` |
| error | `#B3261E` | onError | `#FFFFFF` |
| errorContainer | `#F9DEDC` | onErrorContainer | `#410E0B` |
| scrim | `#000000` | — | — |

**Dark scheme**

| Role | Hex | Role | Hex |
|---|---|---|---|
| primary | `#6CC097` | onPrimary | `#082B1B` |
| primaryContainer | `#0B3D26` | onPrimaryContainer | `#CDEBDA` |
| secondary | `#DDB13D` | onSecondary | `#2B1D03` |
| secondaryContainer | `#714F09` | onSecondaryContainer | `#F5E3AE` |
| tertiary | `#E8A480` | onTertiary | `#221007` |
| tertiaryContainer | `#5B2B14` | onTertiaryContainer | `#F3CAB6` |
| background | `#12150F` | onBackground | `#E3E4DC` |
| surface | `#12150F` | onSurface | `#E3E4DC` |
| surfaceVariant | `#44483D` | onSurfaceVariant | `#C4C8BA` |
| surfaceContainerLowest | `#0C0F0A` | surfaceContainerLow | `#1A1D16` |
| surfaceContainer | `#1E211A` | surfaceContainerHigh | `#292C24` |
| surfaceContainerHighest | `#34372E` | outline | `#8E9388` |
| outlineVariant | `#44483D` | inverseSurface | `#E3E4DC` |
| inverseOnSurface | `#2F312B` | inversePrimary | `#0F5132` |
| error | `#FFB4AB` | onError | `#690005` |
| errorContainer | `#93000A` | onErrorContainer | `#FFDAD6` |
| scrim | `#000000` | — | — |

All pairs meet WCAG AA (≥4.5:1) for body text at their intended size; container/on-container pairs
are verified for the 14–16sp range used in chips and labels.

---

## 4. Typography

### 4.1 Typefaces

- **Bricolage Grotesque** — a contemporary display grotesque with real character (subtle ink-trap
  detailing at small optical sizes, confident at large ones). Used for the wordmark and every
  display/headline/title-large slot — i.e., everything that should feel *designed*, not *typed*.
  SIL Open Font License, bundled as static `.ttf` weights (no runtime font fetch — the app is
  offline-first).
- **Inter** — the workhorse. Used for every titleMedium-and-below slot, including all monetary
  figures, because it has excellent legibility at small sizes and **tabular figures**
  (`fontFeatureSettings = "tnum"`), which is mandatory for any UI where numbers stack in a column
  (member lists, transaction history, repayment schedules) and must align.

### 4.2 Full type scale

| M3 slot | Family | Weight | Size / Line height | Letter spacing | Typical use |
|---|---|---|---|---|---|
| displayLarge | Bricolage Grotesque | SemiBold (600) | 57 / 64 sp | -0.25 sp | reserved (splash) |
| displayMedium | Bricolage Grotesque | SemiBold (600) | 45 / 52 sp | 0 | reserved |
| displaySmall | Bricolage Grotesque | SemiBold (600) | 36 / 44 sp | 0 | "Mfuko" wordmark (auth screens) |
| headlineLarge | Bricolage Grotesque | SemiBold (600) | 32 / 40 sp | 0 | reserved |
| headlineMedium | Bricolage Grotesque | Medium (500) | 28 / 36 sp | 0 | "Congratulations!" success screen |
| headlineSmall | Bricolage Grotesque | Medium (500) | 24 / 32 sp | 0 | dialog titles |
| titleLarge | Bricolage Grotesque | Medium (500) | 22 / 28 sp | 0 | screen section titles ("Contribution Progress", "Nest Settings") |
| titleMedium | Inter | SemiBold (600) | 16 / 24 sp | 0.15 sp | card titles (member name, loan card) |
| titleSmall | Inter | SemiBold (600) | 14 / 20 sp | 0.1 sp | sub-card titles |
| bodyLarge | Inter | Regular (400) | 16 / 24 sp | 0.5 sp | primary body text |
| bodyMedium | Inter | Regular (400) | 14 / 20 sp | 0.25 sp | secondary body text |
| bodySmall | Inter | Regular (400) | 12 / 16 sp | 0.4 sp | captions, helper text |
| labelLarge | Inter | Medium (500) | 14 / 20 sp | 0.1 sp | button labels |
| labelMedium | Inter | Medium (500) | 12 / 16 sp | 0.5 sp | nav bar labels, chip labels |
| labelSmall | Inter | Medium (500) | 11 / 16 sp | 0.5 sp | timestamps, smallest captions |

### 4.3 Money & code styles (non-M3, custom slots)

All monetary text uses Inter with **tabular figures** (`tnum`) and SemiBold weight, at three sizes:

| Style | Family / weight | Size / line height | Use |
|---|---|---|---|
| `moneyLarge` | Inter SemiBold, tabular | 32 / 40 sp | hero balance figure |
| `moneyMedium` | Inter SemiBold, tabular | 20 / 28 sp | card totals (loan outstanding, collected) |
| `moneySmall` | Inter SemiBold, tabular | 16 / 24 sp | inline figures (member rows) |
| `displayCode` | Bricolage Grotesque SemiBold | 40 / 48 sp, letter-spacing 6 sp | invite code display — replaces the current hardcoded `48.sp`/`8.sp` one-off |

---

## 5. Spacing, layout & shape

### 5.1 Spacing scale

Named tokens, dp: `xs=4, sm=8, md=12, lg=16, xl=24, xxl=32, xxxl=48`.

- **Screen horizontal padding:** `lg` (16dp) — minimum; hero/dashboard sections may use `xl` (24dp)
  for extra breathing room around the hero card.
- **Card internal padding:** `lg` (16dp) for standard cards, `xl` (24dp) for the hero/balance card.
- **List item vertical gap:** `sm` (8dp) for dense lists (notifications), `md` (12dp) for member/
  loan cards.
- **Section gap (between major dashboard blocks):** `xl` (24dp).
- **Minimum touch target:** 48dp × 48dp on every tappable element (icon buttons, chips, list rows).

### 5.2 Shape scale

| Token | Radius | Use |
|---|---|---|
| `extraSmall` | 8dp | status chips, small badges |
| `small` | 12dp | text fields, FilterChips |
| `medium` | 16dp | standard cards, buttons |
| `large` | 24dp | hero/balance card, stat cards, member/loan cards |
| `extraLarge` | 28dp | dialogs, bottom sheets |
| `full` | 50% | avatar circles, FAB, pill-shaped nav indicator |

This replaces the current "no `RoundedCornerShape` anywhere" default with a deliberately generous,
soft geometry consistent with Principle 4.

### 5.3 Elevation

| Level | dp | Use |
|---|---|---|
| 0 | 0dp | flat list rows, chips |
| 1 | 1dp | standard cards (member/loan/notification cards) |
| 2 | 3dp | emphasized cards (manager overview, health gauge card) |
| 3 | 6dp | hero/balance card |
| 4 | 8dp | dialogs, bottom sheets (M3 default) |

---

## 6. Components

Every component below replaces a specifically audited piece of the current UI (Section 2) or, where
marked **[NEW — spec only]**, defines a control required by `docs/FEATURES.md` that does not yet
exist in the codebase.

### 6.1 Money display — `MoneyText`

Wraps the existing `formatKes()` utility (kept as-is — only its callers and styling change).
Renders the `KES` prefix in `bodySmall`/`labelMedium` weight at 70% the opacity of the figure, and
the numeric value in the appropriate `money*` style (§4.3) with tabular figures. **Every** monetary
value in the app — including `RepayLoanDialog`'s outstanding balance, which today bypasses
`formatKes()` entirely — renders through this one component. No exceptions.

### 6.2 Buttons

| Variant | Shape | Height | Color | Use |
|---|---|---|---|---|
| Primary (filled) | `medium` (16dp) | 52dp | `primary` / `onPrimary` | main actions ("Log In", "Pay now", "Approve", "Save") |
| Secondary (outlined) | `medium` (16dp) | 52dp | `outline` border, `onSurface` text | secondary actions ("Continue as Demo", "Cancel" in full-width contexts) |
| Text | — | 40dp min | `primary` text | low-emphasis actions ("Already have an account? Log in") |
| Destructive | `medium` (16dp), filled or text | 52dp / 40dp | `error` / `onError` (filled) or `error` text (text variant) | "Remove", "Reject" |
| Icon button | `full` | 48dp | `onSurfaceVariant`, `primary` when active | app-bar/card actions (share, copy, settings, megaphone) |

All full-width buttons are `Modifier.fillMaxWidth().height(52dp)`. In-button loading state replaces
the label with a 22dp `CircularProgressIndicator` in the button's "on" color, `strokeWidth = 2.dp`
— this pattern already exists in the codebase and is kept, just restyled to the new color.

### 6.3 Inputs

`OutlinedTextField`, shape `small` (12dp), border `outline` (default) / `primary` (focused) /
`error` (error state). Label uses `bodyMedium`; supporting/error text uses `bodySmall` in
`onSurfaceVariant` / `error`. Numeric fields (amount, term, phone) right-pad for a leading "KES"
affix where currency is being entered, rendered in `bodyMedium` `onSurfaceVariant` — this is new:
today amount fields are bare numeric inputs with no currency affix.

### 6.4 Cards

| Variant | Shape | Elevation | Background | Use |
|---|---|---|---|---|
| Standard | `medium` (16dp) | 1 | `surfaceContainerLow` | member card, loan request card, notification card |
| Emphasized | `large` (24dp) | 2 | `surfaceContainer` | manager overview card, health gauge card |
| Hero | `large` (24dp) | 3 | `primary` (gradient-free flat fill) with a 2dp `secondary` (gold) top accent rule | dashboard balance/contribution hero |
| Alert | `medium` (16dp) | 1 | `errorContainer` | unpaid-penalty card |

This collapses today's inconsistent elevation values (2dp/4dp/default, no shape) into four named,
reusable variants.

### 6.5 Navigation

- **TopAppBar — one treatment, everywhere:** `surface` background, `onSurface` title in
  `titleLarge` (Bricolage), no `primaryContainer` exceptions. Back/menu icons `onSurfaceVariant`.
  This directly fixes defect #5 (Section 2).
- **NavigationBar (bottom):** `surfaceContainer` background. Selected item: icon in `primary`
  inside a `full`-shape pill indicator filled with `primaryContainer`; label in `labelMedium`
  `primary`. Unselected: outlined icon variant (already implemented via the existing
  filled⇄outlined swap pattern in `BottomNavScaffold`), `onSurfaceVariant`. Tab labels render in
  Inter (`labelMedium`), not Bricolage — bottom-nav labels are functional text, not display text.
- **Profile drawer (`ModalDrawerSheet`):** background `surfaceContainerLow`. Header: a `full`-shape
  44dp avatar circle in `primaryContainer` with the user's initial in `titleMedium`
  `onPrimaryContainer` (replacing the generic `Icons.Default.Person` placeholder), then name
  (`titleMedium`), nest name (`bodyMedium`, `onSurfaceVariant`), role (`bodySmall`, `primary`,
  uppercase, letter-spacing 0.5sp — small "badge-style" treatment instead of plain grey text).
  `NavigationDrawerItem`s keep their existing icons (`SwapHoriz`, `Settings`, `ExitToApp`) styled
  with `onSurfaceVariant` icon/text, `primary` when selected.

### 6.6 Status indicator — `StatusChip` **[shared component, used everywhere status appears]**

A single pill-shaped (`full`) chip, height 24dp, horizontal padding `sm` (8dp), containing a 6dp
colored dot + `labelMedium` text, using the container/on-container/dot triple from §3.4:

| Chip state | Label text | Semantic |
|---|---|---|
| Paid | "Paid" | Success |
| Partial | "Partial" | Warning |
| Unpaid | "Unpaid" | Danger |
| Pending | "Pending" | Neutral |
| Active | "Active" | Success |
| Rejected | "Rejected" | Danger |

This replaces **every** plain `Text("Status: …")` instance in `MembersScreen.MemberCard` and
`LoanListScreen.LoanRequestCard` — status is never rendered as unstyled text again.

### 6.7 Financial health display — `HealthGauge`

Replaces the stock `CircularProgressIndicator` in `HomeScreen.HealthScoreCard`. A custom
`Canvas`/`drawArc` ring:

- 270° arc (8 o'clock → 4 o'clock, leaving a 90° gap at the bottom), 64dp diameter, 8dp stroke,
  `StrokeCap.Round`.
- Track color: `surfaceVariant`. Progress color: banded by score exactly as today's thresholds
  (`≥70` → success green `primary`; `≥40` → warning gold `secondary`; else → danger `error`).
  the band color also fills the centered score number.
- Centered content: score number in `titleLarge` (Bricolage, SemiBold) over a `labelSmall` band
  word ("Good" / "Fair" / "Needs attention") directly beneath, both in the band color.
- Animation: see §9.2.

The **Contribution Progress** ring on Home (`ContributionProgressCircle`) uses the same `HealthGauge`
primitive at 96dp diameter, always in `primary` (it's a completion ring, not a banded score), with
the existing "Paid: X / Y" and "Due: date" text kept beneath it.

### 6.8 Loan progress — `LoanProgressCard`

Replaces the plain `LoanCard`. Emphasized card (§6.4) containing: title "Active Loan" (`titleMedium`),
a `StatusChip` (Active), the outstanding balance in `moneyLarge` via `MoneyText`, a thin horizontal
progress bar (`LinearProgressIndicator`, shape `full`, height 6dp, track `surfaceVariant`, progress
`primary`) showing `repaid / totalRepayable`, a due-date caption (`bodySmall`, `onSurfaceVariant`),
and the "Make a Payment" button (§6.2 primary) right-aligned beneath.

### 6.9 Dialogs & sheets

All dialogs: shape `extraLarge` (28dp), title in `headlineSmall` (Bricolage Medium), body content in
`bodyMedium`/inputs per §6.3, primary action as filled button, secondary as text button — consistent
with M3 `AlertDialog` defaults but with the new shape/type/color. The M-Pesa payment sequence
(`ActivityScreen`) is a single designed flow:
1. **Form** — amount field with KES affix, "Pay now (M-Pesa)" primary button.
2. **Sending** — centered `HealthGauge`-style indeterminate ring (not the bare default spinner) +
   "STK push sent — check your phone..." in `bodyLarge`.
3. **Receipt (success)** — a success-green circular check icon (see §9.1), "Payment Successful" in
   `headlineSmall`, a standard card listing Amount (`MoneyText`)/Ref/Date in `bodyMedium` label +
   value pairs, "Done" primary button.
4. **Failed** — danger-red error icon, "Payment Failed" in `headlineSmall`, message in `bodyMedium`,
   "Try Again" (primary) + "Cancel" (text button, not filled — today both are filled buttons, which
   wrongly implies equal weight).

### 6.10 State views (`StateViews.kt`)

- `LoadingView`: centered `HealthGauge`-style indeterminate ring in `primary`, replacing the bare
  default spinner, with optional caption text below in `bodyMedium`/`onSurfaceVariant`.
- `ErrorView`: a centered danger-colored icon (`Icons.Outlined.ErrorOutline`, `error`), message in
  `bodyLarge`, "Retry" as a secondary (outlined) button.
- `EmptyStateView`: a centered outline icon themed per context (e.g. `Icons.Outlined.SavingsAlt`-style
  glyph for "no contributions", `Icons.Outlined.Groups` for "no members") in `onSurfaceVariant` at
  48dp, message in `bodyLarge`/`onSurfaceVariant` beneath — replacing today's bare text-only empty
  state.

### 6.11 List items

Standard list row (members, notifications, loan requests): `medium` shape card, `lg` (16dp) padding,
leading content (avatar/icon) → `weight(1f)` text column (title `titleMedium`/Inter, subtitle
`bodyMedium`/`onSurfaceVariant`) → trailing `StatusChip` and/or icon actions, in that fixed order,
applied consistently across every list in the app (today each list improvises its own row layout).

### 6.12 Required-but-missing controls **[NEW — spec only, not built in this visual pass]**

These appear in `docs/FEATURES.md` but have no corresponding UI yet. They are specified here so a
future build slots directly into this system without new design decisions:

- **Transaction history list item** — for the Activity page's "Transaction history" section
  (contributions + loans in one feed). Uses the standard list item (§6.11) with a leading
  direction icon (inbound arrow for contributions, outbound for loan disbursement, in `primary`/
  `tertiary` respectively), `MoneyText` trailing amount, `bodySmall` timestamp subtitle.
- **Contribution schedule row** — "upcoming date + due amount" strip for the Activity page, a
  compact standard card showing the next due date (`titleMedium`) and amount (`moneyMedium`) with
  a `bodySmall` "Cycle ends in N days" caption.
- **Loan reason input** — an optional third field in `RequestLoanDialog`, a multi-line
  `OutlinedTextField` (`minLines = 2`, label "Reason (optional)") between Term and the error text.
- **Member sort/filter control** — a `FilterChip` row above the member list (`All / Paid / Unpaid /
  Partial`), styled per §6.3's chip treatment, `sm` (8dp) gap between chips.
- **Send reminder to unpaid members** — an icon button (`Icons.Outlined.NotificationsActive`) in
  `ManagerOverviewCard`'s existing action row (alongside Announcement/Export/Settings), opening a
  confirmation dialog ("Remind N unpaid members?").
- **Switch-nest control** — already wired (drawer → Welcome screen); no new control needed, only
  restyled per §6.5.

---

## 7. Iconography

- **Icon set:** Material Symbols, via the already-included `material-icons-extended` artifact —
  no new icon dependency required.
- **Sizing:** 24dp standard (nav, app bar, list actions), 20dp inline (inside chips/badges), 48dp
  for empty-state hero icons.
- **Style rule (selection state):** wherever an icon represents a navigable/selectable item, use
  the **outlined** variant unselected and the **filled** variant selected — the pattern already
  built for the bottom nav (`Icons.Outlined.Home` ↔ `Icons.Filled.Home`) is the house style and
  should extend to filter chips and any future tab-like control.
- **Style rule (action icons):** standalone action icons (share, copy, settings, megaphone, add,
  remove) always use the **filled** variant — they're commands, not toggles, so there's no
  unselected state to distinguish.
- **Color:** `onSurfaceVariant` by default, `primary` when representing an active/positive state,
  `error` for destructive actions (remove member, reject loan).
- **Current inventory kept as-is, restyled by the rules above:** `Campaign`, `Share`, `Settings`,
  `ContentCopy`, `AddCard`, `PersonRemove`, `CheckCircle`, `Error`, `Person` (replaced by the avatar
  initial, §6.5), `SwapHoriz`, `Group`/`Groups`, `Home`, `Notifications`, `ListAlt`, `ArrowBack`,
  `ExitToApp`.

---

## 8. Brand mark

- **Wordmark:** "Mfuko" set in `displaySmall` (Bricolage Grotesque SemiBold, 36sp) in `primary`
  (light) / `onSurface` (dark, for contrast on dark backgrounds where green-on-dark-green would be
  low-contrast — use `green-300` specifically). Always lowercase "f-u-k-o" following "M" — never
  all-caps, never italicized.
- **Symbol (for app icon / splash only, not used inline in-app):** a simple geometric mark of three
  overlapping, slightly-offset rounded arcs (like woven basket strands, or three coins fanned)
  forming a loose nest shape, rendered in `green-700` with a single `gold-400` strand as accent —
  echoes Principle 4 (woven, communal) without becoming literal basket clip-art.
- **Clear space:** minimum clear space around the wordmark = the cap-height of the "M".
- **Minimum size:** wordmark never set smaller than `titleLarge` (22sp) in any UI context.
- **Usage:** wordmark appears on Login and Register (today only on Login — fixed in the re-skin),
  and may appear in a splash/launch moment; it does **not** appear in the in-app `TopAppBar` (the
  app bar uses the current screen's title instead, per §6.5) to avoid redundant branding once a
  user is past onboarding.

---

## 9. Micro-interactions

Exactly five specified moments. Each is precise enough to implement without further
interpretation.

### 9.1 Contribution payment success (M-Pesa receipt reveal)
**Trigger:** `PaymentStage` transitions `SendingStkPush → Success`.
**Sequence:**
1. The indeterminate ring (§6.9 step 2) fades out — `alpha 1f→0f`, 150ms, `LinearEasing`.
2. A `CheckCircle` icon scales in from `0.6f → 1.05f → 1.0f` — `tween(450ms)`, the overshoot
   segment using `OvershootInterpolator`-equivalent (`Easing { it }` swapped for a spring:
   `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)`),
   tinted `primary`, while simultaneously fading in (`alpha 0f→1f`, 250ms).
3. 80ms after the icon settles, the receipt `Card` slides up + fades in together:
   `translationY 24dp→0dp` + `alpha 0f→1f`, `tween(300ms, easing = FastOutSlowInEasing)`.
**Intent:** the success state should feel *arrived at*, not just swapped in — a small built
moment of reassurance for a payment flow handling someone's real contribution.

### 9.2 Financial-health ring fill on dashboard load
**Trigger:** `HomeScreen` dashboard data becomes available (existing `animateFloatAsState` call
in `HealthScoreCard`, kept and extended).
**Sequence:** the `HealthGauge` arc sweeps from `0f` to the target fraction over `tween(1000ms,
easing = FastOutSlowInEasing)` (the existing 1000ms duration is kept — only the easing curve and
the visual it drives change). Simultaneously, the centered score number **counts up** from 0 to
the final integer score over the same 1000ms window, using an `Int` derived from the same
animated `Float` (`(animatedFraction * targetScore).roundToInt()`) — not a separate animation, so
the number and the ring arrive in lockstep.
**Intent:** turns a static number into a small moment of "watching your standing build up,"
appropriate for a score whose entire purpose is to feel earned.

### 9.3 Status chip transition (Partial → Paid)
**Trigger:** a member's contribution status flips after `recordContribution` succeeds and the
member list recomposes with updated data.
**Sequence:** the `StatusChip`'s container color cross-fades from the Warning pair to the Success
pair — `animateColorAsState(targetValue = newContainerColor, animationSpec = tween(300ms))` for
container and content color together — while the dot performs a single quick scale pulse
(`1.0f → 1.4f → 1.0f`, `tween(300ms)`, synced to the color fade's midpoint).
**Intent:** the chip should never just "snap" to a new state — a payment landing is a small win
worth a half-second of visible motion.

### 9.4 Loan approval action
**Trigger:** a manager taps "Approve" on a `LoanRequestCard` (pending state).
**Sequence:** the button content swaps to a 22dp spinner (existing pattern) for the duration of
the repository call; on success, the entire card performs a brief **state-change ripple**: a
radial `primaryContainer`-tinted overlay expands from the button's tap origin to fill the card
bounds (`tween(350ms, easing = LinearOutSlowInEasing)`) and fades out (`alpha 1f→0f` over the
trailing 150ms of the same animation), after which the card recomposes with its new `StatusChip`
(Active) and the Approve/Reject row removed.
**Intent:** the ripple visually "confirms the source" of the change — the approval clearly came
from this card, this action — before the card settles into its new, calmer state.

### 9.5 Dashboard refresh affordance (pull-to-refresh / tab re-select)
**Trigger:** user pulls down on the Home dashboard, or re-taps the already-selected "Home" bottom
nav item (addressing the known gap where Home doesn't auto-refresh on tab re-selection).
**Sequence:** a compact circular refresh indicator (reusing the `HealthGauge` ring primitive at
32dp, indeterminate banded `primary` sweep) appears pinned just below the app bar, scaling in
(`0.8f→1f`, `tween(150ms)`); on data arrival it performs one full quick rotation
(`360°`, `tween(400ms, easing = LinearEasing)`) and scales out (`1f→0.8f` + `alpha→0f`, `tween(200ms)`)
simultaneously with the dashboard content cross-fading to its refreshed values
(`alpha 0.6f→1f`, `tween(250ms)`, no skeleton — the existing layout stays in place and just
updates its numbers).
**Intent:** gives the user explicit, branded confirmation that "yes, this refreshed" — directly
mitigating the documented staleness gap by making a manual refresh feel deliberate and complete,
not silent.

---

## 10. Implementation mapping

Guidance only — no code is written as part of this document.

| System element | Lands in |
|---|---|
| Font files (Bricolage Grotesque, Inter `.ttf`) | `app/src/main/res/font/` |
| Color ramps + semantic tokens | `ui/theme/Color.kt` |
| `Typography` (§4.2) + money/code styles (§4.3) | `ui/theme/Type.kt` |
| Full light/dark `ColorScheme` (§3.5) | `ui/theme/Theme.kt` |
| Shape scale (§5.2) | new `ui/theme/Shape.kt`, wired into `MaterialTheme(shapes = …)` in `Theme.kt` |
| Spacing/elevation tokens (§5.1, §5.3) | new `ui/theme/Dimens.kt` |
| Window background / splash hex | `res/values/colors.xml`, `res/values/themes.xml` |
| `MoneyText`, `StatusChip`, `HealthGauge`, `LoanProgressCard`, hero/stat card variants | new `ui/components/` package |
| Restyled loading/error/empty states | `ui/util/StateViews.kt` |
| Per-screen application of all of the above | every file under `ui/features/**` — visual properties only, no logic/state/navigation changes |
| Follow-up not in this pass (needs new logic, not just styling) | transaction history list, contribution schedule row, loan-reason field, member sort/filter, send-reminder action (§6.12) |
