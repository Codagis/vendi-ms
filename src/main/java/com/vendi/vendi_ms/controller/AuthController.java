package com.vendi.vendi_ms.controller;

import com.vendi.vendi_ms.dto.LoginRequest;
import com.vendi.vendi_ms.dto.LoginResponse;
import com.vendi.vendi_ms.dto.RefreshTokenRequest;
import com.vendi.vendi_ms.dto.RefreshTokenResponse;
import com.vendi.vendi_ms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelos endpoints de autenticação.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Tentativa de login para username: {}", request.getUsername());
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            boolean isValid = token != null && !token.trim().isEmpty();
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.error("Erro na validação do token: {}", e.getMessage());
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Tentativa de renovação de token");
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
