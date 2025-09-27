package com.vendi.vendi_ms.controller;

import com.vendi.vendi_ms.dto.EmpresaCreateDTO;
import com.vendi.vendi_ms.dto.EmpresaDTO;
import com.vendi.vendi_ms.dto.EmpresaStatsDTO;
import com.vendi.vendi_ms.dto.EmpresaUpdateDTO;
import com.vendi.vendi_ms.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<EmpresaDTO> criar(@Valid @RequestBody EmpresaCreateDTO dto) {
        log.info("Criando nova empresa: {}", dto.getRazaoSocial());
        EmpresaDTO empresa = empresaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(empresa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaUpdateDTO dto) {
        log.info("Atualizando empresa: {}", id);
        EmpresaDTO empresa = empresaService.atualizar(id, dto);
        return ResponseEntity.ok(empresa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> buscarPorId(@PathVariable Long id) {
        EmpresaDTO empresa = empresaService.buscarPorId(id);
        return ResponseEntity.ok(empresa);
    }

    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> listar(
            @RequestParam(required = false) String razaoSocial,
            @RequestParam(required = false) String nomeFantasia,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean ativo) {
        List<EmpresaDTO> empresas = empresaService.listar(razaoSocial, nomeFantasia, cnpj, email, ativo);
        return ResponseEntity.ok(empresas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        log.info("Removendo empresa: {}", id);
        empresaService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id, @RequestParam Boolean ativo) {
        log.info("Alterando status da empresa {} para: {}", id, ativo);
        empresaService.alterarStatus(id, ativo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/logo")
    public ResponseEntity<Map<String, String>> uploadLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("Fazendo upload da logo para empresa: {}", id);
        
        try {
            String urlLogo = empresaService.uploadLogo(id, file);
            
            Map<String, String> response = new HashMap<>();
            response.put("urlLogo", urlLogo);
            response.put("message", "Logo enviada com sucesso");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Erro ao fazer upload da logo: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao fazer upload da logo: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<EmpresaStatsDTO> buscarEstatisticas() {
        log.info("Buscando estatísticas das empresas");
        EmpresaStatsDTO stats = empresaService.buscarEstatisticas();
        return ResponseEntity.ok(stats);
    }
}
