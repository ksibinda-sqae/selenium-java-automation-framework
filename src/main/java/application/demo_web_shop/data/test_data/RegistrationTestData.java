package application.demo_web_shop.data.test_data;

import application.demo_web_shop.data.users.RegistrationUser;
import framework.support.data.DataManager;

public final class RegistrationTestData {

    private static final String VALID_USER =
            "data/users/registration-user.json";

    private RegistrationTestData() {
    }

    public static RegistrationUser validUser() {

        return DataManager.load(
                VALID_USER,
                RegistrationUser.class
        );
    }
}