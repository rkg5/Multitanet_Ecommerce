package com.ecommerce.repository;

import com.ecommerce.entity.FavoriteProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    
    @Query("SELECT fp FROM FavoriteProduct fp WHERE fp.user.id = :userId")
    Page<FavoriteProduct> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT fp FROM FavoriteProduct fp WHERE fp.user.id = :userId AND fp.product.id = :productId")
    Optional<FavoriteProduct> findByUserIdAndProductId(@Param("userId") Long userId, 
                                                     @Param("productId") Long productId);
    
    @Query("SELECT fp FROM FavoriteProduct fp WHERE fp.user.id = :userId AND fp.product.tenant.id = :tenantId")
    Page<FavoriteProduct> findByUserIdAndTenantId(@Param("userId") Long userId, 
                                                @Param("tenantId") Long tenantId, Pageable pageable);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
