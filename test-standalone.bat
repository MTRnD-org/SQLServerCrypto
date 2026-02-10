@echo off
REM Standalone Test Runner for SQLServerCrypto Android Library (Windows)
REM This script compiles and runs tests WITHOUT requiring Android SDK or Gradle

setlocal enabledelayedexpansion

echo.
echo ============================================
echo   SQLServerCrypto - Standalone Test Runner
echo ============================================
echo.

REM Create temporary build directory
set BUILD_DIR=standalone-test-build
set LIB_DIR=%BUILD_DIR%\lib
set CLASSES_DIR=%BUILD_DIR%\classes
set TEST_CLASSES_DIR=%BUILD_DIR%\test-classes

echo Setting up build directories...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%LIB_DIR%"
mkdir "%CLASSES_DIR%"
mkdir "%TEST_CLASSES_DIR%"

REM Download JUnit if not present
set JUNIT_VERSION=4.13.2
set HAMCREST_VERSION=1.3
set JUNIT_JAR=%LIB_DIR%\junit-%JUNIT_VERSION%.jar
set HAMCREST_JAR=%LIB_DIR%\hamcrest-core-%HAMCREST_VERSION%.jar

if not exist "%JUNIT_JAR%" (
    echo Downloading JUnit %JUNIT_VERSION%...
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/junit/junit/%JUNIT_VERSION%/junit-%JUNIT_VERSION%.jar' -OutFile '%JUNIT_JAR%'}" 2>nul
    if !ERRORLEVEL! neq 0 (
        echo Failed to download JUnit. Tests will be skipped.
        set SKIP_TESTS=true
    )
)

if not exist "%HAMCREST_JAR%" (
    echo Downloading Hamcrest %HAMCREST_VERSION%...
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/%HAMCREST_VERSION%/hamcrest-core-%HAMCREST_VERSION%.jar' -OutFile '%HAMCREST_JAR%'}" 2>nul
)

REM Compile main source code
echo.
echo Compiling library source code...
dir /s /b sqlservercrypto-android\src\main\java\*.java > sources.txt
javac -d "%CLASSES_DIR%" @sources.txt

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    del sources.txt
    exit /b 1
)
del sources.txt

echo Library compiled successfully!

REM Compile and run tests if JUnit is available
if not "%SKIP_TESTS%"=="true" (
    echo.
    echo Compiling test code...
    dir /s /b sqlservercrypto-android\src\test\java\*.java > test-sources.txt
    javac -cp "%CLASSES_DIR%;%JUNIT_JAR%;%HAMCREST_JAR%" -d "%TEST_CLASSES_DIR%" @test-sources.txt
    
    if !ERRORLEVEL! neq 0 (
        echo Test compilation failed!
        del test-sources.txt
        exit /b 1
    )
    del test-sources.txt
    
    echo Tests compiled successfully!
    echo.
    echo Running tests...
    echo.
    
    java -cp "%CLASSES_DIR%;%TEST_CLASSES_DIR%;%JUNIT_JAR%;%HAMCREST_JAR%" org.junit.runner.JUnitCore org.mtrnd.sqlservercrypto.SQLServerCryptoMethodTest
    
    if !ERRORLEVEL! equ 0 (
        echo.
        echo All tests passed!
    ) else (
        echo.
        echo Some tests failed!
        exit /b 1
    )
)

echo.
echo Compiled classes location: %CLASSES_DIR%
echo You can use these compiled classes to create a JAR or test manually
echo.

REM Optional: Create a simple test JAR
echo Creating JAR file...
cd "%CLASSES_DIR%"
jar cf ..\sqlservercrypto-android-standalone.jar org\
cd ..\..

if exist "%BUILD_DIR%\sqlservercrypto-android-standalone.jar" (
    echo JAR created: %BUILD_DIR%\sqlservercrypto-android-standalone.jar
)

echo.
echo Build completed successfully!
echo.
echo Usage tips:
echo   1. The compiled classes are in: %CLASSES_DIR%
echo   2. Run tests again: java -cp %CLASSES_DIR%;%TEST_CLASSES_DIR%;%LIB_DIR%\* org.junit.runner.JUnitCore org.mtrnd.sqlservercrypto.SQLServerCryptoMethodTest
echo   3. Use JAR in your project: java -cp %BUILD_DIR%\sqlservercrypto-android-standalone.jar YourClass
echo.

endlocal
