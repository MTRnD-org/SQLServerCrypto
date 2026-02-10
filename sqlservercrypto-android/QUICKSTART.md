# Quick Reference - Building and Using AAR

## Build Commands

### Quick Build (Recommended)
```bash
# Linux/Mac
cd /path/to/SQLServerCrypto
./build-aar.sh release

# Windows
cd C:\path\to\SQLServerCrypto
build-aar.bat release
```

### Using Gradle Directly
```bash
# From project root
./gradlew :sqlservercrypto-android:buildAarRelease

# Or navigate to the module
cd sqlservercrypto-android
../gradlew buildAarRelease
```

### Build Options
- `release` - Build production-ready AAR
- `debug` - Build debug AAR with debugging symbols
- `both` - Build both release and debug
- `clean` - Clean before building

## Output Location

**Release AAR:**
```
sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar
```

**Debug AAR:**
```
sqlservercrypto-android/build/outputs/aar/debug/sqlservercrypto-android-debug-1.0.0.aar
```

## Using the AAR in Your Project

### Step 1: Copy AAR File
Copy the AAR to your Android project:
```
YourAndroidProject/
  app/
    libs/
      sqlservercrypto-android-1.0.0.aar  ← Place it here
```

### Step 2: Update build.gradle

**Method 1 - Simple (Recommended):**
```gradle
dependencies {
    implementation files('libs/sqlservercrypto-android-1.0.0.aar')
}
```

**Method 2 - Using flatDir:**
```gradle
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation(name: 'sqlservercrypto-android-1.0.0', ext: 'aar')
}
```

### Step 3: Sync Project
Click "Sync Now" in Android Studio or run:
```bash
./gradlew sync
```

## Basic Usage Example

```java
import org.mtrnd.sqlservercrypto.SQLServerCryptoMethod;
import org.mtrnd.sqlservercrypto.HexString;

public class Example {
    public void encryptDecrypt() {
        String passphrase = "mypassword";
        String cleartext = "Hello World!";
        
        // Encrypt
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
            passphrase, 
            cleartext
        );
        System.out.println("Encrypted: " + encrypted.toString());
        
        // Decrypt
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(
            passphrase, 
            encrypted.toString()
        );
        System.out.println("Decrypted: " + decrypted);
    }
}
```

## Version Information

Current Version: **1.0.0**

To update the version:
1. Edit `sqlservercrypto-android/build.gradle`
2. Change `LIBRARY_VERSION = '1.0.0'` to your new version
3. Rebuild the AAR

## Troubleshooting

### AAR not found after adding to libs
- Make sure you synced your project after adding the AAR
- Verify the AAR file is in the correct `app/libs/` folder
- Check that the filename in `build.gradle` matches the actual file

### Import errors in code
- Ensure you've synced your Gradle files
- Clean and rebuild your project: `Build > Clean Project` then `Build > Rebuild Project`

### Build fails with "SDK not found"
Set the Android SDK path:
```bash
export ANDROID_HOME=/path/to/android/sdk  # Linux/Mac
set ANDROID_HOME=C:\path\to\android\sdk   # Windows
```

## More Information

- **Full Build Guide:** [../../BUILD_INSTRUCTIONS.md](../../BUILD_INSTRUCTIONS.md)
- **Library Documentation:** [README.md](README.md)
- **Main Project README:** [../../README.md](../../README.md)
