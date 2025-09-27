package com.vendi.vendi_ms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para requisição de refresh token.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token é obrigatório")
    private String refreshToken;
}

