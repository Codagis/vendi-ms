package com.vendi.vendi_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de login.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String refreshToken;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String username;
    private String nome;
    private String email;
    private String role;
    private List<String> permissions;
    private Boolean root;
    private Long empresaId;
    private String empresaNome;
    private Long lojaId;
    private String lojaNome;
    private LocalDateTime ultimoLogin;
    private LocalDateTime expiresAt;
}
