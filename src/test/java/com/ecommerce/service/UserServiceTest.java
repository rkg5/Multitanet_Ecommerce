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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private TenantRepository tenantRepository;
    
    @InjectMocks
    private UserService userService;
    
    private User user;
    private UserDto userDto;
    private Role role;
    private Tenant tenant;
    
    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName(Role.RoleType.USER);
        role.setDescription("Regular User");
        
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Test Tenant");
        tenant.setDomain("test");
        tenant.setIsActive(true);
        
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setKeycloakId("test-keycloak-id");
        user.setRole(role);
        user.setTenant(tenant);
        
        userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setKeycloakId("test-keycloak-id");
        userDto.setRole(Role.RoleType.USER);
        userDto.setTenantId(1L);
    }
    
    @Test
    void createUser_Success() {
        when(roleRepository.findByName(Role.RoleType.USER)).thenReturn(Optional.of(role));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        UserDto result = userService.createUser(userDto);
        
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createUser_UsernameExists_ThrowsValidationException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        
        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
    }
    
    @Test
    void createUser_EmailExists_ThrowsValidationException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
    }
    
    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        UserDto result = userService.getUserById(1L);
        
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
    }
    
    @Test
    void getUserById_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }
    
    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleType.ADMIN)).thenReturn(Optional.of(role));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        userDto.setRole(Role.RoleType.ADMIN);
        userDto.setTenantId(1L);
        UserDto result = userService.updateUser(1L, userDto);
        
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        
        userService.deleteUser(1L);
        
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    void deleteUser_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);
        
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }
    
    @Test
    void getUsersByTenant_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findByTenantId(1L, pageable)).thenReturn(userPage);
        
        Page<UserDto> result = userService.getUsersByTenant(1L, pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(user.getUsername(), result.getContent().get(0).getUsername());
    }
}
