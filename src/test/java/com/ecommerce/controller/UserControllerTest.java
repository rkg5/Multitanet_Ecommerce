package com.ecommerce.controller;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderRequestDto;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.FavoriteProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @MockBean
    private OrderService orderService;
    
    @MockBean
    private FavoriteProductService favoriteProductService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(roles = "USER")
    void getAllProducts_Success() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setPrice(new BigDecimal("99.99"));
        productDto.setQuantity(10);
        
        Page<ProductDto> productPage = new PageImpl<>(List.of(productDto));
        when(productService.getAllProducts(any())).thenReturn(productPage);
        
        mockMvc.perform(get("/api/user/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createOrder_Success() throws Exception {
        OrderRequestDto orderRequest = new OrderRequestDto();
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setOrderNumber("ORD-12345678");
        orderDto.setTotalAmount(new BigDecimal("199.98"));
        orderDto.setStatus(Order.OrderStatus.PENDING);
        
        when(orderService.createOrder(any(OrderRequestDto.class), anyLong())).thenReturn(orderDto);
        
        mockMvc.perform(post("/api/user/orders?userId=1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("ORD-12345678"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getUserOrders_Success() throws Exception {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setOrderNumber("ORD-12345678");
        orderDto.setTotalAmount(new BigDecimal("199.98"));
        
        Page<OrderDto> orderPage = new PageImpl<>(List.of(orderDto));
        when(orderService.getOrdersByUser(1L, any())).thenReturn(orderPage);
        
        mockMvc.perform(get("/api/user/orders?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD-12345678"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void addToFavorites_Success() throws Exception {
        mockMvc.perform(post("/api/user/favorites/1?userId=1")
                .with(csrf()))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void removeFromFavorites_Success() throws Exception {
        mockMvc.perform(delete("/api/user/favorites/1?userId=1")
                .with(csrf()))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getFavoriteProducts_Success() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Favorite Product");
        productDto.setPrice(new BigDecimal("99.99"));
        
        Page<ProductDto> productPage = new PageImpl<>(List.of(productDto));
        when(favoriteProductService.getFavoriteProducts(1L, any())).thenReturn(productPage);
        
        mockMvc.perform(get("/api/user/favorites?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Favorite Product"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void isFavorite_Success() throws Exception {
        when(favoriteProductService.isFavorite(1L, 1L)).thenReturn(true);
        
        mockMvc.perform(get("/api/user/favorites/check/1?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
