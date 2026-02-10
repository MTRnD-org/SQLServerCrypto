# Complete Fix Guide: Gradle Daemon Java Version Issues

## Quick Fix for "Incompatible Daemons" Error

If you're seeing this error:
```
Starting a Gradle Daemon, 1 incompatible and 3 stopped Daemons could not be reused
```

**Run these 3 commands:**

```bash
# 1. Stop all Gradle daemons
./gradlew --stop

# 2. Clean build directories  
rm -rf .gradle build sqlservercrypto-android/build

# 3. Run build
./gradlew :sqlservercrypto-android:buildAarRelease
```

**Windows:**
```cmd
gradlew --stop
rmdir /s /q .gradle build sqlservercrypto-android\build
gradlew :sqlservercrypto-android:buildAarRelease
```

## What This Issue Is

### The Problem
Android Gradle Plugin 8.1.0 requires Java 11 or higher to run, but old Gradle daemons were configured to use Java 8. Even with Java 17 installed, Gradle would try to reuse old daemons, causing build failures.

### The Symptoms
- "No matching variant...compatible with Java 11...consumer needed...Java 8"
- "Incompatible Daemons could not be reused"
- Build fails even though `java -version` shows Java 17

### Why It Happens
- Gradle daemons are long-running processes that cache configuration
- When you upgrade Gradle or change Java versions, old daemons don't update
- Gradle tries to reuse old daemons to save startup time
- Incompatible daemons cause build failures

## Complete Solution

### 1. Configuration Files (Already Applied)

The repository has been updated with proper configuration:

**gradle/wrapper/gradle-wrapper.properties:**
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

**gradle.properties:**
```properties
# JVM memory settings for Gradle daemon
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m

# Ensure Gradle daemon uses Java 11+ (required for Android Gradle Plugin 8.1.0)
org.gradle.java.installations.auto-detect=true
org.gradle.java.installations.auto-download=false
```

### 2. User Action Required

When you clone/pull these changes, you MUST:

1. **Stop old daemons:**
   ```bash
   ./gradlew --stop
   ```
   This terminates all running Gradle daemons.

2. **Clean cached state:**
   ```bash
   rm -rf .gradle build sqlservercrypto-android/build
   ```
   This removes all cached build state.

3. **Run build:**
   ```bash
   ./gradlew :sqlservercrypto-android:buildAarRelease
   ```
   A new daemon will start with correct Java 17 configuration.

## Verification Steps

### Check Java Version
```bash
java -version
```
Should show Java 11 or higher (Java 17 recommended).

### Check Gradle Version
```bash
./gradlew --version
```
Should show:
- Gradle 8.5
- JVM: 17.x.x (or 11.x.x+)

### Verify Daemon Java
```bash
./gradlew tasks --info 2>&1 | grep "Starting process"
```
Should show path to Java 17:
```
Starting process 'Gradle build daemon'... /usr/lib/jvm/temurin-17-jdk-amd64/bin/java
```

### Check Daemon Status
```bash
./gradlew --status
```
After stopping old daemons and running a build, you should see one daemon running with correct version.

## Understanding Gradle Daemons

### What Are Daemons?
- Long-running background processes
- Cache project configuration and dependencies
- Make subsequent builds faster (warm starts)
- Reused across multiple builds

### When to Stop Daemons?
Stop daemons when you:
- Upgrade Gradle version
- Change Java version
- Update gradle.properties
- See "incompatible daemon" errors
- Experience unusual build behavior

### Daemon Lifecycle
```
First Build: Start new daemon → Slow (cold start)
Subsequent: Reuse daemon    → Fast (warm start)
After Stop: Start new daemon → Slow (but fresh state)
```

## Troubleshooting

### Still Getting Java 8 Error?

1. Check if old daemons are running:
   ```bash
   ps aux | grep gradle
   ```

2. Kill them manually if needed:
   ```bash
   pkill -f gradle
   ```

3. Check JAVA_HOME:
   ```bash
   echo $JAVA_HOME
   ```
   Should point to Java 11+ installation.

4. Verify gradle.properties exists and has correct settings.

### Build Still Fails?

If after stopping daemons and cleaning, build still fails:

1. Check error message carefully
2. If it's "dl.google.com: No address", it's a network issue (not Java)
3. If it's still Java version error, you may need to:
   - Reinstall Java 17
   - Set JAVA_HOME explicitly
   - Check for multiple Java installations

### Daemon Won't Start?

If daemon fails to start:

1. Check available memory:
   ```bash
   free -h
   ```

2. Reduce memory in gradle.properties if needed:
   ```properties
   org.gradle.jvmargs=-Xmx1024m -XX:MaxMetaspaceSize=256m
   ```

3. Check disk space:
   ```bash
   df -h
   ```

## Best Practices

### Do This:
✅ Stop daemons after configuration changes
✅ Clean build directories when switching branches
✅ Use Java 17 for Android development
✅ Keep Gradle wrapper up to date
✅ Check daemon status occasionally

### Don't Do This:
❌ Ignore "incompatible daemon" warnings
❌ Mix Java versions in same project
❌ Manually edit gradle wrapper files
❌ Skip cleaning after major changes
❌ Run builds with insufficient memory

## CI/CD Considerations

### GitHub Actions
Always use fresh environment (no daemon issues):
```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'temurin'

- name: Build with Gradle
  run: ./gradlew :sqlservercrypto-android:buildAarRelease
```

### Jenkins
Stop daemons in cleanup:
```groovy
post {
    always {
        sh './gradlew --stop'
    }
}
```

### Local Development
Stop daemons at end of day:
```bash
# Add to ~/.bashrc or ~/.zshrc
alias gradle-clean='./gradlew --stop && rm -rf .gradle build'
```

## Summary

**The Issue:** Old Gradle daemons with Java 8 configuration

**The Fix:** 
1. Upgrade to Gradle 8.5 ✅
2. Configure Java auto-detection ✅  
3. Stop old daemons 👤 (User action)
4. Clean build state 👤 (User action)

**The Result:** Builds work with Java 17, no more compatibility errors

## Additional Resources

- [Gradle Daemon Documentation](https://docs.gradle.org/current/userguide/gradle_daemon.html)
- [Android Gradle Plugin Release Notes](https://developer.android.com/build/releases/gradle-plugin)
- [Java Toolchains in Gradle](https://docs.gradle.org/current/userguide/toolchains.html)

## Quick Reference Card

```
┌─────────────────────────────────────────┐
│ GRADLE DAEMON QUICK COMMANDS            │
├─────────────────────────────────────────┤
│ Stop all daemons:                       │
│   ./gradlew --stop                      │
│                                         │
│ Check daemon status:                    │
│   ./gradlew --status                    │
│                                         │
│ Check Gradle version:                   │
│   ./gradlew --version                   │
│                                         │
│ Clean everything:                       │
│   rm -rf .gradle build */build          │
│                                         │
│ Fresh build:                            │
│   ./gradlew clean build                 │
│                                         │
│ With detailed logging:                  │
│   ./gradlew build --info               │
└─────────────────────────────────────────┘
```
