package framework.core.waits;

import framework.support.elements.Element;
import framework.core.exceptions.WaitTimeoutException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitManager {

    private final WebDriverWait wait;

    public WaitManager(WebDriver driver) {
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
    }

    public WebElement waitForVisible(Element element) {

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            element.locator()
                    )
            );

        } catch (TimeoutException e) {

            throw new WaitTimeoutException(
                    "Timed out waiting for elements to be visible: "
                            + element.elementName(),
                    e
            );
        }
    }

    public WebElement waitForClickable(Element element) {

        try {
            return wait.until(
                    ExpectedConditions.elementToBeClickable(
                            element.locator()
                    )
            );

        } catch (TimeoutException e) {

            throw new WaitTimeoutException(
                    "Timed out waiting for elements to be clickable: "
                            + element.elementName(),
                    e
            );
        }
    }
}