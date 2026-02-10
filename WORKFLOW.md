# AAR Build & Integration Workflow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SQLServerCrypto Repository                           │
│                                                                         │
│  ┌──────────────────────┐                                              │
│  │  Source Code         │                                              │
│  │  ─────────────       │                                              │
│  │  Java classes in:    │                                              │
│  │  sqlservercrypto-    │                                              │
│  │  android/src/        │                                              │
│  └──────────────────────┘                                              │
│            │                                                            │
│            ▼                                                            │
│  ┌──────────────────────────────────────────────┐                     │
│  │          BUILD AAR                           │                     │
│  │  ────────────────────────────                │                     │
│  │                                              │                     │
│  │  Option 1: ./build-aar.sh release           │                     │
│  │  Option 2: build-aar.bat release             │                     │
│  │  Option 3: ./gradlew buildAarRelease         │                     │
│  │                                              │                     │
│  └──────────────────────────────────────────────┘                     │
│            │                                                            │
│            ▼                                                            │
│  ┌──────────────────────────────────────────────┐                     │
│  │     Generated AAR File                       │                     │
│  │  ──────────────────────────────              │                     │
│  │  📦 sqlservercrypto-android-1.0.0.aar        │                     │
│  │  Location:                                    │                     │
│  │  build/outputs/aar/release/                   │                     │
│  └──────────────────────────────────────────────┘                     │
└─────────────────────────────────────────────────────────────────────────┘
                          │
                          │ Copy to your project
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    Your Android Project                                 │
│                                                                         │
│  YourApp/                                                               │
│  ├── app/                                                               │
│  │   ├── libs/                                                          │
│  │   │   └── sqlservercrypto-android-1.0.0.aar  ← Place AAR here      │
│  │   ├── src/                                                           │
│  │   │   └── main/                                                      │
│  │   │       └── java/                                                  │
│  │   │           └── YourCode.java                                      │
│  │   └── build.gradle  ← Add dependency                                │
│  └── build.gradle                                                       │
│                                                                         │
│  ┌──────────────────────────────────────────────┐                     │
│  │  Add to app/build.gradle:                    │                     │
│  │  ──────────────────────────────              │                     │
│  │  dependencies {                               │                     │
│  │      implementation files(                    │                     │
│  │          'libs/sqlservercrypto-android-       │                     │
│  │           1.0.0.aar'                          │                     │
│  │      )                                        │                     │
│  │  }                                            │                     │
│  └──────────────────────────────────────────────┘                     │
│            │                                                            │
│            ▼                                                            │
│  ┌──────────────────────────────────────────────┐                     │
│  │  Sync Gradle Files                           │                     │
│  │  File > Sync Project with Gradle Files       │                     │
│  └──────────────────────────────────────────────┘                     │
│            │                                                            │
│            ▼                                                            │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │  Use in Your Code                                                 │ │
│  │  ──────────────────────────────                                  │ │
│  │                                                                   │ │
│  │  import org.mtrnd.sqlservercrypto.SQLServerCryptoMethod;         │ │
│  │  import org.mtrnd.sqlservercrypto.HexString;                     │ │
│  │                                                                   │ │
│  │  String pass = "password";                                        │ │
│  │  String data = "Hello World";                                     │ │
│  │                                                                   │ │
│  │  // Encrypt                                                       │ │
│  │  HexString encrypted = SQLServerCryptoMethod                      │ │
│  │      .encryptByPassPhrase(pass, data);                            │ │
│  │                                                                   │ │
│  │  // Decrypt                                                       │ │
│  │  String decrypted = SQLServerCryptoMethod                         │ │
│  │      .decryptByPassPhrase(pass, encrypted.toString());            │ │
│  │                                                                   │ │
│  └──────────────────────────────────────────────────────────────────┘ │
│            │                                                            │
│            ▼                                                            │
│  ┌──────────────────────────────────────────────┐                     │
│  │  ✅ Ready to Use!                            │                     │
│  │  - Encrypt/Decrypt data                      │                     │
│  │  - SQL Server compatible                      │                     │
│  │  - Works on Android API 21+                   │                     │
│  └──────────────────────────────────────────────┘                     │
└─────────────────────────────────────────────────────────────────────────┘
```

## Build Commands Reference

| Platform | Command | Output |
|----------|---------|--------|
| Linux/Mac | `./build-aar.sh release` | Release AAR |
| Windows | `build-aar.bat release` | Release AAR |
| Gradle | `./gradlew :sqlservercrypto-android:buildAarRelease` | Release AAR |
| Gradle | `./gradlew :sqlservercrypto-android:buildAarDebug` | Debug AAR |

## Integration Steps

1. **Build** → Run build command
2. **Copy** → Copy AAR to `YourProject/app/libs/`
3. **Configure** → Add dependency to `build.gradle`
4. **Sync** → Sync Gradle files
5. **Code** → Import and use the library
6. **Run** → Build and run your app

## Documentation Quick Links

- 📖 **BUILD_INSTRUCTIONS.md** - Detailed build guide
- 🚀 **QUICKSTART.md** - Quick reference
- 💡 **EXAMPLE_USAGE.md** - Code examples
- 📋 **AAR_BUILD_SUMMARY.md** - Complete overview
- 📚 **README.md** - Library documentation

## Support

For issues or questions, check the documentation files above or refer to the troubleshooting sections.
