package api.service;

import static api.constant.OtherConstantsHolder.ONE;
import static api.constant.OtherConstantsHolder.ROLE_ADMIN;
import static api.constant.OtherConstantsHolder.ROLE_USER;

import api.dto.UserResponseDto;
import api.dto.UserSearchParametersRequestDto;
import api.dto.UserUpdateRequestDto;
import api.mapper.UserMapper;
import api.model.Role;
import api.model.RoleName;
import api.model.User;
import api.repository.UserRepository;
import api.repository.specification.SpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SpecificationBuilder<User, UserSearchParametersRequestDto> specificationBuilder;

    @Override
    public UserResponseDto getMyInfo(User user) {
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateMyInfo(UserUpdateRequestDto requestDto, User user) {
        user = userMapper.toModel(user, requestDto);
        if (requestDto.email() != null && !requestDto.email().isEmpty()) {
            if (userRepository.findByEmailWithoutRoles(requestDto.email()).isPresent()) {
                throw new IllegalArgumentException("""
                        Passed email is already registered.
                        Try another one
                        """);
            }
            user.setEmail(requestDto.email());
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
        Set<Role> roles = new HashSet<>();

        if (hasRole(user, ROLE_ADMIN)
                && roleName.equalsIgnoreCase(ROLE_USER)) {
            roles.add(new Role(1L));
        } else {
            roles.add(new Role(1L));
            roles.add(new Role(2L));
        }
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> search(
            UserSearchParametersRequestDto requestDto, Pageable pageable) {
        if (isEmpty(requestDto)) {
            throw new IllegalArgumentException("Searching should be done by at least 1 param");
        }
        Specification<User> specification = specificationBuilder.build(requestDto);
        return userRepository.findAll(specification, pageable)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = userRepository.findByIdWithoutRoles(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a user by id " + id));
        return userMapper.toResponseDto(user);
    }

    private boolean isEmpty(UserSearchParametersRequestDto requestDto) {
        return requestDto == null
                || ((requestDto.firstName() == null || requestDto.firstName().isEmpty())
                && (requestDto.lastName() == null || requestDto.lastName().isEmpty())
                && (requestDto.address() == null || requestDto.address().isEmpty())
                && (requestDto.phoneNumber() == null || requestDto.phoneNumber().isEmpty())
                && (requestDto.email() == null || requestDto.email().isEmpty())
                && (requestDto.birthDate() == null));
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
