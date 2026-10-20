package com.rendysaptra.task.integration.api;

import com.rendysaptra.task.integration.BaseIntegrationTest;
import com.rendysaptra.task.integration.util.TestDataBuilder;
import com.rendysaptra.task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private static final String SIGNUP_URL = "/api/v1/auth/signup";
    private static final String LOGIN_URL = "/api/v1/auth/login";

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        userRepository.deleteAll();
        TestDataBuilder.resetCounter();
    }

    @Test
    void signup_validRequest_returns201AndToken() throws Exception {
        var request = TestDataBuilder.buildSignupRequest("newuser@example.com", "SecurePass123!");

        mockMvc.perform(post(SIGNUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email", equalTo("newuser@example.com")));
    }

    @Test
    void signup_duplicateEmail_returns500Error() throws Exception {
        var request = TestDataBuilder.buildSignupRequest("duplicate@example.com", "SecurePass123!");

        mockMvc.perform(post(SIGNUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(post(SIGNUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error", containsString("Email already in use")));
    }

    @Test
    void login_validCredentials_returns200AndToken() throws Exception {
        String email = "testuser@example.com";
        String password = "SecurePass123!";
        var signupRequest = TestDataBuilder.buildSignupRequest(email, password);

        mockMvc.perform(post(SIGNUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated());

        var loginRequest = TestDataBuilder.buildLoginRequest(email, password);
        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email", equalTo(email)));
    }

    @Test
    void login_invalidPassword_returns500Error() throws Exception {
        String email = "testuser@example.com";
        var signupRequest = TestDataBuilder.buildSignupRequest(email, "SecurePass123!");

        mockMvc.perform(post(SIGNUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated());

        var loginRequest = TestDataBuilder.buildLoginRequest(email, "WrongPassword123!");
        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error", containsString("Invalid password")));
    }

    @Test
    void login_nonExistentUser_returns500Error() throws Exception {
        var loginRequest = TestDataBuilder.buildLoginRequest("nonexistent@example.com", "Password123!");

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error", containsString("User not found")));
    }

    protected String extractToken(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return objectMapper.readTree(content).get("token").asText();
    }
}
