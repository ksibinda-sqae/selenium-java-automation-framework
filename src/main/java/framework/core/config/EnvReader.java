package framework.core.config;

import framework.core.exceptions.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class EnvReader {

    private static final Properties ENV = new Properties();
    private static final String ENV_FILE_NAME = ".env";

    static {
        try (InputStream input = EnvReader.class
                .getClassLoader()
                .getResourceAsStream(ENV_FILE_NAME)) {

            if (input == null) {
                throw new FrameworkException(".env file not found");
            }

            ENV.load(input);

        } catch (IOException e) {
            throw new FrameworkException(
                    "Failed to load .env file",
                    e
            );
        }
    }

    private EnvReader() {
    }

    public static String get(String key) {

        String value = ENV.getProperty(key.toUpperCase());

        if (value == null || value.isBlank()) {
            throw new FrameworkException(
                    "Environment variable not found or empty: " + key
            );
        }

        return value;
    }
}