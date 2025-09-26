package com.vendi.vendi_ms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

/**
 * Entidade responsável pelo gerenciamento de permissões do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Entity
@Table(name = "permissoes",
       indexes = {
           @Index(name = "idx_permissao_chave", columnList = "chave"),
           @Index(name = "idx_permissao_categoria", columnList = "categoria"),
           @Index(name = "idx_permissao_deleted", columnList = "deleted")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_permissao_chave", columnNames = "chave")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"perfis"})
public class Permissao extends BaseEntity {

    @NotBlank(message = "Chave da permissão é obrigatória")
    @Size(max = 100, message = "Chave deve ter no máximo 100 caracteres")
    @Column(name = "chave", nullable = false, unique = true, length = 100)
    private String chave;

    @NotBlank(message = "Nome da permissão é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(name = "descricao", length = 500)
    private String descricao;

    @NotBlank(message = "Categoria é obrigatória")
    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ManyToMany(mappedBy = "permissoes", fetch = FetchType.LAZY)
    private Set<Perfil> perfis;

    public boolean isAtiva() {
        return ativo != null && ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permissao permissao = (Permissao) o;
        return getId() != null && getId().equals(permissao.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
