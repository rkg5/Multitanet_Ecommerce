package com.ecommerce.service;

import com.ecommerce.dto.TenantDto;
import com.ecommerce.entity.Tenant;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantService {
    
    private final TenantRepository tenantRepository;
    
    public TenantDto createTenant(TenantDto tenantDto) {
        validateTenantCreation(tenantDto);
        
        Tenant tenant = new Tenant();
        tenant.setName(tenantDto.getName());
        tenant.setDomain(tenantDto.getDomain());
        tenant.setDescription(tenantDto.getDescription());
        tenant.setIsActive(tenantDto.getIsActive() != null ? tenantDto.getIsActive() : true);
        
        Tenant savedTenant = tenantRepository.save(tenant);
        return convertToDto(savedTenant);
    }
    
    public TenantDto updateTenant(Long id, TenantDto tenantDto) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));
        
        if (tenantDto.getName() != null && !tenantDto.getName().equals(tenant.getName())) {
            if (tenantRepository.existsByName(tenantDto.getName())) {
                throw new ValidationException("Tenant name already exists: " + tenantDto.getName());
            }
            tenant.setName(tenantDto.getName());
        }
        
        if (tenantDto.getDomain() != null && !tenantDto.getDomain().equals(tenant.getDomain())) {
            if (tenantRepository.existsByDomain(tenantDto.getDomain())) {
                throw new ValidationException("Domain already exists: " + tenantDto.getDomain());
            }
            tenant.setDomain(tenantDto.getDomain());
        }
        
        if (tenantDto.getDescription() != null) {
            tenant.setDescription(tenantDto.getDescription());
        }
        
        if (tenantDto.getIsActive() != null) {
            tenant.setIsActive(tenantDto.getIsActive());
        }
        
        Tenant savedTenant = tenantRepository.save(tenant);
        return convertToDto(savedTenant);
    }
    
    public TenantDto getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));
        return convertToDto(tenant);
    }
    
    public TenantDto getTenantByDomain(String domain) {
        Tenant tenant = tenantRepository.findByDomain(domain)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with domain: " + domain));
        return convertToDto(tenant);
    }
    
    public TenantDto getTenantByName(String name) {
        Tenant tenant = tenantRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + name));
        return convertToDto(tenant);
    }
    
    public Page<TenantDto> getAllTenants(Pageable pageable) {
        Page<Tenant> tenants = tenantRepository.findAll(pageable);
        return tenants.map(this::convertToDto);
    }
    
    public Page<TenantDto> getActiveTenants(Pageable pageable) {
        Page<Tenant> tenants = tenantRepository.findByIsActive(true, pageable);
        return tenants.map(this::convertToDto);
    }
    
    public List<TenantDto> getAllActiveTenants() {
        List<Tenant> tenants = tenantRepository.findActiveTenants();
        return tenants.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public void deleteTenant(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found: " + id);
        }
        tenantRepository.deleteById(id);
    }
    
    private void validateTenantCreation(TenantDto tenantDto) {
        if (tenantRepository.existsByName(tenantDto.getName())) {
            throw new ValidationException("Tenant name already exists: " + tenantDto.getName());
        }
        if (tenantRepository.existsByDomain(tenantDto.getDomain())) {
            throw new ValidationException("Domain already exists: " + tenantDto.getDomain());
        }
    }
    
    private TenantDto convertToDto(Tenant tenant) {
        TenantDto dto = new TenantDto();
        dto.setId(tenant.getId());
        dto.setName(tenant.getName());
        dto.setDomain(tenant.getDomain());
        dto.setDescription(tenant.getDescription());
        dto.setIsActive(tenant.getIsActive());
        return dto;
    }
}
