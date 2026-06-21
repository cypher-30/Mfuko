# Mfuko — App Icon & Launch Animation Redesign Brief

**Status:** Draft v1.0 — 2026-06-21
**Scope note:** Mfuko already has a decision-complete visual system in [`DESIGN_SYSTEM.md`](DESIGN_SYSTEM.md) (colors, type, shape, spacing, component specs). This brief does **not** override that document — it is the source of truth for every token referenced below. This brief's job is the two pieces that document leaves unresolved: the **app icon** (currently the unmodified Android Studio template) and the **launch/splash animation** (currently nonexistent).

---

## App Identity

- **App name:** Mfuko ("fund / wallet" in Swahili)
- **Platform:** Android (`applicationId com.chama.mfuko`, minSdk 24, targetSdk 35)
- **Primary purpose:** Offline-first manager for Kenyan-style *chama* groups — shared savings circles ("Nests") where members contribute on a monthly cycle and can request loans from the collective pool, with automatic interest calculation and manager oversight. No backend required to operate.
- **Target audience:** Members and managers of informal/semi-formal group savings circles in Kenya (and similar chama-style arrangements elsewhere) — a primarily mobile-first, trust-and-social-accountability driven user base, not a generic personal-finance audience.

---

## Current State

### Icon
The app ships the **unmodified Android Studio default template icon**:
- Adaptive icon background: flat `#000000` black (`res/values/ic_launcher_background.xml`)
- Adaptive icon foreground: the stock Android Studio "ruler grid + Android logomark" vector (`res/drawable/ic_launcher_foreground.xml`), filled `#3DDC84` (Android brand green — coincidentally close to, but not derived from, Mfuko's own brand green)
- No relationship to the Mfuko brand whatsoever; this is the single most visible "default/unfinished app" signal on the Play Store listing and home screen.

### Launch animation
**None exists.** There is no splash screen, no `SplashScreen` API usage, and no animated launch moment — the app currently cold-starts directly into the system default white/blank window before the first Compose frame renders. `DESIGN_SYSTEM.md` §4.2 reserves `displayLarge` "for splash" and §8 notes the wordmark "may appear in a splash/launch moment," but no concrete spec exists until this document.

---

## Redesign Goals

### Visual identity direction
No new direction is being introduced — this redesign **executes** the brand voice already defined in `DESIGN_SYSTEM.md` §1 for the two surfaces (icon, launch) that predate that system:
- **Mood:** warm, grounded, communal — "trust around a table," not clinical fintech.
- **Personality:** restrained, premium-through-simplicity, social/woven rather than corporate/geometric.
- **Color palette intent:** deep forest green as the dominant identity color (savings, growth, trust), with a single raw-gold accent strand (wealth, harvest) — never more than one accent color active in the mark at once, per Principle 3 ("trust through restraint").

### Icon redesign specs
| Spec | Value |
|---|---|
| Shape | Standard Android adaptive icon (foreground + background layers, 108×108dp safe zone, 72×72dp visible circle/squircle mask) |
| Icon mark concept | The brand symbol already defined in `DESIGN_SYSTEM.md` §8: **three overlapping, slightly-offset rounded arcs** (woven basket strands / fanned coins) forming a loose "nest" shape — communal and protective, not literal basket clip-art |
| Foreground color | `green-700` (`#0F5132`) for two arcs, `gold-400` (`#D4A017`) for the single accent strand — exactly the existing wordmark/symbol pairing, no new colors introduced |
| Background color | `green-50` (`#EAF6EF`) flat fill (light, warm, lets the dark green/gold marks read with strong contrast) — replaces the placeholder `#000000` |
| Dark variant | Background flips to `green-900` (`#082B1B`); foreground arcs flip to `green-300` (`#6CC097`) + `gold-300` (`#DDB13D`) for AA contrast on the dark fill — mirrors the existing dark-theme token swaps in `Color.kt` |
| Monochrome layer (Android 13+ themed icon) | Single-color silhouette of the three-arc mark, tinted by system wallpaper accent per Android adaptive-icon spec |
| Format | SVG/vector master → exported per Deliverables Checklist below |

### Launch/splash animation specs
| Spec | Value |
|---|---|
| Duration | **2.0s** total (within the 1.5–2.5s recommended range) |
| Animation type | **Reveal / draw-on** — the three arcs of the icon mark draw on sequentially (not a morph, not a particle effect), echoing Principle 3 ("motion that is felt rather than noticed") and reusing the exact spring/easing vocabulary already established in `DESIGN_SYSTEM.md` §9 (e.g. §9.1's `DampingRatioMediumBouncy` overshoot pattern) |
| Easing curves | Arc draw-in: `FastOutSlowInEasing`, 600ms each, 120ms stagger between arcs. Wordmark settle: `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)` — same spring spec already used for the payment-success checkmark (§9.1), kept consistent rather than inventing a new motion signature. |
| Key frames (plain language) | 1) Black/system splash background swaps to `green-50` (light) / `green-900` (dark) the instant the activity starts. 2) Arc 1 (left, green-700) draws on left-to-right. 3) 120ms later, Arc 2 (right, green-700) draws on, overlapping Arc 1's tail. 4) 120ms later, the gold accent strand draws on last, completing the nest shape with a small overshoot settle. 5) 150ms after the mark completes, the "Mfuko" wordmark (Bricolage Grotesque SemiBold, `displayLarge`, §4.2) fades + slides up 12dp beneath the mark. 6) Hold composed state for ~300ms, then cross-fade (200ms) into the real first screen (Login/Demo or Home, depending on session state). |
| Lottie vs native | **Native Compose** — implemented with `Canvas`/`drawArc` (the same primitive `HealthGauge` already uses per §6.7) and `androidx.core.splashscreen` for the system-level splash window, avoiding a new Lottie dependency and keeping the app's offline-first, dependency-light posture. See `launch_animation_spec.md` for the frame-level breakdown. |

