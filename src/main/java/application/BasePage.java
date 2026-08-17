package application;

import framework.core.actions.BrowserActions;
import org.openqa.selenium.WebDriver;

public class BasePage extends BrowserActions {

    protected WebDriver driver;

    public BasePage(WebDriver driver) {super(driver);}
}
