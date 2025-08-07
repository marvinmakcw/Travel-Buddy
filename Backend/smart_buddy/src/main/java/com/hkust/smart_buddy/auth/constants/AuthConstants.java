package com.hkust.smart_buddy.auth.constants;

import lombok.experimental.UtilityClass;

/**
 * Authentication related constants and messages
 */
@UtilityClass
public class AuthConstants {

    // Success messages
    public static final String LOGIN_SUCCESS = "login successfully";
    public static final String USER_CREATED_SUCCESS = "User created successfully";

    // Error messages
    public static final String USER_NOT_EXIST = "User not exist";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String PASSWORD_MISMATCH = "Password and confirm password do not match";
    public static final String USERNAME_EXISTS = "Username already exists";
    public static final String VALIDATION_FAILED = "Validation failed";
}
