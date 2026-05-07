package com.ws101.tomacas.EcommerceApi.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for the JWT login request body.
 *
 * <p>Carries the username and password from the client to the
 * authentication endpoint. Both fields are required.</p>
 *
 * @author Jules Ian C. Tomacas
 */
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    /** Default constructor for Jackson deserialization. */
    public LoginRequestDto() {}

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
