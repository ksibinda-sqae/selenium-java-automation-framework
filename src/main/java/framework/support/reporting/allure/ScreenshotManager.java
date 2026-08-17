package framework.support.reporting.allure;

import framework.core.driver.DriverManager;
import framework.core.exceptions.FrameworkException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public final class ScreenshotManager {

    private ScreenshotManager() {
    }

    public static byte[] captureScreenShot() {

        try {
            return ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);

        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to capture screenshot",
                    e
            );
        }
    }
}