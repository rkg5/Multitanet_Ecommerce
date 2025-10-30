package com.ecommerce.config;

import com.ecommerce.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantService tenantService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();

        // Extract tenant from URL pattern /{tenant}/...
        String[] pathSegments = requestURI.split("/");
        if (pathSegments.length > 1) {
            String potentialTenant = pathSegments[1];

            // Skip for API endpoints that don't follow tenant pattern
            if (!potentialTenant.startsWith("api") && !potentialTenant.startsWith("h2-console") &&
                !potentialTenant.startsWith("actuator")) {
                try {
                    // Validate tenant exists
                    tenantService.getTenantByDomain(potentialTenant);
                    TenantContext.setCurrentTenant(potentialTenant);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
