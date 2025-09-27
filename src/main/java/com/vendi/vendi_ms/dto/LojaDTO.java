package com.vendi.vendi_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para representação de dados de Loja.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LojaDTO {
    
    private Long id;
    private String nome;
    private String codigo;
    private String descricao;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String telefone;
    private Boolean ativo;
    private String enderecoCompleto;
    private Long empresaId;
    private String empresaRazaoSocial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
