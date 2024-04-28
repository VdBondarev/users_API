package api.service;

import static api.constant.CriteriaQueryConstantsHolder.EMAIL_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.FIRST_NAME_COLUMN;
import static api.constant.CriteriaQueryConstantsHolder.LAST_NAME_COLUMN;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
    @DisplayName("Verify that search() method works as expected with valid input params")
    void search_ValidInput_ReturnsValidResponse() {
        UserSearchParametersRequestDto paramsRequestDto = createUserSearchParametersRequestDto(
                "Test",
                "Test",
                "test@email.com",
                null,
                null,
                null
        );

        Specification<User> firstNameSpecification = (root, query, criteriaBuilder)
                -> criteriaBuilder.like(root.get(FIRST_NAME_COLUMN), paramsRequestDto.firstName());

        Specification<User> lastNameSpecification = (root, query, criteriaBuilder)
                -> criteriaBuilder.like(root.get(LAST_NAME_COLUMN), paramsRequestDto.lastName());

        Specification<User> emailSpecification = (root, query, criteriaBuilder)
                -> criteriaBuilder.like(root.get(EMAIL_COLUMN), paramsRequestDto.email());

        Specification<User> specification;
        specification = firstNameSpecification.and(lastNameSpecification).and(emailSpecification);

        PageRequest pageable = PageRequest.of(0, 5);
        Page<User> page = new PageImpl<>(
                List.of(user),
                pageable,
                List.of(user).size());

        UserResponseDto expected = createUserResponseDto(user);

        when(specificationBuilder.build(paramsRequestDto)).thenReturn(specification);
        when(userRepository.findAll(specification, pageable)).thenReturn(page);
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        List<UserResponseDto> expectedList = List.of(expected);
        List<UserResponseDto> actualList = userService.search(paramsRequestDto, pageable);

        assertEquals(expectedList, actualList);
        assertEquals(expected, actualList.get(0));
    }

    @Test
    @DisplayName("Verify that search() throws an exception when passing non-valid params")
    void search_NullPassedParams_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.search(null, PageRequest.of(0, 5)));

        String expected = "Searching should be done by at least 1 param";
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        exception = assertThrows(IllegalArgumentException.class, () -> userService.search(
                createUserSearchParametersRequestDto(
                        null, null, null, null, null, null
                ), PageRequest.of(0, 5)));

        actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    private UserSearchParametersRequestDto createUserSearchParametersRequestDto(
            String firstName,
            String lastName,
            String email,
            String address,
            String phoneNumber,
            List<LocalDate> birthDate
    ) {
        return new UserSearchParametersRequestDto(
                firstName,
                lastName,
                email,
                address,
                phoneNumber,
                birthDate);
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
