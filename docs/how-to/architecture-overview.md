# Architecture Overview

## Framework Design Philosophy

This framework follows the **Page Object Model (POM)** pattern combined with a lightweight **Layered Enterprise Framework (LEF)** structure:

- **Separation of Concerns:** Test code, page/flow logic, and framework utilities are strictly separated
- **Reusability:** Flows and pages are designed for composition and reuse across tests
- **Maintainability:** Centralized locators and actions make updates easier
- **Stability:** Explicit waits and helper methods reduce flakiness

## High-Level Architecture

```
Test Execution Layers (top to bottom):

┌────────────────────────────────────┐
│  Test Cases (src/test/java/)       │  Arrange-Act-Assert pattern
├────────────────────────────────────┤
│  Flows & Assertions                │  Business-level operations
│  (src/main/java/application/.../   │  (RegistrationFlow, LoginFlow)
│   flows, assertions)               │
├────────────────────────────────────┤
│  Page Objects                      │  UI element encapsulation
│  (src/main/java/application/.../   │  (RegistrationPage, LoginPage)
│   pages)                           │
├────────────────────────────────────┤
│  Framework Support                 │  Stable interactions
│  (BrowserActions, WaitManager,     │  (click, wait, fill, navigate)
│   Element wrapper)                 │
├────────────────────────────────────┤
│  Core Framework                    │  Configuration, lifecycle
│  (DriverManager, ConfigManager,    │  (initialize, teardown)
│   EnvReader)                       │
├────────────────────────────────────┤
│  Selenium WebDriver                │  Browser automation API
└────────────────────────────────────┘
```

## Component Architecture

### Project Structure

```
src/main/java/
├── framework/                           # Reusable framework code
│   ├── core/
│   │   ├── driver/
│   │   │   ├── DriverManager.java      # WebDriver lifecycle, CI detection
│   │   │   └── Browser.java            # Browser enum (CHROME, FIREFOX, etc)
│   │   ├── waits/
│   │   │   └── WaitManager.java        # Explicit wait utilities
│   │   ├── actions/
│   │   │   └── BrowserActions.java     # Common browser interactions
│   │   ├── config/
│   │   │   ├── ConfigManager.java      # Property file reader
│   │   │   ├── EnvReader.java          # Environment variable reader
│   │   │   └── exceptions/             # Framework exceptions
│   │   └── exceptions/
│   │       ├── FrameworkException.java
│   │       ├── WaitTimeoutException.java
│   │       └── AssertionException.java
│   ├── support/
│   │   ├── elements/
│   │   │   └── Element.java            # Wrapper around WebElement
│   │   ├── reporting/
│   │   │   ├── AllureManager.java      # Allure API integration
│   │   │   └── ScreenshotManager.java  # Screenshot capture
│   │   ├── logging/
│   │   │   └── FrameworkLogger.java    # Structured logging
│   │   └── data/
│   │       └── DataManager.java        # Test data loading from JSON
│   ├── listeners/
│   │   ├── AllureTestListener.java     # Allure reporting listener
│   │   └── TestListener.java           # Custom test listener
│   └── assertions/
│       └── FrameworkAssertions.java    # Reusable assertion helpers
│
└── application/                         # Application-specific code
    ├── BasePage.java                   # Base class for all page objects
    └── demo_web_shop/
        ├── pages/                      # Page objects
        │   ├── LandingPage.java
        │   ├── RegistrationPage.java
        │   ├── LoginPage.java
        │   └── ...
        ├── flows/                      # High-level flows
        │   ├── RegistrationFlow.java
        │   ├── LoginFlow.java
        │   └── CheckoutFlow.java
        ├── components/                 # Reusable UI components
        │   ├── Header.java
        │   ├── Footer.java
        │   └── Modal.java
        ├── assertions/                 # Application assertions
        │   ├── RegistrationAssertions.java
        │   ├── AuthAssertions.java
        │   └── ...
        ├── fixtures/                   # Test fixtures & factories
        │   ├── TestFixtures.java       # @BeforeClass/@BeforeMethod setup
        │   └── UserFactory.java        # Test data factories
        └── utils/                      # Application utilities
            ├── RandomDataGeneratorUtil.java
            └── CredentialsUtil.java
```

### Test Code Structure

```
src/test/java/
└── demo_web_shop/
    ├── ui/
    │   ├── functional/
    │   │   ├── authentication/
    │   │   │   ├── LoginTests.java
    │   │   │   └── LogoutTests.java
    │   │   ├── registration/
    │   │   │   └── RegistrationPageTests.java
    │   │   └── products/
    │   │       └── ProductsTests.java
    │   └── setup/
    │       └── TestSetup.java
    └── api/
        └── auth/
            └── AuthApiTests.java

src/test/resources/
├── testng.xml                           # Test suite, groups, parallelism
├── logback.xml                          # Logging configuration
└── test-data/                           # Test data files (JSON, CSV)
```

## Detailed Component Descriptions

### 1. DriverManager (Core Driver Management)

