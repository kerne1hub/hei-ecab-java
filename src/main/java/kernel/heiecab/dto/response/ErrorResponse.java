package kernel.heiecab.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse implements Response {
    private String message;
}
