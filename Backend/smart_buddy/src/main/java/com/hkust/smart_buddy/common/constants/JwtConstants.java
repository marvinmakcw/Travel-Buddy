package com.hkust.smart_buddy.common.constants;

import lombok.experimental.UtilityClass;

/**
 * JWT token related constants and error messages
 */
@UtilityClass
public class JwtConstants {

    // JWT Claims
    public static final String USER_ID_CLAIM = "userId";

    // JWT Error Messages
    public static final String TOKEN_NULL_OR_EMPTY = "JWT token cannot be null or empty";
    public static final String TOKEN_EXPIRED = "JWT token has expired";
    public static final String TOKEN_SIGNATURE_INVALID = "JWT token signature is invalid";
    public static final String TOKEN_MALFORMED = "JWT token is malformed";
    public static final String TOKEN_UNSUPPORTED = "JWT token is unsupported";
    public static final String TOKEN_INVALID = "JWT token is invalid";
    public static final String TOKEN_PROCESSING_FAILED = "JWT token processing failed: ";
}
