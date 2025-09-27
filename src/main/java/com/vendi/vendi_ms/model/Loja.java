package com.vendi.vendi_ms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

/**
 * Entidade responsável pelo gerenciamento de lojas/filiais do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Entity
@Table(name = "lojas",
       indexes = {
           @Index(name = "idx_loja_nome", columnList = "nome"),
           @Index(name = "idx_loja_codigo", columnList = "codigo"),
           @Index(name = "idx_loja_empresa", columnList = "empresa_id"),
           @Index(name = "idx_loja_deleted", columnList = "deleted")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_loja_codigo_empresa", columnNames = {"codigo", "empresa_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"empresa", "usuarios"})
public class Loja extends BaseEntity {

    @NotBlank(message = "Nome da loja é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @NotBlank(message = "Código da loja é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(name = "descricao", length = 500)
    private String descricao;

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

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @NotNull(message = "Empresa é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "loja", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios;

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
        Loja loja = (Loja) o;
        return getId() != null && getId().equals(loja.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
