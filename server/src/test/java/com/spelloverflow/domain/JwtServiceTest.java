package com.spelloverflow.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "MDEyMzQ1Njc4OUFCQ0RFRjAxMjM0NTY3ODlBQkNERUY=";

    @Test
    void shouldGenerateSignedTokenWithUserClaims() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);

        String token = jwtService.generateToken(42L, "wand_wrangler");

        SecretKey signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(TEST_SECRET)
        );

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("username", String.class))
                .isEqualTo("wand_wrangler");
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }
    @Test
    void shouldValidateTokenAndReturnClaims() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);
        String token = jwtService.generateToken(42L, "wand_wrangler");

        Claims claims = jwtService.validateToken(token);

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("username", String.class))
                .isEqualTo("wand_wrangler");
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);
        SecretKey signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(TEST_SECRET)
        );

        String token = Jwts.builder()
                           .subject("42")
                           .expiration(new Date(0))
                           .signWith(signingKey)
                           .compact();

        assertThatThrownBy(() -> jwtService.validateToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldRejectTokenSignedWithDifferentKey() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);
        SecretKey differentKey = Jwts.SIG.HS256.key().build();

        String token = Jwts.builder()
                           .subject("42")
                           .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                           .signWith(differentKey)
                           .compact();

        assertThatThrownBy(() -> jwtService.validateToken(token))
                .isInstanceOf(SignatureException.class);
    }
}