package com.vendi.vendi_ms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * DTO para criação de perfis.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class PerfilCreateDTO {
    
    @NotBlank(message = "Nome do perfil é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;
    
    @NotBlank(message = "Código do perfil é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String codigo;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    private Boolean ativo = true;
    
    private Boolean sistema = false;
    
    private List<Long> permissaoIds;
    
    private List<String> permissaoChaves;
}
