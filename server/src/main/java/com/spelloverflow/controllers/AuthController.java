package com.spelloverflow.controllers;

import com.spelloverflow.domain.UserService;
import com.spelloverflow.dto.RegisterUserRequest;
import com.spelloverflow.dto.RegisterUserResponse;
import com.spelloverflow.models.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spelloverflow.dto.LoginUserResponse;
import com.spelloverflow.dto.LoginUserRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
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
        String token = userService.login(request);
        return ResponseEntity.ok(new LoginUserResponse(token));
    }

}