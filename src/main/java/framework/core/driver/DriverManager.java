package framework.core.driver;

import framework.core.exceptions.FrameworkException;
import framework.support.reporting.allure.AllureManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import framework.support.logging.FrameworkLogger;

public final class DriverManager {

    private static final Logger LOGGER = FrameworkLogger.getLogger(DriverManager.class);
    private static WebDriver driver;

    private DriverManager() {
        // Private constructor to prevent instantiation
    }

    public static WebDriver  setUpWebDriver(Browser browser) {

        return switch (browser) {

            case CHROME -> {

                try {
                    LOGGER.info("Creating driver for chrome");

                    AllureManager.addStep("Creating chrome");

                    WebDriverManager.chromedriver().setup();

                    driver = new ChromeDriver();

                    yield driver;

                }catch (Exception e) {

                    throw new FrameworkException("Failed initiating browser: " + browser, e);
                }
            }

            case FIREFOX -> {

                try {
                    LOGGER.info("Creating driver for firefox");

                    AllureManager.addStep("Creating firefox");

                    WebDriverManager.firefoxdriver().setup();

                    driver = new FirefoxDriver();

                    yield driver;

                }catch (Exception e) {

                    throw new FrameworkException("Failed initiating browser: " + browser, e);
                }
            }

            case EDGE -> {

                try {
                    LOGGER.info("Creating driver for edge");

                    AllureManager.addStep("Creating edge");

                    WebDriverManager.edgedriver().setup();

                    driver = new EdgeDriver();

                    yield driver;

                }catch (Exception e) {

                    throw new FrameworkException("Failed initiating browser: " + browser, e);
                }
            }

            default ->  throw new FrameworkException("Unknown browser: " + browser);
        };
    }

    public static WebDriver getDriver() {

        if (driver == null) {
            throw new FrameworkException(
                    "WebDriver has not been initialized. Call setUpWebDriver() first."
            );
        }

        return driver;
    }

    public static void navigateTo(String url) {

        LOGGER.info(
                "Navigating to: {}", url
        );

        AllureManager.addStep(
                "Navigate to: " + url
        );

        getDriver().get(url);
    }

    public static void tearDownDriver() {

        if (driver != null) {
            LOGGER.info("Closing WebDriver");

            AllureManager.addStep("Close browser");

            driver.quit();
            driver = null;
        }
    }


}
