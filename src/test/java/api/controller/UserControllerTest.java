package api.controller;

import static api.constant.OtherConstantsHolder.EMPTY;
import static api.constant.SecurityConstantsHolder.BEARER;
import static api.holder.LinksHolder.DELETE_ALL_USERS_FILE_PATH;
import static api.holder.LinksHolder.INSERT_USER_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import api.dto.UserResponseDto;
import api.dto.UserUpdateRequestDto;
import api.mapper.UserMapper;
import api.model.User;
import api.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(
            scripts = { DELETE_ALL_USERS_FILE_PATH, INSERT_USER_FILE_PATH },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = { DELETE_ALL_USERS_FILE_PATH },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that updateMyInfo works as valid input")
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
        String email = "admin@example.com";

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

    private UserResponseDto getUserResponseDto(Authentication authentication, String email) {
        User user = (User) authentication.getPrincipal();
        user.setEmail(email);
        return userMapper.toResponseDto(user);
    }

    @Test
    void delete() {
    }

    @Test
    void updateRole() {
    }

    @Test
    void search() {
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
}
