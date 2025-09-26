package com.vendi.vendi_ms.controller;

import com.vendi.vendi_ms.dto.PermissaoCreateDTO;
import com.vendi.vendi_ms.dto.PermissaoDTO;
import com.vendi.vendi_ms.dto.PermissaoUpdateDTO;
import com.vendi.vendi_ms.service.PermissaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelos endpoints de permissões.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@RestController
@RequestMapping("/api/permissoes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PermissaoController {

    private final PermissaoService permissaoService;

    @PostMapping
    public ResponseEntity<PermissaoDTO> criar(@Valid @RequestBody PermissaoCreateDTO dto) {
        log.info("Criando nova permissão: {}", dto.getChave());
        PermissaoDTO permissao = permissaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(permissao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissaoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PermissaoUpdateDTO dto) {
        log.info("Atualizando permissão: {}", id);
        PermissaoDTO permissao = permissaoService.atualizar(id, dto);
        return ResponseEntity.ok(permissao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissaoDTO> buscarPorId(@PathVariable Long id) {
        PermissaoDTO permissao = permissaoService.buscarPorId(id);
        return ResponseEntity.ok(permissao);
    }

    @GetMapping("/chave/{chave}")
    public ResponseEntity<PermissaoDTO> buscarPorChave(@PathVariable String chave) {
        PermissaoDTO permissao = permissaoService.buscarPorChave(chave);
        return ResponseEntity.ok(permissao);
    }

    @GetMapping
    public ResponseEntity<List<PermissaoDTO>> listar() {
        List<PermissaoDTO> permissoes = permissaoService.listar();
        return ResponseEntity.ok(permissoes);
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<PermissaoDTO>> listarAtivas() {
        List<PermissaoDTO> permissoes = permissaoService.listarAtivas();
        return ResponseEntity.ok(permissoes);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<PermissaoDTO>> listarPorCategoria(@PathVariable String categoria) {
        List<PermissaoDTO> permissoes = permissaoService.listarPorCategoria(categoria);
        return ResponseEntity.ok(permissoes);
    }

    @GetMapping("/perfil/{perfilId}")
    public ResponseEntity<List<PermissaoDTO>> listarPorPerfil(@PathVariable Long perfilId) {
        List<PermissaoDTO> permissoes = permissaoService.listarPorPerfil(perfilId);
        return ResponseEntity.ok(permissoes);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = permissaoService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        log.info("Removendo permissão: {}", id);
        permissaoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PermissaoDTO> alterarStatus(@PathVariable Long id, @RequestParam Boolean ativo) {
        log.info("Alterando status da permissão {} para: {}", id, ativo);
        PermissaoDTO permissao = permissaoService.alterarStatus(id, ativo);
        return ResponseEntity.ok(permissao);
    }
}
