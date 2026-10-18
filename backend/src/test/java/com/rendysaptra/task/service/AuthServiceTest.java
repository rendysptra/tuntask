package com.rendysaptra.task.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rendysaptra.task.domain.dto.AuthResponseDto;
import com.rendysaptra.task.domain.dto.LoginRequestDto;
import com.rendysaptra.task.domain.dto.SignupRequestDto;
import com.rendysaptra.task.domain.entitty.User;
import com.rendysaptra.task.repository.UserRepository;
import com.rendysaptra.task.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    // ===== signup Tests =====
    @Test
    void signup_Success() {
        // Arrange
        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(jwtTokenProvider.generateToken("test@example.com")).thenReturn("jwt-token-123");

        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        AuthResponseDto response = authService.signup(request);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("jwt-token-123", response.getToken());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).generateToken("test@example.com");
    }

    @Test
    void signup_EmailAlreadyExists() {
        // Arrange
        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.signup(request),
            "Email already in use");
        verify(userRepository).findByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void signup_PasswordEncoded() {
        // Arrange
        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("newuser@example.com");
        request.setPassword("plainPassword");

        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("bcryptedPassword");
        when(jwtTokenProvider.generateToken("newuser@example.com")).thenReturn("jwt-token");

        User savedUser = new User();
        savedUser.setEmail("newuser@example.com");
        savedUser.setPassword("bcryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        authService.signup(request);

        // Assert
        verify(passwordEncoder).encode("plainPassword");
    }

    // ===== login Tests =====
    @Test
    void login_Success() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtTokenProvider.generateToken("test@example.com")).thenReturn("jwt-token-456");

        // Act
        AuthResponseDto response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("jwt-token-456", response.getToken());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword123");
        verify(jwtTokenProvider).generateToken("test@example.com");
    }

    @Test
    void login_UserNotFound() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(request),
            "User not found");
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_InvalidPassword() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(request),
            "Invalid password");
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword123");
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }

    @Test
    void login_TokenGenerated() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtTokenProvider.generateToken("test@example.com")).thenReturn("jwt-token-789");

        // Act
        authService.login(request);

        // Assert
        verify(jwtTokenProvider).generateToken("test@example.com");
    }
}