**Location:** `src/main/java/framework/core/driver/DriverManager.java`

**Responsibility:** Manages WebDriver lifecycle, initialization, and teardown.

**Key Features:**
- Detects CI environment via `System.getenv("CI")` to enable headless mode
- Uses WebDriverManager for automatic driver downloads
- Supports multiple browsers (Chrome, Firefox, Edge, Safari)
- Thread-safe for parallel test execution
- Singleton pattern per thread

**Usage:**
```java
WebDriver driver = DriverManager.getDriver();  // Initialize/get driver
DriverManager.quitDriver();                     // Cleanup
```

**Implementation:**
```java
public class DriverManager {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            driverThreadLocal.set(initializeDriver());
        }
        return driverThreadLocal.get();
    }
    
    private static WebDriver initializeDriver() {
        String browser = ConfigManager.getProperty("browser");
        boolean isHeadless = "true".equals(System.getenv("CI"));
        // ... driver initialization with headless options if CI
    }
}
```

### 2. Page Objects (UI Encapsulation)

**Location:** `src/main/java/application/demo_web_shop/pages/`

**Responsibility:** Encapsulate page-specific elements and interactions.

**Pattern:**
- Extend `BasePage` for common functionality
- Keep locators private (WebDriver By, XPath, CSS)
- Expose public action methods with intent-revealing names
- Use `Element` wrapper and `BrowserActions` for interactions
- Use `WaitManager` for explicit waits

**Example:**
```java
public class LoginPage extends BasePage {
    
    private By emailInput = By.id("email");
    private By passwordInput = By.name("password");
    private By loginButton = By.xpath("//button[text()='Login']");
    
    public void fillEmail(String email) {
        BrowserActions.fill(emailInput, email);  // Uses Element wrapper + wait
    }
    
    public void fillPassword(String password) {
        BrowserActions.fill(passwordInput, password);
    }
    
    public void clickLogin() {
        BrowserActions.click(loginButton);
    }
    
    public boolean isEmailErrorDisplayed() {
        return new WaitManager().waitForElement(By.id("email-error"));
    }
}
```

### 3. Flows (Business-Level Operations)

**Location:** `src/main/java/application/demo_web_shop/flows/`

**Responsibility:** Compose page actions into high-level business operations.

**Pattern:**
- Represent complete user workflows (login, registration, checkout)
- Compose multiple page objects
- Return results for assertions
- Improve test readability by hiding page-level details

**Example:**
```java
public class LoginFlow {
    private LoginPage loginPage;
    private HomePage homePage;
    
    public LoginFlow(WebDriver driver) {
        this.loginPage = new LoginPage(driver);
        this.homePage = new HomePage(driver);
    }
    
    public HomePage loginAs(User user) {
        loginPage.fillEmail(user.getEmail());
        loginPage.fillPassword(user.getPassword());
        loginPage.clickLogin();
        
        // Wait for home page to load
        homePage.waitForPageLoad();
        return homePage;
    }
}
```

### 4. Test Fixtures & Factories

**Location:** `src/main/java/application/demo_web_shop/fixtures/TestFixtures.java`

**Responsibility:** Provide TestNG setup/teardown and test data factories.

**TestNG Lifecycle:**
- `@BeforeSuite` → Once before all tests
- `@BeforeClass` → Once per test class
- `@BeforeMethod` → Before each test method
- `@AfterMethod` → After each test method
- `@AfterClass` → Once after test class

**Example:**
```java
public class TestFixtures {
    protected WebDriver driver;
    protected LoginFlow loginFlow;
    protected HomePage homePage;
    
    @BeforeMethod
    public void setUp() {
        driver = DriverManager.getDriver();
        loginFlow = new LoginFlow(driver);
        homePage = new HomePage(driver);
    }
    
    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
```

### 5. Assertions (Verification & Reporting)

**Location:** `src/main/java/application/demo_web_shop/assertions/`

**Responsibility:** Provide semantic, reusable assertion methods integrated with Allure.

**Pattern:**
- Describe expected behavior, not implementation
- Group related checks
- Attach screenshots/logs to Allure on failure

**Example:**
```java
public class LoginAssertions {
    
    public static void assertLoginSuccessful(HomePage homePage) {
        Assert.assertTrue(
            homePage.isUserLoggedIn(),
            "User should be logged in after successful login"
        );
        AllureManager.attachScreenshot("login_successful");
    }
    
    public static void assertLoginErrorDisplayed(LoginPage loginPage) {
        Assert.assertTrue(
            loginPage.isErrorMessageDisplayed(),
            "Login error message should be displayed"
        );
    }
}
```

### 6. Supporting Classes

**BrowserActions:** Common interactions (click, fill, scroll, wait)
```java
BrowserActions.click(By.id("button"));
BrowserActions.fill(By.id("input"), "text");
BrowserActions.navigateTo("https://example.com");
```

**WaitManager:** Explicit waits for elements and conditions
```java
WaitManager.waitForElement(By.id("element"), 10);  // Up to 10 seconds
WaitManager.waitForElementClickable(By.id("button"), 5);
```

