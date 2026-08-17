package demo_web_shop;

import application.demo_web_shop.data.test_data.RegistrationTestData;
import application.demo_web_shop.data.users.RegistrationUser;
import base.BaseTest;
import org.testng.annotations.Test;

public class RegistrationPageTests extends BaseTest {

    @Test
    public void test_successfulRegistration(){

        RegistrationUser registrationUser = RegistrationTestData.validUser();

        testFixtures.registrationFlow.register(registrationUser);

        testFixtures.registrationAssertions.assertSuccessfulRegistration();
    }
}
