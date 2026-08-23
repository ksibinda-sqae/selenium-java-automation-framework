Framework Conventions

This document outlines naming conventions and best practices used in this Selenium + TestNG Java framework. It mirrors common Playwright conventions but is adapted to this project's Java layout.

Test File Naming

- Convention: {feature}Tests.java (class name ending with Tests)
- Examples:
  - RegistrationPageTests.java - Registration feature tests
  - LoginTests.java - Login-related tests
  - CartTests.java - Cart feature tests
  - ProductsTests.java - Products feature tests

Test Organization

Tests are organized by feature and type under src/test/java:

```
src/test/java/
├── demo_web_shop/
│   ├── ui/
│   │   ├── functional/               # Functional UI tests
│   │   │   ├── authentication/
│   │   │   │   ├── LoginTests.java
│   │   │   │   └── LogoutTests.java
│   │   │   ├── cart/
│   │   │   │   └── CartTests.java
│   │   │   └── products/
│   │   │       └── ProductsTests.java
│   │   └── setup/
│   │       └── TestSetup.java        # Shared setup or suite-level helpers
│   └── api/
│       └── auth/
│           └── AuthApiTests.java
```

Test Case Naming

- Convention: TC{number} - {Description}
- Examples:
  - TC001 - User can register with valid data
  - TC002 - User cannot register with existing email
  - TC010 - User can add product to cart

TestNG Groups / Tags

TestNG uses groups for categorization and filtering.

Standard groups:
- smoke — Critical functionality
- reg — Regression suite
- func — Functional tests
- ui — UI tests
- api — API tests
- auth — Authentication-related tests

Usage example (TestNG):
```java
@Test(groups = {"smoke", "ui", "auth"})
public void TC001_userCanLogin() {
    // test code
}
```

Page Object Naming

- Convention: {Feature}Page.java
- Examples:
  - LoginPage.java
  - ProductsPage.java
  - CartPage.java
  - RegistrationPage.java

Page object structure guidance:
- Keep locators private; expose public actions that describe intent.
- Use Element wrapper, BrowserActions and WaitManager for interactions.

Flow Naming

- Convention: {Action}Flow.java
- Examples:
  - RegistrationFlow.java
  - LoginFlow.java
  - CheckoutFlow.java

Flows should compose page-level actions into high-level business tasks.

Assertion Class Naming

- Convention: {Feature}Assertions.java
- Examples:
  - RegistrationAssertions.java
  - CartAssertions.java
  - AuthApiAssertions.java

Assertions should be small, descriptive methods (assertSuccessfulLogin(), assertErrorMessage()) and integrate with Allure attachments where useful.

Factory Naming

- Convention: {Entity}Factory.java
- Examples:
  - UserFactory.java
  - ProductFactory.java

Factories produce deterministic test data (valid, invalid, edge-cases) for reuse across tests.

Method Naming

Action methods:
- Start with a verb: click..., fill..., navigateTo..., add..., remove...
- Examples: clickRegisterButton(), fillEmail(), navigateToLandingPage()

Assertion methods:
- Start with assert: assertRegisteredSuccessfully(), assertValidationError()

Utility methods:
- Descriptive names: waitForElement(), isElementVisible(), readConfigProperty()

Variable Naming

- Constants: UPPER_SNAKE_CASE (e.g., API_TIMEOUT, DEFAULT_WAIT)
- Fields/Methods/Vars: camelCase (e.g., userEmail, isLoggedIn)
- Classes: PascalCase (e.g., LoginPage, UserFactory)

Locator Conventions

- Prefer stable attributes (id, data-test) when available.
- XPath examples:
  - //button[contains(text(), "Register")]
  - //input[@name='Email']
- CSS examples:
  - [data-test="login-button"]
  - .login-form input[name="username"]

Configuration Properties

Environment and property naming patterns:
- UI_BASE_URL — UI application base URL
- API_BASE_URL — API base URL
- STANDARD_USERNAME, STANDARD_PASSWORD — test credentials (keep secrets out of repo)
- CI=true — CI detection for headless execution

Import Order (Java)

1. JDK imports (java.*, javax.*)
2. Third-party (org.openqa.selenium.*, org.testng.*, io.github.bonigarcia.*, io.qameta.allure.*)
3. Framework packages (framework.*)
4. Application packages (application.demo_web_shop.*)
5. Tests (demo_web_shop.*)

Example:
```java
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import framework.core.driver.DriverManager;
import application.demo_web_shop.pages.RegistrationPage;
import demo_web_shop.tests.BaseTest;
```

Summary

Follow these conventions to keep tests consistent and maintainable. Update documentation when introducing new patterns or exceptions.