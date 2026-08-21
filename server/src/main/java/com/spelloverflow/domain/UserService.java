package com.spelloverflow.domain;


import com.spelloverflow.data.UserRepository;
import com.spelloverflow.dto.LoginUserRequest;
import com.spelloverflow.dto.RegisterUserRequest;
import com.spelloverflow.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        this.jwtService = jwtService;
    }

    public User register(RegisterUserRequest request) {

        if (!request.getPassword().

                equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirmation must match.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        // BCrypt has to hash the pw before the user exists.
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordHash
        );

        return userRepository.save(user);

    }

    public String login(LoginUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException();
        }

        return jwtService.generateToken(user.getId(), user.getUsername());
    }


}