**Element:** Wrapper around WebElement for enhanced methods
```java
Element element = new Element(driver.findElement(By.id("input")));
element.clear();
element.fill("text");
```

**ConfigManager & EnvReader:** Configuration management
```java
String baseUrl = ConfigManager.getProperty("base.url");
String username = EnvReader.getEnv("STANDARD_USERNAME");
```

**AllureManager:** Allure API integration
```java
AllureManager.step("User logs in");
AllureManager.attachScreenshot("login_page");
AllureManager.attachText("response", responseBody);
```

## Test Execution Data Flow

```
Test Method Execution:
1. @BeforeMethod
   ↓ (calls TestFixtures.setUp())
   • DriverManager.getDriver() → initializes WebDriver
   • Instantiate page objects (LoginPage, HomePage)
   • Instantiate flows (LoginFlow)

2. Test Execution (Arrange-Act-Assert)
   ↓
   ARRANGE: Create test data via factories
   └─ User user = UserFactory.validUser();
   
   ↓
   ACT: Call flows/pages
   └─ loginFlow.loginAs(user);
   
   ↓
   ASSERT: Verify via assertion helpers
   └─ LoginAssertions.assertLoginSuccessful(homePage);

3. Listeners Capture Data
   ↓ (AllureTestListener)
   • Screenshots on failure
   • Logs and timeline
   • Test metadata

4. @AfterMethod
   ↓ (calls TestFixtures.tearDown())
   • DriverManager.quitDriver() → closes browser

5. Reporting
   ↓
   • Allure results in allure-results/
   • TestNG reports in target/surefire-reports/
```

## Best Practices

### Page Object Best Practices
- ✅ Keep locators private; expose public action methods
- ✅ Use explicit waits (WaitManager) instead of Thread.sleep()
- ✅ Return page objects or flows from action methods (for chaining)
- ✅ Group related locators and methods
- ❌ Don't put assertions in page objects (move to assertions/assertions classes)
- ❌ Don't create WebDriver inside page objects (inject via constructor)

### Test Writing Best Practices
- ✅ Use BaseTest and TestFixtures for setup/teardown
- ✅ Follow Arrange-Act-Assert pattern
- ✅ Use factories for test data (not hard-coded values)
- ✅ Tag tests with TestNG groups (@Test(groups = {...}))
- ✅ Keep tests independent and idempotent
- ❌ Don't create new WebDriver instances in tests
- ❌ Don't hardcode credentials (use .env)
- ❌ Don't mix multiple concerns in one test method

### Framework Development Best Practices
- ✅ Place reusable utilities in framework/support/
- ✅ Document public methods and classes
- ✅ Use ThreadLocal for thread-safe state management
- ✅ Follow framework conventions for naming and organization
- ❌ Don't create test-specific framework code
- ❌ Don't introduce dependencies between core framework classes

## Configuration Management

**Property Files:**
- `src/main/resources/config.properties` — Application settings (base.url, browser, timeouts)
- `src/main/resources/logback.xml` — Logging levels and output format

**Environment Variables:**
- `src/main/resources/.env` — Sensitive data (credentials, API keys)
- CI detection: `CI=true` (auto-detects headless mode)

**Access in Code:**
```java
String baseUrl = ConfigManager.getProperty("base.url");
String username = EnvReader.getEnv("STANDARD_USERNAME");
```

## Parallel Execution

**Configure in testng.xml:**
```xml
<suite name="Demo Web Shop Automation Suite" parallel="methods" thread-count="4">
    <test name="Demo Web Shop Tests">
        <classes>
            <class name="demo_web_shop.RegistrationPageTests"/>
            <class name="demo_web_shop.LoginTests"/>
        </classes>
    </test>
</suite>
```

**Thread-Safety Considerations:**
- DriverManager uses ThreadLocal for WebDriver instances
- Each thread gets its own browser
- Test fixtures must not share mutable state
- Page objects should not have instance variables (use method parameters)

## Reporting Integration

**Allure Framework:**
- Results written to `allure-results/` during test execution
- Listeners capture screenshots on failure
- Steps, logs, and metadata automatically attached
- Generate HTML report: `allure generate allure-results -o allure-report`

**TestNG/Surefire:**
- HTML reports in `target/surefire-reports/`
- XML results available for CI/CD integration
- Parallel execution details captured

## Where to Look in the Repository

| Task | Location |
|------|----------|
| Add new test | `src/test/java/demo_web_shop/` (follow feature structure) |
| Create page object | `src/main/java/application/demo_web_shop/pages/` |
| Create business flow | `src/main/java/application/demo_web_shop/flows/` |
| Add assertion helper | `src/main/java/application/demo_web_shop/assertions/` |
| Extend framework | `src/main/java/framework/` (follow layer structure) |
| Configure tests | `src/test/resources/testng.xml` |
| Configure app | `src/main/resources/config.properties` |
| Debug options | `src/main/resources/logback.xml` |

