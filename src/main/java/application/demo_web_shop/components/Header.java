package application.demo_web_shop.components;

import framework.support.elements.Element;
import framework.core.actions.BrowserActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Header extends BrowserActions {

    public Header(WebDriver driver) {
        super(driver);
    }

    private final Element registerLink = new Element(By.cssSelector(".ico-register"), "register link");

    public void clickRegisterLink() {
        click(registerLink);
    }
}
