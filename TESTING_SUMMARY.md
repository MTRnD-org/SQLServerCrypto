# Testing Summary - 測試摘要

## Problem / 問題
用戶無法構建Java/Android庫並詢問如何測試。
(User cannot build Java/Android library and asked how to test it.)

**原始問題:** "我build 不了這個java ,可以如何testing 這個android library"

## Solution / 解決方案

### ✅ 獨立測試（無需Android SDK）
### ✅ Standalone Testing (No Android SDK Required)

現在您可以使用純Java測試此庫，無需安裝Android SDK或Android Studio！
(You can now test this library with pure Java, no Android SDK or Android Studio installation needed!)

## Quick Start / 快速開始

### 1. 運行自動化測試 / Run Automated Tests

**Linux/Mac:**
```bash
./test-standalone.sh
```

**Windows:**
```cmd
test-standalone.bat
```

**結果 / Result:**
```
============================================
  SQLServerCrypto - Standalone Test Runner
============================================

📁 Setting up build directories...
📦 Downloading JUnit 4.13.2...
🔨 Compiling library source code...
✅ Library compiled successfully!
🔨 Compiling test code...
✅ Tests compiled successfully!

🧪 Running tests...

JUnit version 4.13.2
........
Time: 0.127

OK (8 tests)

✅ All tests passed!
```

### 2. 運行交互式演示 / Run Interactive Demo

```bash
# 首先編譯 / First compile
./test-standalone.sh

# 然後運行演示 / Then run demo
cd standalone-test-build/classes
java org.mtrnd.sqlservercrypto.demo.InteractiveDemo
```

**示例輸出 / Sample Output:**
```
==========================================
  SQLServerCrypto Interactive Demo
  Test the library without Android SDK
==========================================

Select a test to run:
 1. Basic Encryption/Decryption (V1)
 2. AES256 Encryption (V2)
 3. Encryption with Authenticator
 4. SQL Server Compatibility Test
 5. HexString Utilities
 6. Run All Tests
 0. Exit

Enter your choice: 6

==========================================
Running All Tests
==========================================

1. Basic Encryption...       ✓ Passed
2. V2 Encryption...          ✓ Passed
3. Authenticator...          ✓ Passed
4. SQL Server Compatibility... ✓ Passed
5. HexString...              ✓ Passed

Test Results: 5 passed, 0 failed
```

## What You Get / 您將獲得

### 1. 編譯的類文件 / Compiled Class Files
位置 / Location: `standalone-test-build/classes/`

使用方法 / Usage:
```bash
java -cp standalone-test-build/classes YourProgram
```

### 2. JAR文件 / JAR File
位置 / Location: `standalone-test-build/sqlservercrypto-android-standalone.jar`

使用方法 / Usage:
```bash
java -cp standalone-test-build/sqlservercrypto-android-standalone.jar:. YourProgram
```

### 3. 測試結果 / Test Results
所有8個單元測試都通過！
(All 8 unit tests pass!)

- ✅ 基本加密/解密 (Basic encryption/decryption)
- ✅ V1版本 (TripleDES/SHA1)
- ✅ V2版本 (AES256/SHA256)
- ✅ 帶認證器 (With authenticator)
- ✅ 無認證器解密 (Decrypt without authenticator)
- ✅ HexString工具 (HexString utilities)
- ✅ SQL Server兼容性 (SQL Server compatibility)
- ✅ 錯誤處理 (Error handling)

## Requirements / 要求

**只需要 / Only Need:**
- ✅ Java JDK 8+ (javac and java commands)
- ✅ Internet connection (to download JUnit automatically)

**不需要 / NOT Needed:**
- ❌ Android SDK
- ❌ Android Studio  
- ❌ Gradle (full installation)
- ❌ 複雜配置 (Complex configuration)

## Documentation / 文檔

