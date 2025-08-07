package com.hkust.smart_buddy.auth.controller;

import com.hkust.smart_buddy.auth.constants.AuthConstants;
import com.hkust.smart_buddy.auth.dto.CredentialDto;
import com.hkust.smart_buddy.auth.dto.TokenDto;
import com.hkust.smart_buddy.auth.exception.UserNotExistException;
import com.hkust.smart_buddy.auth.exception.WrongPasswordException;
import com.hkust.smart_buddy.auth.service.TokenService;
import com.hkust.smart_buddy.common.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenControllerTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TokenController tokenController;

    private CredentialDto validCredentials;
    private TokenDto tokenDto;

    @BeforeEach
    void setUp() {
        validCredentials = new CredentialDto();
        validCredentials.setUsername("testuser");
        validCredentials.setPassword("testpassword");

        tokenDto = TokenDto.builder()
                .token("jwt-token-123")
                .build();
    }

    @Test
    void createToken_ValidCredentials_ReturnsTokenSuccessfully() {
        when(tokenService.createToken("testuser", "testpassword"))
                .thenReturn(tokenDto);
        ResponseEntity<ApiResponse<TokenDto>> response = tokenController.createToken(validCredentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getStatus());
        assertEquals(AuthConstants.LOGIN_SUCCESS, response.getBody().getMessage());
        assertEquals("jwt-token-123", response.getBody().getData().getToken());
    }

    @Test
    void createToken_UserNotExist_ThrowsUserNotExistException() {
        CredentialDto invalidCredentials = new CredentialDto();
        invalidCredentials.setUsername("nonexistentuser");
        invalidCredentials.setPassword("password");

        when(tokenService.createToken("nonexistentuser", "password"))
                .thenThrow(new UserNotExistException(AuthConstants.USER_NOT_EXIST));
        assertThrows(UserNotExistException.class, () -> tokenController.createToken(invalidCredentials));
    }

    @Test
    void createToken_WrongPassword_ThrowsWrongPasswordException() {
        CredentialDto wrongPasswordCredentials = new CredentialDto();
        wrongPasswordCredentials.setUsername("testuser");
        wrongPasswordCredentials.setPassword("wrongpassword");

        when(tokenService.createToken("testuser", "wrongpassword"))
                .thenThrow(new WrongPasswordException(AuthConstants.WRONG_PASSWORD));

        assertThrows(WrongPasswordException.class, () -> tokenController.createToken(wrongPasswordCredentials));
    }

    @Test
    void createToken_NullUsername_ThrowsUserNotExistException() {
        CredentialDto nullUsernameCredentials = new CredentialDto();
        nullUsernameCredentials.setPassword("password");

        when(tokenService.createToken(null, "password"))
                .thenThrow(new UserNotExistException(AuthConstants.USER_NOT_EXIST));

        assertThrows(UserNotExistException.class, () -> tokenController.createToken(nullUsernameCredentials));
    }

    @Test
    void createToken_NullPassword_ThrowsWrongPasswordException() {
        CredentialDto nullPasswordCredentials = new CredentialDto();
        nullPasswordCredentials.setUsername("testuser");

        when(tokenService.createToken("testuser", null))
                .thenThrow(new WrongPasswordException(AuthConstants.WRONG_PASSWORD));

        assertThrows(WrongPasswordException.class, () -> tokenController.createToken(nullPasswordCredentials));
    }
}
