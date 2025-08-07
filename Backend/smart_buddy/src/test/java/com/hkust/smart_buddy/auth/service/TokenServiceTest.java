package com.hkust.smart_buddy.auth.service;

import com.hkust.smart_buddy.auth.constants.AuthConstants;
import com.hkust.smart_buddy.auth.domain.User;
import com.hkust.smart_buddy.auth.dto.TokenDto;
import com.hkust.smart_buddy.auth.exception.UserNotExistException;
import com.hkust.smart_buddy.auth.exception.WrongPasswordException;
import com.hkust.smart_buddy.auth.repository.UserRepository;
import com.hkust.smart_buddy.common.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TokenService tokenService;

    private User testUser;
    private String username;
    private String password;
    private String encodedPassword;
    private String expectedToken;
    private String userId;

    @BeforeEach
    void setUp() {
        username = "testuser";
        password = "testpassword";
        encodedPassword = "$2a$10$encodedPassword";
        expectedToken = "jwt-token-123";
        userId = "user-123-uuid-456";

        testUser = new User();
        testUser.setUserId(userId);
        testUser.setUsername(username);
        testUser.setPassword(encodedPassword);
    }

    @Test
    void createToken_ValidCredentials_ReturnsTokenDto() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(username, userId)).thenReturn(expectedToken);

        TokenDto result = tokenService.createToken(username, password);

        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtUtil, times(1)).generateToken(username, userId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"nonexistent", "unknownuser", "invaliduser"})
    void createToken_UserNotFound_ThrowsUserNotExistException(String nonExistentUsername) {
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        UserNotExistException exception = assertThrows(UserNotExistException.class, () ->
            tokenService.createToken(nonExistentUsername, password));

        assertEquals(AuthConstants.USER_NOT_EXIST, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(nonExistentUsername);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void createToken_NullUsername_ThrowsUserNotExistException() {
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        UserNotExistException exception = assertThrows(UserNotExistException.class, () ->
            tokenService.createToken(null, password));

        assertEquals(AuthConstants.USER_NOT_EXIST, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(null);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"wrongpassword", "invalidpass", "badpassword"})
    void createToken_InvalidPassword_ThrowsWrongPasswordException(String invalidPassword) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(invalidPassword, encodedPassword)).thenReturn(false);

        WrongPasswordException exception = assertThrows(WrongPasswordException.class, () ->
            tokenService.createToken(username, invalidPassword));

        assertEquals(AuthConstants.WRONG_PASSWORD, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(invalidPassword, encodedPassword);
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void createToken_UserPasswordIsNull_ThrowsWrongPasswordException() {
        User userWithNullPassword = new User();
        userWithNullPassword.setUserId(userId);
        userWithNullPassword.setUsername(username);
        userWithNullPassword.setPassword(null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userWithNullPassword));
        when(passwordEncoder.matches(password, null)).thenReturn(false);

        WrongPasswordException exception = assertThrows(WrongPasswordException.class, () ->
            tokenService.createToken(username, password));

        assertEquals(AuthConstants.WRONG_PASSWORD, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, null);
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}
