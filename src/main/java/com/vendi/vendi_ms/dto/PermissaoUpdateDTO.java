package com.vendi.vendi_ms.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para atualização de permissões.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class PermissaoUpdateDTO {
    
    private Long id;
    
    @Size(max = 100, message = "Chave deve ter no máximo 100 caracteres")
    private String chave;
    
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    private String categoria;
    
    private Boolean ativo;
}
