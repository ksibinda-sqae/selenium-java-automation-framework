package framework.core.actions;

import framework.support.elements.Element;
import framework.core.exceptions.FrameworkException;
import framework.core.waits.WaitManager;
import framework.support.logging.FrameworkLogger;
import framework.support.reporting.allure.AllureManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

public class BrowserActions {

    private final Logger logger = FrameworkLogger.getLogger(BrowserActions.class);
    protected final WebDriver driver;
    protected final WaitManager wait;

    public BrowserActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitManager(driver);
    }

    public void click(Element element) {
        String elementName = element.elementName();

        try {
            logger.info(
                    "Clicking elements: {}", elementName
            );

            AllureManager.addStep(
                    "Click: " + elementName
            );

            wait.waitForClickable(element).click();

        } catch (FrameworkException e) {
            throw e;

        } catch (Exception e) {

            throw new FrameworkException(
                    "Failed to click elements: " + elementName,
                    e
            );
        }
    }

    public void type(Element element, String text) {

        String elementName = element.elementName();

        try {
            logger.info(
                    "Typing into elements: {}", elementName
            );

            AllureManager.addStep(
                    "Enter text into: " + elementName
            );

            wait.waitForVisible(element).sendKeys(text);

        } catch (FrameworkException e) {
            throw e;

        } catch (Exception e) {

            throw new FrameworkException(
                    "Failed to type into elements: " + elementName,
                    e
            );
        }
    }

    public String getText(Element element) {

        String elementName = element.elementName();

        try {
            logger.info(
                    "Getting text from elements: {}", elementName
            );

            AllureManager.addStep(
                    "Get text from: " + elementName
            );

            return wait.waitForVisible(element).getText();

        } catch (FrameworkException e) {
            throw e;

        } catch (Exception e) {

            throw new FrameworkException(
                    "Failed to get text from elements: " + elementName,
                    e
            );
        }
    }

    public boolean isVisible(Element element) {

        String elementName = element.elementName();

        try {
            logger.info(
                    "Checking if {} is visible", elementName
            );

            AllureManager.addStep(
                    "Checking visible state: " + elementName
            );

            return wait.waitForVisible(element).isDisplayed();

        } catch (FrameworkException e) {
            throw e;

        } catch (Exception e) {

            throw new FrameworkException(
                    "Failed to get element visible state: " + elementName,
                    e
            );
        }
    }
}