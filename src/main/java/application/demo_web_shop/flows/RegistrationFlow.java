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

    public LandingPage register(RegistrationUser registrationUser) {
        AllureManager.step("Register User", () -> {

            landingPage.header().clickRegisterLink();
            registrationPage.clickGenderMale();
            registrationPage.fillFirstName(registrationUser.firstName());
            registrationPage.fillLastName(registrationUser.lastName());
            registrationPage.fillEmail(registrationUser.email());
            registrationPage.fillPassword(registrationUser.password());
            registrationPage.fillConfirmPassword(registrationUser.password());
            return registrationPage.clickRegisterButton();

        });

        return landingPage;
    }
}
