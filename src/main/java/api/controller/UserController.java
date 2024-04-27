package api.controller;

import api.dto.UserResponseDto;
import api.dto.UserUpdateRequestDto;
import api.model.User;
import api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users controller", description = "Endpoints for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get your profile's info")
    @GetMapping
    public UserResponseDto getMyInfo(Authentication authentication) {
        return userService.getMyInfo(getUser(authentication));
    }

    @Operation(summary = "Update your profile's info",
            description = """
                    You can update any field of your profile.
                    But email should be unique
                    Phone number should be formatted to international standard
                    First and last name should start with a capital letter
                    Password and repeat password should match and be of length from 8 to 35 symbols
                    Birth date should be valid as well (you should be 18 years old)
                    """)
    @PutMapping("/update")
    public UserResponseDto updateMyInfo(@RequestBody @Valid UserUpdateRequestDto requestDto,
                                        Authentication authentication) {
        return userService.updateMyInfo(requestDto);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
