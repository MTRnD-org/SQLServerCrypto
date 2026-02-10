#!/bin/bash

# Simple Java Library (JAR) Builder for SQLServerCrypto
# 簡單的Java庫（JAR）構建器

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

print_message "$BLUE" "╔═══════════════════════════════════════════╗"
print_message "$BLUE" "║  SQLServerCrypto - Java Library Builder  ║"
print_message "$BLUE" "║  構建Java庫文件                           ║"
print_message "$BLUE" "╚═══════════════════════════════════════════╝"
echo ""

# Setup directories
BUILD_DIR="library-build"
CLASSES_DIR="$BUILD_DIR/classes"
VERSION="1.0.0"
JAR_NAME="sqlservercrypto-${VERSION}.jar"

print_message "$YELLOW" "📁 Setting up build directory..."
rm -rf "$BUILD_DIR"
mkdir -p "$CLASSES_DIR"

# Compile source code
print_message "$GREEN" "🔨 Compiling source code..."
find sqlservercrypto-android/src/main/java -name "*.java" > sources.txt
javac -d "$CLASSES_DIR" @sources.txt

if [ $? -ne 0 ]; then
    print_message "$RED" "❌ Compilation failed!"
    rm sources.txt
    exit 1
fi
rm sources.txt

print_message "$GREEN" "✅ Compilation successful!"

# Create JAR file
print_message "$GREEN" "📦 Creating JAR file..."
cd "$CLASSES_DIR"
jar cf "../${JAR_NAME}" org/
cd - > /dev/null

# Create manifest
print_message "$GREEN" "📝 Adding manifest..."
cat > "$BUILD_DIR/MANIFEST.MF" << EOF
Manifest-Version: 1.0
Implementation-Title: SQLServerCrypto
Implementation-Version: ${VERSION}
Implementation-Vendor: MTRnD
Built-By: $(whoami)
Build-Date: $(date)
EOF

# Update JAR with manifest
cd "$CLASSES_DIR"
jar ufm "../${JAR_NAME}" "../MANIFEST.MF"
cd - > /dev/null

# Verification
print_message "$BLUE" "🔍 Verifying JAR file..."
jar tf "$BUILD_DIR/${JAR_NAME}" | grep -q "org/mtrnd/sqlservercrypto/SQLServerCryptoMethod.class"

if [ $? -eq 0 ]; then
    print_message "$GREEN" "✅ JAR file created successfully!"
else
    print_message "$RED" "❌ JAR verification failed!"
    exit 1
fi

echo ""
print_message "$BLUE" "═══════════════════════════════════════════"
print_message "$GREEN" "🎉 Build Complete! 構建完成！"
print_message "$BLUE" "═══════════════════════════════════════════"
echo ""
print_message "$BLUE" "📦 JAR file location / JAR文件位置:"
echo "   ${BUILD_DIR}/${JAR_NAME}"
echo ""

# Show file size
FILE_SIZE=$(ls -lh "$BUILD_DIR/${JAR_NAME}" | awk '{print $5}')
print_message "$BLUE" "📊 File size / 文件大小: ${FILE_SIZE}"
echo ""

print_message "$BLUE" "💡 How to use / 如何使用:"
echo "   1. Copy JAR to your project / 複製JAR到你的項目"
echo "      cp ${BUILD_DIR}/${JAR_NAME} /path/to/your/project/libs/"
echo ""
echo "   2. Compile your code / 編譯你的代碼"
echo "      javac -cp ${BUILD_DIR}/${JAR_NAME} YourProgram.java"
echo ""
echo "   3. Run your program / 運行你的程序"
echo "      java -cp ${BUILD_DIR}/${JAR_NAME}:. YourProgram"
echo ""

print_message "$BLUE" "📖 For more details, see:"
echo "   - LIBRARY_BUILD_GUIDE.md"
echo "   - EXAMPLE_USAGE.md"
echo ""

# Create a simple test to verify
print_message "$YELLOW" "🧪 Creating verification test..."
cat > "$BUILD_DIR/VerifyLib.java" << 'EOFTEST'
import org.mtrnd.sqlservercrypto.*;

public class VerifyLib {
    public static void main(String[] args) {
        try {
            String pass = "test";
            String data = "Hello";
            HexString enc = SQLServerCryptoMethod.encryptByPassPhrase(pass, data);
            String dec = SQLServerCryptoMethod.decryptByPassPhrase(pass, enc.toString());
            if (data.equals(dec)) {
                System.out.println("✅ Library verification PASSED!");
            } else {
                System.out.println("❌ Library verification FAILED!");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("❌ Library verification ERROR: " + e.getMessage());
            System.exit(1);
        }
    }
}
EOFTEST

print_message "$GREEN" "✅ Test file created: ${BUILD_DIR}/VerifyLib.java"
echo ""
print_message "$YELLOW" "🔬 Running verification test..."
javac -cp "$BUILD_DIR/${JAR_NAME}" "$BUILD_DIR/VerifyLib.java"
java -cp "$BUILD_DIR/${JAR_NAME}:$BUILD_DIR" VerifyLib

echo ""
print_message "$GREEN" "🎊 Success! Library is ready to use! 庫已經可以使用！"
echo ""
