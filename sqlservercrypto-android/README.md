# SQLServerCrypto Android Library

Android version of the SQLServerCrypto library that provides encryption and decryption methods compatible with SQL Server's `EncryptByPassPhrase` and `DecryptByPassPhrase` functions.

## Features

- **Full compatibility** with SQL Server encryption/decryption functions
- **Multiple versions** support:
  - V1: TripleDES/SHA1 (SQL Server 2008 - SQL Server 2016)
  - V2: AES256/SHA256 (SQL Server 2017+)
- **Authenticator support** for enhanced security
- **Easy-to-use API** with Java

## Installation

### Gradle

Add the library to your Android project by including the module in your `settings.gradle`:

```gradle
include ':sqlservercrypto-android'
```

Then add the dependency in your app's `build.gradle`:

```gradle
dependencies {
    implementation project(':sqlservercrypto-android')
}
```

## Usage

### Basic Encryption/Decryption

```java
import org.mtrnd.sqlservercrypto.SQLServerCryptoMethod;
import org.mtrnd.sqlservercrypto.HexString;

// Encrypt
String passphrase = "password1234";
String cleartext = "Hello World.";
HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, cleartext);
System.out.println(encrypted.toString()); // 0x010000...

// Decrypt
String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, encrypted.toString());
System.out.println(decrypted); // Hello World.
```

### Encryption with SQL Server 2017+ (Version 2)

```java
import org.mtrnd.sqlservercrypto.SQLServerCryptoVersion;

HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
    passphrase, 
    cleartext, 
    SQLServerCryptoVersion.V2
);
```

### Using Authenticator

```java
// Encrypt with authenticator
HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
    "password1234",
    "Hello World.",
    1,  // add_authenticator
    "my_authenticator"
);

// Decrypt with authenticator
String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(
    "password1234",
    encrypted.toString(),
    1,  // add_authenticator
    "my_authenticator"
);
```

### Decrypt without Verification

If you need to decrypt data without verifying the authenticator:

```java
String decrypted = SQLServerCryptoMethod.decryptByPassPhraseWithoutVerification(
    "password1234",
    ciphertext
);
```

## Examples

### 1. Encryption on SQL Server / Decryption in Android

**SQL:**
```sql
DECLARE @passphrase varchar(max) = 'password1234'
SELECT EncryptByPassPhrase(@passphrase, 'Hello World.')
```
**Result:** `0x010000003296649D6782CFD72B8145A07F2C7D7FE3D8B80CF48DA419E94FABC90EEB928D`

**Android (Java):**
```java
String passphrase = "password1234";
String ciphertext = "0x010000003296649D6782CFD72B8145A07F2C7D7FE3D8B80CF48DA419E94FABC90EEB928D";
String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, ciphertext);
System.out.println(decrypted); // Hello World.
```

### 2. Encryption in Android / Decryption on SQL Server

**Android (Java):**
```java
String passphrase = "password1234";
HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, "Hello World.");
System.out.println(encrypted.toString()); // 0x01000000...
```

**SQL:**
```sql
DECLARE @passphrase varchar(max) = 'password1234'
SELECT CAST(DecryptByPassPhrase(@passphrase, 0x01000000...) AS varchar)
```
**Result:** `Hello World.`

## API Reference

### SQLServerCryptoMethod

Main class providing encryption and decryption methods.

#### Methods

- `encryptByPassPhrase(String passphrase, String cleartext)` - Encrypt with default V1
- `encryptByPassPhrase(String passphrase, String cleartext, SQLServerCryptoVersion version)` - Encrypt with specific version
- `encryptByPassPhrase(String passphrase, String cleartext, int add_authenticator, String authenticator)` - Encrypt with authenticator
- `encryptByPassPhrase(String passphrase, String cleartext, int add_authenticator, String authenticator, SQLServerCryptoVersion version)` - Full options
- `decryptByPassPhrase(String passphrase, String ciphertext)` - Basic decryption
- `decryptByPassPhrase(String passphrase, String ciphertext, int add_authenticator, String authenticator)` - Decrypt with authenticator
- `decryptByPassPhraseWithoutVerification(String passphrase, String ciphertext)` - Decrypt without verification

### SQLServerCryptoVersion

Enum for SQL Server crypto versions:
- `V1` - TripleDES/SHA1 (SQL Server 2008-2016)
- `V2` - AES256/SHA256 (SQL Server 2017+)

## Requirements

- Android API Level 21 (Android 5.0) or higher
- Java 8 or higher

## Notes

- The encoding of strings is ASCII, matching SQL Server's behavior
- Maximum cleartext size: 8000 bytes
- Maximum authenticator size: 128 bytes
- If text is encrypted with an authenticator, it can be decrypted without knowing the authenticator using `decryptByPassPhraseWithoutVerification`

## References

- [SQL Server EncryptByPassPhrase](https://docs.microsoft.com/en-us/sql/t-sql/functions/encryptbypassphrase-transact-sql)
- [SQL Server DecryptByPassPhrase](https://docs.microsoft.com/en-us/sql/t-sql/functions/decryptbypassphrase-transact-sql)
- [Original C# Implementation](https://github.com/MTRnD-org/SQLServerCrypto)

## License

See LICENSE file for details.
