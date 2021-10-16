package kernel.heiecab.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse implements Response {
    private Integer id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String username;
    private String email;
    private String type;
    private LocalDateTime createdAt;
}
