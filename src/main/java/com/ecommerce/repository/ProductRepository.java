package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p WHERE p.tenant.id = :tenantId AND p.isActive = true")
    Page<Product> findByTenantIdAndIsActive(@Param("tenantId") Long tenantId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.tenant.id = :tenantId")
    Page<Product> findByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    Page<Product> findActiveProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% AND p.isActive = true")
    Page<Product> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.isActive = true")
    Page<Product> findByCategory(@Param("category") String category, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.isActive = true")
    Page<Product> findByBrand(@Param("brand") String brand, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.tenant.id = :tenantId AND p.name LIKE %:name% AND p.isActive = true")
    Page<Product> findByTenantIdAndNameContaining(@Param("tenantId") Long tenantId, 
                                                 @Param("name") String name, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.tenant.id = :tenantId AND p.category = :category AND p.isActive = true")
    Page<Product> findByTenantIdAndCategory(@Param("tenantId") Long tenantId, 
                                          @Param("category") String category, Pageable pageable);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true")
    List<String> findDistinctCategories();
    
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.isActive = true")
    List<String> findDistinctBrands();
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.tenant.id = :tenantId AND p.isActive = true")
    List<String> findDistinctCategoriesByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.tenant.id = :tenantId AND p.isActive = true")
    List<String> findDistinctBrandsByTenantId(@Param("tenantId") Long tenantId);
}
