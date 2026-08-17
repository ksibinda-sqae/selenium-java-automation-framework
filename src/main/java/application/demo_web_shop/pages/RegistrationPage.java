package application.demo_web_shop.pages;

import application.BasePage;
import framework.support.elements.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegistrationPage extends BasePage {

    private final Element genderMaleBtn = new Element(By.id("gender-male"), "Gender button (Male)");

    private final Element firstNameField = new Element(By.id("FirstName"), "Firs Name Field");

    private final Element lastNameField = new Element(By.id("LastName"), "Last Name Field");

    private final Element emailField = new Element(By.id("Email"), "Email Field");

    private final Element passwordField = new Element(By.id("Password"), "Password Field");

    private final Element confirmPasswordField = new Element(By.id("ConfirmPassword"), "Confirm Password Field");

    private final Element registerButton = new Element(By.id("register-button"), "register button");

    private final Element successfulRegistrationMsg = new Element(By.xpath("//div[@class='result']"), "Successful Registration Message");

    private final Element continueButton = new Element(By.cssSelector("input[value='Continue']"), "Continue Button");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public void clickGenderMale() {
        click(genderMaleBtn);
    }

    public void fillFirstName(String firstName) {
        type(firstNameField, firstName);
    }

    public void fillLastName(String lastName) {
        type(lastNameField, lastName);
    }

    public void fillEmail(String email) {
        type(emailField, email);
    }

    public void fillPassword(String password) {
        type(passwordField, password);
    }

    public void fillConfirmPassword(String confirmPassword) {
        type(confirmPasswordField, confirmPassword);
    }

    public LandingPage clickRegisterButton() {
        click(registerButton);
        return new LandingPage(driver);
    }

    public boolean isSuccessfulRegistrationMsgDisplayed() {
        return isVisible(successfulRegistrationMsg);
    }

    public String getSuccessfulRegistrationMsg() {
        return getText(successfulRegistrationMsg);
    }


}
