package application.demo_web_shop.flows;

import application.demo_web_shop.data.users.RegistrationUser;
import application.demo_web_shop.pages.LandingPage;
import application.demo_web_shop.pages.RegistrationPage;
import framework.support.reporting.allure.AllureManager;
import org.openqa.selenium.WebDriver;

public class RegistrationFlow {

    private final LandingPage landingPage;
    private final RegistrationPage registrationPage;

    public RegistrationFlow(WebDriver driver) {
        this.landingPage = new LandingPage(driver);
        this.registrationPage = new RegistrationPage( driver );
    }

    public LandingPage register(String firstName, String lastName, String email, String password) {
        AllureManager.step("Register User", () -> {

            landingPage.header().clickRegisterLink();
            registrationPage.clickGenderMale();
            registrationPage.fillFirstName(firstName);
            registrationPage.fillLastName(lastName);
            registrationPage.fillEmail(email);
            registrationPage.fillPassword(password);
            registrationPage.fillConfirmPassword(password);
            return registrationPage.clickRegisterButton();

        });

        return landingPage;
    }
}
