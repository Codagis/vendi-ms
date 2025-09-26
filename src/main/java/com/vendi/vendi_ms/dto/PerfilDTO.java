package com.vendi.vendi_ms.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO para resposta de perfis.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class PerfilDTO {
    
    private Long id;
    
    private String nome;
    
    private String codigo;
    
    private String descricao;
    
    private Boolean ativo = true;
    
    private Boolean sistema = false;
    
    private List<PermissaoDTO> permissoes;
    
    private List<String> permissaoChaves;
    
    private String createdAt;
    
    private String updatedAt;
}
