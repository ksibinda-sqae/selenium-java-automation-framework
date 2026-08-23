Selenium Java Automation Framework - How-To Guide

Overview

This documentation provides quick start and how-to guidance for the Selenium Java Automation Framework in this repository. It mirrors a Playwright-style README but is adapted to use Java, Selenium WebDriver, TestNG, WebDriverManager and Allure.

Contents

- architecture-overview.md — Architecture, data flow and where to find core classes
- framework-conventions.md — Naming conventions, file organization and coding rules
- testing-conventions.md — TestNG usage, running tests, reporting and best practices

Prerequisites

- Java 21 (or compatible JDK)
- Maven
- Internet access (WebDriverManager downloads browser drivers)

Installation

Install dependencies and build:

    mvn clean install

Ensure configuration files exist:

- Copy src/main/resources/.env.example to src/main/resources/.env and update any environment-specific values.
- Primary runtime settings live in src/main/resources/config.properties (base.url, browser).

Running Tests

Run all tests (TestNG suite configured in src/test/resources/testng.xml):

    mvn test

Run a specific test class:

    mvn -Dtest=demo_web_shop.RegistrationPageTests test

Run TestNG groups:

    mvn test -Dgroups=smoke

Parallel execution:

- Configure parallelism in src/test/resources/testng.xml (parallel="methods|tests|classes" thread-count="N") or via Surefire/TestNG plugin properties.
- CI detection (headless) is implemented in framework.core.driver.DriverManager using the CI environment variable.

Reporting

Allure results are produced in allure-results. Generate and open the report:

    mvn test
    allure generate allure-results -o allure-report --clean
    allure open allure-report

Surefire/TestNG reports are available under target/surefire-reports.

Architecture

Tests
↓
Flows
↓
Pages / API Clients
↓
Core Framework
↓
Selenium WebDriver

See docs/how-to/architecture-overview.md for detailed architecture and file mappings.

Contributing

Before contributing:
- Follow the framework conventions in framework-conventions.md
- Keep separation of concerns (pages, flows, tests)
- Avoid hard-coded secrets; use .env for environment values
- Run mvn test locally and ensure Allure artifacts are produced when appropriate

How-To Guide

Start with architecture-overview.md, then framework-conventions.md and testing-conventions.md to follow repository standards when adding tests or framework code.

Contact / Further Help

Open an issue or request specific examples (sample TestNG parallel config, example Test class) if you want concrete snippets added to these how-to pages.