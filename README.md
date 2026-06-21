# Mfuko — Group Savings & Loans Manager

> *Mfuko* is Swahili for **fund / wallet**.

Mfuko is an Android app for managing **Kenyan-style chama groups** (group savings circles). Members join a shared *Nest* (group), contribute on a monthly cycle, and can request loans from the collective pool. Interest is calculated automatically and managers oversee the whole process.

---

## Features (see [FEATURES.md](docs/FEATURES.md) for full spec)

| Area | Status |
|---|---|
| Register / Log in (local + demo) | ✅ Phase 3 |
| Create & join a Nest | ✅ Working |
| Dashboard — contributions, loans, penalties | ✅ Working |
| Manager: approve/reject loans | ✅ Working |
| Manager: record member contributions | ✅ Working |
| Contribution schedule & cycles | 🔜 Phase 5 |
| Automatic interest calculation | 🔜 Phase 5 |
| In-app notifications | 🔜 Phase 5 |
| Financial health chart | 🔜 Phase 5 |
| PDF report export | 🔜 Phase 5 |
| M-Pesa integration | 🔜 Phase 5 (simulated) |
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
GroupMoneyManager/           ← Android app (this repo)
  app/src/main/
    java/com/chama/groupmoneymanager/
      data/
        local/               ← DataStore (TokenManager, SessionManager)
                             ← Room database (Phase 3+)
        remote/              ← Retrofit API services & DTOs
        repository/          ← Repository implementations
      di/                    ← Hilt dependency injection (AppModule)
      ui/
        features/            ← Screen / ViewModel pairs (one folder per feature)
        navigation/          ← AppNavHost, Screen sealed class
        theme/               ← Mfuko colour palette, MfukoTheme
        util/                ← formatKes() and other helpers
      core/util/             ← Resource<T> sealed class

GroupMoneyManagerServer/     ← Ktor backend (separate project)
  C:\Users\Alvin\IdeaProjects\GroupMoneyManagerServer\
```

---

## Docs

| File | Contents |
|---|---|
| [SETUP_ANDROID_STUDIO.md](docs/SETUP_ANDROID_STUDIO.md) | Build requirements, sync instructions, known issues |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | Offline-first design, MVVM + Hilt, data flow |
| [FEATURES.md](docs/FEATURES.md) | Full spec from the PDF, implementation status |
| [DATA_MODEL.md](docs/DATA_MODEL.md) | Entity/ERD documentation, Room schema |
| [BACKEND.md](docs/BACKEND.md) | Ktor server setup, API reference, cloud sync |

---

## Tech stack

- **Language:** Kotlin 2.1.x
- **UI:** Jetpack Compose + Material3
- **Architecture:** MVVM, Hilt DI, Unidirectional data flow
- **Local DB:** Room (offline-first, Phase 3+)
- **Preferences:** DataStore
- **Networking:** Retrofit + OkHttp (optional remote sync)
- **Navigation:** Jetpack Navigation Compose
- **Build:** Android Gradle Plugin 8.9, Gradle 8.11.1, KSP
