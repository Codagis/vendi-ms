package com.vendi.vendi_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para dados do usuário incluídos no token JWT.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenData {
    
    private Long id;
    private String username;
    private String email;
    private String nome;
    private Boolean root;
    private Boolean ativo;
    private Boolean contaBloqueada;
    private String perfilNome;
    private List<String> permissoes;
    private Long empresaId;
    private String empresaNome;
    private Long lojaId;
    private String lojaNome;
}
