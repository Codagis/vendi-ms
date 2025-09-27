package com.vendi.vendi_ms.service;

import com.vendi.vendi_ms.dto.LojaCreateDTO;
import com.vendi.vendi_ms.dto.LojaDTO;
import com.vendi.vendi_ms.dto.LojaStatsDTO;
import com.vendi.vendi_ms.dto.LojaUpdateDTO;
import com.vendi.vendi_ms.exception.ConflictException;
import com.vendi.vendi_ms.exception.EntityNotFoundException;
import com.vendi.vendi_ms.model.Empresa;
import com.vendi.vendi_ms.model.Loja;
import com.vendi.vendi_ms.repository.EmpresaRepository;
import com.vendi.vendi_ms.repository.LojaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas operações de negócio relacionadas a Lojas.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LojaService {

    private final LojaRepository lojaRepository;
    private final EmpresaRepository empresaRepository;

    /**
     * Busca todas as lojas com filtros opcionais.
     * 
     * @param filters Filtros de busca
     * @param pageable Configuração de paginação
     * @return Página de lojas
     */
    public Page<LojaDTO> findAll(Map<String, Object> filters, Pageable pageable) {
        log.info("Buscando lojas com filtros: {}", filters);
        
        // Implementar busca com filtros usando Specification ou Query
        // Por enquanto, retornar todas as lojas ativas
        Page<Loja> lojas = lojaRepository.findByDeletedFalse(pageable);
        
        return lojas.map(this::convertToDTO);
    }

    /**
     * Busca todas as lojas com filtros opcionais (sem paginação).
     * 
     * @param filters Filtros de busca
     * @return Lista de lojas
     */
    public List<LojaDTO> findAllList(Map<String, Object> filters) {
        log.info("Buscando lojas com filtros: {}", filters);
        
        List<Loja> lojas;
        
        if (filters == null || filters.isEmpty()) {
            // Buscar todas as lojas não deletadas
            lojas = lojaRepository.findByDeletedFalse();
        } else {
            // Aplicar filtros
            String nome = (String) filters.get("nome");
            Boolean ativo = (Boolean) filters.get("ativo");
            Long empresaId = (Long) filters.get("empresaId");
            
            if (nome != null && !nome.trim().isEmpty()) {
                lojas = lojaRepository.findByNomeContaining(nome);
            } else if (empresaId != null) {
                lojas = lojaRepository.findAtivasByEmpresa(empresaId);
            } else {
                lojas = lojaRepository.findByDeletedFalse();
            }
            
            // Filtrar por status se especificado
            if (ativo != null) {
                lojas = lojas.stream()
                        .filter(loja -> loja.getAtivo().equals(ativo))
                        .collect(Collectors.toList());
            }
        }
        
        return lojas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca lojas para seleção de usuário com filtro de busca.
     * 
     * @param filters Filtros de busca
     * @return Lista de lojas para seleção
     */
    public List<LojaDTO> findAllForUserSelection(Map<String, Object> filters) {
        log.info("Buscando lojas para seleção de usuário com filtros: {}", filters);

        List<Loja> lojas;

        if (filters == null || filters.isEmpty()) {
            lojas = lojaRepository.findByDeletedFalse();
        } else {
            String searchTerm = (String) filters.get("search");
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                lojas = lojaRepository.findByNomeContainingOrEmpresaRazaoSocialContaining(searchTerm.trim());
            } else {
                lojas = lojaRepository.findByDeletedFalse();
            }
        }
        
        return lojas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca loja por ID.
     * 
     * @param id ID da loja
     * @return DTO da loja
     * @throws EntityNotFoundException se loja não encontrada
     */
    public LojaDTO findById(Long id) {
        log.info("Buscando loja com ID: {}", id);
        
        Loja loja = lojaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com ID: " + id));
        
        return convertToDTO(loja);
    }

    /**
     * Busca lojas por empresa.
     * 
     * @param empresaId ID da empresa
     * @return Lista de lojas da empresa
     */
    public List<LojaDTO> findByEmpresa(Long empresaId) {
        log.info("Buscando lojas da empresa: {}", empresaId);
        
        List<Loja> lojas = lojaRepository.findAtivasByEmpresa(empresaId);
        return lojas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cria uma nova loja.
     * 
     * @param createDTO Dados para criação da loja
     * @return DTO da loja criada
     * @throws ConflictException se dados inválidos
     */
    @Transactional
    public LojaDTO create(LojaCreateDTO createDTO) {
        log.info("Criando nova loja: {}", createDTO.getNome());
        
        // Validar se empresa existe
        Empresa empresa = empresaRepository.findByIdAndDeletedFalse(createDTO.getEmpresaId())
                .orElseThrow(() -> new ConflictException("Empresa não encontrada com ID: " + createDTO.getEmpresaId()));
        
        // Validar se código já existe para a empresa
        if (lojaRepository.findByCodigoAndEmpresa(createDTO.getCodigo(), createDTO.getEmpresaId()).isPresent()) {
            throw new ConflictException("Já existe uma loja com o código '" + createDTO.getCodigo() + "' para esta empresa");
        }
        
        Loja loja = Loja.builder()
                .nome(createDTO.getNome())
                .codigo(createDTO.getCodigo())
                .descricao(createDTO.getDescricao())
                .endereco(createDTO.getEndereco())
                .numero(createDTO.getNumero())
                .complemento(createDTO.getComplemento())
                .bairro(createDTO.getBairro())
                .cidade(createDTO.getCidade())
                .uf(createDTO.getUf())
                .cep(createDTO.getCep())
                .telefone(createDTO.getTelefone())
                .ativo(createDTO.getAtivo() != null ? createDTO.getAtivo() : true)
                .empresa(empresa)
                .build();
        
        Loja savedLoja = lojaRepository.save(loja);
        log.info("Loja criada com sucesso: ID {}", savedLoja.getId());
        
        return convertToDTO(savedLoja);
    }

    /**
     * Atualiza uma loja existente.
     * 
     * @param id ID da loja
     * @param updateDTO Dados para atualização
     * @return DTO da loja atualizada
     * @throws EntityNotFoundException se loja não encontrada
     * @throws ConflictException se dados inválidos
     */
    @Transactional
    public LojaDTO update(Long id, LojaUpdateDTO updateDTO) {
        log.info("Atualizando loja ID: {}", id);
        
        Loja loja = lojaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com ID: " + id));
        
        // Validar se código já existe para a empresa (excluindo a loja atual)
        if (lojaRepository.existsByCodigoAndEmpresaAndIdNot(updateDTO.getCodigo(), loja.getEmpresa().getId(), id)) {
            throw new ConflictException("Já existe uma loja com o código '" + updateDTO.getCodigo() + "' para esta empresa");
        }
        
        loja.setNome(updateDTO.getNome());
        loja.setCodigo(updateDTO.getCodigo());
        loja.setDescricao(updateDTO.getDescricao());
        loja.setEndereco(updateDTO.getEndereco());
        loja.setNumero(updateDTO.getNumero());
        loja.setComplemento(updateDTO.getComplemento());
        loja.setBairro(updateDTO.getBairro());
        loja.setCidade(updateDTO.getCidade());
        loja.setUf(updateDTO.getUf());
        loja.setCep(updateDTO.getCep());
        loja.setTelefone(updateDTO.getTelefone());
        
        if (updateDTO.getAtivo() != null) {
            loja.setAtivo(updateDTO.getAtivo());
        }
        
        Loja savedLoja = lojaRepository.save(loja);
        log.info("Loja atualizada com sucesso: ID {}", savedLoja.getId());
        
        return convertToDTO(savedLoja);
    }

    /**
     * Altera o status de uma loja.
     * 
     * @param id ID da loja
     * @param ativo Novo status
     * @return DTO da loja atualizada
     * @throws EntityNotFoundException se loja não encontrada
     */
    @Transactional
    public LojaDTO alterarStatus(Long id, Boolean ativo) {
        log.info("Alterando status da loja ID: {} para {}", id, ativo);
        
        Loja loja = lojaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com ID: " + id));
        
        loja.setAtivo(ativo);
        Loja savedLoja = lojaRepository.save(loja);
        
        log.info("Status da loja alterado com sucesso: ID {}", savedLoja.getId());
        return convertToDTO(savedLoja);
    }

    /**
     * Exclui uma loja (soft delete).
     * 
     * @param id ID da loja
     * @throws EntityNotFoundException se loja não encontrada
     */
    @Transactional
    public void delete(Long id) {
        log.info("Excluindo loja ID: {}", id);
        
        Loja loja = lojaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada com ID: " + id));
        
        loja.setDeleted(true);
        lojaRepository.save(loja);
        
        log.info("Loja excluída com sucesso: ID {}", id);
    }

    /**
     * Busca estatísticas das lojas.
     * 
     * @return Estatísticas das lojas
     */
    public LojaStatsDTO getStats() {
        log.info("Buscando estatísticas das lojas");
        
        Long totalLojas = lojaRepository.countByDeletedFalse();
        Long lojasAtivas = lojaRepository.countByAtivoTrueAndDeletedFalse();
        Long lojasInativas = totalLojas - lojasAtivas;
        Long totalUsuarios = lojaRepository.countUsuariosByDeletedFalse();
        Long totalEmpresas = empresaRepository.countByDeletedFalse();
        
        return LojaStatsDTO.builder()
                .totalLojas(totalLojas)
                .lojasAtivas(lojasAtivas)
                .lojasInativas(lojasInativas)
                .totalUsuarios(totalUsuarios)
                .totalEmpresas(totalEmpresas)
                .build();
    }

    /**
     * Converte entidade Loja para DTO.
     * 
     * @param loja Entidade Loja
     * @return DTO da loja
     */
    private LojaDTO convertToDTO(Loja loja) {
        return LojaDTO.builder()
                .id(loja.getId())
                .nome(loja.getNome())
                .codigo(loja.getCodigo())
                .descricao(loja.getDescricao())
                .endereco(loja.getEndereco())
                .numero(loja.getNumero())
                .complemento(loja.getComplemento())
                .bairro(loja.getBairro())
                .cidade(loja.getCidade())
                .uf(loja.getUf())
                .cep(loja.getCep())
                .telefone(loja.getTelefone())
                .ativo(loja.getAtivo())
                .enderecoCompleto(loja.getEnderecoCompleto())
                .empresaId(loja.getEmpresa().getId())
                .empresaRazaoSocial(loja.getEmpresa().getRazaoSocial())
                .createdAt(loja.getCreatedAt())
                .updatedAt(loja.getUpdatedAt())
                .build();
    }
}
