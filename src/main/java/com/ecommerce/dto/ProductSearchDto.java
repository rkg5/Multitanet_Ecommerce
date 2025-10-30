package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDto {
    
    private String name;
    private String category;
    private String brand;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "name";
    private String sortDirection = "ASC";
}
