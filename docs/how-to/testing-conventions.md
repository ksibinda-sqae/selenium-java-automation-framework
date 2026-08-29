# Testing Conventions

This document provides comprehensive guidelines for writing consistent, maintainable tests in this Selenium + TestNG Java framework.

## Table of Contents

- [Test Structure & Pattern](#test-structure--pattern)
- [Using Fixtures & Setup](#using-fixtures--setup)
- [Test Data Generation](#test-data-generation)
- [Page Object Interactions](#page-object-interactions)
- [Reusable Flows](#reusable-flows)
- [Assertion Best Practices](#assertion-best-practices)
- [TestNG Groups & Organization](#testng-groups--organization)
- [Waiting for Elements](#waiting-for-elements)
- [Error Handling & Debugging](#error-handling--debugging)
- [Test Independence](#test-independence)
- [Test Documentation](#test-documentation)
- [Running Tests Effectively](#running-tests-effectively)
- [Reporting & Debugging](#reporting--debugging)
- [Performance Optimization](#performance-optimization)
- [Common Test Patterns](#common-test-patterns)
- [CI/CD Integration](#cicd-integration)

## Test Structure & Pattern

**Convention:** Follow **Arrange-Act-Assert (AAA)** pattern for all tests.

```java
@Test(groups = {"smoke", "auth"})
public void TC001_userCanLoginWithValidCredentials() {
    
    // ARRANGE - Set up test preconditions and data
    User user = UserFactory.standard();
    
    // ACT - Execute the behavior being tested
    HomePage homePage = loginFlow.loginAs(user);
    
    // ASSERT - Verify the expected outcome
    LoginAssertions.assertLoginSuccessful(homePage);
}
```

**Why AAA?**
- Clear test intent: Setup → Action → Verification
- Easier to debug failures
- Better test documentation
- Consistent across team

**Anti-pattern (avoid):**
```java
❌ // Mixing concerns, unclear flow
public void testLogin() {
    User user = new User("email", "pass");
    loginPage.fillEmail(user.getEmail());
    LoginAssertions.assertLoginSuccessful(...);
    loginPage.fillPassword(user.getPassword());
    // ...
}
```

## Using Fixtures & Setup

### BaseTest Class

All test classes should extend `BaseTest` for automatic lifecycle management:

```java
public abstract class BaseTest {
    
    protected WebDriver driver;
    protected LoginPage loginPage;
    protected HomePage homePage;
    protected LoginFlow loginFlow;
    
    @BeforeClass
    public void beforeClass() {
        // Runs once per test class
        // Used for expensive setup (e.g., DB setup)
    }
    
    @BeforeMethod
    public void setUp() {
        // Runs before each test method
        driver = DriverManager.getDriver();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        loginFlow = new LoginFlow(driver);
    }
    
    @AfterMethod
    public void tearDown() {
        // Runs after each test method
        DriverManager.quitDriver();
    }
    
    @AfterClass
    public void afterClass() {
        // Runs once after all tests in class
        // Used for cleanup (e.g., DB cleanup)
    }
}
```

### Test Class Example

```java
public class LoginTests extends BaseTest {
    
    @Test(groups = {"smoke", "auth"})
    public void TC001_userCanLoginWithValidCredentials() {
        // ARRANGE
        User user = UserFactory.standard();
        
        // ACT
        homePage = loginFlow.loginAs(user);
        
        // ASSERT
        LoginAssertions.assertLoginSuccessful(homePage);
    }
    
    @Test(groups = {"reg", "auth"})
    public void TC002_userCannotLoginWithInvalidPassword() {
        // ARRANGE
        User user = UserFactory.standard().setPassword("WrongPassword123");
        
        // ACT
        loginFlow.loginWithInvalidCredentials(user.getEmail(), user.getPassword());
        
        // ASSERT
        LoginAssertions.assertLoginErrorDisplayed(loginPage);
    }
}
```

**Benefits:**
- ✅ Automatic driver/page object initialization
- ✅ Consistent setup/teardown across all tests
- ✅ No boilerplate in individual tests
- ✅ Easy to add shared utilities

## Test Data Generation

### Factory Pattern

Use factory classes to generate deterministic test data instead of hardcoding values:

```java
public class UserFactory {
    
    // Standard user for happy path
    public static User standard() {
        return new User()
            .setEmail("testuser@example.com")
            .setPassword("ValidPass123!")
            .setFirstName("John")
            .setLastName("Doe")
            .setGender("Male")
            .setDateOfBirth("1990-01-15");
    }
    
    // User for duplicate/existing email tests
    public static User withExistingEmail() {
        return standard()
            .setEmail("existing@example.com");
    }
    
    // User with invalid email format
    public static User withInvalidEmail() {
        return standard()
            .setEmail("not-an-email");
    }
    
    // User with missing required field
    public static User withoutEmail() {
        return standard()
            .setEmail(null);
    }
    
    // Builder for custom combinations
    public static UserBuilder custom() {
        return new UserBuilder();
    }
}
```

### Builder Pattern (for complex data)

```java
public class UserBuilder {
    private String email = "test@example.com";
    private String password = "Pass123!";
    private String firstName = "John";
    private String lastName = "Doe";
    
    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }
    
    public User build() {
        return new User(email, password, firstName, lastName);
    }
}

// Usage
User customUser = UserFactory.custom()
    .withEmail("custom@test.com")
    .withPassword("Custom123!")
    .build();
```

**Benefits:**
- ✅ Centralized test data management
- ✅ Reusable across multiple tests
- ✅ Easy to update (one place)
- ✅ Clear intent (UserFactory.standard() vs hardcoded)
- ✅ Support various scenarios (valid, invalid, edge cases)

## Page Object Interactions

### Best Practices

**DO:**
- ✅ Use page objects for all UI interactions
- ✅ Keep locators private
- ✅ Expose intent-revealing action methods
- ✅ Use `BrowserActions` and `WaitManager` for stability
- ✅ Return page objects for method chaining

**DON'T:**
- ❌ Create WebDriver instances in page objects
- ❌ Put assertions in page objects
- ❌ Hardcode wait times
- ❌ Use generic method names (click(), fill())
- ❌ Access locators from test code

### Page Object Example

```java
public class CheckoutPage extends BasePage {
    
    // Private locators
    private By shippingMethodSelect = By.id("shippingMethod");
    private By continueButton = By.id("continue");
    private By paymentMethodSelect = By.id("paymentMethod");
    private By placeOrderButton = By.xpath("//button[text()='Place Order']");
    private By orderConfirmation = By.id("orderConfirmation");
    
    public CheckoutPage(WebDriver driver) {
        super(driver);
    }
    
    // Action methods - intent-revealing names
    public CheckoutPage selectShippingMethod(String method) {
        BrowserActions.selectDropdown(shippingMethodSelect, method);
        BrowserActions.click(continueButton);
        WaitManager.waitForElement(paymentMethodSelect);  // Wait for next step
        return this;
    }
    
    public CheckoutPage selectPaymentMethod(String method) {
        BrowserActions.selectDropdown(paymentMethodSelect, method);
        return this;
    }
    
    public OrderConfirmationPage placeOrder() {
        BrowserActions.click(placeOrderButton);
        WaitManager.waitForElement(orderConfirmation);
        return new OrderConfirmationPage(driver);
    }
    
    // Query methods for verification
    public String getOrderTotal() {
        return driver.findElement(By.id("orderTotal")).getText();
    }
    
    public boolean isOrderConfirmationDisplayed() {
        return WaitManager.isElementVisible(orderConfirmation);
    }
}
```

## Reusable Flows

### When to Create Flows

- Multi-step business operations (checkout, purchase, registration)
- Reused across multiple test classes
- Improved test readability
- Hide page-level complexity

### Flow Example

```java
public class CheckoutFlow {
    
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private PaymentPage paymentPage;
    private OrderConfirmationPage confirmationPage;
    
    public CheckoutFlow(WebDriver driver) {
        this.cartPage = new CartPage(driver);
        this.checkoutPage = new CheckoutPage(driver);
        this.paymentPage = new PaymentPage(driver);
        this.confirmationPage = new OrderConfirmationPage(driver);
    }
    
    // High-level business operation
    public OrderConfirmationPage completePurchase(Order order, PaymentInfo payment) {
        cartPage.goToCheckout();
        
        checkoutPage
            .selectShippingMethod(order.getShippingMethod())
            .selectPaymentMethod(payment.getMethod());
        
        paymentPage.fillPaymentDetails(payment);
        
        return paymentPage.submitOrder();
    }
}
```

### Flow Usage in Test

```java
public class CheckoutTests extends BaseTest {
    
    private CheckoutFlow checkoutFlow;
    
    @BeforeMethod
    public void setUp() {
        super.setUp();
        checkoutFlow = new CheckoutFlow(driver);
    }
    
    @Test(groups = {"smoke", "cart"})
    public void TC020_userCanCompletePurchase() {
        // ARRANGE
        Order order = OrderFactory.standard();
        PaymentInfo payment = PaymentFactory.validCard();
        
        // ACT
        OrderConfirmationPage confirmation = checkoutFlow.completePurchase(order, payment);
        
        // ASSERT
        CheckoutAssertions.assertOrderConfirmationDisplayed(confirmation);
    }
}
```

## Assertion Best Practices

### Semantic Assertions

Use custom assertion methods that describe behavior, not implementation:

```java
// ✅ Good - describes expected behavior
LoginAssertions.assertUserLoggedIn(homePage);
CartAssertions.assertProductInCart(productId, quantity);
CheckoutAssertions.assertOrderConfirmationDisplayed(page);

// ❌ Bad - implementation details, not behavior
Assert.assertTrue(driver.findElement(By.id("user-name")).isDisplayed());
Assert.assertEquals(driver.findElements(By.id("cart-item")).size(), 1);
```

### Assertion Class Structure

```java
public class LoginAssertions {
    
    // Semantic assertions with Allure integration
    public static void assertLoginSuccessful(HomePage homePage) {
        Assert.assertTrue(
            homePage.isUserLoggedIn(),
            "User should be logged in after successful login"
        );
        AllureManager.step("✅ User is logged in");
        AllureManager.attachScreenshot("login_success");
    }
    
    public static void assertLoginErrorDisplayed(LoginPage loginPage) {
        Assert.assertTrue(
            loginPage.isErrorMessageDisplayed(),
            "Login error message should be displayed"
        );
        AllureManager.step("✅ Login error displayed");
    }
    
    public static void assertErrorMessageContent(LoginPage loginPage, String expected) {
        String actual = loginPage.getErrorMessage();
        Assert.assertEquals(
            actual,
            expected,
            "Error message should match expected value"
        );
        AllureManager.step("✅ Error message: " + actual);
    }
}
```

### Multiple Assertions in One Method

Group related assertions:

```java
public static void assertSuccessfulRegistration(HomePage homePage, User user) {
    // User logged in
    Assert.assertTrue(homePage.isUserLoggedIn(), "User should be logged in");
    
    // User email displayed
    Assert.assertEquals(
        homePage.getLoggedInUserEmail(),
        user.getEmail(),
        "User email should be displayed in header"
    );
    
    // Welcome message shown
    Assert.assertTrue(
        homePage.isWelcomeMessageDisplayed(),
        "Welcome message should be shown"
    );
    
    AllureManager.step("✅ Registration successful - user logged in with welcome message");
    AllureManager.attachScreenshot("registration_success");
}
```

## TestNG Groups & Organization

### Standard Groups

```
smoke    — Critical path tests (must always pass)
reg      — Full regression suite
func     — Functional feature tests
ui       — UI tests (vs. API)
api      — API tests (vs. UI)
auth     — Authentication tests
cart     — Shopping cart tests
orders   — Order/checkout tests
slow     — Tests taking >2 seconds (exclude from quick runs)
flaky    — Known intermittent issues (exclude from CI)
wip      — Work in progress (skip by default)
```

### Applying Groups

```java
// Single group
@Test(groups = "smoke")
public void TC001_...() { }

// Multiple groups
@Test(groups = {"smoke", "auth", "ui"})
public void TC002_...() { }

// Exclude group
@Test(groups = "smoke", enabled = false)  // Skip this test
public void TC003_...() { }
```

### Running by Group

```bash
# Run only smoke tests
mvn test -Dgroups=smoke

# Run regression suite
mvn test -Dgroups=reg

# Multiple groups (OR)
mvn test -Dgroups="smoke,reg"

# All except flaky tests
mvn test -Dgroups="!flaky"

# Smoke + UI only
mvn test -Dgroups="smoke" -Dgroups="ui"  # AND operation
```

### Configure in testng.xml

```xml
<suite name="Demo Web Shop Automation Suite" parallel="methods" thread-count="4">
    
    <!-- Smoke test suite -->
    <test name="Smoke Tests">
        <groups>
            <include name="smoke"/>
        </groups>
        <classes>
            <class name="demo_web_shop.LoginTests"/>
            <class name="demo_web_shop.RegistrationPageTests"/>
        </classes>
    </test>
    
    <!-- Full regression -->
    <test name="Regression Suite">
        <groups>
            <include name="reg"/>
            <exclude name="flaky"/>
            <exclude name="wip"/>
        </groups>
        <classes>
            <class name="demo_web_shop.ui.functional.authentication.LoginTests"/>
            <class name="demo_web_shop.ui.functional.cart.CartTests"/>
        </classes>
    </test>
</suite>
```

## Waiting for Elements

### Use WaitManager (not Thread.sleep)

**DON'T:**
```java
❌ Thread.sleep(5000);  // Magic number, fragile, wastes time
```

**DO:**
```java
✅ WaitManager.waitForElement(By.id("loader"));  // Explicit, clear intent
✅ WaitManager.waitForElementClickable(By.id("button"), 10);
✅ BrowserActions.click(By.id("button"));  // Handles wait internally
```

### WaitManager Methods

```java
// Wait for element to be present in DOM
WaitManager.waitForElement(By.id("element"));

// Wait for element to be visible
WaitManager.waitForElementVisible(By.id("element"));

// Wait for element to be clickable
WaitManager.waitForElementClickable(By.id("button"));

// Wait for element to disappear
WaitManager.waitForElementInvisible(By.id("loader"));

// Wait for URL to change
WaitManager.waitForUrlChange("https://example.com/page");

// Custom explicit wait
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.visibilityOf(element));
```

## Error Handling & Debugging

### Don't Swallow Exceptions

**DON'T:**
```java
❌ try {
    loginPage.clickLogin();
} catch (Exception e) {
    // Silently ignore - bad practice
}
```

**DO:**
```java
✅ // Let test framework handle failures
loginPage.clickLogin();
// Listeners will capture screenshots/logs automatically
```

### Listeners Capture Failures

The framework automatically captures on test failure:
- Screenshot of the browser
- Browser logs
- Test execution timeline (Allure)
- Step descriptions

See `src/main/java/framework/listeners/AllureTestListener.java`

## Test Independence

### Requirements

- Each test must be completely independent
- No test should rely on state from another test
- Tests can run in any order
- Tests can run in parallel

### How to Achieve

```java
// ✅ Good - Independent, uses fresh data
@Test
public void TC001_userCanRegister() {
    User user = UserFactory.standard();  // Fresh data each time
    registrationFlow.register(user);
    RegistrationAssertions.assertUserRegistered(user.getEmail());
}

// ❌ Bad - Depends on data from another test
@Test
public void TC001_userCanRegister() {
    registrationFlow.register(sharedUser);  // Shared state!
}

@Test
public void TC002_userCanLogin() {
    loginFlow.loginAs(sharedUser);  // Depends on TC001 running first!
}
```

### @BeforeMethod Cleanup

```java
@BeforeMethod
public void setUp() {
    driver = DriverManager.getDriver();
    // Ensure clean slate (not state from previous test)
    driver.navigate().to(ConfigManager.getProperty("base.url"));
}
```

## Test Documentation

### Clear Test Names

Test method name should describe what is being tested:

```java
// ✅ Clear intent
public void TC001_userCanRegisterWithValidData() { }
public void TC002_userCannotRegisterWithExistingEmail() { }
public void TC003_userReceivesValidationErrorWithInvalidEmail() { }

// ❌ Unclear
public void testRegister() { }
public void test1() { }
public void registerTest() { }
```

### Comments for Complex Steps

```java
@Test
public void TC010_userCanFilterProductsByPrice() {
    // ARRANGE
    Product product = ProductFactory.withPrice(100);
    
    // ACT
    // User is on products page
    ProductsPage productsPage = new ProductsPage(driver);
    
    // Set price filter range: $50-$200
    // This should include our $100 product
    productsPage.setPriceFilter(50, 200);
    productsPage.clickApplyFilters();
    
    // ASSERT
    ProductsAssertions.assertProductDisplayed(product);
}
```

## Running Tests Effectively

### Local Development

**Full suite:**
```bash
mvn test
```

**Specific test class:**
```bash
mvn -Dtest=demo_web_shop.LoginTests test
```

**Specific test method:**
```bash
mvn -Dtest=demo_web_shop.LoginTests#TC001_userCanLoginWithValidCredentials test
```

**By group:**
```bash
mvn test -Dgroups=smoke
```

**Parallel execution:**
```bash
# Edit testng.xml: parallel="methods" thread-count="4"
mvn test
```

**Skip tests:**
```bash
mvn clean install -DskipTests
```

**Debug single test in IDE:**
1. Set breakpoint in test or page object
2. Right-click test → Debug As → JUnit Test
3. Breakpoint will pause execution for inspection

## Reporting & Debugging

### Allure CLI Installation

Before generating reports locally, install Allure CLI:

**Option 1: npm (Recommended - works on all platforms)**
```bash
npm install -g allure-commandline
allure --version  # Verify
```

**Option 2: Homebrew (macOS/Linux)**
```bash
brew install allure
allure --version
```

**Option 3: Chocolatey (Windows)**
```bash
choco install allure
allure --version
```

**Troubleshooting:**
- If `allure` command not found after installation, add to PATH
- For npm: `npm list -g allure-commandline` to find installation location
- For Homebrew: `which allure` to verify location

### Local Report Generation

```bash
# Run tests (generates results in reports/allure-results/)
mvn test

# Generate Allure report
allure generate reports/allure-results -o reports/allure-report --clean

# Open in browser
allure open reports/allure-report
```

**Report locations:**
- `reports/allure-results/` — Raw Allure test results (generated by tests during execution)
- `reports/allure-report/` — Compiled HTML Allure report (generated by Allure CLI)
- `target/surefire-reports/` — TestNG/Surefire HTML reports

### CI Reports

- **GitHub Actions:** View run → Artifacts → download allure-results
- **GitHub Pages:** Auto-deployed report URL
- **Surefire reports:** target/surefire-reports/ (HTML)

### Debugging Failed Tests

1. **Check Allure report** — Screenshot + logs show failure point
2. **Review test output** — Maven logs include stack traces
3. **Run single test locally** — Reproduce in IDE with non-headless browser
4. **Add detailed logging** — Use `FrameworkLogger.log()` for context
5. **Check CI env** — Some failures only occur in headless mode

## Performance Optimization

### Reduce Test Runtime

**Parallel Execution:**
```xml
<!-- testng.xml: Run tests concurrently -->
<suite name="Demo Web Shop Automation Suite" parallel="methods" thread-count="4">
```

**Skip Non-Essential Setup:**
```java
@BeforeClass  // Runs once for class (vs @BeforeMethod = once per test)
public void expensiveSetup() {
    // DB setup, data provisioning
}
```

**Reuse Test Data:**
```java
// ✅ If safe: share test user across tests
@BeforeClass
public void setUp() {
    testUser = UserFactory.standard();
}

// ❌ Don't modify shared data between tests
```

**Use Smoke Tests:**
```bash
# Quick feedback: run only critical tests
mvn test -Dgroups=smoke  # Runs in < 2 minutes
```

## Common Test Patterns

### Happy Path Test

```java
@Test(groups = {"smoke", "auth"})
public void TC001_userCanLoginWithValidCredentials() {
    // ARRANGE
    User user = UserFactory.standard();
    
    // ACT
    HomePage homePage = loginFlow.loginAs(user);
    
    // ASSERT
    LoginAssertions.assertLoginSuccessful(homePage);
}
```

### Negative Test (Expected Failure)

```java
@Test(groups = {"func", "auth"})
public void TC002_userCannotLoginWithInvalidPassword() {
    // ARRANGE
    User user = UserFactory.standard().setPassword("WrongPassword");
    
    // ACT
    loginFlow.loginWithInvalidCredentials(user.getEmail(), user.getPassword());
    
    // ASSERT
    LoginAssertions.assertLoginErrorDisplayed(loginPage);
}
```

### Parametrized Test

```java
@Test(dataProvider = "invalidEmails", groups = {"func", "registration"})
public void TC003_userCannotRegisterWithInvalidEmail(String email) {
    // ARRANGE
    User user = UserFactory.standard().setEmail(email);
    
    // ACT
    registrationFlow.register(user);
    
    // ASSERT
    RegistrationAssertions.assertEmailValidationError(registrationPage);
}

@DataProvider(name = "invalidEmails")
public Object[][] invalidEmails() {
    return new Object[][]{
        {"not-an-email"},
        {"@example.com"},
        {"user@"},
        {"user@.com"}
    };
}
```

### Multi-Step Business Flow Test

```java
@Test(groups = {"smoke", "cart"})
public void TC020_userCanCompleteFullPurchase() {
    // ARRANGE
    User user = UserFactory.standard();
    Order order = OrderFactory.standard();
    PaymentInfo payment = PaymentFactory.validCard();
    
    // ACT
    // Step 1: Login
    HomePage homePage = loginFlow.loginAs(user);
    
    // Step 2: Browse and add to cart
    ProductsPage productsPage = homePage.clickProducts();
    productsPage.addProductToCart(order.getProduct());
    
    // Step 3: Checkout
    CartPage cartPage = homePage.openCart();
    OrderConfirmationPage confirmationPage = checkoutFlow.completePurchase(order, payment);
    
    // ASSERT
    CheckoutAssertions.assertOrderConfirmationDisplayed(confirmationPage);
    CheckoutAssertions.assertOrderTotal(confirmationPage, order.getTotalAmount());
}
```

## CI/CD Integration

### CI Environment Detection

The framework automatically detects CI:
```java
// DriverManager.java
if ("true".equals(System.getenv("CI"))) {
    // Use headless Chrome options
}
```

### Setting CI Variable

**GitHub Actions:**
```yaml
env:
  CI: true
```

**Local CI emulation:**
```bash
CI=true mvn test
```

### Required GitHub Secrets

Configure in Settings → Secrets:
- `STANDARD_USERNAME` — Test user username
- `STANDARD_PASSWORD` — Test user password
- `REGISTRATION_PASSWORD` — Registration test password
- `USERNAME` — Additional username
- `PASSWORD` — Additional password

### Artifact Retention

- Allure results retained 7 days in GitHub Actions
- Latest report auto-deployed to GitHub Pages
- Surefire reports available in workflow run details