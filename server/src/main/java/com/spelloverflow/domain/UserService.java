package com.spelloverflow.domain;


import com.spelloverflow.data.UserRepository;
import com.spelloverflow.dto.RegisterUserRequest;
import com.spelloverflow.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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


}
