package com.ws101.tomacas.EcommerceApi.dto;

import com.ws101.tomacas.EcommerceApi.model.Role;

/**
 * Data Transfer Object for user registration requests.
 *
 * <p>Contains the fields required to create a new user account.
 * Validation annotations are applied in Task 4 (Bean Validation).</p>
 *
 * @author Jules Ian C. Tomacas
 * @see Role
 */
public class RegisterUserDto {

    private String username;
    private String password;
    private Role role;

    /** Default constructor required for JSON deserialization. */
    public RegisterUserDto() {}

    public RegisterUserDto(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
