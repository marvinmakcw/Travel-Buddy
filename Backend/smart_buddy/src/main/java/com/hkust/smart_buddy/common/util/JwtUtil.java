package com.hkust.smart_buddy.common.util;

import com.hkust.smart_buddy.common.constants.JwtConstants;
import com.hkust.smart_buddy.common.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String username, String userId) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setSubject(username)
                .claim(JwtConstants.USER_ID_CLAIM, userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims decodeToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new InvalidJwtTokenException(JwtConstants.TOKEN_NULL_OR_EMPTY);
        }

        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new InvalidJwtTokenException(JwtConstants.TOKEN_EXPIRED, e);
        } catch (SignatureException e) {
            throw new InvalidJwtTokenException(JwtConstants.TOKEN_SIGNATURE_INVALID, e);
        } catch (MalformedJwtException e) {
            throw new InvalidJwtTokenException(JwtConstants.TOKEN_MALFORMED, e);
        } catch (UnsupportedJwtException e) {
            throw new InvalidJwtTokenException(JwtConstants.TOKEN_UNSUPPORTED, e);
        } catch (IllegalArgumentException e) {
            throw new InvalidJwtTokenException(JwtConstants.TOKEN_INVALID, e);
        } catch (JwtException e) {
            throw new InvalidJwtTokenException(JwtConstants.TOKEN_PROCESSING_FAILED + e.getMessage(), e);
        }
    }
}
