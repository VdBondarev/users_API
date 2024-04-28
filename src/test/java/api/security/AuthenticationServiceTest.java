package api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import api.dto.UserLoginRequestDto;
import api.dto.UserLoginResponseDto;
import api.dto.UserRegistrationRequestDto;
import api.dto.UserResponseDto;
import api.exception.RegistrationException;
import api.mapper.UserMapper;
import api.model.Role;
import api.model.User;
import api.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Verify that registration works fine for valid input params")
    void register_ValidRequest_RegistersUser() throws RegistrationException {
        UserRegistrationRequestDto requestDto =
                createRegistrationRequestDto("test@gmail.com", "testPassword");

        User user = createUser(requestDto);

        UserResponseDto expected = createResponseDto(user);

        when(userRepository.findByEmailWithoutRoles(requestDto.email()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestDto.password())).thenReturn(requestDto.password());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual = authenticationService.register(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify that registration works as expected for already registered email")
    void register_AlreadyRegisteredEmail_ThrowsException() {
        UserRegistrationRequestDto requestDto =
                createRegistrationRequestDto("test@gmail.com", "testPassword");

        User user = createUser(requestDto);

        when(userRepository.findByEmailWithoutRoles(requestDto.email()))
                .thenReturn(Optional.of(user));

        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> authenticationService.register(requestDto));

        String expected = """
                User with passed email is already registered
                Try another one
                """;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify that login() method works as expected")
    void login_ValidInput_ReturnsValidJwtToken() {
        UserLoginRequestDto requestDto = new UserLoginRequestDto(
                "test@email.com", "12345678"
        );

        Authentication mockedAuthentication = mock(Authentication.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        requestDto.email(), requestDto.password()
                );

        String expectedToken = "superSecretToken";

        when(authenticationManager.authenticate(authenticationToken))
                .thenReturn(mockedAuthentication);
        when(jwtUtil.generateToken(mockedAuthentication.getName())).thenReturn(expectedToken);

        UserLoginResponseDto expected = new UserLoginResponseDto(expectedToken);

        UserLoginResponseDto actual = authenticationService.login(requestDto);

        assertEquals(expected, actual);
    }

    private User createUser(UserRegistrationRequestDto requestDto) {
        return new User()
                .setId(1L)
                .setPassword(requestDto.password())
                .setRoles(Set.of(new Role(1L)))
                .setEmail(requestDto.email())
                .setLastName(requestDto.lastName())
                .setFirstName(requestDto.firstName());
    }

    private UserResponseDto createResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getBirthDate());
    }

    private UserRegistrationRequestDto createRegistrationRequestDto(String email, String password) {
        return new UserRegistrationRequestDto(
                "Test",
                "Test",
                email,
                "Test",
                "+38099999999",
                LocalDate.of(2000, 1, 1),
                password,
                password);
    }
}
