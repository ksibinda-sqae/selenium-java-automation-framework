# Selenium Java Automation Framework

A modern, enterprise-grade test automation framework for web applications using **Java**, **Selenium WebDriver**, **TestNG**, and **Allure** reporting.

## What is this?

This framework automates testing for the **Demo Web Shop** application using industry best practices:

- **Page Object Model (POM)** for maintainable UI automation
- **TestNG** for flexible test organization and parallel execution
- **Allure** for beautiful test reports with detailed insights
- **WebDriverManager** for automatic browser driver management
- **CI/CD Integration** with GitHub Actions for continuous testing
- **Environment-based Configuration** for multi-environment support

## Quick Start

### Prerequisites
- **Java 21+**
- **Maven 3.6+**
- **Git**

### Setup in 2 minutes

```bash
# Clone and install
git clone https://github.com/ksibinda-sqae/selenium-java-automation-framework.git
cd selenium-java-automation-framework
mvn clean install

# Configure credentials
cp src/main/resources/.env.example src/main/resources/.env
# Edit .env with your test credentials

# Run tests
mvn test

# View Allure report (optional)
allure serve allure-results
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn -Dtest=demo_web_shop.RegistrationPageTests test

# Run by group (smoke, reg, ui, api)
mvn test -Dgroups=smoke

# Run single test method
mvn -Dtest=demo_web_shop.RegistrationPageTests#TC001_userCanRegisterWithValidData test
```

For detailed test running options, see [docs/how-to/testing-conventions.md](docs/how-to/testing-conventions.md#running-tests-effectively).

## Test Reports

**Local:** After running tests, generate and view Allure report:
```bash
allure generate allure-results -o allure-report --clean
allure open allure-report
```

**CI/CD:** Reports are automatically generated and deployed to GitHub Pages on each workflow run.

## Project Structure

```
src/
├── main/java/
│   ├── application/          # Pages, flows, assertions, fixtures
│   │   ├── BasePage.java
│   │   └── demo_web_shop/
│   │       ├── pages/        # Page objects
│   │       ├── flows/        # Business-level flows
│   │       ├── assertions/   # Custom assertions
│   │       └── utils/        # Utilities
│   └── framework/            # Core framework
│       ├── core/             # Driver, config, waits, actions
│       ├── support/          # Reporting, logging, elements
│       └── listeners/        # Test listeners
├── main/resources/           # config.properties, .env, logback.xml
└── test/
    ├── java/                 # Test classes
    └── resources/            # testng.xml, test data
docs/how-to/                  # Detailed technical documentation
```

For complete structure details, see [docs/how-to/architecture-overview.md](docs/how-to/architecture-overview.md).

## Documentation

This project has two levels of documentation:

| Document | Level | Purpose |
|----------|-------|---------|
| **[docs/how-to/README.md](docs/how-to/README.md)** | Entry Point | Overview of all documentation |
| **[docs/how-to/architecture-overview.md](docs/how-to/architecture-overview.md)** | Technical | System design, components, data flow |
| **[docs/how-to/framework-conventions.md](docs/how-to/framework-conventions.md)** | Technical | Naming standards, file organization, coding rules |
| **[docs/how-to/testing-conventions.md](docs/how-to/testing-conventions.md)** | Technical | Testing guidelines, best practices, patterns |

**Start here:** New developers should read [docs/how-to/README.md](docs/how-to/README.md) first.

## CI/CD Pipeline

Automated testing runs on schedule (daily at 18:00 UTC) or on manual trigger.

**Configure GitHub Secrets:**
- `STANDARD_USERNAME`, `STANDARD_PASSWORD`
- `REGISTRATION_PASSWORD`
- `USERNAME`, `PASSWORD`

See [docs/how-to/testing-conventions.md#ci](docs/how-to/testing-conventions.md#cicd-integration) for CI setup details.

## Contributing

1. Follow [framework-conventions.md](docs/how-to/framework-conventions.md) for naming and organization
2. Write tests using [testing-conventions.md](docs/how-to/testing-conventions.md) guidelines
3. Keep code in separate concerns (pages, flows, tests, framework)
4. Never commit secrets—use `.env` for sensitive data
5. Run `mvn test` and verify Allure reports before submitting
