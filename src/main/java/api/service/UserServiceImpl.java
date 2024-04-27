package api.service;

import static api.constant.OtherConstantsHolder.ONE;
import static api.constant.OtherConstantsHolder.ROLE_ADMIN;
import static api.constant.OtherConstantsHolder.ROLE_USER;

import api.dto.UserResponseDto;
import api.dto.UserUpdateRequestDto;
import api.mapper.UserMapper;
import api.model.Role;
import api.model.RoleName;
import api.model.User;
import api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
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

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDto updateRole(Long id, String roleName) {
        RoleName.fromString(roleName); // just a check if role exists
        User user = userRepository.findByIdWIthRoles(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "User with passed id doesn't exist, id: " + id));
        if (alreadyIs(user, roleName)) {
            return userMapper.toResponseDto(user);
        }
        if (hasRole(user, ROLE_ADMIN)
                && roleName.equalsIgnoreCase(ROLE_USER)) {
            user.setRoles(Set.of(new Role(1L)));
        } else {
            user.setRoles(Set.of(new Role(1L), new Role(2L)));
        }
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    private boolean alreadyIs(User user, String roleName) {
        return (isJustUser(user)
                && roleName.equalsIgnoreCase(ROLE_USER))
                || (hasRole(user, ROLE_ADMIN)
                && roleName.equalsIgnoreCase(ROLE_ADMIN));
    }

    private boolean isJustUser(User user) {
        return user.getRoles().size() == ONE;
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .toList()
                .contains(RoleName.fromString(roleName));
    }
}
