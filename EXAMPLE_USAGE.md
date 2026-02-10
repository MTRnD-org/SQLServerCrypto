# Example: Using SQLServerCrypto AAR in Your Project

This example demonstrates how to integrate and use the SQLServerCrypto Android Library AAR in your Android project.

## Project Structure

```
YourAndroidApp/
├── app/
│   ├── libs/
│   │   └── sqlservercrypto-android-1.0.0.aar  ← Add the AAR here
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/yourcompany/yourapp/
│   │               └── MainActivity.java
│   └── build.gradle  ← Configure AAR dependency here
├── build.gradle
└── settings.gradle
```

## Step-by-Step Integration

### 1. Get the AAR File

Build the AAR from the SQLServerCrypto project:
```bash
cd /path/to/SQLServerCrypto
./build-aar.sh release
```

Copy the generated AAR from:
```
SQLServerCrypto/sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar
```

To your project:
```
YourAndroidApp/app/libs/sqlservercrypto-android-1.0.0.aar
```

### 2. Configure build.gradle

**App-level build.gradle** (`app/build.gradle`):

```gradle
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.yourcompany.yourapp'
    compileSdk 34

    defaultConfig {
        applicationId "com.yourcompany.yourapp"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    // SQLServerCrypto Library
    implementation files('libs/sqlservercrypto-android-1.0.0.aar')
    
    // Your other dependencies
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
}
```

### 3. Sync Gradle

In Android Studio: Click **File > Sync Project with Gradle Files**

Or from command line:
```bash
./gradlew sync
```

## Usage Examples

### Example 1: Basic Encryption/Decryption

```java
package com.yourcompany.yourapp;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import org.mtrnd.sqlservercrypto.SQLServerCryptoMethod;
import org.mtrnd.sqlservercrypto.HexString;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SQLServerCrypto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example usage
        encryptDecryptExample();
    }

    private void encryptDecryptExample() {
        try {
            String passphrase = "mySecurePassword123";
            String originalText = "Hello from Android!";
            
            // Encrypt
            HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
                passphrase, 
                originalText
            );
            Log.d(TAG, "Original: " + originalText);
            Log.d(TAG, "Encrypted: " + encrypted.toString());
            
            // Decrypt
            String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(
                passphrase, 
                encrypted.toString()
            );
            Log.d(TAG, "Decrypted: " + decrypted);
            
            // Verify
            if (originalText.equals(decrypted)) {
                Log.d(TAG, "✓ Encryption/Decryption successful!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
        }
    }
}
```

### Example 2: Encrypting User Data

```java
import org.mtrnd.sqlservercrypto.SQLServerCryptoMethod;
import org.mtrnd.sqlservercrypto.HexString;

public class UserDataEncryption {
    private static final String ENCRYPTION_KEY = "your_secure_key_here";
    
    public String encryptUserEmail(String email) {
        try {
            HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
                ENCRYPTION_KEY, 
                email
            );
            return encrypted.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String decryptUserEmail(String encryptedEmail) {
        try {
            return SQLServerCryptoMethod.decryptByPassPhrase(
                ENCRYPTION_KEY, 
                encryptedEmail
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

### Example 3: Working with SQL Server

```java
import org.mtrnd.sqlservercrypto.SQLServerCryptoMethod;
import org.mtrnd.sqlservercrypto.SQLServerCryptoVersion;

public class SqlServerIntegration {
    
    // Decrypt data encrypted by SQL Server
    public String decryptFromSqlServer(String sqlServerHex) {
        try {
            String passphrase = "shared_password";
            return SQLServerCryptoMethod.decryptByPassPhrase(
                passphrase, 
                sqlServerHex
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Encrypt data for SQL Server (using V2 - AES256)
    public String encryptForSqlServer2017(String data) {
        try {
            String passphrase = "shared_password";
            HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
                passphrase, 
                data,
                SQLServerCryptoVersion.V2
            );
            return encrypted.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

### Example 4: Using with Authenticator

```java
import org.mtrnd.sqlservercrypto.SQLServerCryptoMethod;
import org.mtrnd.sqlservercrypto.HexString;

public class AuthenticatedEncryption {
    
    public String encryptWithAuth(String data, String authenticator) {
        try {
            HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
                "password123",
                data,
                1,  // add_authenticator = 1 (enabled)
                authenticator
            );
            return encrypted.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String decryptWithAuth(String encryptedData, String authenticator) {
        try {
            return SQLServerCryptoMethod.decryptByPassPhrase(
                "password123",
                encryptedData,
                1,  // add_authenticator = 1 (enabled)
                authenticator
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Decrypt without knowing the authenticator
    public String decryptWithoutAuth(String encryptedData) {
        try {
            return SQLServerCryptoMethod.decryptByPassPhraseWithoutVerification(
                "password123",
                encryptedData
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

## Testing Your Integration

### Unit Test Example

Create a test class in `app/src/test/java/`:

```java
package com.yourcompany.yourapp;

import org.junit.Test;
import static org.junit.Assert.*;

import org.mtrnd.sqlservercrypto.SQLServerCryptoMethod;
import org.mtrnd.sqlservercrypto.HexString;
import org.mtrnd.sqlservercrypto.SQLServerCryptoVersion;

public class SQLServerCryptoTest {
    
    @Test
    public void testBasicEncryptionDecryption() {
        String passphrase = "test123";
        String cleartext = "Test Data";
        
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
            passphrase, 
            cleartext
        );
        assertNotNull(encrypted);
        
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(
            passphrase, 
            encrypted.toString()
        );
        assertEquals(cleartext, decrypted);
    }
    
    @Test
    public void testV2Encryption() {
        String passphrase = "test123";
        String cleartext = "Test Data V2";
        
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
            passphrase, 
            cleartext,
            SQLServerCryptoVersion.V2
        );
        assertNotNull(encrypted);
        
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(
            passphrase, 
            encrypted.toString()
        );
        assertEquals(cleartext, decrypted);
    }
}
```

Run tests:
```bash
./gradlew test
```

## Troubleshooting

### Import Errors

If you see red underlines on import statements:
1. Make sure the AAR is in `app/libs/`
2. Sync your project: `File > Sync Project with Gradle Files`
3. Clean and rebuild: `Build > Clean Project` then `Build > Rebuild Project`

### ClassNotFoundException at Runtime

Ensure the AAR is included in your APK:
- Check `build.gradle` has the correct dependency
- Use `implementation` not `compileOnly`
- Rebuild the project

### ProGuard Issues (Release Builds)

If you use ProGuard/R8, add to `proguard-rules.pro`:

```proguard
# Keep SQLServerCrypto classes
-keep class org.mtrnd.sqlservercrypto.** { *; }
-keepclassmembers class org.mtrnd.sqlservercrypto.** { *; }
```

## Additional Resources

- **Library Documentation:** [SQLServerCrypto Android README](../sqlservercrypto-android/README.md)
- **Build Instructions:** [BUILD_INSTRUCTIONS.md](../BUILD_INSTRUCTIONS.md)
- **Quick Start:** [QUICKSTART.md](../sqlservercrypto-android/QUICKSTART.md)

## Need Help?

- Check the [main README](../README.md) for general information
- Review the SQL Server documentation for compatibility details
- Ensure your passphrase is the same on both Android and SQL Server
