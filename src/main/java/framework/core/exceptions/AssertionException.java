package framework.core.exceptions;

public class AssertionException extends FrameworkException {

    public AssertionException(String message) {
        super(message);
    }

    public AssertionException(String message, Throwable cause) {
        super(message, cause);
    }
}