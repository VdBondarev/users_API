package api.dto;

import api.annotation.BeforeThanYearsFromNow;
import api.annotation.FieldMatch;
import api.annotation.PhoneNumber;
import api.annotation.StartsWithCapital;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@FieldMatch(password = "password", repeatPassword = "repeatPassword")
public record UserRegistrationRequestDto(
        @StartsWithCapital
        @NotBlank
        String firstName,
        @StartsWithCapital
        @NotBlank
        String lastName,
        @Email
        @NotBlank
        String email,
        String address,
        @PhoneNumber
        String phoneNumber,
        @NotNull
        @BeforeThanYearsFromNow
        LocalDate birthDate,
        @NotBlank
        @Size(min = 8, max = 35)
        String password,
        @NotBlank
        @Size(min = 8, max = 35)
        String repeatPassword
) {
}
