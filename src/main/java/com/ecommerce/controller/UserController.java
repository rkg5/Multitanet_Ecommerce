package com.ecommerce.controller;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderRequestDto;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductSearchDto;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.FavoriteProductService;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TENANT', 'USER')")
public class UserController {
    
    private final ProductService productService;
    private final OrderService orderService;
    private final FavoriteProductService favoriteProductService;

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getAllProducts(Pageable pageable) {
        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(ProductSearchDto searchDto) {
        Page<ProductDto> products = productService.searchProducts(searchDto, null);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = productService.getCategories(null);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/products/brands")
    public ResponseEntity<List<String>> getBrands() {
        List<String> brands = productService.getBrands(null);
        return ResponseEntity.ok(brands);
    }
    
    // Order Management
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest,
                                               @RequestParam Long userId) {
        OrderDto createdOrder = orderService.createOrder(orderRequest, userId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
    
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDto>> getUserOrders(@RequestParam Long userId, Pageable pageable) {
        Page<OrderDto> orders = orderService.getOrdersByUser(userId, pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id, @RequestParam Long userId) {
        OrderDto order = orderService.getOrderById(id, userId);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id,
                                                    @RequestParam String status,
                                                    @RequestParam Long userId) {
        OrderDto updatedOrder = orderService.updateOrderStatus(id, 
                com.ecommerce.entity.Order.OrderStatus.valueOf(status.toUpperCase()), userId);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id, @RequestParam Long userId) {
        orderService.cancelOrder(id, userId);
        return ResponseEntity.ok().build();
    }
    
    // Favorite Products
    @PostMapping("/favorites/{productId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long productId, @RequestParam Long userId) {
        favoriteProductService.addToFavorites(userId, productId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long productId, @RequestParam Long userId) {
        favoriteProductService.removeFromFavorites(userId, productId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<Page<ProductDto>> getFavoriteProducts(@RequestParam Long userId, Pageable pageable) {
        Page<ProductDto> favorites = favoriteProductService.getFavoriteProducts(userId, pageable);
        return ResponseEntity.ok(favorites);
    }
    
    @GetMapping("/favorites/check/{productId}")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long productId, @RequestParam Long userId) {
        boolean isFavorite = favoriteProductService.isFavorite(userId, productId);
        return ResponseEntity.ok(isFavorite);
    }
}
