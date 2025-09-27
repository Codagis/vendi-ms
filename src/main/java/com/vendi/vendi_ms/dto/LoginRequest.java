package com.vendi.vendi_ms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para requisição de login.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class LoginRequest {
    
    @NotBlank(message = "Username é obrigatório")
    private String username;
    
    @NotBlank(message = "Senha é obrigatória")
    private String password;
    
    private Long lojaId;
}

