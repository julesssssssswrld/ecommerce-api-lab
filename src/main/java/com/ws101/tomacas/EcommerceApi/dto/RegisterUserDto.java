package com.ws101.tomacas.EcommerceApi.dto;

import com.ws101.tomacas.EcommerceApi.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 *
 * <p>Validated using Bean Validation annotations before the
 * registration logic executes. Ensures username and password
 * meet minimum security requirements.</p>
 *
 * @author Jules Ian C. Tomacas
 * @see Role
 */
public class RegisterUserDto {

    /** Username — required, 3-20 characters. */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /** Password — required, minimum 8 characters for security. */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /** User role — required (USER, SELLER, or ADMIN). */
    @NotNull(message = "Role is required")
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
