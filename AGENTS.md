# NumPicker

Android number picker widget built with Jetpack Compose and Canvas.

## Cursor Cloud specific instructions

### Environment

- **JDK 11** is required for Gradle/AGP 7.x builds (`JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64`).
- **Android SDK** is at `/opt/android-sdk` (`ANDROID_HOME`/`ANDROID_SDK_ROOT`).
- Environment variables are configured in `~/.bashrc`.

### Build / Lint / Test

Use **debug** variants only — the release variant has a pre-existing compilation error (missing `@OptIn` for experimental animation API).

```
./gradlew assembleDebug      # build debug APK
./gradlew lintDebug           # lint checks
./gradlew testDebugUnitTest   # unit tests
```

The built APK is at `app/build/outputs/apk/debug/app-debug.apk`.

### Emulator

KVM is **not available** in the cloud VM, so the Android emulator cannot run. An AVD named `test_device` (Pixel 4, API 32) is pre-configured but requires KVM to boot. To run the app, install the APK on a connected device via `adb install`.
