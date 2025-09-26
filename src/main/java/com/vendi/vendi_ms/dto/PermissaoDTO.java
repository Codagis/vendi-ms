package com.vendi.vendi_ms.dto;

import lombok.Data;

/**
 * DTO para resposta de permissões.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class PermissaoDTO {
    
    private Long id;
    
    private String chave;
    
    private String nome;
    
    private String descricao;
    
    private String categoria;
    
    private Boolean ativo = true;
    
    private String createdAt;
    
    private String updatedAt;
}
