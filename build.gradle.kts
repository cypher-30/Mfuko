// Top-level build file. Plugin declarations only; all configuration is in app/build.gradle.kts.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version libs.versions.hilt.get() apply false
    alias(libs.plugins.ksp) apply false
}
