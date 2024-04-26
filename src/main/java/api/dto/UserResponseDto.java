package api.dto;

import java.time.LocalDate;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String address,
        String phoneNumber,
        LocalDate birthDate
) {
}
