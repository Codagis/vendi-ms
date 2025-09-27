package com.vendi.vendi_ms.controller;

import com.vendi.vendi_ms.dto.LojaCreateDTO;
import com.vendi.vendi_ms.dto.LojaDTO;
import com.vendi.vendi_ms.dto.LojaStatsDTO;
import com.vendi.vendi_ms.dto.LojaUpdateDTO;
import com.vendi.vendi_ms.service.LojaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para operações relacionadas a Lojas.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@RestController
@RequestMapping("/api/lojas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LojaController {

    private final LojaService lojaService;

    /**
     * Lista todas as lojas com filtros opcionais.
     * 
     * @param filters Filtros de busca
     * @param pageable Configuração de paginação
     * @return Página de lojas
     */
    @GetMapping
    public ResponseEntity<Page<LojaDTO>> listarLojas(
            @RequestParam(required = false) Map<String, Object> filters,
            Pageable pageable) {
        log.info("Listando lojas com filtros: {}", filters);
        
        Page<LojaDTO> lojas = lojaService.findAll(filters, pageable);
        return ResponseEntity.ok(lojas);
    }

    /**
     * Lista todas as lojas com filtros opcionais (sem paginação) - endpoint alternativo.
     * 
     * @param filters Filtros de busca
     * @return Lista de lojas
     */
    @GetMapping("/all")
    public ResponseEntity<List<LojaDTO>> listarTodasLojas(
            @RequestParam(required = false) Map<String, Object> filters) {
        log.info("Listando todas as lojas com filtros: {}", filters);
        
        List<LojaDTO> lojas = lojaService.findAllList(filters);
        return ResponseEntity.ok(lojas);
    }

    /**
     * Lista lojas para seleção de usuário com filtro de busca.
     * 
     * @param filters Filtros de busca
     * @return Lista de lojas para seleção
     */
    @GetMapping("/for-user-selection")
    public ResponseEntity<List<LojaDTO>> listarLojasParaSelecaoUsuario(
            @RequestParam(required = false) Map<String, Object> filters) {
        log.info("Listando lojas para seleção de usuário com filtros: {}", filters);
        
        List<LojaDTO> lojas = lojaService.findAllForUserSelection(filters);
        return ResponseEntity.ok(lojas);
    }

    /**
     * Lista todas as lojas com filtros opcionais (sem paginação).
     * 
     * @param filters Filtros de busca
     * @return Lista de lojas
     */
    @GetMapping(value = "/list", produces = "application/json")
    public ResponseEntity<List<LojaDTO>> listarLojasList(
            @RequestParam(required = false) Map<String, Object> filters) {
        log.info("Listando lojas com filtros: {}", filters);
        
        List<LojaDTO> lojas = lojaService.findAllList(filters);
        return ResponseEntity.ok(lojas);
    }

    /**
     * Busca loja por ID.
     * 
     * @param id ID da loja
     * @return Dados da loja
     */
    @GetMapping("/{id}")
    public ResponseEntity<LojaDTO> buscarLoja(@PathVariable Long id) {
        log.info("Buscando loja com ID: {}", id);
        
        LojaDTO loja = lojaService.findById(id);
        return ResponseEntity.ok(loja);
    }

    /**
     * Lista lojas por empresa.
     * 
     * @param empresaId ID da empresa
     * @return Lista de lojas da empresa
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<LojaDTO>> listarLojasPorEmpresa(@PathVariable Long empresaId) {
        log.info("Listando lojas da empresa: {}", empresaId);
        
        List<LojaDTO> lojas = lojaService.findByEmpresa(empresaId);
        return ResponseEntity.ok(lojas);
    }

    /**
     * Cria uma nova loja.
     * 
     * @param createDTO Dados para criação da loja
     * @return Dados da loja criada
     */
    @PostMapping
    public ResponseEntity<LojaDTO> criarLoja(@Valid @RequestBody LojaCreateDTO createDTO) {
        log.info("Criando nova loja: {}", createDTO.getNome());
        
        LojaDTO loja = lojaService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(loja);
    }

    /**
     * Atualiza uma loja existente.
     * 
     * @param id ID da loja
     * @param updateDTO Dados para atualização
     * @return Dados da loja atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<LojaDTO> atualizarLoja(
            @PathVariable Long id,
            @Valid @RequestBody LojaUpdateDTO updateDTO) {
        log.info("Atualizando loja ID: {}", id);
        
        LojaDTO loja = lojaService.update(id, updateDTO);
        return ResponseEntity.ok(loja);
    }

    /**
     * Altera o status de uma loja.
     * 
     * @param id ID da loja
     * @param ativo Novo status
     * @return Dados da loja atualizada
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<LojaDTO> alterarStatusLoja(
            @PathVariable Long id,
            @RequestParam Boolean ativo) {
        log.info("Alterando status da loja ID: {} para {}", id, ativo);
        
        LojaDTO loja = lojaService.alterarStatus(id, ativo);
        return ResponseEntity.ok(loja);
    }

    /**
     * Exclui uma loja.
     * 
     * @param id ID da loja
     * @return Resposta de sucesso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirLoja(@PathVariable Long id) {
        log.info("Excluindo loja ID: {}", id);
        
        lojaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca estatísticas das lojas.
     * 
     * @return Estatísticas das lojas
     */
    @GetMapping("/stats")
    public ResponseEntity<LojaStatsDTO> buscarEstatisticas() {
        log.info("Buscando estatísticas das lojas");
        
        LojaStatsDTO stats = lojaService.getStats();
        return ResponseEntity.ok(stats);
    }
}
