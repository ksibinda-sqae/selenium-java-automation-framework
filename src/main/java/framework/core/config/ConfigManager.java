package framework.core.config;

import framework.core.driver.Browser;
import framework.core.exceptions.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

        private static final Properties PROPERTIES = new Properties();

        static {
            try (InputStream input = ConfigManager.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties")) {

                if (input == null) {
                    throw new FrameworkException("config.properties not found");
                }

                PROPERTIES.load(input);

            } catch (IOException e) {
                throw new FrameworkException("Failed to load configuration", e);
            }
        }

        private ConfigManager() {
        }

        public static String get(String key) {
            String value = PROPERTIES.getProperty(key);

            if (value == null) {
                throw new FrameworkException(
                        "Configuration property not found: " + key
                );
            }

            return value;
        }

    public static Browser getBrowser() {
        try {
            return Browser.valueOf(
                    get("browser").toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new FrameworkException(
                    "Unsupported browser configured: " + get("browser"),
                    e
            );
        }
    }

    public static String getBaseUrl() {
        return get("base.url");
    }
}
