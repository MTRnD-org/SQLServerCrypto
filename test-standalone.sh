#!/bin/bash

# Standalone Test Runner for SQLServerCrypto Android Library
# This script compiles and runs tests WITHOUT requiring Android SDK or Gradle

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

print_header() {
    echo ""
    print_message "$BLUE" "============================================"
    print_message "$BLUE" "  SQLServerCrypto - Standalone Test Runner"
    print_message "$BLUE" "============================================"
    echo ""
}

# Main script
print_header

# Create temporary build directory
BUILD_DIR="standalone-test-build"
LIB_DIR="$BUILD_DIR/lib"
CLASSES_DIR="$BUILD_DIR/classes"
TEST_CLASSES_DIR="$BUILD_DIR/test-classes"

print_message "$YELLOW" "📁 Setting up build directories..."
rm -rf "$BUILD_DIR"
mkdir -p "$LIB_DIR"
mkdir -p "$CLASSES_DIR"
mkdir -p "$TEST_CLASSES_DIR"

# Download JUnit if not present
JUNIT_VERSION="4.13.2"
HAMCREST_VERSION="1.3"
JUNIT_JAR="$LIB_DIR/junit-${JUNIT_VERSION}.jar"
HAMCREST_JAR="$LIB_DIR/hamcrest-core-${HAMCREST_VERSION}.jar"

if [ ! -f "$JUNIT_JAR" ]; then
    print_message "$YELLOW" "📦 Downloading JUnit ${JUNIT_VERSION}..."
    curl -L -o "$JUNIT_JAR" "https://repo1.maven.org/maven2/junit/junit/${JUNIT_VERSION}/junit-${JUNIT_VERSION}.jar" 2>/dev/null || {
        print_message "$RED" "❌ Failed to download JUnit. Using local compilation only."
        SKIP_TESTS=true
    }
fi

if [ ! -f "$HAMCREST_JAR" ]; then
    print_message "$YELLOW" "📦 Downloading Hamcrest ${HAMCREST_VERSION}..."
    curl -L -o "$HAMCREST_JAR" "https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/${HAMCREST_VERSION}/hamcrest-core-${HAMCREST_VERSION}.jar" 2>/dev/null || {
        print_message "$RED" "❌ Failed to download Hamcrest."
    }
fi

# Compile main source code
print_message "$GREEN" "🔨 Compiling library source code..."
find sqlservercrypto-android/src/main/java -name "*.java" > sources.txt
javac -d "$CLASSES_DIR" @sources.txt

if [ $? -ne 0 ]; then
    print_message "$RED" "❌ Compilation failed!"
    rm sources.txt
    exit 1
fi
rm sources.txt

print_message "$GREEN" "✅ Library compiled successfully!"

# Compile and run tests if JUnit is available
if [ "$SKIP_TESTS" != "true" ]; then
    print_message "$GREEN" "🔨 Compiling test code..."
    find sqlservercrypto-android/src/test/java -name "*.java" > test-sources.txt
    javac -cp "$CLASSES_DIR:$JUNIT_JAR:$HAMCREST_JAR" -d "$TEST_CLASSES_DIR" @test-sources.txt
    
    if [ $? -ne 0 ]; then
        print_message "$RED" "❌ Test compilation failed!"
        rm test-sources.txt
        exit 1
    fi
    rm test-sources.txt
    
    print_message "$GREEN" "✅ Tests compiled successfully!"
    echo ""
    print_message "$BLUE" "🧪 Running tests..."
    echo ""
    
    # Run tests
    java -cp "$CLASSES_DIR:$TEST_CLASSES_DIR:$JUNIT_JAR:$HAMCREST_JAR" \
        org.junit.runner.JUnitCore \
        org.mtrnd.sqlservercrypto.SQLServerCryptoMethodTest
    
    TEST_RESULT=$?
    echo ""
    
    if [ $TEST_RESULT -eq 0 ]; then
        print_message "$GREEN" "✅ All tests passed!"
    else
        print_message "$RED" "❌ Some tests failed!"
        exit 1
    fi
fi

echo ""
print_message "$BLUE" "📦 Compiled classes location: $CLASSES_DIR"
print_message "$BLUE" "📝 You can use these compiled classes to create a JAR or test manually"
echo ""
print_message "$GREEN" "✨ Build completed successfully!"
echo ""

# Optional: Create a simple test JAR
print_message "$YELLOW" "📦 Creating JAR file..."
cd "$CLASSES_DIR"
jar cf "../sqlservercrypto-android-standalone.jar" org/
cd - > /dev/null

if [ -f "$BUILD_DIR/sqlservercrypto-android-standalone.jar" ]; then
    print_message "$GREEN" "✅ JAR created: $BUILD_DIR/sqlservercrypto-android-standalone.jar"
fi

echo ""
print_message "$BLUE" "💡 Usage tips:"
echo "   1. The compiled classes are in: $CLASSES_DIR"
echo "   2. Run tests again: java -cp $CLASSES_DIR:$TEST_CLASSES_DIR:$LIB_DIR/* org.junit.runner.JUnitCore org.mtrnd.sqlservercrypto.SQLServerCryptoMethodTest"
echo "   3. Use JAR in your project: java -cp $BUILD_DIR/sqlservercrypto-android-standalone.jar YourClass"
echo ""
