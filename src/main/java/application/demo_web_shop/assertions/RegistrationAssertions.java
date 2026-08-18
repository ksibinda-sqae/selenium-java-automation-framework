package application.demo_web_shop.assertions;

import application.demo_web_shop.pages.RegistrationPage;
import framework.assertions.FrameworkAssertions;
import framework.support.data.DataManager;
import framework.support.logging.FrameworkLogger;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import static framework.support.data.DataManager.*;

public class RegistrationAssertions {

    private static final Logger logger = FrameworkLogger.getLogger(RegistrationAssertions.class);

    private final RegistrationPage registrationPage ;

    private final String REGISTRATION_DATA_FILE = "registration-page-data";

    private final String REGISTRATION_SUCCESS_MESSAGE = "successMessage";

    public RegistrationAssertions(WebDriver driver) {

        registrationPage = new RegistrationPage(driver);
    }

    public void assertSuccessfulRegistration() {


        String actualMessage = registrationPage.getSuccessfulRegistrationMsg();

        String expectedMessage = getData(REGISTRATION_SUCCESS_MESSAGE, REGISTRATION_DATA_FILE);

        boolean isDisplayed = registrationPage.isContinueButtonDisplayed();

        FrameworkAssertions.assertEquals(actualMessage, expectedMessage, "Successful registration");

        FrameworkAssertions.assertTrue(isDisplayed, "The continue button should be displayed");

    }
}
