package application.demo_web_shop.utils;

import framework.core.config.EnvReader;

public final class CredentialsUtil {

    private static final String USERNAME =  "username";
    private static final String PASSWORD = "password";
    private static final String REGISTRATION_PASSWORD = "registration_password";

    private CredentialsUtil() {}

    public static String getUsername() {
        return EnvReader.get(USERNAME);
    }

    public static String getPassword() {
        return EnvReader.get(PASSWORD);
    }

    public static String getRegistrationPassword() {
        return EnvReader.get(REGISTRATION_PASSWORD);
    }


}
