package com.hkust.smart_buddy.auth.controller;

import com.hkust.smart_buddy.auth.constants.AuthConstants;
import com.hkust.smart_buddy.auth.dto.UserDto;
import com.hkust.smart_buddy.auth.service.UserService;
import com.hkust.smart_buddy.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/smart_buddy/auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Void>> createUser(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto);
        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .status(200)
                .message(AuthConstants.USER_CREATED_SUCCESS)
                .data(null)
                .build()
        );
    }
}
