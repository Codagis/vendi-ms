package com.vendi.vendi_ms.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * Middleware simplificado de segurança - apenas validação básica de token JWT.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityMiddleware implements HandlerInterceptor {

    
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/auth/validate",
        "/actuator/health",
        "/swagger-ui",
        "/v3/api-docs"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("Interceptando requisição: {} {}", method, requestURI);

        
        if (isPublicEndpoint(requestURI)) {
            log.debug("Endpoint público, pulando validação: {}", requestURI);
            return true;
        }

        
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("ACESSO NEGADO - Token não fornecido: {} {}", method, requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        
        log.debug("ACESSO AUTORIZADO - {} {} - IP: {}", 
                method, requestURI, getClientIP(request));

        return true;
    }

    private boolean isPublicEndpoint(String requestURI) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(endpoint -> requestURI.startsWith(endpoint));
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();
        
        
        if (status >= 400) {
            log.warn("REQUISIÇÃO FINALIZADA COM ERRO - {} {} - Status: {} - IP: {}", 
                    method, requestURI, status, getClientIP(request));
        }
    }
}