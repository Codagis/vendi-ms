package com.vendi.vendi_ms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para atualização de usuários.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class UsuarioUpdateDTO {
    
    private Long id;
    
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;
    
    @Size(max = 50, message = "Username deve ter no máximo 50 caracteres")
    private String username;
    
    @Size(max = 20, message = "Crachá deve ter no máximo 20 caracteres")
    private String cracha;
    
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;
    
    private String senha;
    
    private Long perfilId;
    
    private String perfilNome;
    
    private String fotoPerfil;
    
    private String urlFotoPerfil;
    
    private Boolean ativo;
    
    private LocalDateTime ultimoLogin;
    
    private Integer tentativasLoginFalhadas;
    
    private Boolean contaBloqueada;
    
    private Boolean root;
    
    private Long empresaId;
    
    private String empresaNome;
    
    private Long lojaId;
    
    private String lojaNome;
    
    private List<String> permissions;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
