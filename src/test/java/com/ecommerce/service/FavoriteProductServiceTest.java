package com.ecommerce.service;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.FavoriteProduct;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Tenant;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.repository.FavoriteProductRepository;
import com.ecommerce.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteProductServiceTest {
    
    @Mock
    private FavoriteProductRepository favoriteProductRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private FavoriteProductService favoriteProductService;
    
    private User user;
    private Product product;
    private Tenant tenant;
    private FavoriteProduct favoriteProduct;
    
    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Test Tenant");
        tenant.setDomain("test");
        
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        product.setCategory("Electronics");
        product.setBrand("Test Brand");
        product.setIsActive(true);
        product.setTenant(tenant);
        
        favoriteProduct = new FavoriteProduct();
        favoriteProduct.setId(1L);
        favoriteProduct.setUser(user);
        favoriteProduct.setProduct(product);
    }
    
    @Test
    void addToFavorites_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(favoriteProductRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(favoriteProductRepository.save(any(FavoriteProduct.class))).thenReturn(favoriteProduct);
        
        favoriteProductService.addToFavorites(1L, 1L);
        
        verify(favoriteProductRepository).save(any(FavoriteProduct.class));
    }
    
    @Test
    void addToFavorites_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> favoriteProductService.addToFavorites(1L, 1L));
    }
    
    @Test
    void addToFavorites_ProductNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> favoriteProductService.addToFavorites(1L, 1L));
    }
    
    @Test
    void addToFavorites_AlreadyExists_ThrowsValidationException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(favoriteProductRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);
        
        assertThrows(ValidationException.class, () -> favoriteProductService.addToFavorites(1L, 1L));
    }
    
    @Test
    void removeFromFavorites_Success() {
        when(favoriteProductRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);
        
        favoriteProductService.removeFromFavorites(1L, 1L);
        
        verify(favoriteProductRepository).deleteByUserIdAndProductId(1L, 1L);
    }
    
    @Test
    void removeFromFavorites_NotExists_ThrowsValidationException() {
        when(favoriteProductRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        
        assertThrows(ValidationException.class, () -> favoriteProductService.removeFromFavorites(1L, 1L));
    }
    
    @Test
    void getFavoriteProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FavoriteProduct> favoritePage = new PageImpl<>(List.of(favoriteProduct));
        when(favoriteProductRepository.findByUserId(1L, pageable)).thenReturn(favoritePage);
        
        Page<ProductDto> result = favoriteProductService.getFavoriteProducts(1L, pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(product.getName(), result.getContent().get(0).getName());
    }
    
    @Test
    void getFavoriteProductsByTenant_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FavoriteProduct> favoritePage = new PageImpl<>(List.of(favoriteProduct));
        when(favoriteProductRepository.findByUserIdAndTenantId(1L, 1L, pageable)).thenReturn(favoritePage);
        
        Page<ProductDto> result = favoriteProductService.getFavoriteProductsByTenant(1L, 1L, pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(product.getName(), result.getContent().get(0).getName());
    }
    
    @Test
    void isFavorite_True() {
        when(favoriteProductRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);
        
        boolean result = favoriteProductService.isFavorite(1L, 1L);
        
        assertTrue(result);
    }
    
    @Test
    void isFavorite_False() {
        when(favoriteProductRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        
        boolean result = favoriteProductService.isFavorite(1L, 1L);
        
        assertFalse(result);
    }
    
    @Test
    void getAllFavoriteProducts_Success() {
        Page<FavoriteProduct> favoritePage = new PageImpl<>(List.of(favoriteProduct));
        when(favoriteProductRepository.findByUserId(1L, Pageable.unpaged())).thenReturn(favoritePage);
        
        List<ProductDto> result = favoriteProductService.getAllFavoriteProducts(1L);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getName(), result.get(0).getName());
    }
}
