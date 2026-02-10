# 如何構建庫文件 / How to Build as a Library

本指南說明如何將SQLServerCrypto構建成不同格式的庫文件。
(This guide explains how to build SQLServerCrypto into different library formats.)

## 📦 可用的庫格式 / Available Library Formats

### 1. Android AAR 格式 (用於Android項目)
### Android AAR Format (For Android Projects)

AAR是Android專用的庫格式，包含資源和清單文件。
(AAR is Android-specific library format that includes resources and manifest files.)

**構建方法 / Build Method:**

```bash
# Linux/Mac
./build-aar.sh release

# Windows
build-aar.bat release
```

**或使用Gradle / Or use Gradle:**
```bash
./gradlew :sqlservercrypto-android:buildAarRelease
```

**輸出位置 / Output Location:**
```
sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar
```

**如何使用 / How to Use:**
1. 將AAR文件複製到你的Android項目的 `app/libs/` 文件夾
2. 在 `build.gradle` 中添加依賴：
   ```gradle
   dependencies {
       implementation files('libs/sqlservercrypto-android-1.0.0.aar')
   }
   ```

### 2. Java JAR 格式 (用於普通Java項目)
### Java JAR Format (For Regular Java Projects)

JAR是標準的Java庫格式，可用於任何Java項目。
(JAR is standard Java library format, usable in any Java project.)

**構建方法 / Build Method:**

```bash
# Linux/Mac
./build-library.sh

# Windows  
build-library.bat
```

**輸出位置 / Output Location:**
```
library-build/sqlservercrypto-1.0.0.jar
```

**如何使用 / How to Use:**
```bash
# 在你的項目中使用 / Use in your project
javac -cp library-build/sqlservercrypto-1.0.0.jar YourProgram.java
java -cp library-build/sqlservercrypto-1.0.0.jar:. YourProgram
```

### 3. 源代碼直接集成 (不需要構建)
### Direct Source Integration (No Build Needed)

直接將源代碼複製到你的項目中。
(Copy source code directly into your project.)

**源文件位置 / Source Files Location:**
```
sqlservercrypto-android/src/main/java/org/mtrnd/sqlservercrypto/
  ├── SQLServerCryptoMethod.java
  ├── SQLServerCryptoVersion.java
  ├── SQLServerCryptoAlgorithm.java
  ├── SQLServerCryptoMessage.java
  ├── SQLServerCryptoHeader.java
  └── HexString.java
```

**如何使用 / How to Use:**
1. 將所有 `.java` 文件複製到你的項目
2. 確保保持包結構：`org/mtrnd/sqlservercrypto/`
3. 直接編譯和使用

## 🚀 快速開始 / Quick Start

### 最簡單的方法 - 構建JAR庫 / Easiest Way - Build JAR Library

如果你只需要一個簡單的JAR文件用於普通Java項目：
(If you just need a simple JAR for regular Java projects:)

```bash
# 運行獨立測試腳本 - 它會創建JAR
# Run standalone test script - it creates JAR
./test-standalone.sh

# JAR文件將在這裡 / JAR file will be at:
# standalone-test-build/sqlservercrypto-android-standalone.jar
```

### 構建Android AAR庫 / Build Android AAR Library

如果你需要在Android應用中使用：
(If you need to use in Android app:)

```bash
# 簡單方法 / Easy way
./build-aar.sh release

# AAR文件將在這裡 / AAR file will be at:
# sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar
```

## 📋 詳細構建步驟 / Detailed Build Steps

### 方法1：構建Android AAR (需要Android SDK)
### Method 1: Build Android AAR (Requires Android SDK)

**步驟 / Steps:**

1. **檢查先決條件 / Check Prerequisites:**
   ```bash
   java -version  # 需要 Java 8+ / Need Java 8+
   ```

2. **運行構建腳本 / Run Build Script:**
   ```bash
   # Linux/Mac
   ./build-aar.sh release
   
   # Windows
   build-aar.bat release
   ```

3. **查找輸出文件 / Find Output File:**
   ```bash
   ls -lh sqlservercrypto-android/build/outputs/aar/release/
   # 你會看到 / You will see:
   # sqlservercrypto-android-1.0.0.aar
   ```

4. **驗證AAR文件 / Verify AAR File:**
   ```bash
   # AAR是一個ZIP文件，可以查看內容
   # AAR is a ZIP file, you can view contents
   unzip -l sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar
   ```

### 方法2：構建純Java JAR (不需要Android SDK)
### Method 2: Build Pure Java JAR (No Android SDK Needed)

**步驟 / Steps:**

