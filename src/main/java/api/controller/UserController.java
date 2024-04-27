package api.controller;

import api.dto.UserResponseDto;
import api.dto.UserSearchParametersRequestDto;
import api.dto.UserUpdateRequestDto;
import api.model.User;
import api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
        return userService.updateMyInfo(requestDto, getUser(authentication));
    }

    @Operation(summary = "Delete a user by id", description = "Allowed for admins only")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @Operation(summary = "Update user's roles", description = "Allowed for admins only")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponseDto updateRole(@PathVariable Long id,
                                      @RequestParam(name = "role_name") String roleName) {
        return userService.updateRole(id, roleName);
    }

    @Operation(summary = "Search users by params",
            description = """
                    This action is allowed for admins only.
                    You can search by almost any params you want, except for password and id
                    It will look like that:
                    By email, firstName, lastName, address, phoneNumber: '%value%'
                                        
                    By birthDate: 'BETWEEN (firstElement, secondElement)'
                    """)
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponseDto> search(
            @RequestBody @Valid UserSearchParametersRequestDto requestDto,
            Pageable pageable) {
        return userService.search(requestDto, pageable);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
