package com.example.fakebook.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String BASE64_SECRET = "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(BASE64_SECRET, 1_000L);
        userDetails = new User("alice", "password", Collections.emptyList());
    }

    @Test
    void generateTokenEncodesUsernameAndHonorsExpiration() {
        String token = jwtService.generateToken(userDetails);

        Claims claims = parseClaims(token);
        long lifetime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        assertThat(claims.getSubject()).isEqualTo(userDetails.getUsername());
        assertThat(lifetime).isEqualTo(1_000L);
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void extractUsernameReturnsSubject() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo(userDetails.getUsername());
    }

    @Test
    void isTokenValidReturnsFalseWhenUsernameDiffers() {
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = new User("bob", "password", Collections.emptyList());

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void isTokenValidReturnsFalseWhenTokenExpired() {
        JwtService expiringService = new JwtService(BASE64_SECRET, -1_000L);
        String token = expiringService.generateToken(userDetails);

        boolean isValid = expiringService.isTokenValid(token, userDetails);

        assertThat(isValid).isFalse();
    }

    @Test
    void tamperedTokenTriggersParsingException() {
        String token = jwtService.generateToken(userDetails);
        char lastChar = token.charAt(token.length() - 1);
        char replacement = lastChar == 'a' ? 'b' : 'a';
        String tamperedToken = token.substring(0, token.length() - 1) + replacement;

        assertThatThrownBy(() -> jwtService.extractUsername(tamperedToken))
                .isInstanceOf(JwtException.class);
    }

    private Claims parseClaims(String token) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(BASE64_SECRET));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
