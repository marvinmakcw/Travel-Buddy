package com.hkust.smart_buddy.auth.controller;

import com.hkust.smart_buddy.auth.constants.AuthConstants;
import com.hkust.smart_buddy.common.dto.ApiResponse;
import com.hkust.smart_buddy.auth.dto.TokenDto;
import com.hkust.smart_buddy.auth.dto.CredentialDto;
import com.hkust.smart_buddy.auth.service.TokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/smart_buddy/auth")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/tokens")
    public ResponseEntity<ApiResponse<TokenDto>> createToken(@RequestBody CredentialDto credentialDto) {
        TokenDto tokenDto = tokenService.createToken(credentialDto.getUsername(), credentialDto.getPassword());
        ApiResponse<TokenDto> response = ApiResponse.<TokenDto>builder()
                .status(HttpStatus.OK.value())
                .message(AuthConstants.LOGIN_SUCCESS)
                .data(tokenDto)
                .build();
        return ResponseEntity.ok(response);
    }
}
