package com.vendi.vendi_ms.controller;

import com.vendi.vendi_ms.dto.UsuarioCreateDTO;
import com.vendi.vendi_ms.dto.UsuarioDTO;
import com.vendi.vendi_ms.dto.UsuarioUpdateDTO;
import com.vendi.vendi_ms.dto.UsuarioStatsDTO;
import com.vendi.vendi_ms.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsável pelos endpoints de usuários.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioCreateDTO dto) {
        log.info("Criando novo usuário: {}", dto.getUsername());
        UsuarioDTO usuario = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        log.info("Atualizando usuário: {}", id);
        UsuarioDTO usuario = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long perfilId,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long lojaId,
            @RequestParam(required = false) Boolean ativo) {
        List<UsuarioDTO> usuarios = usuarioService.listar(nome, username, email, 
                perfilId, empresaId, lojaId, ativo);
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        log.info("Removendo usuário: {}", id);
        usuarioService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id, @RequestParam Boolean ativo) {
        log.info("Alterando status do usuário {} para: {}", id, ativo);
        usuarioService.alterarStatus(id, ativo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/can-delete")
    public ResponseEntity<Map<String, Object>> validarExclusao(@PathVariable Long id) {
        log.info("Validando se é possível deletar usuário: {}", id);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean canDelete = usuarioService.podeSerDeletado(id);
            response.put("canDelete", canDelete);
            
            if (!canDelete) {
                List<String> reasons = usuarioService.getRazoesNaoPodeDeletar(id);
                response.put("reasons", reasons);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao validar exclusão do usuário {}: {}", id, e.getMessage());
            response.put("canDelete", false);
            response.put("reasons", List.of("Erro interno do servidor"));
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<UsuarioStatsDTO> buscarEstatisticas() {
        log.info("Buscando estatísticas dos usuários");
        UsuarioStatsDTO stats = usuarioService.buscarEstatisticas();
        return ResponseEntity.ok(stats);
    }
}
