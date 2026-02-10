# SQLServerCrypto

This repository provides implementations for SQL Server's [EncryptByPassPhrase](https://docs.microsoft.com/en-us/sql/t-sql/functions/encryptbypassphrase-transact-sql?view=sql-server-2017) and [DecryptByPassPhrase](https://docs.microsoft.com/en-us/sql/t-sql/functions/decryptbypassphrase-transact-sql?view=sql-server-2017) functions.

## Available Implementations

- **C# (.NET)** - Original implementation in the `SQLServerCrypto` folder
- **Android (Java)** - Android library version in the `sqlservercrypto-android` folder (see [Android README](sqlservercrypto-android/README.md))

Both implementations are fully compatible with SQL Server's encryption/decryption functions. The result returned by any implementation can be decrypted using SQL Server's `DecryptByPassPhrase` function, and vice versa.

## Quick Start - Build as Library / 構建成庫文件

### 構建Java庫 (JAR) / Build Java Library (JAR)

**最簡單！無需Android SDK！/ Easiest! No Android SDK needed!**

```bash
# Linux/Mac
./build-library.sh

# Windows
build-library.bat
```

**輸出 / Output:** `library-build/sqlservercrypto-1.0.0.jar`

### 構建Android庫 (AAR) / Build Android Library (AAR)

**用於Android應用 / For Android apps**

```bash
# Linux/Mac
./build-aar.sh release

# Windows
build-aar.bat release
```

**輸出 / Output:** `sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar`

📖 **完整構建指南 / Complete Build Guide:** [LIBRARY_BUILD_GUIDE.md](LIBRARY_BUILD_GUIDE.md)

## Quick Start - Testing Without Android SDK / 無需Android SDK的測試

**Can't build the Java/Android library? Test it without Android SDK!**
**無法構建？直接測試，不需要Android SDK！**

```bash
# Linux/Mac
./test-standalone.sh

# Windows
test-standalone.bat
```

This will compile the library, run all tests, and create a JAR file - **no Android SDK required!**

📖 See [TESTING.md](TESTING.md) for detailed testing guide (includes Chinese/中文說明)

## Overview

The *SQLServerCryptoMethod.EncryptByPassPhrase* method requires specifying an encryption version - *SQLServerCryptoVersion*. It applies to the encryption / decryption algorithm used by SQL Server.
The default encryption version for the *SQLServerCryptoMethod.EncryptByPassPhrase* is *SQLServerCryptoVersion.V1*.

The *SQLServerCryptoMethod.DecryptByPassPhrase* function reads the version number from the ciphertext.

|                           |                  |                                   |
| ------------------------- | ---------------- | --------------------------------- |
| SQLServerCryptoVersion.V1 | TripleDES / SHA1 | SQL Server 2008 - SQL Server 2016 |
| SQLServerCryptoVersion.V2 | AES256 / SHA256  | SQL Server 2017+                  |

You can also specify *add_authenticator* and *authenticator* arguments, just like in SQL Server methods. 

## Remarks
The encoding of the string passed to the *SQLServerCryptoMethod.EncryptByPassPhrase* function will be changed to ASCII, therefore, the same string of characters before encryption and after decrypting with the same password can be different.

**If the text is encrypted with the authenticator, it can be decrypted without knowing the authenticator string. See example below.**

## Examples

### C# Examples

#### 1. Encryption on SQL Server / decryption in C# code.
**SQL**
```
DECLARE @passphrase varchar(max) = 'password1234'

SELECT EncryptByPassPhrase(@passphrase, 'Hello World.')
```
**Result:** 0x010000003296649D6782CFD72B8145A07F2C7D7FE3D8B80CF48DA419E94FABC90EEB928D

**C#**
```
var passphrase = "password1234";
var decryptedText = SQLServerCryptoMethod.DecryptByPassPhrase(@passphrase, "0x010000003296649D6782CFD72B8145A07F2C7D7FE3D8B80CF48DA419E94FABC90EEB928D");

System.Console.WriteLine(decryptedText);
```
**Result:** Hello World.

#### 2. Encryption in C# code / decryption on SQL Server.
**C#**
```
var passphrase = "password1234";
var encrypted = SQLServerCryptoMethod.EncryptByPassPhrase(@passphrase, "Hello World.");

System.Console.WriteLine(encrypted);
```
**Result:** 0x01000000d743db6ccd7e0e63091fa787c65dead5ea14c440da9ee0f6f60e74520a35c076

**SQL**
```
DECLARE @passphrase varchar(max) = 'password1234'

SELECT cast(DecryptByPassPhrase(@passphrase, 0x01000000d743db6ccd7e0e63091fa787c65dead5ea14c440da9ee0f6f60e74520a35c076) as varchar)
```
**Result:** Hello World.

#### 3. Encryption with authenticator / decryption without authenticator.
**SQL**
```
DECLARE @EncryptionBytes varbinary(max) =  ENCRYPTBYPASSPHRASE('test1234','Hello world.',1,'authenticator')

SELECT @EncryptionBytes

```
**Result:** 0x0100000038C94F7223E0BA2F772B611857F9D45DAF781607CC77F4A856CF08CC2DB9DF14A0593259CB3A4A2BFEDB485C002CA04B6A98BEB1B47EB107

**C#**
```
var ciphertext = "0x0100000038C94F7223E0BA2F772B611857F9D45DAF781607CC77F4A856CF08CC2DB9DF14A0593259CB3A4A2BFEDB485C002CA04B6A98BEB1B47EB107";
var password = "test1234";
var decrypted = SQLServerCryptoMethod.DecryptByPassPhraseWithoutVerification(password, ciphertext);

Console.WriteLine(decrypted);
```
**Result:** Hello world.

### Android (Java) Examples

#### 1. Basic encryption and decryption
**Java**
```java
String passphrase = "password1234";
String cleartext = "Hello World.";

HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, cleartext);
System.out.println(encrypted.toString());

String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, encrypted.toString());
System.out.println(decrypted);
```

#### 2. Encryption on SQL Server / decryption in Android
**SQL**
```sql
DECLARE @passphrase varchar(max) = 'password1234'
SELECT EncryptByPassPhrase(@passphrase, 'Hello World.')
```
**Result:** 0x010000003296649D6782CFD72B8145A07F2C7D7FE3D8B80CF48DA419E94FABC90EEB928D

**Java**
```java
String passphrase = "password1234";
String ciphertext = "0x010000003296649D6782CFD72B8145A07F2C7D7FE3D8B80CF48DA419E94FABC90EEB928D";
String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, ciphertext);
System.out.println(decrypted); // Hello World.
```

For more Android examples, see the [Android README](sqlservercrypto-android/README.md).


## Reference
- https://docs.microsoft.com/en-us/sql/t-sql/functions/encryptbypassphrase-transact-sql?view=sql-server-2017
- https://docs.microsoft.com/en-us/sql/t-sql/functions/decryptbypassphrase-transact-sql?view=sql-server-2017
- https://blogs.msdn.microsoft.com/sqlsecurity/2009/03/30/sql-server-encryptbykey-cryptographic-message-description/
- https://stackoverflow.com/questions/21684733/c-sharp-decrypt-bytes-from-sql-server-encryptbypassphrase
