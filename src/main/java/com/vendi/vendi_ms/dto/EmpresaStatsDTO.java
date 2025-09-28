package com.vendi.vendi_ms.dto;

import lombok.Data;

@Data
public class EmpresaStatsDTO {
    private Long totalEmpresas;
    private Long empresasAtivas;
    private Long empresasInativas;
    private Long totalLojas;
    private Long totalUsuarios;

    public EmpresaStatsDTO() {}

    public EmpresaStatsDTO(Long totalEmpresas, Long empresasAtivas, Long empresasInativas, Long totalLojas, Long totalUsuarios) {
        this.totalEmpresas = totalEmpresas;
        this.empresasAtivas = empresasAtivas;
        this.empresasInativas = empresasInativas;
        this.totalLojas = totalLojas;
        this.totalUsuarios = totalUsuarios;
    }
}


