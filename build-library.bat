@echo off
REM Simple Java Library (JAR) Builder for SQLServerCrypto (Windows)
REM 簡單的Java庫（JAR）構建器

setlocal enabledelayedexpansion

echo ===============================================
echo   SQLServerCrypto - Java Library Builder
echo   構建Java庫文件
echo ===============================================
echo.

REM Setup directories
set BUILD_DIR=library-build
set CLASSES_DIR=%BUILD_DIR%\classes
set VERSION=1.0.0
set JAR_NAME=sqlservercrypto-%VERSION%.jar

echo Setting up build directory...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%CLASSES_DIR%"

REM Compile source code
echo.
echo Compiling source code...
dir /s /b sqlservercrypto-android\src\main\java\*.java > sources.txt
javac -d "%CLASSES_DIR%" @sources.txt

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    del sources.txt
    exit /b 1
)
del sources.txt

echo Compilation successful!

REM Create JAR file
echo.
echo Creating JAR file...
cd "%CLASSES_DIR%"
jar cf ..\%JAR_NAME% org\
cd ..\..

REM Create manifest
echo.
echo Adding manifest...
echo Manifest-Version: 1.0 > %BUILD_DIR%\MANIFEST.MF
echo Implementation-Title: SQLServerCrypto >> %BUILD_DIR%\MANIFEST.MF
echo Implementation-Version: %VERSION% >> %BUILD_DIR%\MANIFEST.MF
echo Implementation-Vendor: MTRnD >> %BUILD_DIR%\MANIFEST.MF

REM Update JAR with manifest
cd "%CLASSES_DIR%"
jar ufm ..\%JAR_NAME% ..\MANIFEST.MF
cd ..\..

REM Verification
echo.
echo Verifying JAR file...
jar tf "%BUILD_DIR%\%JAR_NAME%" | findstr "SQLServerCryptoMethod.class" >nul
if %ERRORLEVEL% equ 0 (
    echo JAR file created successfully!
) else (
    echo JAR verification failed!
    exit /b 1
)

echo.
echo ===============================================
echo Build Complete! 構建完成！
echo ===============================================
echo.

echo JAR file location / JAR文件位置:
echo   %BUILD_DIR%\%JAR_NAME%
echo.

REM Show file size
for %%A in ("%BUILD_DIR%\%JAR_NAME%") do echo File size / 文件大小: %%~zA bytes
echo.

echo How to use / 如何使用:
echo   1. Copy JAR to your project / 複製JAR到你的項目
echo      copy %BUILD_DIR%\%JAR_NAME% \path\to\your\project\libs\
echo.
echo   2. Compile your code / 編譯你的代碼
echo      javac -cp %BUILD_DIR%\%JAR_NAME% YourProgram.java
echo.
echo   3. Run your program / 運行你的程序
echo      java -cp %BUILD_DIR%\%JAR_NAME%;. YourProgram
echo.

echo For more details, see:
echo   - LIBRARY_BUILD_GUIDE.md
echo   - EXAMPLE_USAGE.md
echo.

REM Create a simple test to verify
echo Creating verification test...
echo import org.mtrnd.sqlservercrypto.*; > %BUILD_DIR%\VerifyLib.java
echo. >> %BUILD_DIR%\VerifyLib.java
echo public class VerifyLib { >> %BUILD_DIR%\VerifyLib.java
echo     public static void main(String[] args) { >> %BUILD_DIR%\VerifyLib.java
echo         try { >> %BUILD_DIR%\VerifyLib.java
echo             String pass = "test"; >> %BUILD_DIR%\VerifyLib.java
echo             String data = "Hello"; >> %BUILD_DIR%\VerifyLib.java
echo             HexString enc = SQLServerCryptoMethod.encryptByPassPhrase(pass, data); >> %BUILD_DIR%\VerifyLib.java
echo             String dec = SQLServerCryptoMethod.decryptByPassPhrase(pass, enc.toString()); >> %BUILD_DIR%\VerifyLib.java
echo             if (data.equals(dec)) { >> %BUILD_DIR%\VerifyLib.java
echo                 System.out.println("Library verification PASSED!"); >> %BUILD_DIR%\VerifyLib.java
echo             } else { >> %BUILD_DIR%\VerifyLib.java
echo                 System.out.println("Library verification FAILED!"); >> %BUILD_DIR%\VerifyLib.java
echo                 System.exit(1); >> %BUILD_DIR%\VerifyLib.java
echo             } >> %BUILD_DIR%\VerifyLib.java
echo         } catch (Exception e) { >> %BUILD_DIR%\VerifyLib.java
echo             System.out.println("Library verification ERROR: " + e.getMessage()); >> %BUILD_DIR%\VerifyLib.java
echo             System.exit(1); >> %BUILD_DIR%\VerifyLib.java
echo         } >> %BUILD_DIR%\VerifyLib.java
echo     } >> %BUILD_DIR%\VerifyLib.java
echo } >> %BUILD_DIR%\VerifyLib.java

echo Test file created: %BUILD_DIR%\VerifyLib.java
echo.
echo Running verification test...
javac -cp "%BUILD_DIR%\%JAR_NAME%" "%BUILD_DIR%\VerifyLib.java"
java -cp "%BUILD_DIR%\%JAR_NAME%;%BUILD_DIR%" VerifyLib

echo.
echo Success! Library is ready to use! 庫已經可以使用！
echo.

endlocal
