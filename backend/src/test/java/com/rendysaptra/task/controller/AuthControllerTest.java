package com.rendysaptra.task.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rendysaptra.task.domain.dto.AuthResponseDto;
import com.rendysaptra.task.domain.dto.LoginRequestDto;
import com.rendysaptra.task.domain.dto.SignupRequestDto;
import com.rendysaptra.task.service.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        
        AuthController authController = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new com.rendysaptra.task.controller.GlobalExceptionHandler())
            .build();
    }

    @Test
    void signup_Success() throws Exception {
        SignupRequestDto request = new SignupRequestDto("test@example.com", "password123");
        AuthResponseDto response = new AuthResponseDto("jwt-token-123", "test@example.com");

        when(authService.signup(any(SignupRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authService).signup(any(SignupRequestDto.class));
    }

    @Test
    void signup_EmailAlreadyExists() throws Exception {
        SignupRequestDto request = new SignupRequestDto("existing@example.com", "password123");

        when(authService.signup(any(SignupRequestDto.class)))
            .thenThrow(new RuntimeException("Email already in use"));

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(authService).signup(any(SignupRequestDto.class));
    }

    @Test
    void signup_EmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    void login_Success() throws Exception {
        LoginRequestDto request = new LoginRequestDto("user@example.com", "password123");
        AuthResponseDto response = new AuthResponseDto("jwt-token-456", "user@example.com");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-456"))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    void login_UserNotFound() throws Exception {
        LoginRequestDto request = new LoginRequestDto("nonexistent@example.com", "password123");

        when(authService.login(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    void login_InvalidPassword() throws Exception {
        LoginRequestDto request = new LoginRequestDto("user@example.com", "wrongpassword");

        when(authService.login(any(LoginRequestDto.class)))
            .thenThrow(new RuntimeException("Invalid password"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    void login_EmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }
}
