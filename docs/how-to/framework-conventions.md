# Framework Conventions

This document outlines naming conventions, file organization, and coding standards for this Selenium + TestNG Java framework.

## Table of Contents

- [Test File Naming](#test-file-naming)
- [Test Organization](#test-organization)
- [Test Case Naming](#test-case-naming)
- [TestNG Groups](#testng-groups)
- [Page Object Naming & Structure](#page-object-naming--structure)
- [Flow Naming](#flow-naming)
- [Assertion Class Naming](#assertion-class-naming)
- [Factory Naming](#factory-naming)
- [Method Naming](#method-naming)
- [Variable & Constant Naming](#variable--constant-naming)
- [Locator Conventions](#locator-conventions)
- [Configuration Properties](#configuration-properties)
- [Import Order](#import-order-java)
- [Package Organization](#package-organization)

## Test File Naming

**Convention:** `{Feature}Tests.java`

**Examples:**
- `RegistrationPageTests.java` — Tests for registration feature
- `LoginTests.java` — Login-related tests
- `CartTests.java` — Shopping cart tests
- `ProductsTests.java` — Product listing tests
- `CheckoutTests.java` — Checkout flow tests

**Rationale:**
- "Tests" suffix clearly identifies test classes
- Feature name matches the page object (RegistrationPage → RegistrationPageTests)
- Consistent naming makes test discovery easier

## Test Organization

Tests are organized by feature and type under `src/test/java/`:

```
src/test/java/
├── base/
│   ├── BaseTest.java                 # Abstract base with @BeforeMethod/@AfterMethod
│   └── TestFixtures.java             # Common fixtures and factories
│
├── demo_web_shop/
│   ├── ui/
│   │   ├── functional/
│   │   │   ├── authentication/
│   │   │   │   ├── LoginTests.java
│   │   │   │   ├── LogoutTests.java
│   │   │   │   └── RegisterTests.java
│   │   │   ├── cart/
│   │   │   │   ├── CartTests.java
│   │   │   │   └── CartCheckoutTests.java
│   │   │   ├── products/
│   │   │   │   ├── ProductsTests.java
│   │   │   │   └── ProductDetailsTests.java
│   │   │   └── orders/
│   │   │       └── OrdersTests.java
│   │   └── setup/
│   │       └── TestSetup.java        # Shared setup, suite-level helpers
│   │
│   └── api/
│       ├── auth/
│       │   └── AuthApiTests.java
│       ├── products/
│       │   └── ProductsApiTests.java
│       └── orders/
│           └── OrdersApiTests.java
```

**Organization Principles:**
- Group tests by feature (authentication, cart, products)
- Separate UI tests (`ui/`) from API tests (`api/`)
- Separate functional tests from other test types (performance, security)
- One test class per page/feature (not one class per method)

## Test Case Naming

**Convention:** `TC{number}_{DescriptionInCamelCase}`

**Examples:**
- `TC001_userCanRegisterWithValidData`
- `TC002_userCannotRegisterWithExistingEmail`
- `TC003_userReceivesValidationErrorWithInvalidEmail`
- `TC010_userCanAddProductToCart`
- `TC011_userCanRemoveProductFromCart`

**Format:**
```java
@Test(groups = {"smoke", "ui"})
public void TC001_userCanRegisterWithValidData() {
    // Test implementation
}
```

**Rationale:**
- TC number allows traceability to requirements
- Camel case for readability
- Test name describes expected behavior, not implementation
- Numbers should not be reused (even if test is deleted/modified)

## TestNG Groups

**Convention:** Use standard groups for categorization and filtering.

**Standard Groups:**
```
- smoke      — Critical path / happy path tests (must pass)
- reg        — Full regression suite
- func       — Functional feature tests
- ui         — UI tests (vs. API)
- api        — API tests (vs. UI)
- auth       — Authentication-related
- cart       — Shopping cart related
- orders     — Order/checkout related
- slow       — Tests taking >2 seconds
- flaky      — Known to be intermittent
- wip        — Work in progress (skip by default)
```

**Usage:**
```java
@Test(groups = {"smoke", "ui", "auth"})
public void TC001_userCanLogin() { ... }

@Test(groups = {"reg", "ui", "products"})
public void TC010_userCanViewProductList() { ... }

@Test(groups = {"smoke", "cart"})
public void TC020_userCanAddProductToCart() { ... }
```

**Running by Group:**
```bash
# Smoke tests only
mvn test -Dgroups=smoke

# Smoke + regression
mvn test -Dgroups=smoke,reg

# All except flaky
mvn test -Dgroups="!flaky"

# UI tests only
mvn test -Dgroups=ui
```

**Configuration in testng.xml:**
```xml
<suite name="Demo Web Shop Automation Suite" parallel="methods" thread-count="4">
    <test name="Smoke Tests">
        <groups>
            <include name="smoke"/>
        </groups>
        <classes>
            <class name="demo_web_shop.RegistrationPageTests"/>
            <class name="demo_web_shop.LoginTests"/>
        </classes>
    </test>
</suite>
```

## Page Object Naming & Structure

**Convention:** `{Feature}Page.java`

**Examples:**
- `LoginPage.java`
- `ProductsPage.java`
- `CartPage.java`
- `CheckoutPage.java`
- `RegistrationPage.java`

**Structure:**
```java
public class LoginPage extends BasePage {
    
    // Private locators (By objects)
    private By emailInput = By.id("email");
    private By passwordInput = By.name("password");
    private By loginButton = By.xpath("//button[text()='Login']");
    private By errorMessage = By.id("error");
    
    // Constructor
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    // Public action methods (intent-revealing names)
    public void fillEmail(String email) {
        BrowserActions.fill(emailInput, email);
    }
    
    public void fillPassword(String password) {
        BrowserActions.fill(passwordInput, password);
    }
    
    public HomePage clickLogin() {
        BrowserActions.click(loginButton);
        return new HomePage(driver);  // Return next page for chaining
    }
    
    // Public query/verification methods
    public boolean isErrorMessageDisplayed() {
        return WaitManager.isElementVisible(errorMessage);
    }
    
    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }
}
```

**Best Practices:**
- ✅ Extend `BasePage` for common methods
- ✅ Keep locators private (use `private By` or `private static final By`)
- ✅ Expose intent-revealing action methods (`clickLogin()`, not `click()`)
- ✅ Use `BrowserActions` and `WaitManager` for stability
- ✅ Return page objects for method chaining
- ✅ Group related locators together
- ❌ Don't put assertions in page objects
- ❌ Don't hardcode wait times (use defaults from WaitManager)
- ❌ Don't create WebDriver instances

## Flow Naming

**Convention:** `{Action}Flow.java` or `{Feature}Flow.java`

**Examples:**
- `LoginFlow.java`
- `RegistrationFlow.java`
- `CheckoutFlow.java`
- `PurchaseFlow.java`

**Purpose:** Compose page actions into high-level business operations.

**Structure:**
```java
public class LoginFlow {
    
    private LoginPage loginPage;
    private HomePage homePage;
    
    public LoginFlow(WebDriver driver) {
        this.loginPage = new LoginPage(driver);
        this.homePage = new HomePage(driver);
    }
    
    // High-level business operation
    public HomePage loginAs(User user) {
        loginPage.fillEmail(user.getEmail());
        loginPage.fillPassword(user.getPassword());
        return loginPage.clickLogin();  // Returns HomePage
    }
    
    // Alternative path or error flow
    public LoginPage loginWithInvalidCredentials(String email, String password) {
        loginPage.fillEmail(email);
        loginPage.fillPassword(password);
        loginPage.clickLogin();
        return loginPage;  // Stays on login page (error)
    }
}
```

**When to Create Flows:**
- Multi-step operations (login, checkout, purchase)
- Reusable across multiple test classes
- Improves test readability
- Hides page-level complexity

**When NOT to Create Flows:**
- Single-step operations (just use page object directly)
- Test-specific workflows (inline in test)

## Assertion Class Naming

**Convention:** `{Feature}Assertions.java`

**Examples:**
- `LoginAssertions.java`
- `CartAssertions.java`
- `ProductDetailsAssertions.java`
- `CheckoutAssertions.java`

**Structure:**
```java
public class LoginAssertions {
    
    // Static methods for direct usage
    public static void assertLoginSuccessful(HomePage homePage) {
        Assert.assertTrue(
            homePage.isUserLoggedIn(),
            "User should be logged in after successful login"
        );
        AllureManager.step("User is logged in");
        AllureManager.attachScreenshot("login_success");
    }
    
    public static void assertLoginError(LoginPage loginPage, String expectedError) {
        Assert.assertTrue(
            loginPage.isErrorMessageDisplayed(),
            "Login error message should be displayed"
        );
        Assert.assertEquals(
            loginPage.getErrorMessage(),
            expectedError,
            "Error message should match expected"
        );
    }
    
    public static void assertInvalidEmailError(RegistrationPage page) {
        Assert.assertTrue(
            page.isEmailErrorDisplayed(),
            "Email validation error should be shown"
        );
    }
}
```

**Best Practices:**
- ✅ Use static methods for utility-style assertions
- ✅ Include descriptive failure messages
- ✅ Integrate with Allure (steps, screenshots)
- ✅ Group related assertions in same class
- ❌ Don't use assertion names that repeat "Assert" (use `assertUserLoggedIn()`, not `assertAssertUserLoggedIn()`)
- ❌ Don't mix high-level and low-level assertions

## Factory Naming

**Convention:** `{Entity}Factory.java`

**Examples:**
- `UserFactory.java`
- `ProductFactory.java`
- `OrderFactory.java`

**Purpose:** Generate test data deterministically for reuse.

**Structure:**
```java
public class UserFactory {
    
    // Standard/default user
    public static User standard() {
        return new User()
            .setEmail("standard@example.com")
            .setPassword("SecurePass123!")
            .setFirstName("John")
            .setLastName("Doe");
    }
    
    // User with existing email (for duplicate tests)
    public static User withExistingEmail() {
        return standard()
            .setEmail("existing@example.com");  // Already in system
    }
    
    // User with invalid email format
    public static User withInvalidEmail() {
        return standard()
            .setEmail("not-an-email");
    }
    
    // Builder pattern for flexibility
    public static UserBuilder custom() {
        return new UserBuilder();
    }
}

// Usage in tests:
User user = UserFactory.standard();
User existingUser = UserFactory.withExistingEmail();
User customUser = UserFactory.custom()
    .withEmail("custom@test.com")
    .withPassword("Custom123!")
    .build();
```

**Benefits:**
- Centralized, reusable test data
- Easy to update (update factory, not 20 tests)
- Clear intention (UserFactory.standard() vs hardcoded values)
- Supports various scenarios (valid, invalid, edge cases)

## Method Naming

### Action Methods
**Convention:** Verb at start: `click`, `fill`, `navigate`, `add`, `remove`, etc.

**Examples:**
```java
// Page objects
loginPage.clickLoginButton();
registrationPage.fillEmail("test@example.com");
cartPage.removeProductFromCart(productId);

// Flows
loginFlow.loginAs(user);
checkoutFlow.selectShippingMethod("express");

// Utilities
BrowserActions.fill(By.id("email"), "test@example.com");
BrowserActions.click(By.id("submit"));
WaitManager.waitForElement(By.id("loader"));
```

### Query/Assertion Methods
**Convention:** `is`, `get`, `has` prefix for queries

**Examples:**
```java
loginPage.isErrorMessageDisplayed();
cartPage.getCartItemCount();
productPage.hasReviews();
```

### Assertion Methods
**Convention:** `assert` prefix, describing the state

**Examples:**
```java
LoginAssertions.assertLoginSuccessful();
CartAssertions.assertProductInCart(productId);
CheckoutAssertions.assertOrderConfirmationDisplayed();
```

## Variable & Constant Naming

**Constants:** `UPPER_SNAKE_CASE`
```java
private static final int DEFAULT_WAIT_TIME = 10;
private static final String API_BASE_URL = "https://api.example.com";
private static final By SUBMIT_BUTTON = By.id("submit");
```

**Fields/Variables:** `camelCase`
```java
private WebDriver driver;
private LoginPage loginPage;
String userEmail = "test@example.com";
int itemCount = 5;
boolean isLoggedIn = true;
```

**Classes:** `PascalCase`
```java
public class LoginPage { }
public class UserFactory { }
public class RegistrationFlow { }
```

**Methods:** `camelCase`
```java
public void clickLoginButton() { }
public String getUserEmail() { }
public void loginAs(User user) { }
```

## Locator Conventions

**Priority Order:**
1. **ID attributes** (most stable): `By.id("email")`
2. **Name attributes**: `By.name("password")`
3. **Data attributes**: `By.cssSelector("[data-test='login-button']")`
4. **CSS selectors** (stable class combinations): `By.cssSelector(".login-form input[type='email']")`
5. **XPath** (last resort): `By.xpath("//button[contains(text(), 'Login')]")`

**XPath Examples:**
```java
// Text matching
By.xpath("//button[text()='Submit']")
By.xpath("//button[contains(text(), 'Submit')]")

// Attribute matching
By.xpath("//input[@name='email']")
By.xpath("//input[@type='password']")

// Hierarchy
By.xpath("//form//input[@name='email']")
By.xpath("//div[@id='login-form']//button[last()]")

// Avoid brittle XPath
❌ By.xpath("//div[1]/form[1]/input[2]")  // Too dependent on structure
✅ By.xpath("//input[@name='email']")     // Stable, self-documenting
```

**CSS Selector Examples:**
```java
// Class based
By.cssSelector(".login-button")
By.cssSelector(".form-input.email")

// Attribute based
By.cssSelector("[data-test='login']")
By.cssSelector("input[name='email']")

// Hierarchy (reasonable)
By.cssSelector(".login-form input[type='email']")
```

## Configuration Properties

**Environment Variables** (sensitive data in `.env`):
```properties
STANDARD_USERNAME=testuser
STANDARD_PASSWORD=SecurePass123!
REGISTRATION_PASSWORD=RegPass123!
USERNAME=anotheruser
PASSWORD=AnotherPass123!
API_KEY=xyz123abc
```

**Application Properties** (non-sensitive in `config.properties`):
```properties
# URLs
base.url=https://demo.example.com
api.base.url=https://api.example.com

# Browser
browser=CHROME
headless=false

# Waits (in seconds)
wait.default=10
wait.long=30
wait.short=5

# Logging
log.level=INFO
```

**Naming Pattern:**
- URLs: `base.url`, `api.base.url`, `admin.url`
- Credentials: `{feature}_USERNAME`, `{feature}_PASSWORD`
- Timeouts: `wait.{scope}` (wait.default, wait.long)
- Browser: `browser`, `headless`
- CI Detection: `CI=true` (environment variable)

## Import Order (Java)

Follow this import order to keep code consistent:

```java
// 1. JDK imports
import java.util.*;
import java.time.*;

// 2. Third-party libraries (alphabetical)
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

// 3. Framework packages
import framework.core.driver.DriverManager;
import framework.core.waits.WaitManager;
import framework.support.reporting.AllureManager;

// 4. Application packages
import application.BasePage;
import application.demo_web_shop.pages.*;
import application.demo_web_shop.flows.*;

// 5. Test packages
import base.BaseTest;
```

**IDE Configuration (IntelliJ):**
- Go to Preferences → Editor → Code Style → Java
- Imports tab: Set import order to match above

## Package Organization

**Framework Packages:**
```
framework
├── core
│   ├── driver       # WebDriver management
│   ├── waits        # Wait utilities
│   ├── actions      # Browser actions
│   ├── config       # Configuration readers
│   └── exceptions   # Framework exceptions
├── support
│   ├── elements     # Element wrapper
│   ├── reporting    # Allure integration
│   ├── logging      # Logger
│   └── data         # Data loading
└── listeners        # Test listeners
```

**Application Packages:**
```
application
├── BasePage         # Base page object
└── demo_web_shop
    ├── pages        # Page objects
    ├── flows        # Business flows
    ├── components   # Reusable components
    ├── assertions   # Custom assertions
    ├── fixtures     # Test fixtures
    └── utils        # Utilities
```

**Test Packages:**
```
demo_web_shop
├── ui
│   ├── functional
│   │   ├── authentication
│   │   ├── cart
│   │   ├── products
│   │   └── orders
│   └── setup
└── api
    ├── auth
    ├── products
    └── orders
```

## Summary

Following these conventions ensures:
- **Consistency** — Code looks familiar across the project
- **Maintainability** — Naming makes intent clear
- **Scalability** — New developers understand organization quickly
- **Discoverability** — Logical package/file structure

When introducing new patterns or exceptions to these conventions, update this documentation.