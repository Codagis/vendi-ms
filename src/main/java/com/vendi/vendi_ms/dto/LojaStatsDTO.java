package com.vendi.vendi_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estatísticas de Lojas.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LojaStatsDTO {
    
    private Long totalLojas;
    private Long lojasAtivas;
    private Long lojasInativas;
    private Long totalUsuarios;
    private Long totalEmpresas;
}

