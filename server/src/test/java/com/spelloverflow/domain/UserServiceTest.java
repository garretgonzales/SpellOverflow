package com.spelloverflow.domain;

import com.spelloverflow.data.UserRepository;
import com.spelloverflow.dto.LoginUserRequest;
import com.spelloverflow.dto.RegisterUserRequest;
import com.spelloverflow.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserWhenRequestIsValid() {
        RegisterUserRequest request = createRequest(
                "wand_wrangler",
                "wand@example.com",
                "spell-password",
                "spell-password"
        );

        User savedUser = new User(
                "wand_wrangler",
                "wand@example.com",
                "encoded-password"
        );

        given(userRepository.existsByUsername(request.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encoded-password");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        User result = userService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertThat(result).isSameAs(savedUser);
        assertThat(userToSave.getUsername()).isEqualTo("wand_wrangler");
        assertThat(userToSave.getEmail()).isEqualTo("wand@example.com");
        assertThat(userToSave.getPasswordHash()).isEqualTo("encoded-password");
        verify(passwordEncoder).encode("spell-password");
    }

    @Test
    void shouldRejectRegistrationWhenPasswordsDoNotMatch() {
        RegisterUserRequest request = createRequest(
                "wand_wrangler",
                "wand@example.com",
                "spell-password",
                "different-password"
        );

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password and confirmation must match.");

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void shouldRejectRegistrationWhenUsernameAlreadyExists() {
        RegisterUserRequest request = createRequest(
                "wand_wrangler",
                "wand@example.com",
                "spell-password",
                "spell-password"
        );

        given(userRepository.existsByUsername(request.getUsername())).willReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is already taken.");

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {
        RegisterUserRequest request = createRequest(
                "wand_wrangler",
                "wand@example.com",
                "spell-password",
                "spell-password"
        );

        given(userRepository.existsByUsername(request.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already registered.");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    private RegisterUserRequest createRequest(
            String username,
            String email,
            String password,
            String confirmPassword
    ) {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        return request;
    }

    // jwt service tests
    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("wand@example.com");
        request.setPassword("spell-password");

        User user = new User(
                "wand_wrangler",
                "wand@example.com",
                "encoded-password"
        );

        ReflectionTestUtils.setField(user, "id", 42L);

        given(userRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )).willReturn(true);
        given(jwtService.generateToken(42L, "wand_wrangler"))
                .willReturn("signed-token");

        String token = userService.login(request);

        assertThat(token).isEqualTo("signed-token");
    }

    @Test
    void shouldRejectLoginWhenEmailDoesNotExist() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("missing@example.com");
        request.setPassword("spell-password");

        given(userRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password.");

        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void shouldRejectLoginWhenPasswordIsIncorrect() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("wand@example.com");
        request.setPassword("wrong-password");

        User user = new User(
                "wand_wrangler",
                "wand@example.com",
                "encoded-password"
        );

        given(userRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )).willReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password.");

        verifyNoInteractions(jwtService);
    }

}