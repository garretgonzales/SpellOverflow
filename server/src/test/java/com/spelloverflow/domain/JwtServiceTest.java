package com.spelloverflow.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void shouldParseValidTokenAndReturnOriginalClaims() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);

        String token = jwtService.generateToken(42L, "wand_wrangler");
        Claims claims = jwtService.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("username", String.class))
                .isEqualTo("wand_wrangler");
    }

    @Test
    void shouldThrowWhenTokenIsExpired() throws InterruptedException {
        JwtService jwtService = new JwtService(TEST_SECRET, 10);
        String token = jwtService.generateToken(42L, "wand_wrangler");

        Thread.sleep(50);

        assertThatThrownBy(() -> jwtService.parseToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldThrowWhenSignatureIsInvalid() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);
        String token = jwtService.generateToken(42L, "wand_wrangler");

        String tamperedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("A") ? "B" : "A");

        assertThatThrownBy(() -> jwtService.parseToken(tamperedToken))
                .isInstanceOf(SignatureException.class);
    }
}