package com.spelloverflow.controllers;

import com.spelloverflow.config.JwtAuthenticationFilter;
import com.spelloverflow.domain.AuthenticatedUser;
import com.spelloverflow.domain.JwtService;
import com.spelloverflow.domain.LoginResult;
import com.spelloverflow.domain.UserService;
import com.spelloverflow.dto.LoginUserRequest;
import com.spelloverflow.dto.LoginUserResponse;
import com.spelloverflow.dto.RegisterUserRequest;
import com.spelloverflow.dto.RegisterUserResponse;
import com.spelloverflow.models.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final boolean cookieSecure;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            @Value("${app.cookie-secure:false}") boolean cookieSecure
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        User user = userService.register(request);

        RegisterUserResponse response = new RegisterUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(
            @Valid @RequestBody LoginUserRequest request
    ) {
        LoginResult result = userService.login(request);

        ResponseCookie cookie = authCookie(
                result.token(),
                Duration.ofMillis(jwtService.getExpirationMs())
        );

        LoginUserResponse response = new LoginUserResponse(
                result.user().getId(),
                result.user().getUsername()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginUserResponse> me(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        return ResponseEntity.ok(
                new LoginUserResponse(principal.id(), principal.username())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = authCookie("", Duration.ZERO);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private ResponseCookie authCookie(String token, Duration maxAge) {
        return ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }

}
