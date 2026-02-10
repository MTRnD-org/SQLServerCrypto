# Testing Guide for SQLServerCrypto Android Library

本指南提供了多種測試此Android庫的方法，**無需完整的Android SDK或Gradle構建**。
(This guide provides multiple ways to test this Android library **WITHOUT needing full Android SDK or Gradle build**.)

## Quick Start - 快速開始

### Option 1: Standalone Test Runner (推薦 / Recommended)

**不需要Android SDK！只需要Java！**
(No Android SDK needed! Just Java!)

**Linux/Mac:**
```bash
./test-standalone.sh
```

**Windows:**
```cmd
test-standalone.bat
```

這將：
(This will:)
1. 自動下載JUnit測試框架 (Auto-download JUnit test framework)
2. 編譯所有源代碼 (Compile all source code)
3. 運行所有測試 (Run all tests)
4. 創建可用的JAR文件 (Create usable JAR file)

輸出結果：
(Output:)
- 編譯的類文件：`standalone-test-build/classes/`
- JAR文件：`standalone-test-build/sqlservercrypto-android-standalone.jar`

## Testing Methods - 測試方法

### 1. 自動化測試 (Automated Testing)

#### 使用獨立腳本 (Using Standalone Script)

最簡單的方法 - 只需要安裝Java：
(Easiest method - only requires Java:)

```bash
# Linux/Mac
./test-standalone.sh

# Windows  
test-standalone.bat
```

#### 使用Gradle (Using Gradle)

如果你有Android SDK：
(If you have Android SDK:)

```bash
./gradlew :sqlservercrypto-android:test
```

### 2. 交互式測試 (Interactive Testing)

運行交互式演示程序：
(Run interactive demo program:)

**步驟 (Steps):**

1. 首先編譯庫：
   (First compile the library:)
   ```bash
   ./test-standalone.sh
   ```

2. 運行交互式演示：
   (Run interactive demo:)
   ```bash
   cd standalone-test-build/classes
   java org.mtrnd.sqlservercrypto.demo.InteractiveDemo
   ```

**交互式菜單包括：**
(Interactive menu includes:)
- 基本加密/解密測試 (Basic encryption/decryption)
- AES256加密測試 (AES256 encryption)
- 帶認證器的加密 (Encryption with authenticator)
- SQL Server兼容性測試 (SQL Server compatibility)
- HexString工具測試 (HexString utilities)

### 3. 手動測試 (Manual Testing)

創建你自己的測試文件：
(Create your own test file:)

```java
// MyTest.java
import org.mtrnd.sqlservercrypto.*;

public class MyTest {
    public static void main(String[] args) {
        // 測試加密和解密
        String passphrase = "mypassword";
        String text = "Hello World!";
        
        // 加密 (Encrypt)
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
            passphrase, text);
        System.out.println("Encrypted: " + encrypted);
        
        // 解密 (Decrypt)
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(
            passphrase, encrypted.toString());
        System.out.println("Decrypted: " + decrypted);
        
        // 驗證 (Verify)
        if (text.equals(decrypted)) {
            System.out.println("SUCCESS!");
        }
    }
}
```

編譯和運行：
(Compile and run:)

```bash
# 1. 首先運行獨立腳本以獲取編譯的類
#    (First run standalone script to get compiled classes)
./test-standalone.sh

# 2. 編譯你的測試文件
#    (Compile your test file)
javac -cp standalone-test-build/classes MyTest.java

# 3. 運行測試
#    (Run test)
java -cp .:standalone-test-build/classes MyTest
```

### 4. 在IDE中測試 (Testing in IDE)

#### IntelliJ IDEA

1. 打開項目 (Open project)
2. 右鍵點擊 `SQLServerCryptoMethodTest.java`
3. 選擇 "Run tests" (選擇運行測試)

#### Eclipse

1. 導入項目 (Import project)
2. 右鍵點擊測試文件 (Right-click test file)
3. Run As → JUnit Test

#### VS Code

1. 安裝 Java Extension Pack
2. 打開測試文件 (Open test file)
3. 點擊測試方法旁的 "Run Test" 按鈕

