package com.ecommerce.controller;

import com.ecommerce.dto.TenantDto;
import com.ecommerce.dto.UserDto;
import com.ecommerce.service.TenantService;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final TenantService tenantService;
    private final UserService userService;

//    @GetMapping("/tenant")
//    public ResponseEntity<String> getTenant() {
//        return ResponseEntity.ok("Tenant info here");
//    }
    @PostMapping("/tenants")
    public ResponseEntity<TenantDto> createTenant(@Valid @RequestBody TenantDto tenantDto) {
        TenantDto createdTenant = tenantService.createTenant(tenantDto);
        return new ResponseEntity<>(createdTenant, HttpStatus.CREATED);
    }
    
    @GetMapping("/tenants")
    public ResponseEntity<Page<TenantDto>> getAllTenants(Pageable pageable) {
        Page<TenantDto> tenants = tenantService.getAllTenants(pageable);
        return ResponseEntity.ok(tenants);
    }
    
    @GetMapping("/tenants/{id}")
    public ResponseEntity<TenantDto> getTenantById(@PathVariable Long id) {
        TenantDto tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }
    
    @PutMapping("/tenants/{id}")
    public ResponseEntity<TenantDto> updateTenant(@PathVariable Long id, @Valid @RequestBody TenantDto tenantDto) {
        TenantDto updatedTenant = tenantService.updateTenant(id, tenantDto);
        return ResponseEntity.ok(updatedTenant);
    }
    
    @DeleteMapping("/tenants/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
    
    // User Management
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserDto> users = userService.getUsersByTenant(null, pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/tenants/{tenantId}/users")
    public ResponseEntity<Page<UserDto>> getUsersByTenant(@PathVariable Long tenantId, Pageable pageable) {
        Page<UserDto> users = userService.getUsersByTenant(tenantId, pageable);
        return ResponseEntity.ok(users);
    }
}
