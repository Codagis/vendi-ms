package com.vendi.vendi_ms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

/**
 * Entidade responsável pelo gerenciamento de empresas do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Entity
@Table(name = "empresas",
       indexes = {
           @Index(name = "idx_empresa_razao_social", columnList = "razao_social"),
           @Index(name = "idx_empresa_cnpj", columnList = "cnpj"),
           @Index(name = "idx_empresa_email", columnList = "email"),
           @Index(name = "idx_empresa_deleted", columnList = "deleted")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_empresa_cnpj", columnNames = "cnpj")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"usuarios", "lojas"})
public class Empresa extends BaseEntity {

    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres")
    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(min = 14, max = 18, message = "CNPJ deve ter entre 14 e 18 caracteres")
    @Column(name = "cnpj", nullable = false, unique = true, length = 18)
    private String cnpj;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Size(max = 20, message = "Inscrição municipal deve ter no máximo 20 caracteres")
    @Column(name = "inscricao_municipal", length = 20)
    private String inscricaoMunicipal;

    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    @Column(name = "endereco", length = 200)
    private String endereco;

    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    @Column(name = "numero", length = 10)
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(name = "complemento", length = 100)
    private String complemento;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Column(name = "bairro", length = 100)
    private String bairro;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "cidade", length = 100)
    private String cidade;

    @Size(max = 2, message = "UF deve ter no máximo 2 caracteres")
    @Column(name = "uf", length = 2)
    private String uf;

    @Size(max = 10, message = "CEP deve ter no máximo 10 caracteres")
    @Column(name = "cep", length = 10)
    private String cep;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "telefone", length = 20)
    private String telefone;

    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 100, message = "Site deve ter no máximo 100 caracteres")
    @Column(name = "site", length = 100)
    private String site;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private Set<Loja> lojas;

    public boolean isAtiva() {
        return ativo != null && ativo;
    }

    public String getEnderecoCompleto() {
        StringBuilder enderecoCompleto = new StringBuilder();
        if (endereco != null) {
            enderecoCompleto.append(endereco);
        }
        if (numero != null && !numero.trim().isEmpty()) {
            enderecoCompleto.append(", ").append(numero);
        }
        if (complemento != null && !complemento.trim().isEmpty()) {
            enderecoCompleto.append(", ").append(complemento);
        }
        if (bairro != null && !bairro.trim().isEmpty()) {
            enderecoCompleto.append(", ").append(bairro);
        }
        if (cidade != null && !cidade.trim().isEmpty()) {
            enderecoCompleto.append(", ").append(cidade);
        }
        if (uf != null && !uf.trim().isEmpty()) {
            enderecoCompleto.append(" - ").append(uf);
        }
        if (cep != null && !cep.trim().isEmpty()) {
            enderecoCompleto.append(" - CEP: ").append(cep);
        }
        return enderecoCompleto.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Empresa empresa = (Empresa) o;
        return getId() != null && getId().equals(empresa.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
