package com.spelloverflow.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

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
}