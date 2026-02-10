# Building AAR for SQLServerCrypto Android Library

This guide provides instructions for building the AAR (Android Archive) file for distribution.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Android SDK (automatically downloaded by Gradle if not present)
- Gradle (included via wrapper - `./gradlew`)

## Quick Start

### Build Release AAR

```bash
./gradlew :sqlservercrypto-android:buildAarRelease
```

This will:
1. Build the library in release mode
2. Generate the AAR file
3. Copy it to `sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar`

### Build Debug AAR

```bash
./gradlew :sqlservercrypto-android:buildAarDebug
```

This will generate: `sqlservercrypto-android/build/outputs/aar/debug/sqlservercrypto-android-debug-1.0.0.aar`

### Build Both Release and Debug

```bash
./gradlew :sqlservercrypto-android:buildAarRelease :sqlservercrypto-android:buildAarDebug
```

## Output Location

After building, the AAR files will be in:
```
sqlservercrypto-android/
  └── build/
      └── outputs/
          └── aar/
              ├── release/
              │   └── sqlservercrypto-android-1.0.0.aar
              └── debug/
                  └── sqlservercrypto-android-debug-1.0.0.aar
```

## Available Gradle Tasks

- `assembleRelease` - Builds the release AAR (outputs to `build/outputs/aar/`)
- `assembleDebug` - Builds the debug AAR (outputs to `build/outputs/aar/`)
- `buildAarRelease` - Builds and copies the release AAR with version name
- `buildAarDebug` - Builds and copies the debug AAR with version name
- `clean` - Cleans the build directory

## Build Options

### Clean Build

To ensure a fresh build, first clean the project:

```bash
./gradlew clean
./gradlew :sqlservercrypto-android:buildAarRelease
```

### Build with Specific Java Version

If you have multiple Java versions installed:

```bash
JAVA_HOME=/path/to/jdk ./gradlew :sqlservercrypto-android:buildAarRelease
```

### View All Available Tasks

```bash
./gradlew :sqlservercrypto-android:tasks
```

## Versioning

The current library version is defined in `sqlservercrypto-android/build.gradle`:

```gradle
ext {
    LIBRARY_VERSION = '1.0.0'
    LIBRARY_GROUP = 'org.mtrnd'
    LIBRARY_ARTIFACT = 'sqlservercrypto-android'
}
```

To release a new version, update the `LIBRARY_VERSION` value and rebuild.

## Using the AAR

See the [README.md](sqlservercrypto-android/README.md) for instructions on how to integrate the AAR file into your Android project.

## Troubleshooting

### Build Fails with "SDK not found"

Make sure you have the Android SDK installed. Gradle will attempt to download it automatically, but you may need to set the `ANDROID_HOME` environment variable:

```bash
export ANDROID_HOME=/path/to/android/sdk
./gradlew :sqlservercrypto-android:buildAarRelease
```

### Build Fails with Memory Error

Increase Gradle's memory allocation in `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
```

### Permission Denied on gradlew

Make the Gradle wrapper executable:

```bash
chmod +x gradlew
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Build AAR
  run: ./gradlew :sqlservercrypto-android:buildAarRelease

- name: Upload AAR
  uses: actions/upload-artifact@v3
  with:
    name: sqlservercrypto-android-aar
    path: sqlservercrypto-android/build/outputs/aar/release/*.aar
```

### Jenkins Example

```groovy
stage('Build AAR') {
    steps {
        sh './gradlew :sqlservercrypto-android:buildAarRelease'
    }
}
stage('Archive AAR') {
    steps {
        archiveArtifacts artifacts: 'sqlservercrypto-android/build/outputs/aar/release/*.aar'
    }
}
```
