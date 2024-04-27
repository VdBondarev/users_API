package api.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record UserSearchParametersRequestDto(
        String firstName,
        String lastName,
        String email,
        String address,
        String phoneNumber,
        @Size(min = 2, max = 2)
        List<LocalDate> birthDate
) {
}
