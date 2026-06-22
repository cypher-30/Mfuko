# Mfuko — Group Savings & Loans Manager

> *Mfuko* is Swahili for **fund / wallet**.

Mfuko is an offline-first Android app for managing **Kenyan-style chama groups** (group savings circles). Members join a shared *Nest* (group), contribute on a monthly cycle, and can request loans from the collective pool. Interest is calculated automatically and managers oversee the whole process — no backend required to run it.

---

## Features (see [FEATURES.md](docs/FEATURES.md) for full spec)

| Area | Status |
|---|---|
| Register / log in (local + one-tap demo) | ✅ |
| Create & join a Nest | ✅ |
| Dashboard — contribution progress, active loan, financial health score | ✅ |
| Manager panel — overview card, invite code, remove member, announcements | ✅ |
| Contribution schedule & auto-rolling cycles | ✅ |
| Automatic interest (flat or reducing-balance) | ✅ |
| In-app notifications (contributions, loan decisions, announcements) | ✅ |
| PDF report export & share | ✅ |
| M-Pesa payment flow (simulated STK push) | ✅ |
| Settings — notification toggle, change password | ✅ |
| Automated tests | 🔜 Phase 6 |
| Cloud sync (Ktor backend) | 🔜 Phase 7 |

---

## Quick start

### Run in demo mode (no backend required)

1. Open the project in Android Studio (see [SETUP_ANDROID_STUDIO.md](docs/SETUP_ANDROID_STUDIO.md)).
2. Build & run on an emulator or device.
3. Tap **Continue as demo** on the login screen — a pre-seeded nest opens immediately.

### Register a real account (offline)

1. Tap **Create account** and register with a phone number and password.
2. Create or join a Nest.
3. All data is stored locally (no network needed).

### Enable cloud sync (optional)

See [BACKEND.md](docs/BACKEND.md) for running the Ktor backend and switching on remote sync.

---

## Project structure

```
Mfuko/                       ← Android app (this repo)
  app/src/main/
    java/com/chama/mfuko/
      data/
        local/               ← Room DB, DataStore (TokenManager, SessionManager), DemoSeeder, CycleRoller
        remote/               ← Retrofit API services & DTOs (Phase 7, optional)
        repository/          ← Local*RepositoryImpl (default) + network RepositoryImpl (Phase 7)
      di/                    ← Hilt dependency injection (AppModule)
      ui/
        features/            ← Screen / ViewModel pairs (one folder per feature)
        navigation/          ← AppNavHost, BottomNavScaffold, Screen sealed class
        theme/               ← Mfuko colour palette, MfukoTheme
        components/          ← Shared composables (MfukoCard, MfukoGauge, ...)
        util/                ← formatKes() and other helpers
      core/util/             ← LoanInterestCalculator, NestReportPdfGenerator, Resource<T>

MfukoServer/                  ← Ktor backend (separate project)
  C:\Users\Alvin\IdeaProjects\MfukoServer\
```

---

## Docs

| File | Contents |
|---|---|
| [SETUP_ANDROID_STUDIO.md](docs/SETUP_ANDROID_STUDIO.md) | Build requirements, sync instructions, known issues |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | Offline-first design, MVVM + Hilt, data flow |
| [FEATURES.md](docs/FEATURES.md) | Full spec from the original PDF, implementation status |
| [DATA_MODEL.md](docs/DATA_MODEL.md) | Entity/ERD documentation, Room schema |
| [BACKEND.md](docs/BACKEND.md) | Ktor server setup, API reference, cloud sync |
| [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) | Colour palette, type scale, component guidelines |

---

## Tech stack

- **Language:** Kotlin 2.1.x
- **UI:** Jetpack Compose + Material3 (dynamic color off, Mfuko brand palette)
- **Architecture:** MVVM, Hilt DI (KSP), unidirectional data flow
- **Local DB:** Room — offline-first, source of truth
- **Preferences:** DataStore
- **Networking:** Retrofit + OkHttp (optional remote sync, off by default)
- **Navigation:** Jetpack Navigation Compose
- **Build:** Android Gradle Plugin 8.9, Gradle 8.11.1, KSP
