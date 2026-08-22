package framework.core.config;

import framework.core.exceptions.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class EnvReader {

    private static final Properties ENV = new Properties();
    private static final String ENV_FILE_NAME = ".env";

    static {
        loadEnvFile();
    }

    private EnvReader() {
    }

    private static void loadEnvFile() {

        try (InputStream input = EnvReader.class
                .getClassLoader()
                .getResourceAsStream(ENV_FILE_NAME)) {

            if (input != null) {
                ENV.load(input);
            }

        } catch (IOException e) {
            throw new FrameworkException(
                    "Failed to load .env file",
                    e
            );
        }
    }

    public static String get(String key) {

        String normalizedKey = key.toUpperCase();

        // CI / system environment variables take priority
        String value = System.getenv(normalizedKey);

        // Fall back to .env for local execution
        if (value == null || value.isBlank()) {
            value = ENV.getProperty(normalizedKey);
        }

        if (value == null || value.isBlank()) {
            throw new FrameworkException(
                    "Environment variable not found or empty: " + key
            );
        }

        return value;
    }
}