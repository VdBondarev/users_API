package api.controller;

import api.dto.UserLoginRequestDto;
import api.dto.UserLoginResponseDto;
import api.dto.UserRegistrationRequestDto;
import api.dto.UserResponseDto;
import api.exception.RegistrationException;
import api.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication controller",
        description = """
                Register and login endpoints.
                Available for every user (even not authenticated)
                """)
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Registration endpoint",
            description = """
            Available to every user (even those not authenticated)
            """)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return authenticationService.register(requestDto);
    }

    @Operation(summary = "Login endpoint for signed-up users",
            description = """
                    Get JWT token.
                    This endpoint available for every (even not authenticated) user
                    """)
    @GetMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }
}
