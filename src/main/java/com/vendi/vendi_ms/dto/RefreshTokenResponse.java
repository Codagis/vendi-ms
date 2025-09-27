package com.vendi.vendi_ms.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para resposta de refresh token.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
@Builder
public class RefreshTokenResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
}

