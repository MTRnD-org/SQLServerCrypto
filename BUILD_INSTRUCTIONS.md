# Building AAR for SQLServerCrypto Android Library

This guide provides instructions for building the AAR (Android Archive) file for distribution.

## Prerequisites

- **Java Development Kit (JDK) 11 or higher** (Java 17 recommended)
  - Android Gradle Plugin 8.1.0 requires Java 11+
  - Check your Java version: `java -version`
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

### Build Fails with "Incompatible Daemons could not be reused"

**Error:** `Starting a Gradle Daemon, 1 incompatible and 3 stopped Daemons could not be reused`

This happens when old Gradle daemons are cached with incompatible Java versions.

**Solution:**

1. Stop all Gradle daemons:
   ```bash
   ./gradlew --stop
   ```

2. Clean build directories:
   ```bash
   # Linux/Mac
   rm -rf .gradle build sqlservercrypto-android/build
   
   # Windows
   rmdir /s /q .gradle build sqlservercrypto-android\build
   ```

3. Run the build again:
   ```bash
   ./gradlew :sqlservercrypto-android:buildAarRelease
   ```

The new daemon will start with Java 17 and the correct configuration.

### Build Fails with Java Version Error

**Error:** `No matching variant of com.android.tools.build:gradle:8.1.0 was found...Incompatible because this component declares a component for use during compile-time, compatible with Java 11`

**Solution:** Android Gradle Plugin 8.1.0 requires Java 11 or higher. 

1. Check your Java version:
   ```bash
   java -version
   ```

2. If you have Java 8, upgrade to Java 11 or higher:
   - **Ubuntu/Debian:** `sudo apt-get install openjdk-17-jdk`
   - **macOS:** `brew install openjdk@17`
   - **Windows:** Download from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)

3. Set JAVA_HOME (if needed):
   ```bash
   export JAVA_HOME=/path/to/java17
   ```

4. The project has been configured to use Gradle 8.5, which properly supports Java 11+ with Android Gradle Plugin 8.1.0.

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
- name: Set up JDK 17
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'temurin'

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
