package com.ecommerce.repository;

import com.ecommerce.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    Optional<Tenant> findByName(String name);
    
    Optional<Tenant> findByDomain(String domain);
    
    boolean existsByName(String name);
    
    boolean existsByDomain(String domain);
    
    @Query("SELECT t FROM Tenant t WHERE t.isActive = true")
    List<Tenant> findActiveTenants();
    
    @Query("SELECT t FROM Tenant t WHERE t.isActive = :isActive")
    Page<Tenant> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);
}
