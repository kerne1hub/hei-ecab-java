package kernel.heiecab.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String patronymic;
    private String username;
    private String email;
    private String password;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();
}
