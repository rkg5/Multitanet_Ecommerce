package com.ecommerce.service;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderItemDto;
import com.ecommerce.dto.OrderRequestDto;
import com.ecommerce.entity.*;
import com.ecommerce.exception.InsufficientQuantityException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public OrderDto createOrder(OrderRequestDto orderRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        
        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new ValidationException("Order must contain at least one item");
        }
        
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;
        
        for (OrderItemDto itemDto : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemDto.getProductId()));
            
            if (!product.getIsActive()) {
                throw new ValidationException("Product is not available: " + product.getName());
            }
            
            if (product.getQuantity() < itemDto.getQuantity()) {
                throw new InsufficientQuantityException("Insufficient quantity for product: " + product.getName() + 
                        ". Available: " + product.getQuantity() + ", Requested: " + itemDto.getQuantity());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            totalQuantity += itemDto.getQuantity();
            
            // Update product quantity
            product.setQuantity(product.getQuantity() - itemDto.getQuantity());
            productRepository.save(product);
        }
        
        order.setTotalAmount(totalAmount);
        order.setTotalQuantity(totalQuantity);
        
        Order savedOrder = orderRepository.save(order);
        
        // Save order items
        for (OrderItemDto itemDto : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).orElseThrow();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            orderItemRepository.save(orderItem);
        }
        
        return convertToDto(savedOrder);
    }
    
    public OrderDto getOrderById(Long id, Long userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new ValidationException("Order does not belong to this user");
        }
        
        return convertToDto(order);
    }
    
    public Page<OrderDto> getOrdersByUser(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::convertToDto);
    }
    
    public Page<OrderDto> getOrdersByTenant(Long tenantId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByTenantId(tenantId, pageable);
        return orders.map(this::convertToDto);
    }
    
    public OrderDto updateOrderStatus(Long id, Order.OrderStatus status, Long userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new ValidationException("Order does not belong to this user");
        }
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }
    
    public void cancelOrder(Long id, Long userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new ValidationException("Order does not belong to this user");
        }
        
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new ValidationException("Order is already cancelled");
        }
        
        // Restore product quantities
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotalQuantity(order.getTotalQuantity());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser().getId());
        dto.setUsername(order.getUser().getUsername());
        dto.setCreatedAt(order.getCreatedAt());
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDto> orderItemDtos = orderItems.stream()
                .map(this::convertOrderItemToDto)
                .collect(Collectors.toList());
        dto.setOrderItems(orderItemDtos);
        
        return dto;
    }
    
    private OrderItemDto convertOrderItemToDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setTotalPrice(orderItem.getTotalPrice());
        return dto;
    }
}
