#!/bin/bash

# SQLServerCrypto Android Library - AAR Build Script
# This script builds the AAR file for the Android library

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored message
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Print header
print_header() {
    echo ""
    print_message "$BLUE" "============================================"
    print_message "$BLUE" "  SQLServerCrypto Android Library Builder"
    print_message "$BLUE" "============================================"
    echo ""
}

# Print usage
print_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  release        Build release AAR (default)"
    echo "  debug          Build debug AAR"
    echo "  both           Build both release and debug AARs"
    echo "  clean          Clean build directory before building"
    echo "  help           Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                    # Build release AAR"
    echo "  $0 release            # Build release AAR"
    echo "  $0 debug              # Build debug AAR"
    echo "  $0 both               # Build both release and debug"
    echo "  $0 clean release      # Clean then build release"
    echo ""
}

# Main script
print_header

# Parse arguments
BUILD_TYPE="release"
CLEAN=false

while [[ $# -gt 0 ]]; do
    case $1 in
        release)
            BUILD_TYPE="release"
            shift
            ;;
        debug)
            BUILD_TYPE="debug"
            shift
            ;;
        both)
            BUILD_TYPE="both"
            shift
            ;;
        clean)
            CLEAN=true
            shift
            ;;
        help|--help|-h)
            print_usage
            exit 0
            ;;
        *)
            print_message "$RED" "Unknown option: $1"
            print_usage
            exit 1
            ;;
    esac
done

# Clean if requested
if [ "$CLEAN" = true ]; then
    print_message "$YELLOW" "🧹 Cleaning build directory..."
    ./gradlew clean
    echo ""
fi

# Build AAR based on type
case $BUILD_TYPE in
    release)
        print_message "$GREEN" "🔨 Building Release AAR..."
        ./gradlew :sqlservercrypto-android:buildAarRelease
        ;;
    debug)
        print_message "$GREEN" "🔨 Building Debug AAR..."
        ./gradlew :sqlservercrypto-android:buildAarDebug
        ;;
    both)
        print_message "$GREEN" "🔨 Building Release AAR..."
        ./gradlew :sqlservercrypto-android:buildAarRelease
        echo ""
        print_message "$GREEN" "🔨 Building Debug AAR..."
        ./gradlew :sqlservercrypto-android:buildAarDebug
        ;;
esac

echo ""
print_message "$GREEN" "✅ Build completed successfully!"
echo ""
print_message "$BLUE" "📦 AAR file(s) location:"

if [ "$BUILD_TYPE" = "release" ] || [ "$BUILD_TYPE" = "both" ]; then
    AAR_PATH="sqlservercrypto-android/build/outputs/aar/release/sqlservercrypto-android-1.0.0.aar"
    if [ -f "$AAR_PATH" ]; then
        print_message "$GREEN" "   Release: $AAR_PATH"
    else
        print_message "$YELLOW" "   Release: sqlservercrypto-android/build/outputs/aar/sqlservercrypto-android-release.aar"
    fi
fi

if [ "$BUILD_TYPE" = "debug" ] || [ "$BUILD_TYPE" = "both" ]; then
    AAR_PATH="sqlservercrypto-android/build/outputs/aar/debug/sqlservercrypto-android-debug-1.0.0.aar"
    if [ -f "$AAR_PATH" ]; then
        print_message "$GREEN" "   Debug: $AAR_PATH"
    else
        print_message "$YELLOW" "   Debug: sqlservercrypto-android/build/outputs/aar/sqlservercrypto-android-debug.aar"
    fi
fi

echo ""
print_message "$BLUE" "💡 Next steps:"
echo "   1. Copy the AAR file to your project's libs folder"
echo "   2. Add to your app's build.gradle:"
echo "      implementation files('libs/sqlservercrypto-android-1.0.0.aar')"
echo ""
print_message "$BLUE" "📖 For more details, see:"
echo "   - BUILD_INSTRUCTIONS.md"
echo "   - sqlservercrypto-android/README.md"
echo ""
