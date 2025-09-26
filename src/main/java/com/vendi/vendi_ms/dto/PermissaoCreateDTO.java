package com.vendi.vendi_ms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para criação de permissões.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class PermissaoCreateDTO {
    
    @NotBlank(message = "Chave da permissão é obrigatória")
    @Size(max = 100, message = "Chave deve ter no máximo 100 caracteres")
    private String chave;
    
    @NotBlank(message = "Nome da permissão é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    @NotBlank(message = "Categoria é obrigatória")
    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    private String categoria;
    
    private Boolean ativo = true;
}
