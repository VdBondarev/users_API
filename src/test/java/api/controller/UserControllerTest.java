package api.controller;

import static api.constant.OtherConstantsHolder.EMPTY;
import static api.constant.OtherConstantsHolder.ONE;
import static api.constant.OtherConstantsHolder.TWO;
import static api.constant.SecurityConstantsHolder.BEARER;
import static api.holder.LinksHolder.DELETE_ALL_USERS_FILE_PATH;
import static api.holder.LinksHolder.DELETE_ALL_USER_ROLES_FILE_PATH;
import static api.holder.LinksHolder.INSERT_ADMIN_ROLES_FILE_PATH;
import static api.holder.LinksHolder.INSERT_FIVE_USERS_FILE_PATH;
import static api.holder.LinksHolder.INSERT_USER_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import api.dto.UserResponseDto;
import api.dto.UserSearchParametersRequestDto;
import api.dto.UserUpdateRequestDto;
import api.mapper.UserMapper;
import api.model.User;
import api.repository.UserRepository;
import api.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    INSERT_USER_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that updateMyInfo works as expected with valid input")
    void updateMyInfo_ValidInput_Success() throws Exception {
        UserUpdateRequestDto requestDto = createUpdateRequestDto(
                null,
                null,
                "updatedEmail@email.com",
                null,
                null,
                null,
                null,
                null
        );

        // expecting that this user is already added to the database
        String email = "user@example.com";

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email, "1234567890"
                ));

        String requestDtoContent = objectMapper.writeValueAsString(requestDto);

        String jwt = jwtUtil.generateToken(authentication.getName());

        MvcResult result = mockMvc.perform(
                        put("/users/update")
                                .principal(authentication)
                                .content(requestDtoContent)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, BEARER + EMPTY + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );

        UserResponseDto expected = getUserResponseDto(authentication, requestDto.email());

        assertEquals(expected, actual);
    }

    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    INSERT_USER_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify that updateRole() method works as expected
            When updating user to admin
            """)
    @Test
    @WithMockUser(username = "mockedUser@example.com", authorities = "ADMIN")
    void updateRole_UserToAdmin_Success() throws Exception {
        String roleName = "ADMIN";

        Long id = 1L;

        mockMvc.perform(
                        put("/users/update/" + id)
                                .param("role_name", roleName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // expecting that if get() throws NoSuchElementException, test will fail
        User user = userRepository.findByIdWIthRoles(id).get();
        assertEquals(TWO, user.getRoles().size());
    }

    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH,
                    INSERT_USER_FILE_PATH,
                    INSERT_ADMIN_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify that updateRole() method works as expected
            When updating admin to user
            """)
    @Test
    @WithMockUser(username = "mockedUser@example.com", authorities = "ADMIN")
    void updateRole_AdminToUser_Success() throws Exception {
        String roleName = "USER";

        Long id = 1L;

        mockMvc.perform(
                put("/users/update/" + id)
                        .param("role_name", roleName)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // expecting that if get() throws NoSuchElementException, test will fail
        User user = userRepository.findByIdWIthRoles(id).get();
        assertEquals(ONE, user.getRoles().size());
    }

    @Test
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH,
                    INSERT_FIVE_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Verify that search() endpoint works as expected with valid input")
    @WithMockUser(username = "admin@example.com", authorities = "ADMIN")
    void search_ValidInput_Success() throws Exception {
        UserSearchParametersRequestDto requestDto =
                new UserSearchParametersRequestDto(
                        "User",
                        "User",
                        "user",
                        "Test",
                        "+380",
                        List.of(
                                LocalDate.of(1995, 1, 1),
                                LocalDate.of(2000, 12, 1)
                        )
                );

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        get("/users/search")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), UserResponseDto[].class
        );

        // expecting that all the users present in the database fit search params
        assertEquals(5, actual.length);
    }

    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("Verify that search() endpoint works as expected with non-valid input")
    @WithMockUser(username = "admin@example.com", authorities = "ADMIN")
    @Test
    void search_NonValidInput_Failure() throws Exception {
        UserSearchParametersRequestDto requestDto = null;

        String content = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                get("/users/search")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        requestDto = new UserSearchParametersRequestDto(
                null,
                null,
                null,
                null,
                null,
                null
        );

        content = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        get("/users/search")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    private UserUpdateRequestDto createUpdateRequestDto(
            String firstName,
            String lastName,
            String email,
            String address,
            String phoneNumber,
            LocalDate birthDate,
            String password,
            String repeatPassword) {
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

    private UserResponseDto getUserResponseDto(Authentication authentication, String email) {
        User user = (User) authentication.getPrincipal();
        user.setEmail(email);
        return userMapper.toResponseDto(user);
    }
}
