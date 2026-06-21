# Mfuko — Architecture

## Overview

Mfuko follows **MVVM (Model-View-ViewModel)** with **Hilt** for dependency injection and
**Jetpack Compose** for the UI. Data flows unidirectionally: UI → ViewModel → Repository → Data source.

The app is being built **offline-first**: a local Room database is the single source of truth,
with an optional Ktor backend for multi-device cloud sync (Phase 7).

---

## Layer map

```
┌────────────────────────────────────────────────┐
│  UI (Compose)                                  │
│  ui/features/<feature>/                        │
│    Screen.kt  ←  ViewModel.kt                  │
│    (observes State)   (emits State + UiEvents) │
└─────────────────┬──────────────────────────────┘
                  │ suspend calls / Flow
┌─────────────────▼──────────────────────────────┐
│  Repository interfaces                         │
│  data/repository/*Repository.kt                │
└─────────────────┬──────────────────────────────┘
         ┌────────┴──────────┐
         │                   │
┌────────▼──────┐   ┌────────▼──────────────┐
│ Local (Room)  │   │ Remote (Retrofit)      │
│ data/local/   │   │ data/remote/           │
│ entities, DAOs│   │ API services, DTOs     │
└───────────────┘   └────────────────────────┘
```

### Key rules
- **Screens never call repositories directly.** They only read `State` from ViewModels.
- **ViewModels never know about Compose.** They emit `State` and `UiEvent` through Kotlin channels.
- **Repositories are interface-typed** in ViewModels — concrete implementations are injected by Hilt.
- **Resource<T>** (`core/util/Resource.kt`) wraps every async response: `Success`, `Error`, `Loading`.

---

## Offline-first design (Phase 3+)

```
              ┌─────────────┐
              │   Room DB   │  ← single source of truth
              └──────┬──────┘
    ┌─────────────────┼─────────────────┐
    ▼                 ▼                 ▼
 Auth DAO     Nest/Member DAO    Loan/Contribution DAO
    │                 │                 │
    └────── Repositories ───────────────┘
                      │
            Optional RemoteSync
            (USE_REMOTE = false by default)
                      │
               Ktor Backend
               (Phase 7)
```

### Local auth
- Registration: hash password with BCrypt locally, store `User` row in Room.
- Login: verify hash; generate a local JWT-like session token stored in DataStore.
- Demo mode: pre-seed Room with demo data (manager + 3 members, 2 cycles, 1 loan).

### Data persistence
| Store | Contents |
|---|---|
| `DataStore ("user_prefs")` | Auth token, current nestId |
| `Room (MfukoDatabase)` | All domain data: User, Nest, Membership, Cycle, Contribution, Loan, LoanRepayment, Penalty, Notification, Announcement |

---

## Dependency injection (Hilt)

- `AppModule.kt` provides all singletons: `TokenManager`, `SessionManager`, Retrofit services, repositories.
- ViewModels are `@HiltViewModel` — Hilt injects them automatically via `hiltViewModel()` in Compose.
- Room's `MfukoDatabase` will be added as a singleton in Phase 3.

---

## Navigation

`AppNavHost.kt` owns the entire nav graph. `Screen.kt` is a sealed class of all routes.
The start destination is decided by `MainViewModel` on launch:

```
No token   → Login
Token + empty dashboard → Welcome (create/join nest)
Token + data            → Home (dashboard)
Error fetching dashboard → Login
```

---

## Theming

- `ui/theme/Color.kt` — Mfuko brand palette (green / gold / terracotta).
- `ui/theme/Theme.kt` → `MfukoTheme` — wraps `MaterialTheme` with the palette; dynamic colour is OFF.
- `ui/theme/Type.kt` — typography (Material3 defaults, customise in Phase 4).
- All monetary values use `ui/util/formatKes(amount: Double)` — outputs `"KES 10,000.00"`.

---

## BuildConfig fields

| Field | debug default | Purpose |
|---|---|---|
| `USE_REMOTE` | `false` | When `true`, repositories call the Ktor backend instead of local Room |
| `BASE_URL` | `http://10.0.2.2:8081/` | Backend base URL; change to LAN IP for physical device |
