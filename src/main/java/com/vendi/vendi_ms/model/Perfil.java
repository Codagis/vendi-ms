package com.vendi.vendi_ms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

/**
 * Entidade responsável pelo gerenciamento de perfis de usuário do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Entity
@Table(name = "perfis",
       indexes = {
           @Index(name = "idx_perfil_nome", columnList = "nome"),
           @Index(name = "idx_perfil_codigo", columnList = "codigo"),
           @Index(name = "idx_perfil_deleted", columnList = "deleted")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_perfil_codigo", columnNames = "codigo")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"permissoes", "usuarios"})
public class Perfil extends BaseEntity {

    @NotBlank(message = "Nome do perfil é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Código do perfil é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "sistema", nullable = false)
    private Boolean sistema = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "perfil_permissoes",
        joinColumns = @JoinColumn(name = "perfil_id"),
        inverseJoinColumns = @JoinColumn(name = "permissao_id"),
        indexes = {
            @Index(name = "idx_perfil_permissoes_perfil", columnList = "perfil_id"),
            @Index(name = "idx_perfil_permissoes_permissao", columnList = "permissao_id")
        }
    )
    @JsonIgnore
    private Set<Permissao> permissoes;

    @OneToMany(mappedBy = "perfil", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Usuario> usuarios;

    public boolean isAtivo() {
        return ativo != null && ativo;
    }

    public boolean isSistema() {
        return sistema != null && sistema;
    }

    public boolean possuiPermissao(String chavePermissao) {
        if (permissoes == null) {
            return false;
        }
        return permissoes.stream()
                .anyMatch(permissao -> permissao.getChave().equals(chavePermissao) && permissao.isAtiva());
    }

    public boolean possuiPermissao(Permissao permissao) {
        if (permissao == null || permissoes == null) {
            return false;
        }
        return permissoes.contains(permissao) && permissao.isAtiva();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Perfil perfil = (Perfil) o;
        return getId() != null && getId().equals(perfil.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
