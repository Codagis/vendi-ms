package com.vendi.vendi_ms.service;

import com.vendi.vendi_ms.dto.EmpresaCreateDTO;
import com.vendi.vendi_ms.dto.EmpresaDTO;
import com.vendi.vendi_ms.dto.EmpresaStatsDTO;
import com.vendi.vendi_ms.dto.EmpresaUpdateDTO;
import com.vendi.vendi_ms.exception.EntityNotFoundException;
import com.vendi.vendi_ms.exception.ConflictException;
import com.vendi.vendi_ms.model.Empresa;
import com.vendi.vendi_ms.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final GoogleCloudStorageService storageService;

    @Transactional(readOnly = true)
    public List<EmpresaDTO> listar(String razaoSocial, String nomeFantasia, String cnpj, String email, Boolean ativo) {
        log.info("Buscando empresas com filtros: razaoSocial={}, nomeFantasia={}, cnpj={}, email={}, ativo={}", 
                razaoSocial, nomeFantasia, cnpj, email, ativo);
        
        List<Empresa> empresas = empresaRepository.findWithFilters(razaoSocial, nomeFantasia, cnpj, email, ativo);
        
        return empresas.stream()
                .map(this::mapearEntityParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaDTO buscarPorId(Long id) {
        log.info("Buscando empresa por ID: {}", id);
        
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        
        return mapearEntityParaDTO(empresa);
    }

    @Transactional
    public EmpresaDTO criar(EmpresaCreateDTO dto) {
        log.info("Criando empresa: {}", dto.getRazaoSocial());

        if (empresaRepository.findByCnpjAndNotDeleted(dto.getCnpj()).isPresent()) {
            throw new ConflictException("CNPJ já existe");
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (empresaRepository.findByEmailAndNotDeleted(dto.getEmail()).isPresent()) {
                throw new ConflictException("Email já existe");
            }
        }

        Empresa empresa = mapearCreateDTOParaEntity(dto);
        empresa.setCreatedAt(LocalDateTime.now());
        empresa.setUpdatedAt(LocalDateTime.now());

        Empresa empresaSalva = empresaRepository.save(empresa);
        log.info("Empresa criada com sucesso: {}", empresaSalva.getId());

        return mapearEntityParaDTO(empresaSalva);
    }

    @Transactional
    public EmpresaDTO atualizar(Long id, EmpresaUpdateDTO dto) {
        log.info("Atualizando empresa: {}", id);

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        if (empresaRepository.existsByCnpjAndIdNot(dto.getCnpj(), id)) {
            throw new ConflictException("CNPJ já existe");
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (empresaRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw new ConflictException("Email já existe");
            }
        }

        mapearUpdateDTOParaEntity(dto, empresa);
        empresa.setUpdatedAt(LocalDateTime.now());

        Empresa empresaSalva = empresaRepository.save(empresa);
        log.info("Empresa atualizada com sucesso: {}", empresaSalva.getId());

        return mapearEntityParaDTO(empresaSalva);
    }

    @Transactional
    public void remover(Long id) {
        log.info("Removendo empresa: {}", id);
        
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        
        empresa.setDeleted(true);
        empresa.setUpdatedAt(LocalDateTime.now());
        
        empresaRepository.save(empresa);
        log.info("Empresa removida com sucesso: {}", id);
    }

    @Transactional
    public EmpresaDTO alterarStatus(Long id, Boolean ativo) {
        log.info("Alterando status da empresa {} para: {}", id, ativo);
        
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        
        empresa.setAtivo(ativo);
        empresa.setUpdatedAt(LocalDateTime.now());
        
        Empresa empresaSalva = empresaRepository.save(empresa);
        log.info("Status da empresa alterado com sucesso: {}", id);
        
        return mapearEntityParaDTO(empresaSalva);
    }

    @Transactional
    public String uploadLogo(Long id, MultipartFile file) throws IOException {
        log.info("Fazendo upload da logo para empresa: {}", id);
        
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        if (empresa.getUrlLogo() != null) {
            storageService.deleteLogo(empresa.getUrlLogo());
        }

        String urlLogo = storageService.uploadLogo(file, empresa.getRazaoSocial());
        empresa.setUrlLogo(urlLogo);
        empresa.setUpdatedAt(LocalDateTime.now());

        empresaRepository.save(empresa);
        log.info("Logo da empresa atualizada com sucesso: {}", id);

        return urlLogo;
    }

    @Transactional(readOnly = true)
    public EmpresaStatsDTO buscarEstatisticas() {
        log.info("Buscando estatísticas das empresas");
        
        try {
            return buscarEstatisticasAlternativo();
        } catch (Exception e) {
            log.error("Erro ao buscar estatísticas: {}", e.getMessage(), e);
            return new EmpresaStatsDTO(0L, 0L, 0L, 0L, 0L);
        }
    }
    
    @Transactional(readOnly = true)
    private EmpresaStatsDTO buscarEstatisticasAlternativo() {
        log.info("Usando método alternativo para buscar estatísticas");
        
        try {
            long totalEmpresas = empresaRepository.countByDeletedFalse();
            long empresasAtivas = empresaRepository.countByAtivoTrueAndDeletedFalse();
            long empresasInativas = totalEmpresas - empresasAtivas;
            
            EmpresaStatsDTO statsDTO = new EmpresaStatsDTO();
            statsDTO.setTotalEmpresas(totalEmpresas);
            statsDTO.setEmpresasAtivas(empresasAtivas);
            statsDTO.setEmpresasInativas(empresasInativas);
            statsDTO.setTotalLojas(0L);
            statsDTO.setTotalUsuarios(0L);
            
            log.info("Estatísticas encontradas: Total={}, Ativas={}, Inativas={}", 
                    statsDTO.getTotalEmpresas(), statsDTO.getEmpresasAtivas(), statsDTO.getEmpresasInativas());
            
            return statsDTO;
            
        } catch (Exception e) {
            log.error("Erro ao buscar estatísticas alternativas: {}", e.getMessage(), e);
            return new EmpresaStatsDTO(0L, 0L, 0L, 0L, 0L);
        }
    }

    private Empresa mapearCreateDTOParaEntity(EmpresaCreateDTO dto) {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setCnpj(dto.getCnpj());
        empresa.setInscricaoEstadual(dto.getInscricaoEstadual());
        empresa.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        empresa.setEndereco(dto.getEndereco());
        empresa.setNumero(dto.getNumero());
        empresa.setComplemento(dto.getComplemento());
        empresa.setBairro(dto.getBairro());
        empresa.setCidade(dto.getCidade());
        empresa.setUf(dto.getUf());
        empresa.setCep(dto.getCep());
        empresa.setTelefone(dto.getTelefone());
        empresa.setEmail(dto.getEmail());
        empresa.setSite(dto.getSite());
        empresa.setAtivo(dto.getAtivo());
        empresa.setDeleted(false);
        
        return empresa;
    }

    private void mapearUpdateDTOParaEntity(EmpresaUpdateDTO dto, Empresa empresa) {
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setCnpj(dto.getCnpj());
        empresa.setInscricaoEstadual(dto.getInscricaoEstadual());
        empresa.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        empresa.setEndereco(dto.getEndereco());
        empresa.setNumero(dto.getNumero());
        empresa.setComplemento(dto.getComplemento());
        empresa.setBairro(dto.getBairro());
        empresa.setCidade(dto.getCidade());
        empresa.setUf(dto.getUf());
        empresa.setCep(dto.getCep());
        empresa.setTelefone(dto.getTelefone());
        empresa.setEmail(dto.getEmail());
        empresa.setSite(dto.getSite());
        empresa.setAtivo(dto.getAtivo());
    }

    private EmpresaDTO mapearEntityParaDTO(Empresa empresa) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(empresa.getId());
        dto.setRazaoSocial(empresa.getRazaoSocial());
        dto.setNomeFantasia(empresa.getNomeFantasia());
        dto.setCnpj(empresa.getCnpj());
        dto.setInscricaoEstadual(empresa.getInscricaoEstadual());
        dto.setInscricaoMunicipal(empresa.getInscricaoMunicipal());
        dto.setEndereco(empresa.getEndereco());
        dto.setNumero(empresa.getNumero());
        dto.setComplemento(empresa.getComplemento());
        dto.setBairro(empresa.getBairro());
        dto.setCidade(empresa.getCidade());
        dto.setUf(empresa.getUf());
        dto.setCep(empresa.getCep());
        dto.setTelefone(empresa.getTelefone());
        dto.setEmail(empresa.getEmail());
        dto.setSite(empresa.getSite());
        dto.setAtivo(empresa.getAtivo());
        dto.setUrlLogo(empresa.getUrlLogo());
        dto.setEnderecoCompleto(empresa.getEnderecoCompleto());
        dto.setCreatedAt(empresa.getCreatedAt());
        dto.setUpdatedAt(empresa.getUpdatedAt());
        
        return dto;
    }

}
