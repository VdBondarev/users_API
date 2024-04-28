package api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import api.dto.UserResponseDto;
import api.dto.UserSearchParametersRequestDto;
import api.dto.UserUpdateRequestDto;
import api.mapper.UserMapper;
import api.model.Role;
import api.model.RoleName;
import api.model.User;
import api.repository.UserRepository;
import api.repository.specification.SpecificationBuilder;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SpecificationBuilder<User, UserSearchParametersRequestDto> specificationBuilder;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User()
                .setId(1L)
                .setPassword("1234567890")
                .setAddress("Test")
                .setEmail("test@email.com")
                .setFirstName("Test")
                .setLastName("Test")
                .setBirthDate(LocalDate.of(1980, 3, 3))
                .setPhoneNumber("+38011111111")
                .setRoles(Set.of(new Role(1L)));
    }

    @Test
    @DisplayName("Verify that updateMyInfo() method works fine with valid input params")
    void updateMyInfo_ValidInput_ReturnsValidResponse() {
        UserUpdateRequestDto requestDto = createUserUpdateRequestDto(
                "Updated",
                "Updated",
                "updated@email.com",
                LocalDate.of(2000, 12, 1),
                "Updated",
                "+380939999999",
                "12345678",
                "12345678");

        User userUpdated = createUser(
                requestDto.firstName(),
                requestDto.lastName(),
                requestDto.email(),
                requestDto.birthDate(),
                requestDto.address(),
                requestDto.password(),
                requestDto.phoneNumber()
        );

        UserResponseDto expected = createUserResponseDto(userUpdated);

        when(userMapper.toModel(userUpdated, requestDto)).thenReturn(userUpdated);
        when(userRepository.findByEmailWithoutRoles(
                requestDto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestDto.password())).thenReturn(requestDto.password());
        when(userRepository.save(userUpdated)).thenReturn(userUpdated);
        when(userMapper.toResponseDto(userUpdated)).thenReturn(expected);

        UserResponseDto actual = userService.updateMyInfo(requestDto, userUpdated);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify that updateMyInfo() method will not update email,
            If one already exists in database
            """)
    void updateMyInfo_AlreadyRegisteredEmail_ThrowsException() {
        UserUpdateRequestDto requestDto = createUserUpdateRequestDto(
                null,
                null,
                "registered@email.com",
                null,
                null,
                null,
                null,
                null);

        when(userMapper.toModel(user, requestDto)).thenReturn(user);
        when(userRepository.findByEmailWithoutRoles(
                requestDto.email())).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateMyInfo(requestDto, user)
        );

        String exceptedMessage = """
                Passed email is already registered.
                Try another one
                """;

        String actualMessage = exception.getMessage();

        assertEquals(exceptedMessage, actualMessage);
    }

    @Test
    @DisplayName("Verify that updateRole() works fine when updating user to user")
    void updateUserRole_AlreadyCustomer_ReturnsNothingUpdated() {
        UserResponseDto expected = createUserResponseDto(user);

        when(userRepository.findByIdWIthRoles(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual =
                userService.updateRole(user.getId(), "USER");

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify that updateRole() works fine when updating admin to user")
    void updateUserRole_UpdateManagerToCustomer_ReturnsUpdatedUser() {
        Role userRole = new Role(1L);
        userRole.setName(RoleName.USER);

        Role admin = new Role(2L);
        admin.setName(RoleName.ADMIN);

        user.setRoles(Set.of(userRole, admin));

        UserResponseDto expected = createUserResponseDto(user);

        when(userRepository.findByIdWIthRoles(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual =
                userService.updateRole(user.getId(), "USER");

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify that updateRole() works fine when updating user to admin")
    void updateUserRole_UpdateCustomerToManager_ReturnsUpdatedUser() {
        // expecting that user will be admin after updating
        UserResponseDto expected = createUserResponseDto(user);

        when(userRepository.findByIdWIthRoles(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual =
                userService.updateRole(user.getId(), "ADMIN");

        assertEquals(expected, actual);
    }

    @Test
    void search() {
    }

    private User createUser(
            String firstName,
            String lastName,
            String email,
            LocalDate birthDate,
            String address,
            String password,
            String phoneNumber
    ) {
        return new User()
                .setPassword(password)
                .setAddress(address)
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setBirthDate(birthDate)
                .setPhoneNumber(phoneNumber);
    }

    private UserResponseDto createUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getBirthDate()
        );
    }

    private UserUpdateRequestDto createUserUpdateRequestDto(
            String firstName,
            String lastName,
            String email,
            LocalDate birthDate,
            String address,
            String phoneNumber,
            String password,
            String repeatPassword
    ) {
        return new UserUpdateRequestDto(
                firstName,
                lastName,
                email,
                birthDate,
                address,
                phoneNumber,
                password,
                repeatPassword
        );
    }
}

/**

 *
 *
 *
 *     @Override
 *     public List<UserResponseDto> search(
 *             UserSearchParametersRequestDto requestDto, Pageable pageable) {
 *         if (isEmpty(requestDto)) {
 *             throw new IllegalArgumentException("Searching should be done by at least 1 param");
 *         }
 *         Specification<User> specification = specificationBuilder.build(requestDto);
 *         return userRepository.findAll(specification, pageable)
 *                 .stream()
 *                 .map(userMapper::toResponseDto)
 *                 .collect(Collectors.toList());
 *     }
 *
 *     private boolean isEmpty(UserSearchParametersRequestDto requestDto) {
 *         return requestDto == null
 *                 || ((requestDto.firstName() == null || requestDto.firstName().isEmpty())
 *                 && (requestDto.lastName() == null || requestDto.lastName().isEmpty())
 *                 && (requestDto.address() == null || requestDto.address().isEmpty())
 *                 && (requestDto.phoneNumber() == null || requestDto.phoneNumber().isEmpty())
 *                 && (requestDto.email() == null || requestDto.email().isEmpty())
 *                 && (requestDto.birthDate() == null));
 *     }
 *
 *     private boolean alreadyIs(User user, String roleName) {
 *         return (isJustUser(user)
 *                 && roleName.equalsIgnoreCase(ROLE_USER))
 *                 || (hasRole(user, ROLE_ADMIN)
 *                 && roleName.equalsIgnoreCase(ROLE_ADMIN));
 *     }
 *
 *     private boolean isJustUser(User user) {
 *         return user.getRoles().size() == ONE;
 *     }
 *
 *     private boolean hasRole(User user, String roleName) {
 *         return user.getRoles()
 *                 .stream()
 *                 .map(Role::getName)
 *                 .toList()
 *                 .contains(RoleName.fromString(roleName));
 *     }
 * }
 */
