package com.vendi.vendi_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estatísticas de usuários.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioStatsDTO {
    private Long totalUsuarios;
    private Long usuariosAtivos;
    private Long usuariosInativos;
    private Long administradores;
    private Long vendedores;
    private Long outrosPerfis;
}
