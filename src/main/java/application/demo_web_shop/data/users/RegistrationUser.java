package application.demo_web_shop.data.users;

public record RegistrationUser(
        String firstName,
        String lastName,
        String email,
        String password
) {
}