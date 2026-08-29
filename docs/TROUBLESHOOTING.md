# Troubleshooting Guide

Common issues and solutions when working with the Selenium Java Automation Framework.

## Allure CLI Issues

### Error: "allure: command not found"

**Cause:** Allure CLI is not installed or not in PATH.

**Solutions:**

**1. Install via npm (Recommended)**
```bash
npm install -g allure-commandline
allure --version
```

**2. Install via Homebrew**
```bash
brew install allure
allure --version
```

**3. Install via Chocolatey (Windows)**
```bash
choco install allure
allure --version
```

**4. Add to PATH (if already installed)**

**Windows:**
- Find Allure installation: `where allure` or `npm list -g allure-commandline`
- Add directory to System Environment Variables → PATH

**macOS/Linux:**
- Find Allure installation: `which allure` or check npm global bin: `npm config get prefix`
- Add to shell config (~/.bashrc, ~/.zshrc): `export PATH="$PATH:/path/to/allure/bin"`

### Report generation fails silently

**Cause:** reports/allure-results directory is empty or missing test results.

**Solution:**
```bash
# Verify results exist
ls -la reports/allure-results/

# Run tests to generate results
mvn test

# Then generate report
allure generate reports/allure-results -o reports/allure-report --clean
```

### "allure generate" produces empty report

**Cause:** Allure listeners not configured or test results corrupted.

**Solution:**
```bash
# Check testng.xml has listener configured
cat src/test/resources/testng.xml
# Should include: <listener class-name="framework.listeners.AllureTestListener"/>

# Clean and regenerate
rm -rf allure-results/ allure-report/
mvn clean test
allure generate allure-results -o allure-report --clean
allure open allure-report
```

---

## Test Execution Issues

### Tests hang or timeout

**Cause 1: Long implicit/explicit waits**
- Default wait time too high
- Element never appears on page

**Solutions:**
```bash
# Check wait timeout in config.properties
cat src/main/resources/config.properties | grep wait

# Run single test with debug output
mvn -Dtest=demo_web_shop.LoginTests#TC001_userCanLoginWithValidCredentials test

# Increase JVM timeout (if tests are genuinely slow)
mvn -DforkCount=1 -DargLine="-Xmx1024m" test
```

**Cause 2: Target application not running**

**Solution:**
```bash
# Verify base URL is accessible
curl http://localhost:8080  # or your base.url from config.properties

# Check config.properties
cat src/main/resources/config.properties | grep base.url
```

### Tests fail only in CI (GitHub Actions), not locally

**Cause:** CI runs in headless mode (no UI), locators may be different.

**Solutions:**
```bash
# Emulate CI locally
CI=true mvn test

# Review headless-specific issues in logs
mvn test -X  # Verbose output

# Check DriverManager headless configuration
cat src/main/java/framework/core/driver/DriverManager.java | grep -A5 "CI"
```

### Tests fail with WebDriverManager errors

**Error:** "Could not read https://..."

**Cause:** Internet connectivity or firewall blocking driver downloads.

**Solutions:**
```bash
# Test internet connectivity
curl https://github.com/seleniumhq/selenium/releases

# Check proxy settings
mvn -Dhttp.proxyHost=proxy.example.com -Dhttp.proxyPort=8080 test

# Pre-download drivers (offline mode)
mvn dependency:resolve
```

### Flaky tests (intermittent failures)

**Cause:** Race conditions, timing issues, or unstable locators.

**Solutions:**
1. **Increase wait times** in config.properties:
   ```properties
   wait.default=15
   wait.long=30
   ```

2. **Use more stable locators** in page objects:
   ```java
   ❌ By.xpath("//div[2]/button[1]")  // Position-dependent
   ✅ By.id("submit-button")          // Stable
   ```

3. **Mark as flaky** in test temporarily:
   ```java
   @Test(groups = {"flaky"})
   public void TC001_unstableTest() { }
   
   // Skip when running: mvn test -Dgroups="!flaky"
   ```

4. **Add explicit waits**:
   ```java
   WaitManager.waitForElementClickable(By.id("button"), 15);
   ```

---

## WebDriver Issues

### ChromeDriver/GeckoDriver not found

**Cause:** WebDriverManager failed to download driver.

**Solutions:**
```bash
# Clear WebDriverManager cache
rm -rf ~/.wdm/  # Linux/macOS
del %USERPROFILE%\.wdm\*  # Windows

# Re-run tests (will re-download)
mvn clean test
```

### "Browser not found" error

**Cause:** Browser not installed or wrong browser specified.

**Solutions:**
```bash
# Check config.properties for browser setting
cat src/main/resources/config.properties | grep browser
# Should be: CHROME, FIREFOX, EDGE, SAFARI

# Verify browser is installed
# Chrome: google-chrome (Linux), /Applications/Google Chrome.app (macOS), C:\Program Files\Google\Chrome (Windows)

# Change browser if needed
# Edit config.properties or set environment variable
```

### SSL/Certificate errors

**Cause:** Testing against HTTPS site without proper certificates.

**Solutions:**
```java
// In DriverManager, add capability
ChromeOptions options = new ChromeOptions();
options.setAcceptInsecureCerts(true);
driver = new ChromeDriver(options);
```

---

## Configuration Issues

### Secrets/Credentials not loaded

**Cause 1: .env file missing**
```bash
# Create from example
cp src/main/resources/.env.example src/main/resources/.env

# Edit with actual values
cat src/main/resources/.env
```