1. **運行獨立測試腳本 / Run Standalone Test Script:**
   ```bash
   # Linux/Mac
   ./test-standalone.sh
   
   # Windows
   test-standalone.bat
   ```

2. **JAR文件會自動創建 / JAR File Created Automatically:**
   ```bash
   ls -lh standalone-test-build/sqlservercrypto-android-standalone.jar
   ```

3. **測試JAR文件 / Test JAR File:**
   ```bash
   # 創建測試程序 / Create test program
   cat > TestLib.java << 'EOF'
   import org.mtrnd.sqlservercrypto.*;
   
   public class TestLib {
       public static void main(String[] args) {
           String pass = "test123";
           String data = "Hello World!";
           HexString enc = SQLServerCryptoMethod.encryptByPassPhrase(pass, data);
           String dec = SQLServerCryptoMethod.decryptByPassPhrase(pass, enc.toString());
           System.out.println("Original: " + data);
           System.out.println("Encrypted: " + enc);
           System.out.println("Decrypted: " + dec);
           System.out.println("Success: " + data.equals(dec));
       }
   }
   EOF
   
   # 編譯和運行 / Compile and run
   javac -cp standalone-test-build/sqlservercrypto-android-standalone.jar TestLib.java
   java -cp standalone-test-build/sqlservercrypto-android-standalone.jar:. TestLib
   ```

### 方法3：使用Gradle直接構建
### Method 3: Build Directly with Gradle

**構建AAR / Build AAR:**
```bash
# 清理舊的構建 / Clean old builds
./gradlew clean

# 構建release AAR / Build release AAR
./gradlew :sqlservercrypto-android:assembleRelease

# 輸出在 / Output at:
# sqlservercrypto-android/build/outputs/aar/sqlservercrypto-android-release.aar
```

**構建debug AAR / Build debug AAR:**
```bash
./gradlew :sqlservercrypto-android:assembleDebug

# 輸出在 / Output at:
# sqlservercrypto-android/build/outputs/aar/sqlservercrypto-android-debug.aar
```

## 🔍 驗證構建結果 / Verify Build Results

### 驗證AAR文件 / Verify AAR File

```bash
# 檢查文件大小 / Check file size
ls -lh sqlservercrypto-android/build/outputs/aar/release/*.aar

# 查看AAR內容 / View AAR contents
unzip -l sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar

# 應該包含 / Should contain:
# - classes.jar (編譯的類 / compiled classes)
# - AndroidManifest.xml (清單文件 / manifest)
# - R.txt (資源文件 / resources)
```

### 驗證JAR文件 / Verify JAR File

```bash
# 檢查文件大小 / Check file size
ls -lh standalone-test-build/sqlservercrypto-android-standalone.jar

# 查看JAR內容 / View JAR contents
jar tf standalone-test-build/sqlservercrypto-android-standalone.jar

# 應該包含所有類 / Should contain all classes:
# org/mtrnd/sqlservercrypto/SQLServerCryptoMethod.class
# org/mtrnd/sqlservercrypto/SQLServerCryptoVersion.class
# etc.
```

### 快速功能測試 / Quick Function Test

```bash
# 使用交互式演示測試 / Test with interactive demo
cd standalone-test-build/classes
echo "6" | java org.mtrnd.sqlservercrypto.demo.InteractiveDemo

# 應該顯示所有測試通過 / Should show all tests pass
```

## 📚 不同用途的建議 / Recommendations for Different Use Cases

### 用於Android應用 / For Android Apps
**推薦 / Recommended:** 使用AAR格式
- ✅ 包含Android資源
- ✅ 與Android Studio集成良好
- ✅ 支持ProGuard/R8

**構建命令 / Build Command:**
```bash
./build-aar.sh release
```

### 用於普通Java應用 / For Regular Java Apps
**推薦 / Recommended:** 使用JAR格式
- ✅ 更小的文件大小
- ✅ 標準Java兼容
- ✅ 易於分發

**構建命令 / Build Command:**
```bash
./test-standalone.sh
```

### 用於源代碼級別集成 / For Source-Level Integration
**推薦 / Recommended:** 直接複製源文件
- ✅ 完全控制
- ✅ 易於調試
- ✅ 可以自定義

**源文件位置 / Source Location:**
```
sqlservercrypto-android/src/main/java/org/mtrnd/sqlservercrypto/
```

## 🛠️ 高級構建選項 / Advanced Build Options

### 自定義版本號 / Custom Version Number

編輯 `sqlservercrypto-android/build.gradle`:
```gradle
ext {
    LIBRARY_VERSION = '1.0.0'  // 改成你想要的版本 / Change to your version
}
```

