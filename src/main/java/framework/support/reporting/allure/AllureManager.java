package framework.support.reporting.allure;

import framework.core.exceptions.FrameworkException;

import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static io.qameta.allure.Allure.*;

public final class AllureManager {

    private AllureManager() {
    }

    public static void step(String name, ThrowableRunnable action) {
            Allure.step(name, action);
    }

    public static void addStep(String step) {

        try {
            Allure.step(step);

        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to add Allure step: " + step,
                    e
            );
        }
    }

    public static void attachText(String name, String content) {

        try {
            addAttachment(
                    name,
                    "text/plain",
                    content
            );

        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to attach text to Allure: " + name,
                    e
            );
        }
    }

    public static void attachException(
            String name,
            Throwable throwable
    ) {

        if (throwable == null) {
            return;
        }

        try {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);

            throwable.printStackTrace(printWriter);

            attachText(
                    name,
                    writer.toString()
            );

        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to attach exception to Allure: " + name,
                    e
            );
        }
    }

    public static void attachScreenshot(String name) {

        try {
            byte[] screenshot = ScreenshotManager.captureScreenShot();

            addAttachment(
                    name,
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );

        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to attach screenshot to Allure: " + name,
                    e
            );
        }
    }
}