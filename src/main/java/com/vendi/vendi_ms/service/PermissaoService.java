package com.vendi.vendi_ms.service;

import com.vendi.vendi_ms.dto.PermissaoCreateDTO;
import com.vendi.vendi_ms.dto.PermissaoDTO;
import com.vendi.vendi_ms.dto.PermissaoUpdateDTO;
import com.vendi.vendi_ms.exception.EntityNotFoundException;
import com.vendi.vendi_ms.exception.ConflictException;
import com.vendi.vendi_ms.model.Permissao;
import com.vendi.vendi_ms.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pelo gerenciamento de permissões do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissaoService {

    private final PermissaoRepository permissaoRepository;

    @Transactional(readOnly = true)
    public List<PermissaoDTO> listar() {
        log.info("Buscando todas as permissões");
        
        List<Permissao> permissoes = permissaoRepository.findAllNotDeleted();
        
        return permissoes.stream()
                .map(this::mapearEntityParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PermissaoDTO> listarAtivas() {
        log.info("Buscando permissões ativas");
        
        List<Permissao> permissoes = permissaoRepository.findAtivas();
        
        return permissoes.stream()
                .map(this::mapearEntityParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PermissaoDTO> listarPorCategoria(String categoria) {
        log.info("Buscando permissões por categoria: {}", categoria);
        
        List<Permissao> permissoes = permissaoRepository.findByCategoria(categoria);
        
        return permissoes.stream()
                .map(this::mapearEntityParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PermissaoDTO> listarPorPerfil(Long perfilId) {
        log.info("Buscando permissões por perfil: {}", perfilId);
        
        List<Permissao> permissoes = permissaoRepository.findByPerfil(perfilId);
        
        return permissoes.stream()
                .map(this::mapearEntityParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        log.info("Buscando categorias de permissões");
        
        List<Permissao> permissoes = permissaoRepository.findAtivas();
        
        return permissoes.stream()
                .map(Permissao::getCategoria)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PermissaoDTO buscarPorId(Long id) {
        log.info("Buscando permissão por ID: {}", id);
        
        Permissao permissao = permissaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada"));
        
        return mapearEntityParaDTO(permissao);
    }

    @Transactional(readOnly = true)
    public PermissaoDTO buscarPorChave(String chave) {
        log.info("Buscando permissão por chave: {}", chave);
        
        Permissao permissao = permissaoRepository.findByChaveAndNotDeleted(chave)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada"));
        
        return mapearEntityParaDTO(permissao);
    }

    @Transactional
    public PermissaoDTO criar(PermissaoCreateDTO dto) {
        log.info("Criando permissão: {}", dto.getChave());

        if (permissaoRepository.findByChaveAndNotDeleted(dto.getChave()).isPresent()) {
            throw new ConflictException("Chave da permissão já existe");
        }

        Permissao permissao = mapearCreateDTOParaEntity(dto);
        permissao.setCreatedAt(LocalDateTime.now());
        permissao.setUpdatedAt(LocalDateTime.now());

        Permissao permissaoSalva = permissaoRepository.save(permissao);
        log.info("Permissão criada com sucesso: {}", permissaoSalva.getId());

        return mapearEntityParaDTO(permissaoSalva);
    }

    @Transactional
    public PermissaoDTO atualizar(Long id, PermissaoUpdateDTO dto) {
        log.info("Atualizando permissão: {}", id);

        Permissao permissao = permissaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada"));

        if (dto.getChave() != null && permissaoRepository.existsByChaveAndIdNot(dto.getChave(), id)) {
            throw new ConflictException("Chave da permissão já existe");
        }

        mapearUpdateDTOParaEntity(dto, permissao);
        permissao.setUpdatedAt(LocalDateTime.now());

        Permissao permissaoSalva = permissaoRepository.save(permissao);
        log.info("Permissão atualizada com sucesso: {}", permissaoSalva.getId());

        return mapearEntityParaDTO(permissaoSalva);
    }

    @Transactional
    public void remover(Long id) {
        log.info("Removendo permissão: {}", id);
        
        Permissao permissao = permissaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada"));
        
        if (permissao.getPerfis() != null && !permissao.getPerfis().isEmpty()) {
            throw new ConflictException("Não é possível remover permissão com perfis associados");
        }
        
        permissao.setDeleted(true);
        permissao.setUpdatedAt(LocalDateTime.now());
        
        permissaoRepository.save(permissao);
        log.info("Permissão removida com sucesso: {}", id);
    }

    @Transactional
    public PermissaoDTO alterarStatus(Long id, Boolean ativo) {
        log.info("Alterando status da permissão {} para: {}", id, ativo);
        
        Permissao permissao = permissaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada"));
        
        permissao.setAtivo(ativo);
        permissao.setUpdatedAt(LocalDateTime.now());
        
        Permissao permissaoSalva = permissaoRepository.save(permissao);
        log.info("Status da permissão alterado com sucesso: {}", id);
        
        return mapearEntityParaDTO(permissaoSalva);
    }

    private Permissao mapearCreateDTOParaEntity(PermissaoCreateDTO dto) {
        Permissao permissao = new Permissao();
        permissao.setChave(dto.getChave());
        permissao.setNome(dto.getNome());
        permissao.setDescricao(dto.getDescricao());
        permissao.setCategoria(dto.getCategoria());
        permissao.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        permissao.setDeleted(false);
        
        return permissao;
    }

    private void mapearUpdateDTOParaEntity(PermissaoUpdateDTO dto, Permissao permissao) {
        if (dto.getChave() != null) {
            permissao.setChave(dto.getChave());
        }
        if (dto.getNome() != null) {
            permissao.setNome(dto.getNome());
        }
        if (dto.getDescricao() != null) {
            permissao.setDescricao(dto.getDescricao());
        }
        if (dto.getCategoria() != null) {
            permissao.setCategoria(dto.getCategoria());
        }
        if (dto.getAtivo() != null) {
            permissao.setAtivo(dto.getAtivo());
        }
    }

    private PermissaoDTO mapearEntityParaDTO(Permissao permissao) {
        PermissaoDTO dto = new PermissaoDTO();
        dto.setId(permissao.getId());
        dto.setChave(permissao.getChave());
        dto.setNome(permissao.getNome());
        dto.setDescricao(permissao.getDescricao());
        dto.setCategoria(permissao.getCategoria());
        dto.setAtivo(permissao.getAtivo());
        dto.setCreatedAt(permissao.getCreatedAt() != null ? permissao.getCreatedAt().toString() : null);
        dto.setUpdatedAt(permissao.getUpdatedAt() != null ? permissao.getUpdatedAt().toString() : null);
        
        return dto;
    }
}
