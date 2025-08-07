package com.hkust.smart_buddy.auth.service;

import com.hkust.smart_buddy.auth.constants.AuthConstants;
import com.hkust.smart_buddy.auth.domain.User;
import com.hkust.smart_buddy.auth.dto.UserDto;
import com.hkust.smart_buddy.auth.exception.PasswordMismatchException;
import com.hkust.smart_buddy.auth.exception.UsernameExistsException;
import com.hkust.smart_buddy.auth.repository.UserRepository;
import com.hkust.smart_buddy.common.constants.DatabaseConstants;
import com.hkust.smart_buddy.common.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String generateUniqueUserId() {
        return UuidUtil.generateUniqueId(candidateId -> 
            userRepository.findByUserId(candidateId).isPresent()
        );
    }

    private void validateUserDto(UserDto userDto) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new PasswordMismatchException(AuthConstants.PASSWORD_MISMATCH);
        }

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UsernameExistsException(AuthConstants.USERNAME_EXISTS);
        }
    }

    public void createUser(UserDto userDto) {
        validateUserDto(userDto);
        String userId = generateUniqueUserId();
        User user = new User();
        user.setUserId(userId);
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setCreatedBy(DatabaseConstants.DEFAULT_CREATED_BY);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedBy(DatabaseConstants.DEFAULT_CREATED_BY);
        user.setLastModifiedDate(LocalDateTime.now());
        user.setRecordVersion(DatabaseConstants.DEFAULT_RECORD_VERSION);
        userRepository.save(user);
    }
}
