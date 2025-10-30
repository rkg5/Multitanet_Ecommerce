package com.ecommerce.service;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderItemDto;
import com.ecommerce.dto.OrderRequestDto;
import com.ecommerce.entity.*;
import com.ecommerce.exception.InsufficientQuantityException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;
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
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderItemRepository orderItemRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private OrderService orderService;
    
    private User user;
    private Product product;
    private Order order;
    private OrderRequestDto orderRequestDto;
    
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        product.setIsActive(true);
        
        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-12345678");
        order.setUser(user);
        order.setTotalQuantity(2);
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setStatus(Order.OrderStatus.PENDING);
        
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(1L);
        orderItemDto.setQuantity(2);
        
        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setOrderItems(List.of(orderItemDto));
    }
    
    @Test
    void createOrder_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(new OrderItem());
        
        OrderDto result = orderService.createOrder(orderRequestDto, 1L);
        
        assertNotNull(result);
        assertEquals(order.getOrderNumber(), result.getOrderNumber());
        assertEquals(order.getTotalAmount(), result.getTotalAmount());
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    void createOrder_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderRequestDto, 1L));
    }
    
    @Test
    void createOrder_ProductNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderRequestDto, 1L));
    }
    
    @Test
    void createOrder_InsufficientQuantity_ThrowsInsufficientQuantityException() {
        product.setQuantity(1); // Less than requested quantity (2)
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        assertThrows(InsufficientQuantityException.class, () -> orderService.createOrder(orderRequestDto, 1L));
    }
    
    @Test
    void createOrder_EmptyOrderItems_ThrowsValidationException() {
        orderRequestDto.setOrderItems(List.of());
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        assertThrows(ValidationException.class, () -> orderService.createOrder(orderRequestDto, 1L));
    }
    
    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of());
        
        OrderDto result = orderService.getOrderById(1L, 1L);
        
        assertNotNull(result);
        assertEquals(order.getOrderNumber(), result.getOrderNumber());
    }
    
    @Test
    void getOrderById_NotFound_ThrowsResourceNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L, 1L));
    }
    
    @Test
    void getOrderById_WrongUser_ThrowsValidationException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        assertThrows(ValidationException.class, () -> orderService.getOrderById(1L, 2L));
    }
    
    @Test
    void getOrdersByUser_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findByUserId(1L, pageable)).thenReturn(orderPage);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of());
        
        Page<OrderDto> result = orderService.getOrdersByUser(1L, pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(order.getOrderNumber(), result.getContent().get(0).getOrderNumber());
    }
    
    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of());
        
        OrderDto result = orderService.updateOrderStatus(1L, Order.OrderStatus.CONFIRMED, 1L);
        
        assertNotNull(result);
        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    void cancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        orderService.cancelOrder(1L, 1L);
        
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    void cancelOrder_AlreadyCancelled_ThrowsValidationException() {
        order.setStatus(Order.OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        assertThrows(ValidationException.class, () -> orderService.cancelOrder(1L, 1L));
    }
}
