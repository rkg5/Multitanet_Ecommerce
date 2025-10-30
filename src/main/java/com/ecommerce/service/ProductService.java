package com.ecommerce.service;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductSearchDto;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Tenant;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;
    
    public ProductDto createProduct(ProductDto productDto, Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantId));
        
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setCategory(productDto.getCategory());
        product.setBrand(productDto.getBrand());
        product.setIsActive(productDto.getIsActive() != null ? productDto.getIsActive() : true);
        product.setTenant(tenant);
        
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }
    
    public ProductDto updateProduct(Long id, ProductDto productDto, Long tenantId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        
        if (!product.getTenant().getId().equals(tenantId)) {
            throw new ValidationException("Product does not belong to this tenant");
        }
        
        if (productDto.getName() != null) {
            product.setName(productDto.getName());
        }
        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }
        if (productDto.getPrice() != null) {
            product.setPrice(productDto.getPrice());
        }
        if (productDto.getQuantity() != null) {
            product.setQuantity(productDto.getQuantity());
        }
        if (productDto.getCategory() != null) {
            product.setCategory(productDto.getCategory());
        }
        if (productDto.getBrand() != null) {
            product.setBrand(productDto.getBrand());
        }
        if (productDto.getIsActive() != null) {
            product.setIsActive(productDto.getIsActive());
        }
        
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }
    
    public ProductDto getProductById(Long id, Long tenantId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        
        if (!product.getTenant().getId().equals(tenantId)) {
            throw new ValidationException("Product does not belong to this tenant");
        }
        
        return convertToDto(product);
    }
    
    public Page<ProductDto> getProductsByTenant(Long tenantId, Pageable pageable) {
        Page<Product> products = productRepository.findByTenantIdAndIsActive(tenantId, pageable);
        return products.map(this::convertToDto);
    }
    
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findActiveProducts(pageable);
        return products.map(this::convertToDto);
    }
    
    public Page<ProductDto> searchProducts(ProductSearchDto searchDto, Long tenantId) {
        Pageable pageable = createPageable(searchDto);
        
        if (tenantId != null) {
            if (searchDto.getName() != null && !searchDto.getName().trim().isEmpty()) {
                return productRepository.findByTenantIdAndNameContaining(tenantId, searchDto.getName(), pageable)
                        .map(this::convertToDto);
            } else if (searchDto.getCategory() != null && !searchDto.getCategory().trim().isEmpty()) {
                return productRepository.findByTenantIdAndCategory(tenantId, searchDto.getCategory(), pageable)
                        .map(this::convertToDto);
            } else {
                return productRepository.findByTenantIdAndIsActive(tenantId, pageable)
                        .map(this::convertToDto);
            }
        } else {
            if (searchDto.getName() != null && !searchDto.getName().trim().isEmpty()) {
                return productRepository.findByNameContaining(searchDto.getName(), pageable)
                        .map(this::convertToDto);
            } else if (searchDto.getCategory() != null && !searchDto.getCategory().trim().isEmpty()) {
                return productRepository.findByCategory(searchDto.getCategory(), pageable)
                        .map(this::convertToDto);
            } else if (searchDto.getBrand() != null && !searchDto.getBrand().trim().isEmpty()) {
                return productRepository.findByBrand(searchDto.getBrand(), pageable)
                        .map(this::convertToDto);
            } else {
                return productRepository.findActiveProducts(pageable)
                        .map(this::convertToDto);
            }
        }
    }
    
    public List<String> getCategories(Long tenantId) {
        if (tenantId != null) {
            return productRepository.findDistinctCategoriesByTenantId(tenantId);
        } else {
            return productRepository.findDistinctCategories();
        }
    }
    
    public List<String> getBrands(Long tenantId) {
        if (tenantId != null) {
            return productRepository.findDistinctBrandsByTenantId(tenantId);
        } else {
            return productRepository.findDistinctBrands();
        }
    }
    
    public void deleteProduct(Long id, Long tenantId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        
        if (!product.getTenant().getId().equals(tenantId)) {
            throw new ValidationException("Product does not belong to this tenant");
        }
        
        productRepository.deleteById(id);
    }
    
    public ProductDto updateProductQuantity(Long id, Integer quantity, Long tenantId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        
        if (!product.getTenant().getId().equals(tenantId)) {
            throw new ValidationException("Product does not belong to this tenant");
        }
        
        if (quantity < 0) {
            throw new ValidationException("Quantity cannot be negative");
        }
        
        product.setQuantity(quantity);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }
    
    private Pageable createPageable(ProductSearchDto searchDto) {
        Sort sort = Sort.by(Sort.Direction.fromString(searchDto.getSortDirection()), searchDto.getSortBy());
        return PageRequest.of(searchDto.getPage(), searchDto.getSize(), sort);
    }
    
    private ProductDto convertToDto(Product product) {
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
