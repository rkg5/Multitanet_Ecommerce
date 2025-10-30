package com.ecommerce.controller;

import com.ecommerce.config.TenantContext;
import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderRequestDto;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductSearchDto;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.FavoriteProductService;
import com.ecommerce.service.TenantService;
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
@RequestMapping("/{tenant}")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TENANT', 'USER')")
public class MultiTenantController {
    
    private final ProductService productService;
    private final OrderService orderService;
    private final FavoriteProductService favoriteProductService;
    private final TenantService tenantService;
    
    // Tenant-specific product browsing
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getTenantProducts(Pageable pageable) {
        String tenantDomain = TenantContext.getCurrentTenant();
        Long tenantId = tenantService.getTenantByDomain(tenantDomain).getId();
        Page<ProductDto> products = productService.getProductsByTenant(tenantId, pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductDto>> searchTenantProducts(ProductSearchDto searchDto) {
        String tenantDomain = TenantContext.getCurrentTenant();
        Long tenantId = tenantService.getTenantByDomain(tenantDomain).getId();
        Page<ProductDto> products = productService.searchProducts(searchDto, tenantId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/categories")
    public ResponseEntity<List<String>> getTenantCategories() {
        String tenantDomain = TenantContext.getCurrentTenant();
        Long tenantId = tenantService.getTenantByDomain(tenantDomain).getId();
        List<String> categories = productService.getCategories(tenantId);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/products/brands")
    public ResponseEntity<List<String>> getTenantBrands() {
        String tenantDomain = TenantContext.getCurrentTenant();
        Long tenantId = tenantService.getTenantByDomain(tenantDomain).getId();
        List<String> brands = productService.getBrands(tenantId);
        return ResponseEntity.ok(brands);
    }
    
    // Tenant-specific order management
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createTenantOrder(@Valid @RequestBody OrderRequestDto orderRequest,
                                                     @RequestParam Long userId) {
        OrderDto createdOrder = orderService.createOrder(orderRequest, userId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
    
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDto>> getTenantOrders(@RequestParam Long userId, Pageable pageable) {
        Page<OrderDto> orders = orderService.getOrdersByUser(userId, pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getTenantOrderById(@PathVariable Long id, @RequestParam Long userId) {
        OrderDto order = orderService.getOrderById(id, userId);
        return ResponseEntity.ok(order);
    }
    
    // Tenant-specific favorite products
    @PostMapping("/favorites/{productId}")
    public ResponseEntity<Void> addToTenantFavorites(@PathVariable Long productId, @RequestParam Long userId) {
        favoriteProductService.addToFavorites(userId, productId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<Void> removeFromTenantFavorites(@PathVariable Long productId, @RequestParam Long userId) {
        favoriteProductService.removeFromFavorites(userId, productId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<Page<ProductDto>> getTenantFavoriteProducts(@RequestParam Long userId, Pageable pageable) {
        String tenantDomain = TenantContext.getCurrentTenant();
        Long tenantId = tenantService.getTenantByDomain(tenantDomain).getId();
        Page<ProductDto> favorites = favoriteProductService.getFavoriteProductsByTenant(userId, tenantId, pageable);
        return ResponseEntity.ok(favorites);
    }
}
