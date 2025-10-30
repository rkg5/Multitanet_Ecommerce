package com.ecommerce.controller;

import com.ecommerce.dto.TenantDto;
import com.ecommerce.dto.UserDto;
import com.ecommerce.entity.Role;
import com.ecommerce.service.TenantService;
import com.ecommerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TenantService tenantService;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createTenant_Success() throws Exception {
        TenantDto tenantDto = new TenantDto();
        tenantDto.setName("Test Tenant");
        tenantDto.setDomain("test");
        tenantDto.setDescription("Test Description");
        tenantDto.setIsActive(true);
        
        when(tenantService.createTenant(any(TenantDto.class))).thenReturn(tenantDto);
        
        mockMvc.perform(post("/api/admin/tenants")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tenantDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Tenant"))
                .andExpect(jsonPath("$.domain").value("test"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllTenants_Success() throws Exception {
        TenantDto tenantDto = new TenantDto();
        tenantDto.setId(1L);
        tenantDto.setName("Test Tenant");
        tenantDto.setDomain("test");
        
        Page<TenantDto> tenantPage = new PageImpl<>(List.of(tenantDto));
        when(tenantService.getAllTenants(any())).thenReturn(tenantPage);
        
        mockMvc.perform(get("/api/admin/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Tenant"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getTenantById_Success() throws Exception {
        TenantDto tenantDto = new TenantDto();
        tenantDto.setId(1L);
        tenantDto.setName("Test Tenant");
        tenantDto.setDomain("test");
        
        when(tenantService.getTenantById(1L)).thenReturn(tenantDto);
        
        mockMvc.perform(get("/api/admin/tenants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Tenant"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setRole(Role.RoleType.USER);
        
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
        
        mockMvc.perform(post("/api/admin/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createTenant_Unauthorized() throws Exception {
        TenantDto tenantDto = new TenantDto();
        tenantDto.setName("Test Tenant");
        tenantDto.setDomain("test");
        
        mockMvc.perform(post("/api/admin/tenants")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tenantDto)))
                .andExpect(status().isForbidden());
    }
}
