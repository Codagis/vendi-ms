package com.vendi.vendi_ms.controller;

import com.vendi.vendi_ms.security.TokenValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para validações de segurança.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SecurityController {

    private final TokenValidationService tokenValidationService;

    @PostMapping("/validate-integrity")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> validateIntegrity(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean rootStatus) {
        
        log.info("Validação de integridade solicitada para usuário: {}", userId);
        
        String token = getTokenFromRequest();
        
        TokenValidationService.ValidationResult result = tokenValidationService
                .validateTokenIntegrity(token, userId, rootStatus);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", result.isValid());
        response.put("reason", result.getReason());
        response.put("timestamp", result.getTimestamp());
        
        if (result.isValid() && result.getUsuario() != null) {
            response.put("userData", Map.of(
                "id", result.getUsuario().getId(),
                "username", result.getUsuario().getUsername(),
                "root", result.getUsuario().getRoot(),
                "ativo", result.getUsuario().getAtivo(),
                "permissions", result.getUsuario().getPerfil().getPermissoes().stream()
                    .map(p -> p.getChave())
                    .toList()
            ));
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-permission")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> validatePermission(
            @RequestParam Long userId,
            @RequestParam String permission) {
        
        log.info("Validação de permissão solicitada - Usuário: {} - Permissão: {}", userId, permission);
        
        boolean hasPermission = tokenValidationService.validateUserPermissions(userId, permission);
        
        Map<String, Object> response = new HashMap<>();
        response.put("hasPermission", hasPermission);
        response.put("userId", userId);
        response.put("permission", permission);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-module-access")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> validateModuleAccess(
            @RequestParam Long userId,
            @RequestParam String module) {
        
        log.info("Validação de acesso ao módulo solicitada - Usuário: {} - Módulo: {}", userId, module);
        
        boolean canAccess = tokenValidationService.validateModuleAccess(userId, module);
        
        Map<String, Object> response = new HashMap<>();
        response.put("canAccess", canAccess);
        response.put("userId", userId);
        response.put("module", module);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-data/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getUserData(@PathVariable Long userId) {
        log.info("Solicitação de dados do usuário: {}", userId);
        
        String token = getTokenFromRequest();
        
        TokenValidationService.ValidationResult result = tokenValidationService
                .validateTokenIntegrity(token, userId, null);
        
        if (!result.isValid()) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Acesso negado",
                "reason", result.getReason()
            ));
        }
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", result.getUsuario().getId());
        userData.put("username", result.getUsuario().getUsername());
        userData.put("nome", result.getUsuario().getNome());
        userData.put("email", result.getUsuario().getEmail());
        userData.put("root", result.getUsuario().getRoot());
        userData.put("ativo", result.getUsuario().getAtivo());
        userData.put("permissions", result.getUsuario().getPerfil().getPermissoes().stream()
            .map(p -> p.getChave())
            .toList());
        
        return ResponseEntity.ok(userData);
    }

    private String getTokenFromRequest() {
        return null;
    }
}
