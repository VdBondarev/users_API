package api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email
        String email,
        @Size(min = 8, max = 35)
        String password
) {
}
