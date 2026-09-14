package com.spelloverflow.controllers;

import com.spelloverflow.config.JwtAuthenticationFilter;
import com.spelloverflow.config.SecurityConfig;
import com.spelloverflow.domain.JwtService;
import com.spelloverflow.domain.LoginResult;
import com.spelloverflow.domain.UserService;
import com.spelloverflow.dto.RegisterUserRequest;
import com.spelloverflow.models.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.spelloverflow.domain.InvalidCredentialsException;
import com.spelloverflow.dto.LoginUserRequest;



@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class, JwtService.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateAccountWhenRegistrationIsValid() throws Exception {
        User savedUser = new User(
                "wand_wrangler",
                "wand@example.com",
                "encoded-password"
        );

        given(userService.register(any(RegisterUserRequest.class)))
                .willReturn(savedUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "wand_wrangler",
                                  "email": "wand@example.com",
                                  "password": "spell-password",
                                  "confirmPassword": "spell-password"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("wand_wrangler"))
                .andExpect(jsonPath("$.email").value("wand@example.com"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(userService).register(any(RegisterUserRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenRequestValidationFails() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "email": "not-an-email",
                                  "password": "short",
                                  "confirmPassword": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed."))
                .andExpect(jsonPath("$.errors.username").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.confirmPassword").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnBadRequestWhenUsernameIsTaken() throws Exception {
        given(userService.register(any(RegisterUserRequest.class)))
                .willThrow(new IllegalArgumentException(
                        "Username is already taken."
                ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "wand_wrangler",
                                  "email": "wand@example.com",
                                  "password": "spell-password",
                                  "confirmPassword": "spell-password"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Username is already taken."));
    }

    @Test
    void shouldSetAuthCookieWhenLoginIsValid() throws Exception {
        User user = new User(
                "wand_wrangler",
                "wand@example.com",
                "encoded-password"
        );

        given(userService.login(any(LoginUserRequest.class)))
                .willReturn(new LoginResult(user, "test-token"));

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                            {
                              "usernameOrEmail": "wand@example.com",
                              "password": "spell-password"
                            }
                            """))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").doesNotExist())
               .andExpect(jsonPath("$.username").value("wand_wrangler"))
               .andExpect(cookie().value(JwtAuthenticationFilter.AUTH_COOKIE_NAME, "test-token"))
               .andExpect(cookie().httpOnly(JwtAuthenticationFilter.AUTH_COOKIE_NAME, true))
               .andExpect(cookie().path(JwtAuthenticationFilter.AUTH_COOKIE_NAME, "/"));
    }

    @Test
    void shouldSetAuthCookieWhenLoggingInWithUsername() throws Exception {
        User user = new User(
                "wand_wrangler",
                "wand@example.com",
                "encoded-password"
        );

        given(userService.login(any(LoginUserRequest.class)))
                .willReturn(new LoginResult(user, "test-token"));

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                            {
                              "usernameOrEmail": "wand_wrangler",
                              "password": "spell-password"
                            }
                            """))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("wand_wrangler"))
               .andExpect(cookie().value(JwtAuthenticationFilter.AUTH_COOKIE_NAME, "test-token"));
    }

    @Test
    void shouldReturnCurrentUserWhenAuthenticated() throws Exception {
        String token = jwtService.generateToken(7L, "wand_wrangler");

        mockMvc.perform(get("/api/auth/me")
                       .cookie(new Cookie(JwtAuthenticationFilter.AUTH_COOKIE_NAME, token)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(7))
               .andExpect(jsonPath("$.username").value("wand_wrangler"));
    }

    @Test
    void shouldRejectMeWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
               .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldClearAuthCookieOnLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
               .andExpect(status().isNoContent())
               .andExpect(cookie().maxAge(JwtAuthenticationFilter.AUTH_COOKIE_NAME, 0));
    }

    @Test
    void shouldRejectInvalidLoginInput() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                            {
                              "usernameOrEmail": "",
                              "password": ""
                            }
                            """))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errors.usernameOrEmail").exists())
               .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        given(userService.login(any(LoginUserRequest.class)))
                .willThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("""
                            {
                              "usernameOrEmail": "wand@example.com",
                              "password": "incorrect-password"
                            }
                            """))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.message")
                       .value("Invalid username, email, or password."))
               .andExpect(jsonPath("$.token").doesNotExist());
    }
}