然後重新構建 / Then rebuild:
```bash
./build-aar.sh release
```

### 創建帶源代碼的JAR / Create JAR with Sources

```bash
# 編譯類 / Compile classes
./test-standalone.sh

# 添加源代碼到JAR / Add sources to JAR
cd sqlservercrypto-android/src/main/java
jar cf ../../../../standalone-test-build/sqlservercrypto-with-sources.jar org/
cd -

# 合併編譯的類和源代碼 / Merge compiled classes and sources
cd standalone-test-build/classes
jar uf ../sqlservercrypto-with-sources.jar org/
cd -
```

### 創建混淆的JAR / Create Obfuscated JAR

使用ProGuard混淆代碼：
(Use ProGuard to obfuscate code:)

```bash
# 安裝ProGuard / Install ProGuard
# 下載: https://github.com/Guardsquare/proguard/releases

# 創建ProGuard配置 / Create ProGuard config
cat > proguard-rules.pro << 'EOF'
-keep public class org.mtrnd.sqlservercrypto.SQLServerCryptoMethod {
    public *;
}
-keep public class org.mtrnd.sqlservercrypto.HexString {
    public *;
}
-keep public enum org.mtrnd.sqlservercrypto.SQLServerCryptoVersion {
    *;
}
EOF

# 運行ProGuard / Run ProGuard
java -jar proguard.jar @proguard-rules.pro \
    -injars standalone-test-build/sqlservercrypto-android-standalone.jar \
    -outjars standalone-test-build/sqlservercrypto-obfuscated.jar \
    -libraryjars $JAVA_HOME/jre/lib/rt.jar
```

## 📦 分發庫文件 / Distributing Library Files

### 方法1：直接分發文件 / Method 1: Direct File Distribution

```bash
# 複製AAR到目標位置 / Copy AAR to destination
cp sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar /path/to/distribution/

# 或複製JAR / Or copy JAR
cp standalone-test-build/sqlservercrypto-android-standalone.jar /path/to/distribution/
```

### 方法2：上傳到Maven倉庫 / Method 2: Upload to Maven Repository

（需要額外配置 - 請參考Maven文檔）
(Requires additional configuration - refer to Maven documentation)

### 方法3：發布到GitHub Releases / Method 3: Publish to GitHub Releases

1. 創建GitHub Release
2. 上傳AAR/JAR文件作為附件
3. 用戶可以直接下載使用

## ❓ 常見問題 / FAQ

### Q: AAR和JAR有什麼區別？/ What's the difference between AAR and JAR?

**A:** 
- **AAR** - Android專用，包含資源、清單等
  (Android-specific, includes resources, manifest, etc.)
- **JAR** - 標準Java，只包含編譯的類
  (Standard Java, only contains compiled classes)

### Q: 我應該使用哪種格式？/ Which format should I use?

**A:**
- 用於 **Android應用** → 使用 **AAR**
  (For Android apps → Use AAR)
- 用於 **普通Java應用** → 使用 **JAR**
  (For regular Java apps → Use JAR)
- 用於 **完全控制** → 使用 **源代碼**
  (For full control → Use source code)

### Q: 構建失敗怎麼辦？/ What if build fails?

**A:** 檢查 / Check:
1. Java版本 ≥ 8: `java -version`
2. 對於AAR：確保有Android SDK
   (For AAR: Ensure Android SDK available)
3. 清理並重試 / Clean and retry:
   ```bash
   ./gradlew clean
   rm -rf standalone-test-build
   ```

### Q: 如何更新庫版本？/ How to update library version?

**A:** 編輯 / Edit `sqlservercrypto-android/build.gradle`:
```gradle
ext {
    LIBRARY_VERSION = '1.1.0'  // 新版本 / New version
}
```

## 📖 更多資源 / More Resources

- **AAR構建詳情 / AAR Build Details:** [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)
- **AAR使用示例 / AAR Usage Examples:** [EXAMPLE_USAGE.md](EXAMPLE_USAGE.md)
- **測試指南 / Testing Guide:** [TESTING.md](TESTING.md)
- **快速入門 / Quick Start:** [QUICKSTART.md](sqlservercrypto-android/QUICKSTART.md)

## 📝 摘要 / Summary

**最快的方法 / Fastest Way:**
```bash
# 對於Java項目 / For Java projects:
./test-standalone.sh
# 輸出 / Output: standalone-test-build/sqlservercrypto-android-standalone.jar

# 對於Android項目 / For Android projects:
./build-aar.sh release
# 輸出 / Output: sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar
```

**就是這麼簡單！/ That's it! 🎉**
