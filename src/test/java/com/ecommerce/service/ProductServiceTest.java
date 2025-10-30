package com.ecommerce.service;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductSearchDto;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Tenant;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.TenantRepository;
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
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private TenantRepository tenantRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product product;
    private ProductDto productDto;
    private Tenant tenant;
    
    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Test Tenant");
        tenant.setDomain("test");
        tenant.setIsActive(true);
        
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
        
        productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setDescription("Test Description");
        productDto.setPrice(new BigDecimal("99.99"));
        productDto.setQuantity(10);
        productDto.setCategory("Electronics");
        productDto.setBrand("Test Brand");
        productDto.setIsActive(true);
    }
    
    @Test
    void createProduct_Success() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductDto result = productService.createProduct(productDto, 1L);
        
        assertNotNull(result);
        assertEquals(productDto.getName(), result.getName());
        assertEquals(productDto.getPrice(), result.getPrice());
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void createProduct_TenantNotFound_ThrowsResourceNotFoundException() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productDto, 1L));
    }
    
    @Test
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        ProductDto result = productService.getProductById(1L, 1L);
        
        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getPrice(), result.getPrice());
    }
    
    @Test
    void getProductById_NotFound_ThrowsResourceNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L, 1L));
    }
    
    @Test
    void getProductById_WrongTenant_ThrowsValidationException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        assertThrows(ValidationException.class, () -> productService.getProductById(1L, 2L));
    }
    
    @Test
    void updateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductDto result = productService.updateProduct(1L, productDto, 1L);
        
        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void updateProduct_WrongTenant_ThrowsValidationException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        assertThrows(ValidationException.class, () -> productService.updateProduct(1L, productDto, 2L));
    }
    
    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        productService.deleteProduct(1L, 1L);
        
        verify(productRepository).deleteById(1L);
    }
    
    @Test
    void deleteProduct_WrongTenant_ThrowsValidationException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        assertThrows(ValidationException.class, () -> productService.deleteProduct(1L, 2L));
    }
    
    @Test
    void updateProductQuantity_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductDto result = productService.updateProductQuantity(1L, 20, 1L);
        
        assertNotNull(result);
        assertEquals(20, product.getQuantity());
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void updateProductQuantity_NegativeQuantity_ThrowsValidationException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        assertThrows(ValidationException.class, () -> productService.updateProductQuantity(1L, -5, 1L));
    }
    
    @Test
    void searchProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Product> productPage = new PageImpl<>(List.of(product));
        ProductSearchDto searchDto = new ProductSearchDto();
        searchDto.setName("Test");
        searchDto.setPage(0);
        searchDto.setSize(10);
        searchDto.setSortBy("name");
        searchDto.setSortDirection("ASC");
        
        when(productRepository.findByTenantIdAndNameContaining(1L, "Test", pageable)).thenReturn(productPage);
        
        Page<ProductDto> result = productService.searchProducts(searchDto, 1L);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(product.getName(), result.getContent().get(0).getName());
    }
    
    @Test
    void getCategories_Success() {
        List<String> categories = List.of("Electronics", "Clothing");
        when(productRepository.findDistinctCategoriesByTenantId(1L)).thenReturn(categories);
        
        List<String> result = productService.getCategories(1L);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Electronics"));
        assertTrue(result.contains("Clothing"));
    }
}
