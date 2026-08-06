# CivicConnect Android Project Foundation

This plan outlines the steps to set up the Android project foundation for CivicConnect, following Clean Architecture, MVVM, and modern Android development practices (2026).

## User Review Required

> [!IMPORTANT]
> The package name will be changed from `com.example.civicconnect` to `com.civicconnect` as per the requirements.

## Proposed Changes

### 1. Version Catalog & Build Configuration
- Update `libs.versions.toml` with Hilt, Retrofit, Room, Coil, Navigation, and KSP.
- Update `app/build.gradle.kts` to apply plugins and add dependencies.
- Update root `build.gradle.kts` if needed for Hilt/KSP plugins.

### 2. Package Structure & Refactoring
- [MODIFY] Rename package `com.example.civicconnect` to `com.civicconnect`.
- [NEW] Create Clean Architecture package structure: `data`, `domain`, `presentation`, `di`, `utils`.
- [NEW] Create sub-packages for `presentation/screens` (Splash, Login, etc.).

### 3. Core Components
- [NEW] `CivicConnectApp`: Hilt Application class.
- [MODIFY] `MainActivity`: Update with `@AndroidEntryPoint` and proper package name.
- [MODIFY] `AndroidManifest.xml`: Add permissions (Internet, Camera, Location, Notifications) and register `CivicConnectApp`.

### 4. Dependency Injection Setup
- [NEW] `NetworkModule`: Hilt module for Retrofit/OkHttp.
- [NEW] `DatabaseModule`: Hilt module for Room.
- [NEW] `RepositoryModule`: Hilt module for repository bindings.

## Verification Plan

### Automated Tests
- `gradlew assembleDebug` to ensure the project builds correctly with new dependencies and structure.

### Manual Verification
- Verify that the application launches on a device/emulator.
- Check that the Hilt integration is working (app doesn't crash on start).
