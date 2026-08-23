Testing Conventions

Guidelines for writing consistent, maintainable tests in this framework (adapted to Java + TestNG).

Test Structure

Follow Arrange-Act-Assert (AAA) pattern in TestNG tests. Use descriptive test method names matching TC convention.

Example:

```java
@Test(groups = {"smoke", "auth"})
public void TC001_userCanLoginWithValidCredentials() {
    // ARRANGE - prepare data (use factories)
    User user = UserFactory.standard();

    // ACT - perform actions via flows/page objects
    registrationFlow.register(user);

    // ASSERT - verify using assertion helpers
    RegistrationAssertions.assertUserRegistered(user.getEmail());
}
```

Using Fixtures / Test Setup

- Use BaseTest and TestFixtures (TestNG @BeforeClass/@BeforeMethod) to initialize DriverManager, pages and flows.
- Avoid instantiating page objects or flows inside test methods; rely on fixtures provided by BaseTest/TestFixtures.

Available fixtures (examples in this repo):
- DriverManager (browser lifecycle)
- BaseTest/TestFixtures (setup/teardown)
- RegistrationFlow / LoginFlow
- Pages: LandingPage, RegistrationPage
- Assertions: RegistrationAssertions, FrameworkAssertions
- Utilities: DataManager, RandomDataGeneratorUtil

Test Data Generation

Use factory classes to produce consistent test data. Place factories under src/test/java or src/main/java/application/demo_web_shop/utils.

Example:

```java
User user = UserFactory.standard();
User invalid = UserFactory.withInvalidEmail();
```

Benefits: centralized, reusable, and easy to update test data.

Page Object Interactions

- All UI interactions should go through page objects (src/main/java/application/*/pages).
- Keep locators private; expose intent-revealing methods.
- Use framework helpers (Element, BrowserActions, WaitManager) to avoid flaky selectors.

Example:

```java
registrationPage.fillFirstName(user.getFirstName());
registrationPage.fillEmail(user.getEmail());
registrationPage.clickRegister();
```

Reusable Flows

- Flows compose multiple page actions into business-level operations (e.g., RegistrationFlow.register(user)).
- Use flows in tests to reduce duplication and improve readability.

Assertion Best Practices

1. Semantic assertions
   - Assertions should describe expected behavior, not implementation details.
   - Use framework assertion helpers (RegistrationAssertions, FrameworkAssertions) which also attach useful Allure context.

2. Group related assertions
   - One public assertion method can wrap multiple checks (assertSuccessfulRegistration()).

3. Clear messages
   - When using TestNG Assert, include messages: Assert.assertTrue(condition, "Expected user to be registered");

Tag Usage (TestNG groups)

Standard groups:
- smoke, reg, func, ui, api, auth

Running groups:

    mvn test -Dgroups=smoke

Example annotation:

```java
@Test(groups = {"smoke", "ui"})
public void TC010_addProductToCart() { ... }
```

Variable & Naming Rules

- Test class names: FeatureTests.java (e.g., RegistrationPageTests.java)
- Test methods: TC{number}_description in camelCase or PascalCase as shown above
- Page objects: PascalCase ending with Page (RegistrationPage.java)
- Flows: {Action}Flow.java (RegistrationFlow.java)
- Assertions: {Feature}Assertions.java
- Factory: {Entity}Factory.java
- Constants: UPPER_SNAKE_CASE
- Variables/methods: camelCase

Waiting for Elements

- Prefer framework WaitManager or WebDriverWait + ExpectedConditions.
- Avoid Thread.sleep(); use explicit waits instead.

Example:

```java
new WebDriverWait(driver, Duration.ofSeconds(10))
  .until(ExpectedConditions.visibilityOf(element));
```

Error Handling

- Let test framework handle failures. Do not swallow exceptions in tests.
- Use listeners (AllureTestListener/TestListener) to capture screenshots and logs on failure.

Test Independence

- Each test must be independent and idempotent.
- Do not rely on ordering or shared mutable state between tests.
- Use fresh data via factories or clean setup in @BeforeMethod.

Test Documentation

- Add short comments for non-obvious test steps and reference TC IDs in method names.
- Keep tests readable and focused on one behavior per test.

Running Tests Effectively

Local development:

- Full suite: mvn test
- Single class: mvn -Dtest=demo_web_shop.RegistrationPageTests test
- Single method: mvn -Dtest=demo_web_shop.RegistrationPageTests#TC001_userCanLoginWithValidCredentials test

CI:

- Ensure CI sets CI=true for headless runs (DriverManager detects CI env var).
- Preserve allure-results as artifacts.

Reporting & Debugging

- Allure: generate and open after mvn test

    allure generate allure-results -o allure-report --clean
    allure open allure-report

- Surefire reports under target/surefire-reports
- For debugging, run tests in IDE with non-headless browser by setting CI env absent or adjusting DriverManager.

Performance Tips

- Run relevant groups in parallel using TestNG parallel configuration in src/test/resources/testng.xml.
- Use factory data to avoid creating heavy fixtures repeatedly.
- Reuse shared setup where safe (e.g., test data provisioning), but avoid shared browser state between parallel threads unless designed for it.

Common Patterns (Java + TestNG)

Setup-Action-Assert:

```java
@Test(groups = {"reg"})
public void TC005_userCanOpenProductDetails() {
    User user = UserFactory.standard();
    loginFlow.loginAs(user);
    productsPage.goToProductDetails("Backpack");
    ProductDetailsAssertions.assertUserOnProductDetailsPage("Backpack");
}
```

Negative test example:

```java
@Test(groups = {"func"})
public void TC002_userCannotRegisterWithExistingEmail() {
    User user = UserFactory.withExistingEmail();
    registrationFlow.register(user);
    RegistrationAssertions.assertRegistrationFailedWithMessage("Email already exists");
}
```

Appendix: Useful imports (order)

1. JDK
2. org.testng.*
3. org.openqa.selenium.*
4. framework.*
5. application.*

Follow these conventions to keep tests consistent and maintainable. Update docs when introducing new patterns.