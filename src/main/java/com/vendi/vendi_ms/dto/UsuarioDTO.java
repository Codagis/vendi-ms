package com.vendi.vendi_ms.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de usuários.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
public class UsuarioDTO {
    
    private Long id;
    
    private String nome;
    
    private String username;
    
    private String cracha;
    
    private String email;
    
    private String senha;
    
    private Long perfilId;
    
    private String perfilNome;
    
    private String fotoPerfil;
    
    private String urlFotoPerfil;
    
    private Boolean ativo = true;
    
    private LocalDateTime ultimoLogin;
    
    private Integer tentativasLoginFalhadas = 0;
    
    private Boolean contaBloqueada = false;
    
    private Boolean root = false;
    
    private Long empresaId;
    
    private String empresaNome;
    
    private Long lojaId;
    
    private String lojaNome;
    
    private List<String> permissions;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
