package api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import api.dto.UserRegistrationRequestDto;
import api.dto.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Verify that registration endpoint works fine with valid input params")
    void register_ValidInput_Success() throws Exception {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto(
                "Test",
                "Test",
                "test@email.com",
                "Test",
                "+38093556754",
                LocalDate.of(2000, 3, 3),
                "12345678",
                "12345678"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDto expected = createUserResponseDto(1L, requestDto);

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );

        assertEquals(expected, actual);
    }

    private UserResponseDto createUserResponseDto(Long id, UserRegistrationRequestDto requestDto) {
        return new UserResponseDto(
                id,
                requestDto.firstName(),
                requestDto.lastName(),
                requestDto.email(),
                requestDto.address(),
                requestDto.phoneNumber(),
                requestDto.birthDate()
        );
    }

    @Test
    void login() {
    }

    private UserRegistrationRequestDto createUserRegistrationRequestDto(
            String firstName,
            String lastName,
            String email,
            String address,
            String phoneNumber,
            LocalDate birthDate,
            String password,
            String repeatPassword
    ) {
        return new UserRegistrationRequestDto(
                firstName,
                lastName,
                email,
                address,
                phoneNumber,
                birthDate,
                password,
                repeatPassword
        );
    }
}
