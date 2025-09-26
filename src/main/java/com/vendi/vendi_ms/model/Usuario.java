package com.vendi.vendi_ms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade responsável pelo gerenciamento de usuários do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Entity
@Table(name = "usuarios", 
       indexes = {
           @Index(name = "idx_usuario_email", columnList = "email"),
           @Index(name = "idx_usuario_username", columnList = "username"),
           @Index(name = "idx_usuario_cracha", columnList = "cracha"),
           @Index(name = "idx_usuario_perfil", columnList = "perfil_id"),
           @Index(name = "idx_usuario_empresa", columnList = "empresa_id"),
           @Index(name = "idx_usuario_loja", columnList = "loja_id"),
           @Index(name = "idx_usuario_deleted", columnList = "deleted")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"),
           @UniqueConstraint(name = "uk_usuario_username", columnNames = "username"),
           @UniqueConstraint(name = "uk_usuario_cracha", columnNames = "cracha")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"perfil", "empresa", "loja"})
public class Usuario extends BaseEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(max = 50, message = "Nome de usuário deve ter no máximo 50 caracteres")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Size(max = 20, message = "Crachá deve ter no máximo 20 caracteres")
    @Column(name = "cracha", nullable = true, unique = true, length = 20)
    private String cracha;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Column(name = "senha", nullable = false, columnDefinition = "TEXT")
    private String senha;

    @NotNull(message = "Perfil é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    @JsonIgnore
    private Perfil perfil;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Size(max = 1000, message = "URL da foto de perfil deve ter no máximo 1000 caracteres")
    @Column(name = "url_foto_perfil", length = 1000)
    private String urlFotoPerfil;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "tentativas_login_falhadas")
    private Integer tentativasLoginFalhadas = 0;

    @Column(name = "conta_bloqueada")
    private Boolean contaBloqueada = false;

    @Column(name = "root", nullable = false)
    private Boolean root = false;

    @NotNull(message = "Empresa é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnore
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loja_id")
    @JsonIgnore
    private Loja loja;
	
    public boolean podeFazerLogin() {
        return isActive() && ativo && (contaBloqueada == null || !contaBloqueada);
    }
	
    public void incrementarTentativasFalhadas() {
        this.tentativasLoginFalhadas = (this.tentativasLoginFalhadas == null ? 0 : this.tentativasLoginFalhadas) + 1;
		if (this.tentativasLoginFalhadas >= 5) {
            this.contaBloqueada = true;
        }
    }
	
    public void resetarTentativasFalhadas() {
        this.tentativasLoginFalhadas = 0;
        this.contaBloqueada = false;
    }

    public boolean possuiPermissao(String chavePermissao) {
        return isRoot() || (perfil != null && perfil.possuiPermissao(chavePermissao));
    }

    public boolean possuiPermissao(Permissao permissao) {
        return isRoot() || (perfil != null && perfil.possuiPermissao(permissao));
    }

    public boolean isRoot() {
        return root != null && root;
    }

    public boolean isAtivo() {
        return ativo != null && ativo;
    }

    public boolean isContaBloqueada() {
        return contaBloqueada != null && contaBloqueada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return getId() != null && getId().equals(usuario.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
