package com.spelloverflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginUserRequest {

    @NotBlank(message = "Username or email is required.")
    @Size(max = 320, message = "Username or email must not exceed 320 characters.")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required.")
    private String password;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
