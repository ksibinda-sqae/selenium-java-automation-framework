package application.demo_web_shop.fixtures;

import application.demo_web_shop.assertions.RegistrationAssertions;
import application.demo_web_shop.flows.RegistrationFlow;
import org.openqa.selenium.WebDriver;

public class TestFixtures {

    public RegistrationFlow registrationFlow;
    public RegistrationAssertions registrationAssertions;


    public TestFixtures(WebDriver driver) {
        this.registrationFlow = new RegistrationFlow(driver);
        this.registrationAssertions = new RegistrationAssertions(driver);
    }
}