### 詳細指南 / Detailed Guides
- 📖 [TESTING.md](TESTING.md) - 完整測試指南（中英文）(Complete testing guide - Chinese/English)
- 📖 [README.md](README.md) - 主要文檔 (Main documentation)
- 📖 [QUICKSTART.md](sqlservercrypto-android/QUICKSTART.md) - 快速入門 (Quick start)

### 腳本 / Scripts
- 🔧 `test-standalone.sh` - Linux/Mac測試腳本
- 🔧 `test-standalone.bat` - Windows測試腳本
- 🔧 `build-aar.sh` - 構建AAR文件（如果需要）
- 🔧 `build-aar.bat` - 構建AAR文件（Windows）

### 源代碼 / Source Code
- 📁 `sqlservercrypto-android/src/main/java/` - 庫源代碼
- 📁 `sqlservercrypto-android/src/test/java/` - 測試代碼
- 🎯 `InteractiveDemo.java` - 交互式演示程序

## Example Usage / 使用示例

### 創建測試文件 / Create Test File

```java
// MyTest.java
import org.mtrnd.sqlservercrypto.*;

public class MyTest {
    public static void main(String[] args) {
        // 測試基本功能 / Test basic functionality
        String password = "mypassword";
        String data = "Hello World!";
        
        // 加密 / Encrypt
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
            password, data);
        System.out.println("Encrypted: " + encrypted);
        
        // 解密 / Decrypt
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(
            password, encrypted.toString());
        System.out.println("Decrypted: " + decrypted);
        
        // 驗證 / Verify
        System.out.println("Match: " + data.equals(decrypted));
    }
}
```

### 編譯和運行 / Compile and Run

```bash
# 1. 運行獨立測試腳本 / Run standalone test script
./test-standalone.sh

# 2. 編譯您的測試 / Compile your test
javac -cp standalone-test-build/classes MyTest.java

# 3. 運行測試 / Run test
java -cp .:standalone-test-build/classes MyTest
```

## Troubleshooting / 故障排除

### 問題：找不到javac / Problem: javac not found

**解決方案 / Solution:**
```bash
# 安裝Java JDK / Install Java JDK

# Ubuntu/Debian:
sudo apt-get install default-jdk

# macOS:
brew install openjdk

# Windows: Download from Oracle or AdoptOpenJDK
```

### 問題：測試失敗 / Problem: Tests fail

**解決方案 / Solution:**
1. 確保Java版本 ≥ 8 (Check Java version ≥ 8)
   ```bash
   java -version
   ```

2. 清理並重試 (Clean and retry)
   ```bash
   rm -rf standalone-test-build
   ./test-standalone.sh
   ```

3. 檢查網絡連接（用於下載JUnit）(Check internet - for downloading JUnit)

## Summary / 總結

**之前 / Before:**
- ❌ 無法構建 (Cannot build)
- ❌ 需要Android SDK (Needs Android SDK)
- ❌ 配置複雜 (Complex setup)

**現在 / Now:**
- ✅ 一個命令測試 (One command to test)
- ✅ 只需要Java (Just needs Java)
- ✅ 快速簡單 (Fast and simple)
- ✅ 跨平台支持 (Cross-platform)
- ✅ 中英文文檔 (Chinese/English docs)

## Need Help? / 需要幫助？

1. 閱讀 [TESTING.md](TESTING.md) 獲取詳細說明
   (Read [TESTING.md](TESTING.md) for detailed instructions)

2. 查看示例文檔 [EXAMPLE_USAGE.md](EXAMPLE_USAGE.md)
   (Check example documentation)

3. 運行交互式演示進行手動測試
   (Run interactive demo for manual testing)

## Success! / 成功！

現在您可以：
(Now you can:)

1. ✅ 測試庫而無需Android SDK (Test library without Android SDK)
2. ✅ 編譯和運行單元測試 (Compile and run unit tests)
3. ✅ 創建獨立JAR文件 (Create standalone JAR file)
4. ✅ 手動交互式測試 (Test interactively)
5. ✅ 在任何Java項目中使用 (Use in any Java project)

**快樂測試！/ Happy Testing! 🎉**
