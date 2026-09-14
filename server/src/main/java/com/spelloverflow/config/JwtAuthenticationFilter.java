package com.spelloverflow.config;

import com.spelloverflow.domain.AuthenticatedUser;
import com.spelloverflow.domain.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_COOKIE_NAME = "auth_token";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie authCookie = WebUtils.getCookie(request, AUTH_COOKIE_NAME);

        if (authCookie != null) {
            try {
                Claims claims = jwtService.parseToken(authCookie.getValue());
                Long userId = Long.valueOf(claims.getSubject());
                String username = claims.get("username", String.class);

                AuthenticatedUser principal = new AuthenticatedUser(userId, username);

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(principal, null, List.of())
                );
            } catch (JwtException | IllegalArgumentException e) {
                // Invalid, expired, or malformed token: leave the request unauthenticated
                // and let Spring Security's authorization rules decide what happens next.
            }
        }

        filterChain.doFilter(request, response);
    }
}
