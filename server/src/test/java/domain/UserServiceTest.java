package domain;

import data.UserRepository;
import dto.RegisterUserRequest;
import models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}