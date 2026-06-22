# Mfuko Launch Animation — Frame Spec

Companion to [`APP_REDESIGN_BRIEF.md`](APP_REDESIGN_BRIEF.md). Implemented natively in Compose
(`ui/features/splash/MfukoSplash.kt`, `Canvas` + `androidx.core.splashscreen`) — the CSS/Lottie
keyframe blocks below exist purely as a portable, tool-agnostic description for design
handoff/prototyping (Figma, After Effects preview, or a web mockup, as in
`icon and launch animations/Mfuko Visual Redesign.dc.html`), not because the shipped app uses either.

Total duration: **~4.1s** — tuned deliberately slow, since this is the one moment in the app meant
to be watched rather than reacted to instantly. All easing/spring constants reuse the vocabulary
already established in `DESIGN_SYSTEM.md` §9 — no new motion language is introduced. Coin
geometry/palette is identical to `app/src/main/res/drawable/ic_launcher_foreground.xml` (same 0..108
viewBox), so the splash mark and the home-screen icon read as the same brand object.

The system splash window (`Theme.Mfuko.Splash`) shows **only the background color** — its
`windowSplashScreenAnimatedIcon` is overridden to a fully transparent placeholder
(`drawable/splash_icon_empty.xml`) specifically so the platform doesn't paint the app's *static,
already-complete* launcher icon before handing off to Compose. Without that override the coin stack
would flash in fully-formed, disappear, and then visibly rebuild itself from empty via the animation
below — the whole point of this sequence is that the coins are *never* seen at rest until they've
animated into place.

**The exit is gated on app readiness, not just the clock.** `MainViewModel.startDestination` is
resolved asynchronously from DataStore; if the splash disappeared on a fixed timer alone, a slow
cold-start read could leave the app showing a blank `Surface` (no nav content yet) after the splash
fades. `MfukoSplash` takes a `contentReady: Boolean` parameter (wired from `MainActivity` as
`startDestination != null`) — the splash finishes its entrance and holds for a minimum
`MIN_HOLD_BEFORE_EXIT_MS`, but the cross-fade out only starts once `contentReady` is *also* true.
In the normal case the data is ready well before the entrance animation even finishes, so this adds
no visible delay; it only ever extends the hold, never shortens it below the floor.

---

## Frame-by-frame breakdown

| Time (ms) | Event |
|---|---|
| 0 | System splash window background swaps instantly to `green-50` `#EAF6EF` (light) / `green-900` `#082B1B` (dark), no icon. No transition — this is the OS-level splash background set via `windowSplashScreenBackground` (`Theme.Mfuko.Splash`). |
| 0–~1200 | **Coin 4** (bottom, green ramp) rises into place (`translationY +18dp→0dp`, `alpha 0f→1f`, `scaleX 0.88f→1f`) via `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 90f)` — well below `Spring.StiffnessLow` (200f) for a slow, floaty settle; the spring's natural overshoot produces the bounce, no separate animation needed. (Settle time is physics-driven, not a fixed duration — ~1200ms is approximate.) |
| 320–~1500 | **Coin 3** rises the same way, starting 320ms after Coin 4 — while Coin 4 is still settling, so the two visibly overlap mid-rise. |
| 640–~1800 | **Coin 2** rises, 320ms after Coin 3. |
| 960–~2100 | **Gold coin** (top) rises last, 320ms after Coin 2, crowning the stack with its own settle-bounce. |
| ~2100–2600 | Hold — complete four-coin stack at rest, nothing animates (lets the completed mark register before the wordmark appears). |
| 2600–3100 | **Wordmark** ("Mfuko", Bricolage Grotesque SemiBold, `displayLarge`) fades in (`alpha 0f→1f`) and slides up (`translationY +18dp→0dp`) simultaneously, `tween(500ms, FastOutSlowInEasing)`, positioned `sm` (8dp) below the coin stack — tight, not the standard "section gap" spacing, since the wordmark reads as part of the same mark rather than a separate block. |
| 3100–3800 | Hold (minimum 700ms — extends further here if `contentReady` is still false) — composed splash (coin stack + wordmark) sits static. |
| 3800–4150 | Cross-fade (`alpha 1f→0f` on splash content, `tween(350ms, LinearEasing)`) into the first real screen (Login/Demo-continue or Home, depending on existing session — handled by the `onFinished` callback in `MfukoSplash`, only invoked once `contentReady` is true). |

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

@keyframes mfuko-coin-enter {
  0%   { opacity: 0; transform: translateY(18px) scaleX(0.88); }
  22%  { opacity: 1; }
  62%  { transform: translateY(-6px) scaleX(1.04); }
  80%  { transform: translateY(3px) scaleX(0.98); }
  100% { opacity: 1; transform: translateY(0) scaleX(1); }
}

.coin-4 { animation: mfuko-coin-enter 1200ms ease 0ms both; }
.coin-3 { animation: mfuko-coin-enter 1200ms ease 320ms both; }
.coin-2 { animation: mfuko-coin-enter 1200ms ease 640ms both; }
.coin-1-gold { animation: mfuko-coin-enter 1200ms ease 960ms both; }

