package framework.listeners;

import org.testng.ITestResult;
import framework.support.reporting.allure.AllureManager;

public class AllureTestListener implements TestListener {

    @Override
    public void onTestStart(ITestResult result) {

        String message = String.format("Test Started: %s", result.getName());

        AllureManager.addStep(message);
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        String message = String.format("Test Passed");

        AllureManager.addStep(message);
    }

    @Override
    public void onTestFailure(ITestResult result) {

        String message = String.format("Test Failed: %s", result.getName());

        AllureManager.addStep(message);

        String failureMessage = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "No failure message available.";

        AllureManager.attachException("Failure Details", result.getThrowable());

        AllureManager.attachScreenshot(
                "Failure Screenshot"
        );
    }

    @Override
    public void onTestSkipped(ITestResult result) {

        String message = String.format("Test Skipped: %s", result.getName());

        AllureManager.addStep(message);
    }
}
