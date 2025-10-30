package com.ecommerce.controller;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductSearchDto;
import com.ecommerce.dto.UserDto;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TENANT')")
public class TenantController {
    
    private final ProductService productService;
    private final UserService userService;
    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto, 
                                                   @RequestParam Long tenantId) {
        ProductDto createdProduct = productService.createProduct(productDto, tenantId);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }
    
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getProducts(@RequestParam Long tenantId, Pageable pageable) {
        Page<ProductDto> products = productService.getProductsByTenant(tenantId, pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id, @RequestParam Long tenantId) {
        ProductDto product = productService.getProductById(id, tenantId);
        return ResponseEntity.ok(product);
    }
    
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, 
                                                   @Valid @RequestBody ProductDto productDto,
                                                   @RequestParam Long tenantId) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto, tenantId);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @RequestParam Long tenantId) {
        productService.deleteProduct(id, tenantId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/products/{id}/quantity")
    public ResponseEntity<ProductDto> updateProductQuantity(@PathVariable Long id, 
                                                          @RequestParam Integer quantity,
                                                          @RequestParam Long tenantId) {
        ProductDto updatedProduct = productService.updateProductQuantity(id, quantity, tenantId);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(@RequestParam Long tenantId,
                                                          ProductSearchDto searchDto) {
        Page<ProductDto> products = productService.searchProducts(searchDto, tenantId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/categories")
    public ResponseEntity<List<String>> getCategories(@RequestParam Long tenantId) {
        List<String> categories = productService.getCategories(tenantId);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/products/brands")
    public ResponseEntity<List<String>> getBrands(@RequestParam Long tenantId) {
        List<String> brands = productService.getBrands(tenantId);
        return ResponseEntity.ok(brands);
    }
    
    // Tenant User Management
    @PostMapping("/users")
    public ResponseEntity<UserDto> createTenantUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getTenantUsers(@RequestParam Long tenantId, Pageable pageable) {
        Page<UserDto> users = userService.getUsersByTenant(tenantId, pageable);
        return ResponseEntity.ok(users);
    }
}