## 測試內容 (Test Coverage)

現有測試涵蓋：
(Existing tests cover:)

✅ 基本加密和解密 (Basic encryption/decryption)
✅ V1版本 (TripleDES/SHA1)
✅ V2版本 (AES256/SHA256)  
✅ 帶認證器的加密 (Encryption with authenticator)
✅ 無驗證解密 (Decryption without verification)
✅ HexString工具 (HexString utilities)
✅ SQL Server兼容性 (SQL Server compatibility)
✅ 錯誤處理 (Error handling)

## 常見問題 (Troubleshooting)

### ❌ 問題：找不到Java編譯器
(Problem: Java compiler not found)

**解決方案 (Solution):**
```bash
# 安裝Java JDK (Install Java JDK)
# Ubuntu/Debian:
sudo apt-get install default-jdk

# macOS:
brew install openjdk

# Windows: 從Oracle下載JDK
# Download JDK from Oracle
```

### ❌ 問題：無法下載JUnit
(Problem: Cannot download JUnit)

**解決方案 (Solution):**
手動下載並放置在 `standalone-test-build/lib/` 目錄：
(Manually download and place in `standalone-test-build/lib/` directory:)
- junit-4.13.2.jar
- hamcrest-core-1.3.jar

從這裡下載 (Download from): https://repo1.maven.org/maven2/junit/junit/

### ❌ 問題：編譯錯誤
(Problem: Compilation errors)

**解決方案 (Solution):**
確保你使用的是Java 8或更高版本：
(Make sure you're using Java 8 or higher:)
```bash
java -version
javac -version
```

## 性能測試 (Performance Testing)

創建性能測試：
(Create performance test:)

```java
public class PerformanceTest {
    public static void main(String[] args) {
        String passphrase = "password";
        String text = "Test data";
        int iterations = 1000;
        
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < iterations; i++) {
            HexString enc = SQLServerCryptoMethod.encryptByPassPhrase(
                passphrase, text);
            SQLServerCryptoMethod.decryptByPassPhrase(
                passphrase, enc.toString());
        }
        
        long end = System.currentTimeMillis();
        System.out.println("Time for " + iterations + " iterations: " + 
                          (end - start) + "ms");
    }
}
```

## CI/CD集成 (CI/CD Integration)

### GitHub Actions

```yaml
name: Test Library
on: [push]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Run tests
        run: ./test-standalone.sh
```

### Jenkins

```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh './test-standalone.sh'
            }
        }
    }
}
```

## 資源 (Resources)

- **測試腳本 (Test Scripts):** `test-standalone.sh`, `test-standalone.bat`
- **單元測試 (Unit Tests):** `src/test/java/org/mtrnd/sqlservercrypto/`
- **交互式演示 (Interactive Demo):** `InteractiveDemo.java`
- **文檔 (Documentation):** `README.md`, `QUICKSTART.md`, `EXAMPLE_USAGE.md`

## 獲取幫助 (Getting Help)

如果測試失敗：
(If tests fail:)

1. 檢查Java版本 (至少需要Java 8) (Check Java version - need at least Java 8)
2. 確保所有源文件都存在 (Ensure all source files exist)
3. 查看錯誤消息 (Look at error messages)
4. 嘗試清理並重新運行 (Try cleaning and re-running):
   ```bash
   rm -rf standalone-test-build
   ./test-standalone.sh
   ```

## 摘要 (Summary)

**最簡單的測試方法：**
(Easiest way to test:)

1. 運行 `./test-standalone.sh` (Linux/Mac) 或 `test-standalone.bat` (Windows)
2. 等待測試完成
3. 查看結果 - 應該看到 "OK (8 tests)"

**無需：**
(No need for:)
- ❌ Android SDK
- ❌ Android Studio
- ❌ Gradle完整安裝 (Full Gradle installation)
- ❌ 複雜配置 (Complex configuration)

**只需要：**
(Only need:)
- ✅ Java JDK 8+
- ✅ 運行一個腳本 (Run one script)
