package kernel.heiecab.utils;

import kernel.heiecab.domain.User;
import kernel.heiecab.dto.request.AuthRequest;
import kernel.heiecab.dto.request.RegisterRequest;

public class TestUtils {
    public static User mockUser() {
        return User.builder()
                .id(1)
                .firstName("FirstName")
                .lastName("LastName")
                .patronymic("Patronymic")
                .username("login")
                .email("email@mail.com")
                .password("password")
                .type("USER")
                .build();
    }

    public static RegisterRequest mockRegisterRequest() {
        User user = mockUser();
        RegisterRequest request = new RegisterRequest();

        request.setFirstName(user.getFirstName());
        request.setLastName(user.getLastName());
        request.setPatronymic(user.getPatronymic());
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());
        request.setPassword(user.getPassword());
        request.setType(user.getType());

        return request;
    }

    public static AuthRequest mockAuthRequest() {
        User user = mockUser();
        AuthRequest request = new AuthRequest();

        request.setUsername(user.getUsername());
        request.setPassword(user.getPassword());

        return request;
    }
}
