# Mfuko Launch Animation — Frame Spec

Companion to [`APP_REDESIGN_BRIEF.md`](APP_REDESIGN_BRIEF.md). Production implementation target is **native Compose** (`Canvas` + `androidx.core.splashscreen`) — the CSS/Lottie keyframe blocks below exist purely as a portable, tool-agnostic description for design handoff/prototyping (Figma, After Effects preview, or a web mockup), not because the shipped app uses either.

Total duration: **2.0s @ 60fps = 120 frames**. All easing/spring constants reuse the vocabulary already established in `DESIGN_SYSTEM.md` §9 — no new motion language is introduced.

---

## Frame-by-frame breakdown

| Frame (60fps) | Time (ms) | Event |
|---|---|---|
| 0 | 0 | System splash window background swaps instantly to `green-50` `#EAF6EF` (light) / `green-900` `#082B1B` (dark). No transition — this is the OS-level splash background set via `windowSplashScreenBackground`. |
| 0–36 | 0–600 | **Arc 1** (left strand, `green-700` `#0F5132` light / `green-300` `#6CC097` dark) strokes on, 0%→100% path trim, `FastOutSlowInEasing`. |
| 7–43 | 120–720 | **Arc 2** (right strand, same color as Arc 1) strokes on, 0%→100%, `FastOutSlowInEasing` — starts 120ms after Arc 1, while Arc 1 is still completing, so the two visibly overlap mid-draw. |
| 14–50 | 240–840 | **Gold accent strand** (`gold-400` `#D4A017` light / `gold-300` `#DDB13D` dark) strokes on, 0%→100%, `FastOutSlowInEasing` — completes the three-arc nest shape. |
| 50–60 | 840–1000 | Full mark performs a single settle bounce: scale `1.0f → 1.05f → 1.0f`, `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)` — identical spring spec to the §9.1 payment-success checkmark overshoot. |
| 60–69 | 1000–1150 | Hold — mark fully settled, nothing animates (lets the completed mark register before the wordmark appears). |
| 69–87 | 1150–1450 | **Wordmark** ("Mfuko", Bricolage Grotesque SemiBold, `displayLarge`) fades in (`alpha 0f→1f`) and slides up (`translationY +12dp→0dp`) simultaneously, `tween(300ms, FastOutSlowInEasing)`, positioned `xl` (24dp) below the mark per the spacing scale. |
| 87–105 | 1450–1750 | Hold — composed splash (mark + wordmark) sits static. |
| 105–117 | 1750–1950 | Cross-fade (`alpha 1f→0f` on splash content, `tween(200ms, LinearEasing)`) into the first real screen (Login/Demo-continue or Home, depending on existing session — handled by `androidx.core.splashscreen`'s exit animation hook). |
| 117–120 | 1950–2000 | Safety margin / rounding buffer before the splash activity is fully removed. |

---

## CSS keyframe logic (design-tool reference only)

```css
:root {
  --mfuko-green-700: #0F5132;
  --mfuko-gold-400: #D4A017;
  --mfuko-bg-light: #EAF6EF;
}

.splash-bg {
  background: var(--mfuko-bg-light);
}

.arc-1, .arc-2 {
  stroke: var(--mfuko-green-700);
  stroke-dasharray: 100;
  stroke-dashoffset: 100; /* hidden */
  animation: draw-on 600ms cubic-bezier(0.4, 0, 0.2, 1) forwards;
}
.arc-2 { animation-delay: 120ms; }

.arc-gold {
  stroke: var(--mfuko-gold-400);
  stroke-dasharray: 100;
  stroke-dashoffset: 100;
  animation: draw-on 600ms cubic-bezier(0.4, 0, 0.2, 1) 240ms forwards;
}

.mark-group {
  animation: settle-bounce 160ms cubic-bezier(0.34, 1.56, 0.64, 1) 840ms forwards;
}

.wordmark {
  opacity: 0;
  transform: translateY(12px);
  animation: wordmark-in 300ms cubic-bezier(0.4, 0, 0.2, 1) 1150ms forwards;
}

.splash-root {
  animation: splash-out 200ms linear 1750ms forwards;
}

@keyframes draw-on {
  to { stroke-dashoffset: 0; }
}
@keyframes settle-bounce {
  0%   { transform: scale(1.0); }
  50%  { transform: scale(1.05); }
  100% { transform: scale(1.0); }
}
@keyframes wordmark-in {
  to { opacity: 1; transform: translateY(0); }
}
@keyframes splash-out {
  to { opacity: 0; }
}
```

## Lottie keyframe logic (design-tool reference only)

Equivalent structure if prototyped in After Effects → Lottie JSON (frame numbers at 60fps, matching the table above):

```json
{
  "fr": 60,
  "ip": 0,
  "op": 120,
  "layers": [
    {
      "nm": "splash_bg",
      "ks": { "o": { "a": 0, "k": 100 } }
    },
    {
      "nm": "arc_1",
      "shapes": [{ "ty": "tm", "s": { "a": 1, "k": [{ "t": 0, "s": [0] }, { "t": 36, "s": [100] }] } }],
      "ip": 0, "op": 36
    },
    {
      "nm": "arc_2",
      "shapes": [{ "ty": "tm", "s": { "a": 1, "k": [{ "t": 7, "s": [0] }, { "t": 43, "s": [100] }] } }],
      "ip": 7, "op": 43
    },
    {
      "nm": "arc_gold",
      "shapes": [{ "ty": "tm", "s": { "a": 1, "k": [{ "t": 14, "s": [0] }, { "t": 50, "s": [100] }] } }],
      "ip": 14, "op": 50
    },
    {
      "nm": "mark_group_scale",
      "ks": { "s": { "a": 1, "k": [
        { "t": 50, "s": [100, 100, 100] },
        { "t": 55, "s": [105, 105, 100] },
        { "t": 60, "s": [100, 100, 100] }
      ] } }
    },
    {
      "nm": "wordmark",
      "ks": {
        "o": { "a": 1, "k": [{ "t": 69, "s": [0] }, { "t": 87, "s": [100] }] },
        "p": { "a": 1, "k": [{ "t": 69, "s": [0, 12] }, { "t": 87, "s": [0, 0] }] }
      },
      "ip": 69, "op": 120
    },
    {
      "nm": "splash_root_fade_out",
      "ks": { "o": { "a": 1, "k": [{ "t": 105, "s": [100] }, { "t": 117, "s": [0] }] } }
    }
  ]
}
```

---

## SVG placeholder — icon mark

Placeholder geometry only (three overlapping rounded arcs + gold accent strand, on the 108×108dp adaptive-icon safe zone). Not final art — for layout/proportion reference until the icon is produced in Figma per the Deliverables Checklist.

```svg
<svg width="108" height="108" viewBox="0 0 108 108" xmlns="http://www.w3.org/2000/svg">
  <rect width="108" height="108" fill="#EAF6EF"/>

  <!-- Arc 1 -->
  <path d="M 30 70 A 24 24 0 0 1 66 54"
        fill="none" stroke="#0F5132" stroke-width="7" stroke-linecap="round"/>

  <!-- Arc 2 -->
  <path d="M 42 78 A 24 24 0 0 1 78 62"
        fill="none" stroke="#0F5132" stroke-width="7" stroke-linecap="round" opacity="0.85"/>

  <!-- Gold accent strand -->
  <path d="M 36 50 A 22 22 0 0 1 70 38"
        fill="none" stroke="#D4A017" stroke-width="6" stroke-linecap="round"/>
</svg>
```
