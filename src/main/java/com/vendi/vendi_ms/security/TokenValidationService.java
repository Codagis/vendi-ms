package com.vendi.vendi_ms.security;

import com.vendi.vendi_ms.model.Usuario;
import com.vendi.vendi_ms.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para validação de integridade de tokens e dados do usuário.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidationService {

    private final UsuarioRepository usuarioRepository;
    
    
    private final Map<String, ValidationResult> validationCache = new HashMap<>();
    private static final long CACHE_DURATION_MINUTES = 5;

    @Transactional(readOnly = true)
    public ValidationResult validateTokenIntegrity(String token, Long userId, Boolean rootStatus) {
        String cacheKey = token + "_" + userId + "_" + rootStatus;
        
        
        ValidationResult cached = validationCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached;
        }

        try {
            
            Usuario usuario = usuarioRepository.findByIdWithPerfilAndPermissoes(userId)
                    .orElse(null);

            if (usuario == null) {
                log.warn("TENTATIVA DE VALIDAÇÃO COM USUÁRIO INEXISTENTE - ID: {}", userId);
                return createInvalidResult("Usuário não encontrado");
            }

            
            if (!usuario.getAtivo()) {
                log.warn("TENTATIVA DE VALIDAÇÃO COM USUÁRIO INATIVO - ID: {}", userId);
                return createInvalidResult("Usuário inativo");
            }

            
            if (usuario.getContaBloqueada()) {
                log.warn("TENTATIVA DE VALIDAÇÃO COM CONTA BLOQUEADA - ID: {}", userId);
                return createInvalidResult("Conta bloqueada");
            }

            
            if (rootStatus != null && !rootStatus.equals(usuario.getRoot())) {
                log.warn("TENTATIVA DE MANIPULAÇÃO DE STATUS ROOT - ID: {} - Enviado: {} - Real: {}", 
                        userId, rootStatus, usuario.getRoot());
                return createInvalidResult("Manipulação de dados detectada");
            }

            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && !usuario.getUsername().equals(auth.getName())) {
                log.warn("TENTATIVA DE USO DE TOKEN DE OUTRO USUÁRIO - ID: {} - Username: {} - Auth: {}", 
                        userId, usuario.getUsername(), auth.getName());
                return createInvalidResult("Token não pertence ao usuário");
            }

            
            ValidationResult result = ValidationResult.builder()
                    .isValid(true)
                    .usuario(usuario)
                    .timestamp(LocalDateTime.now())
                    .build();

            
            validationCache.put(cacheKey, result);
            
            log.debug("VALIDAÇÃO DE TOKEN BEM-SUCEDIDA - ID: {}", userId);
            return result;

        } catch (Exception e) {
            log.error("ERRO NA VALIDAÇÃO DE TOKEN - ID: {} - Erro: {}", userId, e.getMessage());
            return createInvalidResult("Erro interno de validação");
        }
    }

    @Transactional(readOnly = true)
    public boolean validateUserPermissions(Long userId, String requiredPermission) {
        try {
            Usuario usuario = usuarioRepository.findByIdWithPerfilAndPermissoes(userId)
                    .orElse(null);

            if (usuario == null || !usuario.getAtivo() || usuario.getContaBloqueada()) {
                return false;
            }

            
            if (usuario.isRoot()) {
                return true;
            }

            
            return usuario.possuiPermissao(requiredPermission);

        } catch (Exception e) {
            log.error("ERRO NA VALIDAÇÃO DE PERMISSÕES - ID: {} - Permissão: {} - Erro: {}", 
                    userId, requiredPermission, e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public boolean validateModuleAccess(Long userId, String module) {
        try {
            Usuario usuario = usuarioRepository.findByIdWithPerfilAndPermissoes(userId)
                    .orElse(null);

            if (usuario == null || !usuario.getAtivo() || usuario.getContaBloqueada()) {
                return false;
            }

            
            if (usuario.isRoot()) {
                return true;
            }

            
            String requiredPermission = getRequiredPermissionForModule(module);
            if (requiredPermission == null) {
                return true;
            }

            return usuario.possuiPermissao(requiredPermission);

        } catch (Exception e) {
            log.error("ERRO NA VALIDAÇÃO DE ACESSO AO MÓDULO - ID: {} - Módulo: {} - Erro: {}", 
                    userId, module, e.getMessage());
            return false;
        }
    }

    private String getRequiredPermissionForModule(String module) {
        switch (module) {
            case "dashboard": return "dashboard";
            case "pos": return "pos";
            case "products": return "products";
            case "customers": return "customers";
            case "inventory": return "inventory";
            case "financial": return "financial";
            case "reports": return "reports";
            case "users": return "users";
            case "perfis": return "perfis";
            case "permissoes": return "permissoes";
            case "settings": return "settings";
            default: return null;
        }
    }

    private ValidationResult createInvalidResult(String reason) {
        return ValidationResult.builder()
                .isValid(false)
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public void clearCache() {
        validationCache.clear();
        log.debug("Cache de validações limpo");
    }

    @lombok.Data
    @lombok.Builder
    public static class ValidationResult {
        private boolean isValid;
        private String reason;
        private Usuario usuario;
        private LocalDateTime timestamp;

        public boolean isExpired() {
            return timestamp.isBefore(LocalDateTime.now().minusMinutes(CACHE_DURATION_MINUTES));
        }
    }
}
