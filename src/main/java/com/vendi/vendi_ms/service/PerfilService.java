package com.vendi.vendi_ms.service;

import com.vendi.vendi_ms.dto.PerfilCreateDTO;
import com.vendi.vendi_ms.dto.PerfilDTO;
import com.vendi.vendi_ms.dto.PerfilUpdateDTO;
import com.vendi.vendi_ms.dto.PermissaoDTO;
import com.vendi.vendi_ms.exception.EntityNotFoundException;
import com.vendi.vendi_ms.exception.ConflictException;
import com.vendi.vendi_ms.model.Perfil;
import com.vendi.vendi_ms.model.Permissao;
import com.vendi.vendi_ms.repository.PerfilRepository;
import com.vendi.vendi_ms.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pelo gerenciamento de perfis de usuário.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final PermissaoRepository permissaoRepository;

    @Transactional(readOnly = true)
    public List<PerfilDTO> listar() {
        log.info("Buscando todos os perfis");
        
        List<Perfil> perfis = perfilRepository.findAllNotDeleted();
        
        perfis.forEach(perfil -> {
            if (perfil.getPermissoes() != null) {
                perfil.getPermissoes().size();
            }
        });
        
        return perfis.stream()
                .map(this::mapearEntityParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PerfilDTO> listarAtivos() {
        log.info("Buscando perfis ativos");
        
        List<Perfil> perfis = perfilRepository.findAtivos();
        
        perfis.forEach(perfil -> {
            if (perfil.getPermissoes() != null) {
                perfil.getPermissoes().size();
            }
        });
        
        return perfis.stream()
                .map(this::mapearEntityParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PerfilDTO buscarPorId(Long id) {
        log.info("Buscando perfil por ID: {}", id);
        
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));
        
        if (perfil.getPermissoes() != null) {
            perfil.getPermissoes().size();
        }
        
        return mapearEntityParaDTO(perfil);
    }

    @Transactional(readOnly = true)
    public PerfilDTO buscarPorCodigo(String codigo) {
        log.info("Buscando perfil por código: {}", codigo);
        
        Perfil perfil = perfilRepository.findByCodigoAndNotDeleted(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));
        
        if (perfil.getPermissoes() != null) {
            perfil.getPermissoes().size();
        }
        
        return mapearEntityParaDTO(perfil);
    }

    @Transactional
    public PerfilDTO criar(PerfilCreateDTO dto) {
        log.info("Criando perfil: {}", dto.getCodigo());

        if (perfilRepository.findByCodigoAndNotDeleted(dto.getCodigo()).isPresent()) {
            throw new ConflictException("Código do perfil já existe");
        }

        Perfil perfil = mapearCreateDTOParaEntity(dto);
        perfil.setCreatedAt(LocalDateTime.now());
        perfil.setUpdatedAt(LocalDateTime.now());

        if (dto.getPermissaoIds() != null && !dto.getPermissaoIds().isEmpty()) {
            atualizarPermissoesDoPerfil(perfil, dto.getPermissaoIds());
        } else if (dto.getPermissaoChaves() != null && !dto.getPermissaoChaves().isEmpty()) {
            atualizarPermissoesDoPerfilPorChaves(perfil, dto.getPermissaoChaves());
        }

        Perfil perfilSalvo = perfilRepository.save(perfil);
        log.info("Perfil criado com sucesso: {}", perfilSalvo.getId());

        return mapearEntityParaDTO(perfilSalvo);
    }

    @Transactional
    public PerfilDTO atualizar(Long id, PerfilUpdateDTO dto) {
        log.info("Atualizando perfil: {}", id);

        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        if (dto.getCodigo() != null && perfilRepository.existsByCodigoAndIdNot(dto.getCodigo(), id)) {
            throw new ConflictException("Código do perfil já existe");
        }

        mapearUpdateDTOParaEntity(dto, perfil);
        perfil.setUpdatedAt(LocalDateTime.now());

        if (dto.getPermissaoIds() != null && !dto.getPermissaoIds().isEmpty()) {
            atualizarPermissoesDoPerfil(perfil, dto.getPermissaoIds());
        } else if (dto.getPermissaoChaves() != null && !dto.getPermissaoChaves().isEmpty()) {
            atualizarPermissoesDoPerfilPorChaves(perfil, dto.getPermissaoChaves());
        }

        Perfil perfilSalvo = perfilRepository.save(perfil);
        log.info("Perfil atualizado com sucesso: {}", perfilSalvo.getId());

        return mapearEntityParaDTO(perfilSalvo);
    }

    @Transactional
    public void remover(Long id) {
        log.info("Removendo perfil: {}", id);
        
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));
        
        if (perfil.isSistema()) {
            throw new ConflictException("Não é possível remover perfis do sistema");
        }
        
        if (perfil.getUsuarios() != null && !perfil.getUsuarios().isEmpty()) {
            throw new ConflictException("Não é possível remover perfil com usuários associados");
        }
        
        perfil.setDeleted(true);
        perfil.setUpdatedAt(LocalDateTime.now());
        
        perfilRepository.save(perfil);
        log.info("Perfil removido com sucesso: {}", id);
    }

    @Transactional
    public PerfilDTO alterarStatus(Long id, Boolean ativo) {
        log.info("Alterando status do perfil {} para: {}", id, ativo);
        
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));
        
        perfil.setAtivo(ativo);
        perfil.setUpdatedAt(LocalDateTime.now());
        
        Perfil perfilSalvo = perfilRepository.save(perfil);
        log.info("Status do perfil alterado com sucesso: {}", id);
        
        return mapearEntityParaDTO(perfilSalvo);
    }

    private Perfil mapearCreateDTOParaEntity(PerfilCreateDTO dto) {
        Perfil perfil = new Perfil();
        perfil.setNome(dto.getNome());
        perfil.setCodigo(dto.getCodigo());
        perfil.setDescricao(dto.getDescricao());
        perfil.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        perfil.setSistema(dto.getSistema() != null ? dto.getSistema() : false);
        perfil.setDeleted(false);
        
        return perfil;
    }

    private void mapearUpdateDTOParaEntity(PerfilUpdateDTO dto, Perfil perfil) {
        if (dto.getNome() != null) {
            perfil.setNome(dto.getNome());
        }
        if (dto.getCodigo() != null) {
            perfil.setCodigo(dto.getCodigo());
        }
        if (dto.getDescricao() != null) {
            perfil.setDescricao(dto.getDescricao());
        }
        if (dto.getAtivo() != null) {
            perfil.setAtivo(dto.getAtivo());
        }
        if (dto.getSistema() != null) {
            perfil.setSistema(dto.getSistema());
        }
    }

    private PerfilDTO mapearEntityParaDTO(Perfil perfil) {
        PerfilDTO dto = new PerfilDTO();
        dto.setId(perfil.getId());
        dto.setNome(perfil.getNome());
        dto.setCodigo(perfil.getCodigo());
        dto.setDescricao(perfil.getDescricao());
        dto.setAtivo(perfil.getAtivo());
        dto.setSistema(perfil.getSistema());
        dto.setCreatedAt(perfil.getCreatedAt() != null ? perfil.getCreatedAt().toString() : null);
        dto.setUpdatedAt(perfil.getUpdatedAt() != null ? perfil.getUpdatedAt().toString() : null);
        
        if (perfil.getPermissoes() != null) {
            dto.setPermissoes(perfil.getPermissoes().stream()
                    .map(this::mapearPermissaoParaDTO)
                    .collect(Collectors.toList()));
            
            dto.setPermissaoChaves(perfil.getPermissoes().stream()
                    .map(Permissao::getChave)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    private PermissaoDTO mapearPermissaoParaDTO(Permissao permissao) {
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

    private void atualizarPermissoesDoPerfil(Perfil perfil, List<Long> permissaoIds) {
        log.info("Atualizando permissões do perfil {} por IDs: {}", perfil.getId(), permissaoIds);
        
        List<Permissao> novasPermissoes = permissaoRepository.findAllById(permissaoIds);
        
        if (novasPermissoes.size() != permissaoIds.size()) {
            log.warn("Algumas permissões não foram encontradas. Esperadas: {}, Encontradas: {}", 
                    permissaoIds.size(), novasPermissoes.size());
        }
        
        perfil.setPermissoes(new java.util.HashSet<>(novasPermissoes));
        
        log.info("Permissões do perfil {} atualizadas com sucesso", perfil.getId());
    }

    private void atualizarPermissoesDoPerfilPorChaves(Perfil perfil, List<String> chavesPermissoes) {
        log.info("Atualizando permissões do perfil {} por chaves: {}", perfil.getId(), chavesPermissoes);
        
        List<Permissao> novasPermissoes = permissaoRepository.findByChaveIn(chavesPermissoes);
        
        if (novasPermissoes.size() != chavesPermissoes.size()) {
            log.warn("Algumas permissões não foram encontradas. Esperadas: {}, Encontradas: {}", 
                    chavesPermissoes.size(), novasPermissoes.size());
        }
        
        perfil.setPermissoes(new java.util.HashSet<>(novasPermissoes));
        
        log.info("Permissões do perfil {} atualizadas com sucesso", perfil.getId());
    }
}
