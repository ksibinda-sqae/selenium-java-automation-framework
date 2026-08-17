package framework.core.exceptions;

public class WaitTimeoutException extends FrameworkException {

    public WaitTimeoutException(String message) {
        super(message);
    }

    public WaitTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