**Cause 2: EnvReader not reading .env**
```java
// Check EnvReader implementation
String username = EnvReader.getEnv("STANDARD_USERNAME");
System.out.println("Username: " + username);  // Debug
```

**Cause 3: GitHub Secrets not configured (CI only)**
```bash
# Go to: Settings → Secrets and Variables → Actions
# Add required secrets:
# - STANDARD_USERNAME
# - STANDARD_PASSWORD
# - REGISTRATION_PASSWORD
# - USERNAME
# - PASSWORD
```

### Configuration properties not loaded

**Cause:** Wrong property name or ConfigManager not initialized.

**Solution:**
```bash
# Verify property exists in config.properties
grep "base.url" src/main/resources/config.properties

# Check property name (case-sensitive)
String value = ConfigManager.getProperty("base.url");  # Correct
String value = ConfigManager.getProperty("BASE_URL");  # Wrong
```

---

## Maven Build Issues

### Dependency resolution fails

**Cause:** Network issues or corrupted local repository.

**Solutions:**
```bash
# Clear local Maven cache
rm -rf ~/.m2/repository/

# Resolve dependencies offline
mvn dependency:resolve

# Try alternate Maven repo
mvn -DarchetypeRepository=https://repo.maven.apache.org/maven2 clean install
```

### "No tests found"

**Cause:** Test classes don't match naming pattern or not in correct location.

**Solutions:**
```bash
# Verify test exists
find src/test/java -name "*Tests.java"

# Run specific test
mvn -Dtest=demo_web_shop.LoginTests test

# Run all tests
mvn test

# Check testng.xml for suite configuration
cat src/test/resources/testng.xml
```

### Surefire plugin errors

**Cause:** Version incompatibility or configuration issue.

**Solutions:**
```bash
# Update pom.xml with specific Surefire version
mvn help:describe -Dplugin=org.apache.maven.plugins:maven-surefire-plugin

# Run tests with verbose output
mvn -X test
```

---

## IDE Issues

### Tests not running in IntelliJ/Eclipse

**Cause 1: IDE not configured for TestNG**

**Solution (IntelliJ):**
1. Right-click test class → "Run 'TestClass'" 
2. If not working: File → Project Settings → Add TestNG library

**Cause 2: SDK not configured**

**Solution:**
1. File → Project Settings → SDK
2. Select Java 21+

### Breakpoints not hit during debug

**Cause:** Code compiled with optimization, mismatched sources.

**Solution:**
```bash
# Clean and rebuild
mvn clean install

# Run test in debug mode from IDE
Right-click test → Debug As → JUnit Test
```

### Intellisense/autocomplete not working for framework classes

**Cause:** Maven dependencies not downloaded.

**Solution:**
```bash
mvn dependency:resolve
mvn dependency:sources  # Download sources for better IDE support
```

---

## CI/CD Issues (GitHub Actions)

### Workflow fails with "command not found: allure"

**Fix:** Already implemented in `.github/workflows/ci.yml`
- Uses `npm install -g allure-commandline` instead of apt-get
- Includes Node.js setup step

### GitHub Pages deployment fails

**Cause:** GitHub Pages not enabled or wrong branch configured.

**Solution:**
1. Go to Settings → Pages
2. Source: Deploy from a branch
3. Branch: gh-pages
4. Root folder

### Workflow timeout (20 minutes)

**Cause:** Tests taking too long, no parallelism.

**Solutions:**
1. Run only smoke tests in CI:
   ```yaml
   run: mvn test -Dgroups=smoke
   ```

2. Enable parallel execution in testng.xml:
   ```xml
   <suite parallel="methods" thread-count="4">
   ```

3. Skip long tests:
   ```yaml
   run: mvn test -Dgroups="!slow"
   ```

### Artifacts not being uploaded

**Cause:** Test failed to generate reports/ directory.

**Solution:**
```bash
# Check if tests ran successfully (no execution errors)
# reports/allure-results/ should be created automatically

# If directory is missing, run tests locally to debug
mvn clean test
ls -la reports/allure-results/
```

---

## Performance Issues

### Tests running very slowly

**Cause 1: Sequential execution**
- Default is single-threaded

**Solution:**
```xml
<!-- testng.xml -->
<suite parallel="methods" thread-count="4">
    <!-- tests -->
</suite>
```

**Cause 2: High wait timeouts**
- Default wait is 10+ seconds per element

**Solution:**
```properties
# config.properties
wait.default=5
wait.long=15
```

**Cause 3: Heavy test setup**
- Creating resources per test

**Solution:**
```java
@BeforeClass  // Once per class
public void expensiveSetup() { }

@BeforeMethod  // Once per test (avoid)
public void setUp() { }
```

---

## Need More Help?

1. **Check framework documentation:** `docs/how-to/`
2. **Review test examples:** `src/test/java/demo_web_shop/`
3. **Read source comments:** Code has inline documentation
4. **Open GitHub issue:** With logs, environment info, and reproduction steps

---

## Quick Reference Commands

```bash
# Clean build
mvn clean install

# Run all tests
mvn test

# Run smoke tests only
mvn test -Dgroups=smoke

# Run single test class
mvn -Dtest=ClassName test

# Run single test method
mvn -Dtest=ClassName#methodName test

# Skip tests
mvn clean install -DskipTests

# Debug output
mvn -X test

# Generate Allure report
allure generate reports/allure-results -o reports/allure-report --clean
allure open reports/allure-report

# Emulate CI locally
CI=true mvn test

# Show help for command
mvn help:describe -Dplugin=maven-surefire-plugin
```
