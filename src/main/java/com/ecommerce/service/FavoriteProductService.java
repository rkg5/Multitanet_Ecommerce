package com.ecommerce.service;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.FavoriteProduct;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.repository.FavoriteProductRepository;
import com.ecommerce.repository.ProductRepository;
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
public class FavoriteProductService {
    
    private final FavoriteProductRepository favoriteProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public void addToFavorites(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        
        if (favoriteProductRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ValidationException("Product is already in favorites");
        }
        
        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setUser(user);
        favoriteProduct.setProduct(product);
        
        favoriteProductRepository.save(favoriteProduct);
    }
    
    public void removeFromFavorites(Long userId, Long productId) {
        if (!favoriteProductRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ValidationException("Product is not in favorites");
        }
        
        favoriteProductRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    public Page<ProductDto> getFavoriteProducts(Long userId, Pageable pageable) {
        Page<FavoriteProduct> favoriteProducts = favoriteProductRepository.findByUserId(userId, pageable);
        return favoriteProducts.map(fp -> convertProductToDto(fp.getProduct()));
    }
    
    public Page<ProductDto> getFavoriteProductsByTenant(Long userId, Long tenantId, Pageable pageable) {
        Page<FavoriteProduct> favoriteProducts = favoriteProductRepository.findByUserIdAndTenantId(userId, tenantId, pageable);
        return favoriteProducts.map(fp -> convertProductToDto(fp.getProduct()));
    }
    
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteProductRepository.existsByUserIdAndProductId(userId, productId);
    }
    
    public List<ProductDto> getAllFavoriteProducts(Long userId) {
        List<FavoriteProduct> favoriteProducts = favoriteProductRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        return favoriteProducts.stream()
                .map(fp -> convertProductToDto(fp.getProduct()))
                .collect(Collectors.toList());
    }
    
    private ProductDto convertProductToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setIsActive(product.getIsActive());
        dto.setTenantId(product.getTenant().getId());
        dto.setTenantName(product.getTenant().getName());
        return dto;
    }
}
