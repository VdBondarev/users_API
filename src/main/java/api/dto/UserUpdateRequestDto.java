package api.dto;

import api.annotation.BeforeThanYearsFromNow;
import api.annotation.FieldMatch;
import api.annotation.PhoneNumber;
import api.annotation.StartsWithCapital;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

@FieldMatch(password = "password", repeatPassword = "repeatPassword")
public record UserUpdateRequestDto(
        @StartsWithCapital
        String firstName,
        @StartsWithCapital
        String lastName,
        @Email
        String email,
        @BeforeThanYearsFromNow
        LocalDate birthDate,
        String address,
        @PhoneNumber
        String phoneNumber,
        String password,
        String repeatPassword
) {
}
