package api.dto;

import api.annotation.FieldMatch;
import api.annotation.PhoneNumber;
import api.annotation.StartsWithCapital;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

@FieldMatch(password = "password", repeatPassword = "repeatPassword")
public record UserRegistrationRequestDto(
        @StartsWithCapital(message = "First name should start with a capital letter")
        @NotBlank
        String firstName,
        @StartsWithCapital(message = "Last name should start with a capital letter")
        @NotBlank
        String lastName,
        @Email
        @NotBlank
        String email,
        String address,
        @PhoneNumber
        String phoneNumber,
        @Past
        @NotNull
        LocalDate birthDate,
        @NotBlank
        String password,
        @NotBlank
        String repeatPassword
) {
}
