package com.vendi.vendi_ms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para criação de usuários.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class UsuarioCreateDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;
    
    @NotBlank(message = "Username é obrigatório")
    @Size(max = 50, message = "Username deve ter no máximo 50 caracteres")
    private String username;
    
    @Size(max = 20, message = "Crachá deve ter no máximo 20 caracteres")
    private String cracha;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;
    
    @NotNull(message = "Perfil é obrigatório")
    private Long perfilId;
    
    private String perfilNome;
    
    private String fotoPerfil;
    
    private String urlFotoPerfil;
    
    private Boolean ativo = true;
    
    private LocalDateTime ultimoLogin;
    
    private Integer tentativasLoginFalhadas = 0;
    
    private Boolean contaBloqueada = false;
    
    private Boolean root = false;
    
    @NotNull(message = "Empresa é obrigatória")
    private Long empresaId;
    
    private String empresaNome;
    
    private Long lojaId;
    
    private String lojaNome;
    
    private List<String> permissions;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
