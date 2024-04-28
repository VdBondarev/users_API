package api.security;

import api.dto.UserLoginRequestDto;
import api.dto.UserLoginResponseDto;
import api.dto.UserRegistrationRequestDto;
import api.dto.UserResponseDto;
import api.exception.RegistrationException;
import api.mapper.UserMapper;
import api.model.User;
import api.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmailWithoutRoles(requestDto.email()).isPresent()) {
            throw new RegistrationException("""
                    User with passed email is already registered
                    Try another one
                    """);
        }
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        User user = userMapper.toModel(requestDto)
                .setPassword(encodedPassword);
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public UserLoginResponseDto login(@Valid UserLoginRequestDto requestDto) {
        final Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                requestDto.email(), requestDto.password())
                );
        return new UserLoginResponseDto(jwtUtil.generateToken(authentication.getName()));
    }
}
