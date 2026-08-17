package application.demo_web_shop.assertions;

import application.demo_web_shop.pages.RegistrationPage;
import framework.assertions.FrameworkAssertions;
import framework.support.logging.FrameworkLogger;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

public class RegistrationAssertions {

    private static final Logger logger = FrameworkLogger.getLogger(RegistrationAssertions.class);

    WebDriver driver;
    RegistrationPage registrationPage ;

    public RegistrationAssertions(WebDriver driver) {
        this.driver = driver;
        registrationPage = new RegistrationPage(driver);
    }

    public void assertSuccessfulRegistration() {

        logger.info("Assert Successful Registration");

            String actualMessage = "registrationPage.getSuccessfulRegistrationMsg().trim()";

            String expectedMessage = "Your registration completed";

            FrameworkAssertions.assertEquals(actualMessage, expectedMessage, "Successful registration");

    }
}