@keyframes mfuko-wm-in {
  from { opacity: 0; transform: translateY(18px); }
  to   { opacity: 1; transform: translateY(0); }
}
.wordmark {
  opacity: 0;
  animation: mfuko-wm-in 500ms cubic-bezier(0.4, 0, 0.2, 1) 2600ms forwards;
}

@keyframes mfuko-out {
  to { opacity: 0; }
}
.splash-root {
  /* Fires once contentReady is true AND this minimum hold has elapsed — see note above. */
  animation: mfuko-out 350ms linear 3800ms forwards;
}
```

## Lottie keyframe logic (design-tool reference only)

Equivalent structure if prototyped in After Effects → Lottie JSON (frame numbers at 60fps):

```json
{
  "fr": 60,
  "ip": 0,
  "op": 249,
  "layers": [
    { "nm": "splash_bg", "ks": { "o": { "a": 0, "k": 100 } } },
    {
      "nm": "coin_4",
      "ks": {
        "p": { "a": 1, "k": [{ "t": 0, "s": [54, 88] }, { "t": 72, "s": [54, 70] }] },
        "o": { "a": 1, "k": [{ "t": 0, "s": [0] }, { "t": 16, "s": [100] }] }
      },
      "ip": 0, "op": 72
    },
    {
      "nm": "coin_3",
      "ks": {
        "p": { "a": 1, "k": [{ "t": 19, "s": [54, 76] }, { "t": 90, "s": [54, 58] }] },
        "o": { "a": 1, "k": [{ "t": 19, "s": [0] }, { "t": 35, "s": [100] }] }
      },
      "ip": 19, "op": 90
    },
    {
      "nm": "coin_2",
      "ks": {
        "p": { "a": 1, "k": [{ "t": 38, "s": [54, 64] }, { "t": 108, "s": [54, 46] }] },
        "o": { "a": 1, "k": [{ "t": 38, "s": [0] }, { "t": 54, "s": [100] }] }
      },
      "ip": 38, "op": 108
    },
    {
      "nm": "coin_1_gold",
      "ks": {
        "p": { "a": 1, "k": [{ "t": 58, "s": [54, 52] }, { "t": 126, "s": [54, 34] }] },
        "o": { "a": 1, "k": [{ "t": 58, "s": [0] }, { "t": 74, "s": [100] }] }
      },
      "ip": 58, "op": 126
    },
    {
      "nm": "wordmark",
      "ks": {
        "o": { "a": 1, "k": [{ "t": 156, "s": [0] }, { "t": 186, "s": [100] }] },
        "p": { "a": 1, "k": [{ "t": 156, "s": [0, 18] }, { "t": 186, "s": [0, 0] }] }
      },
      "ip": 156, "op": 249
    },
    {
      "nm": "splash_root_fade_out",
      "ks": { "o": { "a": 1, "k": [{ "t": 228, "s": [100] }, { "t": 249, "s": [0] }] } }
    }
  ]
}
```

---

## SVG reference — icon/splash mark

Exact geometry/palette match for `ic_launcher_foreground.xml` — four ellipse-pairs (edge + face +
translucent highlight), bottom coin first, gold coin last so it renders on top of the stack.

```svg
<svg width="108" height="108" viewBox="0 0 108 108" xmlns="http://www.w3.org/2000/svg">
  <rect width="108" height="108" fill="#EAF6EF"/>

  <!-- Coin 4 (bottom, green) -->
  <ellipse cx="54" cy="73" rx="26" ry="7.5" fill="#040F08"/>
  <ellipse cx="54" cy="70" rx="26" ry="7.5" fill="#0B3D26"/>
  <ellipse cx="54" cy="67.5" rx="19" ry="2.8" fill="#0F5132" opacity="0.35"/>

  <!-- Coin 3 (green) -->
  <ellipse cx="54" cy="61" rx="26" ry="7.5" fill="#06200F"/>
  <ellipse cx="54" cy="58" rx="26" ry="7.5" fill="#0F5132"/>
  <ellipse cx="54" cy="55.5" rx="19" ry="2.8" fill="#146647" opacity="0.4"/>

  <!-- Coin 2 (green) -->
  <ellipse cx="54" cy="49" rx="26" ry="7.5" fill="#0A2C22"/>
  <ellipse cx="54" cy="46" rx="26" ry="7.5" fill="#1F8059"/>
  <ellipse cx="54" cy="43.5" rx="19" ry="2.8" fill="#3FA476" opacity="0.4"/>

  <!-- Coin 1 (top, gold harvest) -->
  <ellipse cx="54" cy="37" rx="26" ry="7.5" fill="#7A5F09"/>
  <ellipse cx="54" cy="34" rx="26" ry="7.5" fill="#D4A017"/>
  <ellipse cx="54" cy="31.5" rx="19" ry="2.8" fill="#EBCB6E" opacity="0.55"/>
</svg>
```
