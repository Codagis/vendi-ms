package com.vendi.vendi_ms.security;

import com.vendi.vendi_ms.model.Usuario;
import com.vendi.vendi_ms.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Serviço responsável pela autorização de usuários.
 * Implementa validações robustas de permissões e roles.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public boolean hasPermission(String permission) {
        Usuario usuario = getCurrentUser();
        if (usuario == null) {
            log.warn("Tentativa de verificação de permissão sem usuário autenticado");
            return false;
        }

        if (usuario.isRoot()) {
            log.debug("Usuário root {} tem acesso total", usuario.getUsername());
            return true;
        }
        boolean hasPermission = usuario.possuiPermissao(permission);
        log.debug("Usuário {} tem permissão {}: {}", usuario.getUsername(), permission, hasPermission);
        
        return hasPermission;
    }

    @Transactional(readOnly = true)
    public boolean hasAnyPermission(String... permissions) {
        Usuario usuario = getCurrentUser();
        if (usuario == null) {
            log.warn("Tentativa de verificação de permissões sem usuário autenticado");
            return false;
        }

        if (usuario.isRoot()) {
            log.debug("Usuário root {} tem acesso total", usuario.getUsername());
            return true;
        }
        boolean hasAny = Arrays.stream(permissions)
                .anyMatch(usuario::possuiPermissao);
        
        log.debug("Usuário {} tem alguma das permissões {}: {}", 
                usuario.getUsername(), Arrays.toString(permissions), hasAny);
        
        return hasAny;
    }

    @Transactional(readOnly = true)
    public boolean hasAllPermissions(String... permissions) {
        Usuario usuario = getCurrentUser();
        if (usuario == null) {
            log.warn("Tentativa de verificação de permissões sem usuário autenticado");
            return false;
        }

        if (usuario.isRoot()) {
            log.debug("Usuário root {} tem acesso total", usuario.getUsername());
            return true;
        }
        boolean hasAll = Arrays.stream(permissions)
                .allMatch(usuario::possuiPermissao);
        
        log.debug("Usuário {} tem todas as permissões {}: {}", 
                usuario.getUsername(), Arrays.toString(permissions), hasAll);
        
        return hasAll;
    }

    @Transactional(readOnly = true)
    public boolean hasRole(String role) {
        Usuario usuario = getCurrentUser();
        if (usuario == null) {
            log.warn("Tentativa de verificação de role sem usuário autenticado");
            return false;
        }

        if (usuario.isRoot()) {
            log.debug("Usuário root {} tem acesso total", usuario.getUsername());
            return true;
        }
        boolean hasRole = usuario.getPerfil() != null && 
                         role.equals(usuario.getPerfil().getCodigo());
        
        log.debug("Usuário {} tem role {}: {}", usuario.getUsername(), role, hasRole);
        
        return hasRole;
    }

    @Transactional(readOnly = true)
    public boolean hasAnyRole(String... roles) {
        Usuario usuario = getCurrentUser();
        if (usuario == null) {
            log.warn("Tentativa de verificação de roles sem usuário autenticado");
            return false;
        }

        if (usuario.isRoot()) {
            log.debug("Usuário root {} tem acesso total", usuario.getUsername());
            return true;
        }
        boolean hasAny = usuario.getPerfil() != null && 
                        Arrays.stream(roles)
                                .anyMatch(role -> role.equals(usuario.getPerfil().getCodigo()));
        
        log.debug("Usuário {} tem alguma das roles {}: {}", 
                usuario.getUsername(), Arrays.toString(roles), hasAny);
        
        return hasAny;
    }

    @Transactional(readOnly = true)
    public boolean hasAllRoles(String... roles) {
        Usuario usuario = getCurrentUser();
        if (usuario == null) {
            log.warn("Tentativa de verificação de roles sem usuário autenticado");
            return false;
        }

        if (usuario.isRoot()) {
            log.debug("Usuário root {} tem acesso total", usuario.getUsername());
            return true;
        }
        boolean hasAll = usuario.getPerfil() != null && 
                        Arrays.stream(roles)
                                .allMatch(role -> role.equals(usuario.getPerfil().getCodigo()));
        
        log.debug("Usuário {} tem todas as roles {}: {}", 
                usuario.getUsername(), Arrays.toString(roles), hasAll);
        
        return hasAll;
    }

    @Transactional(readOnly = true)
    public boolean isRoot() {
        Usuario usuario = getCurrentUser();
        if (usuario == null) {
            log.warn("Tentativa de verificação de root sem usuário autenticado");
            return false;
        }

        boolean isRoot = usuario.isRoot();
        log.debug("Usuário {} é root: {}", usuario.getUsername(), isRoot);
        
        return isRoot;
    }

    @Transactional(readOnly = true)
    public boolean hasSystemAccess() {
        Usuario usuario = getCurrentUser();
        if (usuario == null) {
            log.warn("Tentativa de verificação de acesso ao sistema sem usuário autenticado");
            return false;
        }

        if (usuario.isRoot()) {
            return true;
        }
        boolean hasSystemAccess = hasAnyPermission("all", "users", "perfis", "permissoes", "settings");
        
        log.debug("Usuário {} tem acesso ao sistema: {}", usuario.getUsername(), hasSystemAccess);
        
        return hasSystemAccess;
    }

    private Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Nenhum usuário autenticado encontrado no contexto de segurança");
            return null;
        }

        String username = authentication.getName();
        if (username == null || username.trim().isEmpty()) {
            log.warn("Username nulo ou vazio no contexto de segurança");
            return null;
        }

        try {
            return usuarioRepository.findByUsernameWithPerfil(username)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário {} no banco de dados: {}", username, e.getMessage());
            return null;
        }
    }

    public void logAccessDenied(String resource, String action) {
        Usuario usuario = getCurrentUser();
        String username = usuario != null ? usuario.getUsername() : "ANONYMOUS";
        
        log.warn("ACESSO NEGADO - Usuário: {}, Recurso: {}, Ação: {}, IP: {}", 
                username, resource, action, getClientIP());
    }

    private String getClientIP() {
        return "UNKNOWN";
    }
}
