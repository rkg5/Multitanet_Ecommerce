package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByKeycloakId(String keycloakId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.tenant.id = :tenantId")
    Page<User> findByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role.name = :roleType")
    List<User> findByRole(@Param("roleType") com.ecommerce.entity.Role.RoleType roleType);
    
    @Query("SELECT u FROM User u WHERE u.tenant.id = :tenantId AND u.role.name = :roleType")
    List<User> findByTenantIdAndRole(@Param("tenantId") Long tenantId, 
                                   @Param("roleType") com.ecommerce.entity.Role.RoleType roleType);
}
