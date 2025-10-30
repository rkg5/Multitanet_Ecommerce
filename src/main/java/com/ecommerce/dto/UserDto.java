package com.ecommerce.dto;

import com.ecommerce.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String firstName;
    private String lastName;
    private String keycloakId;
    
    @NotNull(message = "Role is required")
    private Role.RoleType role;
    
    private Long tenantId;
    private String tenantName;
}
