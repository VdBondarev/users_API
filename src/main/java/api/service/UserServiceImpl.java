package api.service;

import api.dto.UserResponseDto;
import api.dto.UserUpdateRequestDto;
import api.mapper.UserMapper;
import api.model.User;
import api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto getMyInfo(User user) {
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateMyInfo(UserUpdateRequestDto requestDto, User user) {
        if (requestDto.address() != null && !requestDto.address().isEmpty()) {
            user.setAddress(requestDto.address());
        }
        if (requestDto.birthDate() != null) {
            user.setBirthDate(requestDto.birthDate());
        }
        if (requestDto.email() != null && !requestDto.email().isEmpty()) {
            if (userRepository.findByEmailWithoutRoles(requestDto.email()).isPresent()) {
                throw new IllegalArgumentException("""
                        Passed email is already registered.
                        Try another one
                        """);
            }
            user.setEmail(requestDto.email());
        }
        if (requestDto.firstName() != null && !requestDto.firstName().isEmpty()) {
            user.setFirstName(requestDto.firstName());
        }
        if (requestDto.lastName() != null && !requestDto.lastName().isEmpty()) {
            user.setLastName(requestDto.lastName());
        }
        if (requestDto.password() != null) {
            String encodedPassword = passwordEncoder.encode(requestDto.password());
            user.setPassword(encodedPassword);
        }
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }
}
