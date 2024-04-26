package api.dto;

import jakarta.validation.constraints.Email;

public record UserLoginRequestDto(
        @Email
        String email,
        String password
) {
}
