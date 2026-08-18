package demo_web_shop;

import application.demo_web_shop.pages.LandingPage;
import application.demo_web_shop.utils.CredentialsUtil;
import application.demo_web_shop.utils.RandomDataGeneratorUtil;
import base.BaseTest;
import org.testng.annotations.Test;

import static application.demo_web_shop.utils.CredentialsUtil.*;
import static application.demo_web_shop.utils.RandomDataGeneratorUtil.*;

public class RegistrationPageTests extends BaseTest {

    @Test
    public void test_successfulRegistration(){
        String firstName = firstName();
        String lastName = lastName();
        String email = email();
        String password = getRegistrationPassword();

        testFixtures.registrationFlow.register(
                firstName,
                lastName,
                email,
                password
        );

        testFixtures.registrationAssertions.assertSuccessfulRegistration();
    }
}
