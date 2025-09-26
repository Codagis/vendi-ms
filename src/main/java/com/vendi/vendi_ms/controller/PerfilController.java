package com.vendi.vendi_ms.controller;

import com.vendi.vendi_ms.dto.PerfilCreateDTO;
import com.vendi.vendi_ms.dto.PerfilDTO;
import com.vendi.vendi_ms.dto.PerfilUpdateDTO;
import com.vendi.vendi_ms.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelos endpoints de perfis.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@RestController
@RequestMapping("/api/perfis")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PerfilController {

    private final PerfilService perfilService;

    @PostMapping
    public ResponseEntity<PerfilDTO> criar(@Valid @RequestBody PerfilCreateDTO dto) {
        log.info("Criando novo perfil: {}", dto.getCodigo());
        PerfilDTO perfil = perfilService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(perfil);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PerfilUpdateDTO dto) {
        log.info("Atualizando perfil: {}", id);
        PerfilDTO perfil = perfilService.atualizar(id, dto);
        return ResponseEntity.ok(perfil);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilDTO> buscarPorId(@PathVariable Long id) {
        PerfilDTO perfil = perfilService.buscarPorId(id);
        return ResponseEntity.ok(perfil);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PerfilDTO> buscarPorCodigo(@PathVariable String codigo) {
        PerfilDTO perfil = perfilService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(perfil);
    }

    @GetMapping
    public ResponseEntity<List<PerfilDTO>> listar() {
        List<PerfilDTO> perfis = perfilService.listar();
        return ResponseEntity.ok(perfis);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<PerfilDTO>> listarAtivos() {
        List<PerfilDTO> perfis = perfilService.listarAtivos();
        return ResponseEntity.ok(perfis);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        log.info("Removendo perfil: {}", id);
        perfilService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PerfilDTO> alterarStatus(@PathVariable Long id, @RequestParam Boolean ativo) {
        log.info("Alterando status do perfil {} para: {}", id, ativo);
        PerfilDTO perfil = perfilService.alterarStatus(id, ativo);
        return ResponseEntity.ok(perfil);
    }
}