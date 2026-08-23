Architecture Overview

Framework Design

This framework follows a Page Object Model (POM) pattern with TestNG fixtures and a small Layered Enterprise Framework (LEF) structure to keep tests readable and maintainable.

Component Architecture

```
tests/
├── ui/                          # UI tests (src/test/java)
│   ├── functional/              # Functional test suites
│   └── setup/                   # Test setup/fixtures
└── api/                         # API tests

src/main/java/application/       # Page objects, flows, assertions, fixtures
├── BasePage.java
├── demo_web_shop/
│   ├── pages/
│   │   ├── LandingPage.java
│   │   └── RegistrationPage.java
│   ├── flows/
│   │   └── RegistrationFlow.java
│   ├── components/
│   │   └── Header.java
│   ├── assertions/
│   │   └── RegistrationAssertions.java
│   └── utils/
│       ├── RandomDataGeneratorUtil.java
│       └── CredentialsUtil.java

src/main/java/framework/         # Framework core and support
├── core/
│   ├── driver/                  # DriverManager, Browser wrapper
│   │   └── DriverManager.java
│   ├── waits/                   # WaitManager.java
│   ├── actions/                 # BrowserActions.java
│   └── config/                  # ConfigManager.java, EnvReader.java
├── support/
│   ├── elements/                # Element.java wrapper
│   ├── reporting/               # AllureManager.java, ScreenshotManager.java
│   └── logging/                 # FrameworkLogger.java
└── listeners/                   # Test listeners (AllureTestListener, TestListener)

src/main/resources/              # Configuration, .env example
src/test/resources/              # testng.xml, test data JSON
```

Data Flow

```
Test Case
    ↓
BaseTest / Fixtures (TestFixtures.java)
    ↓
Flows & Page Objects (RegistrationFlow, RegistrationPage)
    ↓
BrowserActions and WaitManager
    ↓
Selenium WebDriver (DriverManager)
    ↓
Assertions
    ↓
Allure / Surefire reports
```

Key Components

1. Test Fixtures (src/main/java/application/demo_web_shop/fixtures/TestFixtures.java)
   - TestNG setup helpers and small fixtures used by tests (e.g., test data preparation, browser wiring via DriverManager).

2. DriverManager (src/main/java/framework/core/driver/DriverManager.java)
   - Manages WebDriver lifecycle using WebDriverManager. Detects CI via System.getenv("CI") to enable headless Chrome options.

3. BasePage & Page Objects (src/main/java/application/BasePage.java and pages/)
   - Encapsulate element locators and page-level actions. Use Element, BrowserActions and WaitManager for stability.

4. Flows (application/demo_web_shop/flows/RegistrationFlow.java)
   - Compose page actions into high-level user flows to keep tests readable.

5. Assertions (src/main/java/application/demo_web_shop/assertions/RegistrationAssertions.java and framework/assertions)
   - Provide semantic, reusable checks and integrate with reporting/listeners.

6. Support utilities (framework/support and framework/core)
   - DataManager, RandomDataGeneratorUtil, FrameworkLogger, AllureManager and ScreenshotManager support data loading, logging and report attachments.

7. Configuration (src/main/resources/config.properties and src/main/resources/.env)
   - Centralized environment configuration and property reader (ConfigManager/EnvReader).

Test Execution Flow

1. Setup Phase
   - Load properties and environment overrides
   - Initialize DriverManager (creates browser instance)
   - Prepare test fixtures (TestFixtures)

2. Test Execution
   - Execute test steps using flows and page objects
   - BrowserActions and WaitManager perform stable interactions
   - Capture screenshots/traces on failure via listeners

3. Reporting Phase
   - Allure and Surefire results produced under allure-results and target/surefire-reports
   - Generate HTML Allure report: `allure generate allure-results -o allure-report --clean` and `allure open allure-report`

Best Practices

- Use Page Objects for UI element encapsulation and Flows for business-level sequences.
- Prefer TestFixtures and BaseTest wiring over creating new drivers or page objects inside tests.
- Reuse utilities in framework.support to avoid duplication (DataManager, RandomDataGeneratorUtil).
- Keep environment-specific values out of source; use .env and config.properties with ConfigManager/EnvReader.
- Tag tests using groups (smoke, regression, api) and configure TestNG parallelism in src/test/resources/testng.xml.
- Attach screenshots and meaningful logs on failures via Allure listeners for faster debugging.

Where to look in the repo

- Core framework: src/main/java/framework/
- Application examples (pages/flows/assertions): src/main/java/application/
- Tests: src/test/java/
- Config: src/main/resources/

