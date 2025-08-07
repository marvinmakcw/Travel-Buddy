package com.hkust.smart_buddy.common.util;

import com.hkust.smart_buddy.common.constants.JwtConstants;
import com.hkust.smart_buddy.common.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "dlNuWEwpXJch0fZYvy8TyE8NtNK9JIPN";
    private final long expiration = 3600000;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
    }

    @Test
    void decodeToken_ValidUnmodifiedToken_ReturnsCorrectClaims() {
        String username = "testuser";
        String userId = "user-123-uuid-456";

        long beforeGeneration = (System.currentTimeMillis() / 1000) * 1000;
        String token = jwtUtil.generateToken(username, userId);
        long afterGeneration = ((System.currentTimeMillis() / 1000) + 1) * 1000;

        System.out.println("Generated token: " + token);
        Claims claims = jwtUtil.decodeToken(token);

        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertEquals(userId, claims.get(JwtConstants.USER_ID_CLAIM));
        assertEquals(userId, claims.get(JwtConstants.USER_ID_CLAIM, String.class));

        long expectedExpirationMin = beforeGeneration + expiration;
        long expectedExpirationMax = afterGeneration + expiration;
        long actualExpiration = claims.getExpiration().getTime();

        assertTrue(actualExpiration >= expectedExpirationMin && actualExpiration <= expectedExpirationMax,
            "Expiration time should be within the expected range. Expected: " + expectedExpirationMin + "-" + expectedExpirationMax + ", Actual: " + actualExpiration);

        long actualIssuedAt = claims.getIssuedAt().getTime();
        assertTrue(actualIssuedAt >= beforeGeneration && actualIssuedAt <= afterGeneration,
            "Issued at should be within token generation time range. Expected: " + beforeGeneration + "-" + afterGeneration + ", Actual: " + actualIssuedAt);
    }

    @Test
    void decodeToken_ExpiredToken_ThrowsInvalidJwtTokenException() {
        JwtUtil shortExpirationJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", secret);
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "expiration", -1000);
        String token = shortExpirationJwtUtil.generateToken("testuser", "user-123");

        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            jwtUtil.decodeToken(token));

        assertTrue(exception.getMessage().contains(JwtConstants.TOKEN_EXPIRED));
    }

    @Test
    void decodeToken_TamperedToken_ThrowsInvalidJwtTokenException() {
        String originalToken = jwtUtil.generateToken("testuser", "user-123");
        String[] tokenParts = originalToken.split("\\.");
        if (tokenParts.length == 3) {
            String tamperedSignature = tokenParts[2].substring(0, Math.max(1, tokenParts[2].length() - 5)) + "XXXXX";
            String tamperedToken = tokenParts[0] + "." + tokenParts[1] + "." + tamperedSignature;

            InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
                jwtUtil.decodeToken(tamperedToken));

            assertTrue(exception.getMessage().contains(JwtConstants.TOKEN_SIGNATURE_INVALID) ||
                      exception.getMessage().contains(JwtConstants.TOKEN_MALFORMED) ||
                      exception.getMessage().contains(JwtConstants.TOKEN_INVALID));
        } else {
            fail("Generated token does not have expected JWT format");
        }
    }

    @Test
    void decodeToken_WrongSecret_ThrowsInvalidJwtTokenException() {
        String token = jwtUtil.generateToken("testuser", "user-123");

        JwtUtil wrongSecretJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(wrongSecretJwtUtil, "secret", "wrongsecret123456789012345678901234");
        ReflectionTestUtils.setField(wrongSecretJwtUtil, "expiration", expiration);

        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            wrongSecretJwtUtil.decodeToken(token));

        assertTrue(exception.getMessage().contains("JWT token signature is invalid"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void decodeToken_NullEmptyOrBlankToken_ThrowsInvalidJwtTokenException(String token) {
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            jwtUtil.decodeToken(token));

        assertEquals(JwtConstants.TOKEN_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void decodeToken_UnsupportedJwtToken_ThrowsInvalidJwtTokenException() {
        String unsupportedToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ0ZXN0In0.";

        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            jwtUtil.decodeToken(unsupportedToken));

        assertTrue(exception.getMessage().contains(JwtConstants.TOKEN_UNSUPPORTED));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "not.a.valid.jwt.format.at.all",
        "invalid.jwt.token.structure",
        "eyJhbGciOiJIUzI1NiJ9.invalid@base64!content.signature",
        "..",
        "eyJhbGciOiJIUzI1NiJ9.invalid-base64-content!@#$.invalid-signature",
        "onlyOneSegment"
    })
    void decodeToken_MalformedToken_ThrowsInvalidJwtTokenException(String malformedToken) {
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            jwtUtil.decodeToken(malformedToken));

        assertTrue(exception.getMessage().contains(JwtConstants.TOKEN_INVALID) ||
                  exception.getMessage().contains(JwtConstants.TOKEN_MALFORMED) ||
                  exception.getMessage().contains(JwtConstants.TOKEN_PROCESSING_FAILED) ||
                  exception.getMessage().contains(JwtConstants.TOKEN_SIGNATURE_INVALID));
    }

    @Test
    void decodeToken_TokenWithSpecialCharacters_ThrowsInvalidJwtTokenException() {
        String tokenWithNullBytes = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjAwMDAwMDAwfQ.\u0000invalid";

        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () ->
            jwtUtil.decodeToken(tokenWithNullBytes));

        assertTrue(exception.getMessage().contains(JwtConstants.TOKEN_INVALID) ||
                  exception.getMessage().contains(JwtConstants.TOKEN_MALFORMED) ||
                  exception.getMessage().contains(JwtConstants.TOKEN_PROCESSING_FAILED) ||
                  exception.getMessage().contains(JwtConstants.TOKEN_SIGNATURE_INVALID));
    }
}
