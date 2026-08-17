package framework.support.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FrameworkLogger {

    private FrameworkLogger() {

    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
