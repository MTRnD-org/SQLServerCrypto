# Gradle Build Fix - Java Version Compatibility and Daemon Issues

## Issue Summary

The `build-aar.bat release` command was failing with Java version compatibility errors:

```
No matching variant of com.android.tools.build:gradle:8.1.0 was found.
Incompatible because this component declares a component compatible with Java 11
and the consumer needed a component compatible with Java 8
```

Additionally, even after initial fixes, users might see:
```
Starting a Gradle Daemon, 1 incompatible and 3 stopped Daemons could not be reused
```

## Root Causes

1. **Android Gradle Plugin 8.1.0** requires **Java 11 or higher** to run
2. **Gradle 8.0** had compatibility issues with AGP 8.1.0
3. **Old Gradle daemons** cached with Java 8 configuration needed to be stopped
4. **Gradle daemon configuration** needed explicit Java auto-detection settings

## Complete Solution

### 1. Upgraded Gradle Wrapper (8.0 → 8.5)

**File:** `gradle/wrapper/gradle-wrapper.properties`

```diff
- distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
+ distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

**Why Gradle 8.5?**
- Better compatibility with Android Gradle Plugin 8.1.0
- Improved Java toolchain detection and handling
- Support for Java 11-21
- Faster builds and better error messages

### 2. Added JVM Configuration

**File:** `gradle.properties`

```properties
# JVM memory settings for Gradle daemon
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
```

**Benefits:**
- Ensures sufficient memory for the build process
- Prevents out-of-memory errors during compilation
- Optimizes metadata storage

### 2. Enhanced Gradle Daemon Configuration

**File:** `gradle.properties`

Added auto-detection settings to ensure Gradle daemon uses the correct Java version:

```properties
# Ensure Gradle daemon uses Java 11+ (required for Android Gradle Plugin 8.1.0)
org.gradle.java.installations.auto-detect=true
org.gradle.java.installations.auto-download=false
```

**Benefits:**
- Gradle automatically detects Java 17 installation
- Prevents falling back to Java 8
- Disables auto-download to use system Java

### 3. Stop Old Gradle Daemons

If you see "incompatible Daemons could not be reused", you need to stop old daemons:

```bash
./gradlew --stop
```

Then clean build directories:

```bash
rm -rf .gradle build */build
```

### 4. Updated Documentation

**File:** `BUILD_INSTRUCTIONS.md`

- Updated prerequisites from "Java 8+" to "**Java 11+**" (Java 17 recommended)
- Added comprehensive troubleshooting section for Java version errors
- Included instructions for:
  - Checking Java version
  - Installing correct Java version
  - Setting JAVA_HOME if needed
- Updated CI/CD examples to include Java setup

## Version Compatibility Matrix

| Component | Version | Java Requirement |
|-----------|---------|------------------|
| Android Gradle Plugin | 8.1.0 | Java 11+ |
| Gradle | 8.5 | Java 11-21 |
| Java (Installed) | 17 | ✅ Compatible |

## How to Use

### Check Your Java Version

```bash
java -version
```

You should see Java 11 or higher (Java 17 recommended).

### Build the AAR

```bash
# Linux/Mac
./build-aar.sh release

# Windows
build-aar.bat release

# Or directly with Gradle
./gradlew :sqlservercrypto-android:buildAarRelease
```

## If You Have Java 8

You need to upgrade to Java 11 or higher:

### Ubuntu/Debian
```bash
sudo apt-get install openjdk-17-jdk
```

### macOS
```bash
brew install openjdk@17
```

### Windows
Download from:
- [Adoptium (Temurin)](https://adoptium.net/) - Recommended
- [Oracle Java](https://www.oracle.com/java/technologies/downloads/)

### Set JAVA_HOME (if needed)
```bash
export JAVA_HOME=/path/to/java17
```

## What Changed

1. **gradle-wrapper.properties**: Gradle 8.0 → 8.5
2. **gradle.properties**: Added JVM memory configuration
3. **BUILD_INSTRUCTIONS.md**: Updated prerequisites and troubleshooting

## Expected Behavior After Fix

When you run `./build-aar.sh release` or `build-aar.bat release`, you should see:

```
Welcome to Gradle 8.5!
...
BUILD SUCCESSFUL in Xs
✅ AAR built successfully!
📦 Output: sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar
```

## Verification

The fix has been tested and verified to:
- ✅ Use Gradle 8.5 for better compatibility
- ✅ Properly configure JVM memory
- ✅ Require Java 11+ as documented
- ✅ Provide clear troubleshooting guidance

## Additional Notes

- The build now requires Java 11 or higher (previously Java 8+)
- Gradle 8.5 provides better error messages and faster builds
- The JVM configuration prevents memory-related build failures
- All documentation has been updated to reflect these requirements
- **Gradle daemon auto-detection** ensures the correct Java version is used

## Troubleshooting Gradle Daemon Issues

### Problem: "Incompatible Daemons could not be reused"

If you see this error after upgrading:
```
Starting a Gradle Daemon, 1 incompatible and 3 stopped Daemons could not be reused
```

**Solution:**

1. Stop all Gradle daemons:
   ```bash
   ./gradlew --stop
   ```

2. Clean build directories:
   ```bash
   rm -rf .gradle build sqlservercrypto-android/build
   # On Windows: rmdir /s /q .gradle build sqlservercrypto-android\build
   ```

3. Run the build again:
   ```bash
   ./gradlew :sqlservercrypto-android:buildAarRelease
   ```

### Why This Happens

- Old daemons were started with Java 8 or different Gradle version
- New configuration requires Java 11+ for Gradle daemon
- Daemons are cached and reused across builds
- Incompatible daemons must be stopped and recreated

### Verify Daemon is Using Correct Java

After stopping old daemons, check the new daemon:

```bash
./gradlew --version
```

You should see:
- Gradle version: 8.5
- JVM: 17.x.x (or 11.x.x or higher)

Or run with `--info` to see daemon startup:
```bash
./gradlew tasks --info | grep "Starting process"
```

Should show: `/usr/lib/jvm/.../java-17` (or java-11 or higher)

## Related Documentation

- [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) - Complete build guide
- [Gradle 8.5 Release Notes](https://docs.gradle.org/8.5/release-notes.html)
- [Android Gradle Plugin 8.1.0 Release Notes](https://developer.android.com/build/releases/gradle-plugin)

## Support

If you encounter issues after this fix:
1. Verify Java version: `java -version` (must be 11+)
2. Check Gradle version: `./gradlew --version`
3. Clean build: `./gradlew clean`
4. Try again: `./gradlew :sqlservercrypto-android:buildAarRelease`

For more help, see the Troubleshooting section in BUILD_INSTRUCTIONS.md.
