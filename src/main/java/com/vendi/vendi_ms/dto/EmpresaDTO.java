package com.vendi.vendi_ms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmpresaDTO {
    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String inscricaoMunicipal;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String telefone;
    private String email;
    private String site;
    private Boolean ativo;
    private String urlLogo;
    private String enderecoCompleto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