---

## Design Tokens

All tokens below are sourced directly from `DESIGN_SYSTEM.md` (§3–§5) — reproduced here only as the subset relevant to icon/launch work, not redefined.

### Color
| Role | Light | Dark |
|---|---|---|
| Primary | `#0F5132` (green-700) | `#6CC097` (green-300) |
| Secondary / accent | `#96690C` (gold-600) — mid accent used in mark: `#D4A017` (gold-400) | `#DDB13D` (gold-300) |
| Tertiary | `#954824` (clay-500) — not used in icon/splash, reserved for in-app accents | `#E8A480` (clay-200) |
| Background (splash) | `#EAF6EF` (green-50) | `#082B1B` (green-900) |
| Surface | `#FAFDF6` | `#12150F` |
| Text (wordmark on splash) | `#0F5132` (primary) | `#E3E4DC` (onSurface, dark) |

### Typography
| Slot | Font | Use |
|---|---|---|
| Display | Bricolage Grotesque, SemiBold 600 | Splash wordmark (`displayLarge`, 57/64sp) — the one slot `DESIGN_SYSTEM.md` §4.2 already reserves "for splash" |
| Body | Inter, Regular 400 | Not used on splash; reserved for in-app content |
| UI | Inter, Medium 500 | Not used on splash; reserved for in-app labels/buttons |

### Shape, shadow, spacing (from `DESIGN_SYSTEM.md` §5)
- **Border radius scale:** `extraSmall 8dp`, `small 12dp`, `medium 16dp`, `large 24dp`, `extraLarge 28dp`, `full 50%` — the icon's arcs use `StrokeCap.Round` consistent with this generously-rounded scale; no sharp terminals anywhere in the mark.
- **Shadow/elevation:** icon and splash are flat, elevation `0` — no drop shadows on the mark itself (adaptive icon masking handles any system-applied shadow).
- **Spacing scale:** `xs=4, sm=8, md=12, lg=16, xl=24, xxl=32, xxxl=48` dp — wordmark sits `xl` (24dp) below the icon mark on the splash screen, matching the "section gap" token used for major UI blocks elsewhere in the app.

---

## Deliverables Checklist

- [ ] App icon — 1024×1024 master (SVG + PNG)
- [ ] Adaptive icon layers — foreground (vector, 108×108dp) + background (flat color, 108×108dp) + monochrome layer (Android 13+ themed icon)
- [ ] Splash/launch animation — implemented natively in Compose (`Canvas` + `androidx.core.splashscreen`); no Lottie/After Effects source required per the decision above
- [ ] Figma component library — icon mark + splash sequence as reusable Figma components, linked to existing `DESIGN_SYSTEM.md` tokens
- [ ] Dark mode variants — icon background/foreground dark swap; splash background/wordmark dark swap (both specified above)
- [ ] Export specs for iOS and Android — **Android only for now** (no iOS target exists in this codebase); Android exports: `mipmap-mdpi` through `mipmap-xxxhdpi` adaptive icon webp/PNG layers, plus `mipmap-anydpi-v26` adaptive icon XML and `drawable` monochrome XML

---

## References & Inspiration

- Primary reference: this app's own `DESIGN_SYSTEM.md` §1 (brand principles), §3 (color), §8 (brand mark concept), §9 (existing micro-interaction motion vocabulary) — the icon and splash should look like they were designed by the same hand as the rest of the re-skin, not a separate exercise.
- Visual touchstone for the "three woven arcs" mark: layered/fanned coin or basket-weave motifs — communal and protective, deliberately avoiding literal basket clip-art or generic fintech chart/coin iconography.
- _No external mood-board links provided yet — add here if/when available._
