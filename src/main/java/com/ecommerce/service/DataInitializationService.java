package com.ecommerce.service;

import com.ecommerce.entity.Role;
import com.ecommerce.entity.Tenant;
import com.ecommerce.entity.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.TenantRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeTenants();
        initializeUsers();
    }
    
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName(Role.RoleType.ADMIN);
            adminRole.setDescription("Platform Administrator");
            roleRepository.save(adminRole);
            
            Role tenantRole = new Role();
            tenantRole.setName(Role.RoleType.TENANT);
            tenantRole.setDescription("Tenant Administrator");
            roleRepository.save(tenantRole);
            
            Role userRole = new Role();
            userRole.setName(Role.RoleType.USER);
            userRole.setDescription("Regular User");
            roleRepository.save(userRole);
        }
    }
    
    private void initializeTenants() {
        if (tenantRepository.count() == 0) {
            Tenant nikeTenant = new Tenant();
            nikeTenant.setName("Nike");
            nikeTenant.setDomain("nike");
            nikeTenant.setDescription("Nike Sports Brand");
            nikeTenant.setIsActive(true);
            tenantRepository.save(nikeTenant);
            
            Tenant adidasTenant = new Tenant();
            adidasTenant.setName("Adidas");
            adidasTenant.setDomain("adidas");
            adidasTenant.setDescription("Adidas Sports Brand");
            adidasTenant.setIsActive(true);
            tenantRepository.save(adidasTenant);
            
            Tenant pumaTenant = new Tenant();
            pumaTenant.setName("Puma");
            pumaTenant.setDomain("puma");
            pumaTenant.setDescription("Puma Sports Brand");
            pumaTenant.setIsActive(true);
            tenantRepository.save(pumaTenant);
        }
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName(Role.RoleType.ADMIN).orElseThrow();
            Role tenantRole = roleRepository.findByName(Role.RoleType.TENANT).orElseThrow();
            Role userRole = roleRepository.findByName(Role.RoleType.USER).orElseThrow();
            
            Tenant nikeTenant = tenantRepository.findByDomain("nike").orElseThrow();
            Tenant adidasTenant = tenantRepository.findByDomain("adidas").orElseThrow();
            
            // Admin user
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@ecommerce.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setKeycloakId("admin-keycloak-id");
            adminUser.setRole(adminRole);
            userRepository.save(adminUser);
            
            // Nike tenant admin
            User nikeAdmin = new User();
            nikeAdmin.setUsername("nike_admin");
            nikeAdmin.setEmail("admin@nike.com");
            nikeAdmin.setFirstName("Nike");
            nikeAdmin.setLastName("Admin");
            nikeAdmin.setKeycloakId("nike-admin-keycloak-id");
            nikeAdmin.setRole(tenantRole);
            nikeAdmin.setTenant(nikeTenant);
            userRepository.save(nikeAdmin);
            
            // Adidas tenant admin
            User adidasAdmin = new User();
            adidasAdmin.setUsername("adidas_admin");
            adidasAdmin.setEmail("admin@adidas.com");
            adidasAdmin.setFirstName("Adidas");
            adidasAdmin.setLastName("Admin");
            adidasAdmin.setKeycloakId("adidas-admin-keycloak-id");
            adidasAdmin.setRole(tenantRole);
            adidasAdmin.setTenant(adidasTenant);
            userRepository.save(adidasAdmin);
            
            // Regular users
            User regularUser1 = new User();
            regularUser1.setUsername("john_doe");
            regularUser1.setEmail("john@example.com");
            regularUser1.setFirstName("John");
            regularUser1.setLastName("Doe");
            regularUser1.setKeycloakId("john-keycloak-id");
            regularUser1.setRole(userRole);
            userRepository.save(regularUser1);
            
            User regularUser2 = new User();
            regularUser2.setUsername("jane_smith");
            regularUser2.setEmail("jane@example.com");
            regularUser2.setFirstName("Jane");
            regularUser2.setLastName("Smith");
            regularUser2.setKeycloakId("jane-keycloak-id");
            regularUser2.setRole(userRole);
            userRepository.save(regularUser2);
        }
    }
}
