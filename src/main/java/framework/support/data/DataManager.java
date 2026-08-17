package framework.support.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.core.exceptions.FrameworkException;

import java.io.InputStream;

public final class DataManager {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    private DataManager() {
    }

    public static <T> T load(
            String resourcePath,
            Class<T> type
    ) {

        try (InputStream input =
                     DataManager.class
                             .getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new FrameworkException(
                        "Test data file not found: " + resourcePath
                );
            }

            return OBJECT_MAPPER.readValue(
                    input,
                    type
            );

        } catch (FrameworkException e) {
            throw e;

        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to load test data: " + resourcePath,
                    e
            );
        }
    }
}
