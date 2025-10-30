package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {
    
    private Long id;
    
    @NotBlank(message = "Tenant name is required")
    private String name;
    
    @NotBlank(message = "Domain is required")
    private String domain;
    
    private String description;
    private Boolean isActive;
}
