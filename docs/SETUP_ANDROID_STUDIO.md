# Android Studio Setup Guide

## Requirements

| Tool | Version | Notes |
|---|---|---|
| Android Studio | Meerkat 2024.3+ | Earlier versions may not support AGP 8.9 |
| JDK | 21 (JetBrains Runtime) | Bundled with Android Studio; also pinned in `gradle-daemon-jvm.properties` |
| Gradle | **8.11.1** | Pinned in `gradle/wrapper/gradle-wrapper.properties` |
| AGP | **8.9.0** | Declared in `gradle/libs.versions.toml` |
| Kotlin | **2.1.21** | Declared in `gradle/libs.versions.toml` |
| KSP | **2.1.21-1.0.29** | KSP version must match Kotlin version exactly |
| Android SDK | API 35 (compileSdk / targetSdk) | Install via SDK Manager |
| minSdk | 24 (Android 7.0) | |

> **Why KSP instead of KAPT?**
> The project was migrated from KAPT to KSP (Kotlin Symbol Processing) for Hilt DI.
> KSP is the modern, faster replacement for KAPT on Kotlin 2.x. Never add `kotlin-kapt`
> back to the plugins block.

---

## First sync (fresh clone)

1. Open the project folder `Mfuko/` in Android Studio.
2. Android Studio will prompt to sync Gradle — click **Sync Now**.
   - Gradle 8.11.1 will be downloaded if not already cached (`~/.gradle/wrapper/dists`).
   - All library deps will be fetched from Maven Central / Google Maven.
3. Wait for **"Gradle sync finished"** in the bottom status bar.
4. Select an emulator or connected device and press **Run**.

---

## Common sync / build issues

### "Could not resolve com.android.application:9.x.x"
AGP `9.x` does not exist yet. This project was previously declared with `agp = "9.2.1"` which is invalid.
**Fix:** `libs.versions.toml` now has `agp = "8.9.0"`. Run **File → Sync Project with Gradle Files**.

### IDE shows red underlines after changing libs.versions.toml
The IDE resolves TOML catalog references lazily. Run a Gradle sync first — the red lines disappear after sync completes.

### "There are multiple DataStores active for the same file"
This crash occurred when both `TokenManager` and `SessionManager` declared a
`preferencesDataStore(name = "user_prefs")` delegate. **Fixed:** `SessionManager` now reads
through `TokenManager` only and has no DataStore delegate.

### "Cannot find symbol: GroupMoneyManagerTheme"
Renamed to `MfukoTheme` in `ui/theme/Theme.kt`. `MainActivity.kt` already uses the new name.
If you see this in your IDE, run **Build → Clean Project** then **Sync**.

### "Unresolved reference: BuildConfig"
`buildFeatures { buildConfig = true }` is now set in `app/build.gradle.kts`.
If the IDE still shows the error, do **Build → Make Project** once to generate `BuildConfig.kt`.

### Build fails with KAPT errors
KAPT has been removed. If an old cached Gradle run is using KAPT, run:
```
./gradlew clean :app:kspDebugKotlin
```

### Emulator can't reach the backend
The base URL is now `http://10.0.2.2:8081/` (Android emulator loopback).
For a **physical device** on the same Wi-Fi: open `app/build.gradle.kts`,
find the `debug` block and change `BASE_URL` to your machine's LAN IP:
```kotlin
buildConfigField("String", "BASE_URL", "\"http://192.168.x.x:8081/\"")
```

---

## Running in offline / demo mode

`BuildConfig.USE_REMOTE = false` (the default for both debug and release).
In this mode the app uses a local Room database and no network calls are made.
The backend does **not** need to be running.

To enable cloud sync after Phase 7:
1. Set `buildConfigField("Boolean", "USE_REMOTE", "true")` in the debug block.
2. Ensure the Ktor backend is running (see [BACKEND.md](BACKEND.md)).

---

## Useful Gradle tasks

```bash
./gradlew :app:assembleDebug          # Build debug APK
./gradlew :app:installDebug           # Build and install on connected device
./gradlew testDebugUnitTest           # Run unit tests
./gradlew :app:kspDebugKotlin         # Run KSP annotation processing only
./gradlew clean                       # Wipe build outputs
```
