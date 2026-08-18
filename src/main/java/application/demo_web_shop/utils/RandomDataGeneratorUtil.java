package application.demo_web_shop.utils;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomDataGeneratorUtil {

    private static final String[] FIRST_NAMES = {
            "John", "Michael", "David", "James",
            "Robert", "Daniel", "William", "Thomas"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown",
            "Jones", "Miller", "Davis", "Wilson"
    };

    private RandomDataGeneratorUtil() {
    }

    public static String firstName() {
        return randomValue(FIRST_NAMES);
    }

    public static String lastName() {
        return randomValue(LAST_NAMES);
    }

    public static String email() {
        return String.format(
                "%s.%s@demo.test",
                firstName().toLowerCase(),
                randomNumber()
        );
    }

    private static String randomValue(String[] values) {
        return values[
                ThreadLocalRandom.current().nextInt(values.length)
                ];
    }

    private static int randomNumber() {
        return ThreadLocalRandom.current().nextInt(10000, 99999);
    }
}