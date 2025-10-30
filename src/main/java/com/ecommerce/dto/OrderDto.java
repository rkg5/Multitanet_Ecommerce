package com.ecommerce.dto;

import com.ecommerce.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long id;
    private String orderNumber;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private List<OrderItemDto> orderItems;
}
