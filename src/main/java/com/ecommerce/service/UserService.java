package com.ecommerce.service;

import com.ecommerce.dto.UserDto;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.Tenant;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.TenantRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    
    public UserDto createUser(UserDto userDto) {
        validateUserCreation(userDto);
        
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setKeycloakId(userDto.getKeycloakId());
        
        Role role = roleRepository.findByName(userDto.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + userDto.getRole()));
        user.setRole(role);
        
        if (userDto.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(userDto.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + userDto.getTenantId()));
            user.setTenant(tenant);
        }
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        
        if (userDto.getUsername() != null && !userDto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new ValidationException("Username already exists: " + userDto.getUsername());
            }
            user.setUsername(userDto.getUsername());
        }
        
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new ValidationException("Email already exists: " + userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }
        
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        
        if (userDto.getRole() != null) {
            Role role = roleRepository.findByName(userDto.getRole())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + userDto.getRole()));
            user.setRole(role);
        }
        
        if (userDto.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(userDto.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + userDto.getTenantId()));
            user.setTenant(tenant);
        }
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return convertToDto(user);
    }
    
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return convertToDto(user);
    }
    
    public UserDto getUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Keycloak ID: " + keycloakId));
        return convertToDto(user);
    }
    
    public Page<UserDto> getUsersByTenant(Long tenantId, Pageable pageable) {
        Page<User> users = userRepository.findByTenantId(tenantId, pageable);
        return users.map(this::convertToDto);
    }
    
    public List<UserDto> getUsersByRole(Role.RoleType roleType) {
        List<User> users = userRepository.findByRole(roleType);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
    
    private void validateUserCreation(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new ValidationException("Username already exists: " + userDto.getUsername());
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ValidationException("Email already exists: " + userDto.getEmail());
        }
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setKeycloakId(user.getKeycloakId());
        dto.setRole(user.getRole().getName());
        if (user.getTenant() != null) {
            dto.setTenantId(user.getTenant().getId());
            dto.setTenantName(user.getTenant().getName());
        }
        return dto;
    }
}
