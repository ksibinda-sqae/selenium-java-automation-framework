package framework.support.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.core.exceptions.FrameworkException;

import java.io.File;
import java.io.InputStream;

public final class DataManager {

    private static final String DIR_PATH = "src/test/resources/test-data";

    private DataManager() {
    }

    public static String getData(String key, String fileName) {
        try {
            String path = String.format("%s/%s.json", DIR_PATH, fileName);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode json = mapper.readTree(new File(path));

            return json.get(key).asText();

        } catch (Exception e) {

            throw new FrameworkException("Failed to read json value", e);
        }
    }
}
