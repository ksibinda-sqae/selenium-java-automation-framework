package framework.assertions;

import framework.core.exceptions.AssertionException;
import framework.core.exceptions.FrameworkException;
import framework.support.logging.FrameworkLogger;
import framework.support.reporting.allure.AllureManager;
import org.slf4j.Logger;
import org.testng.Assert;

public final class FrameworkAssertions {

    private static final Logger LOGGER =
            FrameworkLogger.getLogger(FrameworkAssertions.class);

    private FrameworkAssertions() {
    }

    public static void assertEquals(
            Object actual,
            Object expected,
            String description
    ) {

        try {

            LOGGER.info(
                    "Asserting: {}",
                    description
            );

            AllureManager.step(
                    "Assert: " + description,
                    () -> {
                        Assert.assertEquals(
                                actual,
                                expected,
                                description
                        );
                        return null;
                    }
            );

        }catch (AssertionError e) {

            LOGGER.error(
                    "Assertion failed: {} | Expected: {} | Actual: {}",
                    description,
                    expected,
                    actual
            );

            AllureManager.attachText(
                    "Assertion Failure",
                    "Assertion failed: " + description +
                            "\nExpected: " + expected +
                            "\nActual: " + actual
            );

            throw e;
        }

        /*catch (AssertionError e) {

            throw new AssertionException(
                    "\n\n\s\s\sAssertion failed: " + description
                            + "\n\s\s\s\s\s Expected: " + expected
                            + "\n\s\s\s\s\s Actual: " + actual,
                    e
            );

        }*/ catch (FrameworkException e) {

            throw e;

        } catch (Exception e) {

            throw new AssertionException(
                    "Failed to execute assertion: " + description,
                    e
            );
        }
    }

    public static void assertTrue(
            boolean condition,
            String message
    ) {
        Assert.assertTrue(condition, message);
    }

    public static void assertFalse(
            boolean condition,
            String message
    ) {
        Assert.assertFalse(condition, message);
    }
}