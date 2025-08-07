package com.hkust.smart_buddy.auth.service;

import com.hkust.smart_buddy.auth.constants.AuthConstants;
import com.hkust.smart_buddy.auth.dto.TokenDto;
import com.hkust.smart_buddy.auth.exception.UserNotExistException;
import com.hkust.smart_buddy.auth.exception.WrongPasswordException;
import com.hkust.smart_buddy.auth.repository.UserRepository;
import com.hkust.smart_buddy.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public TokenDto createToken(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId());
                        return TokenDto.builder()
                                .token(token)
                                .build();
                    } else {
                        throw new WrongPasswordException(AuthConstants.WRONG_PASSWORD);
                    }
                })
                .orElseThrow(() -> new UserNotExistException(AuthConstants.USER_NOT_EXIST));
    }
}
