package base;

import application.demo_web_shop.fixtures.TestFixtures;
import framework.core.config.ConfigManager;
import framework.core.driver.Browser;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import static framework.core.driver.DriverManager.*;

public class BaseTest {

    private WebDriver driver;
    protected TestFixtures testFixtures;


    @BeforeSuite
    public void beforeSuite() {

        String browserName = ConfigManager.getBrowser().name();

        Browser browser = Browser.valueOf(browserName.toUpperCase());

        driver = setUpWebDriver(browser);
    }

    @BeforeMethod
    public void setUp() {

        String url = ConfigManager.getBaseUrl();

        navigateTo(url);

        testFixtures = new TestFixtures(driver);
    }

    @AfterMethod
    public void tearDown() {
      tearDownDriver();
    }
}
