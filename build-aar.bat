@echo off
REM SQLServerCrypto Android Library - AAR Build Script (Windows)
REM This script builds the AAR file for the Android library

setlocal enabledelayedexpansion

:parse_args
set BUILD_TYPE=release
set CLEAN=false

if "%~1"=="" goto run_build
if /i "%~1"=="release" (
    set BUILD_TYPE=release
    shift
    goto parse_args
)
if /i "%~1"=="debug" (
    set BUILD_TYPE=debug
    shift
    goto parse_args
)
if /i "%~1"=="both" (
    set BUILD_TYPE=both
    shift
    goto parse_args
)
if /i "%~1"=="clean" (
    set CLEAN=true
    shift
    goto parse_args
)
if /i "%~1"=="help" goto show_help
if /i "%~1"=="--help" goto show_help
if /i "%~1"=="-h" goto show_help
if /i "%~1"=="/?" goto show_help

echo Unknown option: %~1
goto show_help

:show_help
echo.
echo SQLServerCrypto Android Library Builder
echo ========================================
echo.
echo Usage: %~nx0 [OPTIONS]
echo.
echo Options:
echo   release        Build release AAR (default)
echo   debug          Build debug AAR
echo   both           Build both release and debug AARs
echo   clean          Clean build directory before building
echo   help           Show this help message
echo.
echo Examples:
echo   %~nx0                    # Build release AAR
echo   %~nx0 release            # Build release AAR
echo   %~nx0 debug              # Build debug AAR
echo   %~nx0 both               # Build both release and debug
echo   %~nx0 clean release      # Clean then build release
echo.
goto end

:run_build
echo.
echo ============================================
echo   SQLServerCrypto Android Library Builder
echo ============================================
echo.

if "%CLEAN%"=="true" (
    echo Cleaning build directory...
    call gradlew.bat clean
    echo.
)

if "%BUILD_TYPE%"=="release" (
    echo Building Release AAR...
    call gradlew.bat :sqlservercrypto-android:buildAarRelease
) else if "%BUILD_TYPE%"=="debug" (
    echo Building Debug AAR...
    call gradlew.bat :sqlservercrypto-android:buildAarDebug
) else if "%BUILD_TYPE%"=="both" (
    echo Building Release AAR...
    call gradlew.bat :sqlservercrypto-android:buildAarRelease
    echo.
    echo Building Debug AAR...
    call gradlew.bat :sqlservercrypto-android:buildAarDebug
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo Build failed with error code %ERRORLEVEL%
    goto end
)

echo.
echo ========================================
echo Build completed successfully!
echo ========================================
echo.
echo AAR file(s) location:

if "%BUILD_TYPE%"=="release" (
    echo   Release: sqlservercrypto-android\build\outputs\aar\release\sqlservercrypto-android-1.0.0.aar
) else if "%BUILD_TYPE%"=="debug" (
    echo   Debug: sqlservercrypto-android\build\outputs\aar\debug\sqlservercrypto-android-debug-1.0.0.aar
) else if "%BUILD_TYPE%"=="both" (
    echo   Release: sqlservercrypto-android\build\outputs\aar\release\sqlservercrypto-android-1.0.0.aar
    echo   Debug: sqlservercrypto-android\build\outputs\aar\debug\sqlservercrypto-android-debug-1.0.0.aar
)

echo.
echo Next steps:
echo   1. Copy the AAR file to your project's libs folder
echo   2. Add to your app's build.gradle:
echo      implementation files('libs/sqlservercrypto-android-1.0.0.aar')
echo.
echo For more details, see:
echo   - BUILD_INSTRUCTIONS.md
echo   - sqlservercrypto-android\README.md
echo.

:end
endlocal
