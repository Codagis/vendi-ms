package com.vendi.vendi_ms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmpresaUpdateDTO {
    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres")
    private String razaoSocial;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(min = 14, max = 18, message = "CNPJ deve ter entre 14 e 18 caracteres")
    private String cnpj;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    private String inscricaoEstadual;

    @Size(max = 20, message = "Inscrição municipal deve ter no máximo 20 caracteres")
    private String inscricaoMunicipal;

    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @Size(max = 2, message = "UF deve ter no máximo 2 caracteres")
    private String uf;

    @Size(max = 10, message = "CEP deve ter no máximo 10 caracteres")
    private String cep;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    @Size(max = 100, message = "Site deve ter no máximo 100 caracteres")
    private String site;

    private Boolean ativo;
}
