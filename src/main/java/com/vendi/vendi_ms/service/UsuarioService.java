package com.vendi.vendi_ms.service;

import com.vendi.vendi_ms.dto.UsuarioCreateDTO;
import com.vendi.vendi_ms.dto.UsuarioDTO;
import com.vendi.vendi_ms.dto.UsuarioUpdateDTO;
import com.vendi.vendi_ms.dto.UsuarioStatsDTO;
import com.vendi.vendi_ms.exception.EntityNotFoundException;
import com.vendi.vendi_ms.exception.ConflictException;
import com.vendi.vendi_ms.model.Empresa;
import com.vendi.vendi_ms.model.Loja;
import com.vendi.vendi_ms.model.Perfil;
import com.vendi.vendi_ms.model.Permissao;
import com.vendi.vendi_ms.model.Usuario;
import com.vendi.vendi_ms.repository.EmpresaRepository;
import com.vendi.vendi_ms.repository.LojaRepository;
import com.vendi.vendi_ms.repository.PerfilRepository;
import com.vendi.vendi_ms.repository.PermissaoRepository;
import com.vendi.vendi_ms.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pelo gerenciamento de usuários do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PermissaoRepository permissaoRepository;
    private final EmpresaRepository empresaRepository;
    private final LojaRepository lojaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listar(String nome, String username, String email, 
                                  Long perfilId, Long empresaId, Long lojaId, Boolean ativo) {
        log.info("Buscando usuários com filtros: nome={}, username={}, email={}, perfilId={}, empresaId={}, lojaId={}, ativo={}", 
                nome, username, email, perfilId, empresaId, lojaId, ativo);
        
        List<Usuario> usuarios = usuarioRepository.findWithFilters(nome, username, email, perfilId, empresaId, lojaId, ativo);
        
        
        usuarios.forEach(usuario -> {
            if (usuario.getPerfil() != null) {
                
                usuario.getPerfil().getPermissoes().size();
            }
        });
        
        return usuarios.stream()
                .map(this::mapearEntityParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        
        return mapearEntityParaDTO(usuario);
    }

    @Transactional
    public UsuarioDTO criar(UsuarioCreateDTO dto) {
        log.info("Criando usuário: {}", dto.getUsername());

        
        if (usuarioRepository.findByUsernameAndNotDeleted(dto.getUsername()).isPresent()) {
            throw new ConflictException("Username já existe");
        }

        
        if (usuarioRepository.findByEmailAndNotDeleted(dto.getEmail()).isPresent()) {
            throw new ConflictException("Email já existe");
        }

        if (dto.getCracha() != null && !dto.getCracha().trim().isEmpty()) {
            if (usuarioRepository.findByCrachaAndNotDeleted(dto.getCracha()).isPresent()) {
                throw new ConflictException("Crachá já existe");
            }
        }

        
        Perfil perfil = perfilRepository.findById(dto.getPerfilId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        
        Loja loja = null;
        if (dto.getLojaId() != null) {
            loja = lojaRepository.findById(dto.getLojaId())
                    .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada"));
        }

        
        Usuario usuario = mapearCreateDTOParaEntity(dto, perfil, empresa, loja);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setUpdatedAt(LocalDateTime.now());

        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso: {}", usuarioSalvo.getId());

        return mapearEntityParaDTO(usuarioSalvo);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioUpdateDTO dto) {
        log.info("Atualizando usuário: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (usuarioRepository.existsByUsernameAndIdNot(dto.getUsername(), id)) {
            throw new ConflictException("Username já existe");
        }

        if (usuarioRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ConflictException("Email já existe");
        }

        if (dto.getCracha() != null && !dto.getCracha().trim().isEmpty()) {
            if (usuarioRepository.existsByCrachaAndIdNot(dto.getCracha(), id)) {
                throw new ConflictException("Crachá já existe");
            }
        }

        
        Perfil perfil = perfilRepository.findById(dto.getPerfilId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        
        Loja loja = null;
        if (dto.getLojaId() != null) {
            loja = lojaRepository.findById(dto.getLojaId())
                    .orElseThrow(() -> new EntityNotFoundException("Loja não encontrada"));
        }

        
        mapearUpdateDTOParaEntity(dto, usuario, perfil, empresa, loja);
        usuario.setUpdatedAt(LocalDateTime.now());

        
        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        
        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            atualizarPermissoesDoPerfil(perfil, dto.getPermissions());
        }

        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário atualizado com sucesso: {}", usuarioSalvo.getId());

        return mapearEntityParaDTO(usuarioSalvo);
    }

    @Transactional
    public void remover(Long id) {
        log.info("Removendo usuário: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        
        usuario.setDeleted(true);
        usuario.setUpdatedAt(LocalDateTime.now());
        
        usuarioRepository.save(usuario);
        log.info("Usuário removido com sucesso: {}", id);
    }

    @Transactional
    public UsuarioDTO alterarStatus(Long id, Boolean ativo) {
        log.info("Alterando status do usuário {} para: {}", id, ativo);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        
        usuario.setAtivo(ativo);
        usuario.setUpdatedAt(LocalDateTime.now());
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Status do usuário alterado com sucesso: {}", id);
        
        return mapearEntityParaDTO(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public UsuarioStatsDTO buscarEstatisticas() {
        log.info("Buscando estatísticas dos usuários");
        
        try {
            Object[] stats = usuarioRepository.findUsuarioStats();
            
            if (stats == null || stats.length == 0) {
                log.warn("Nenhuma estatística encontrada via query nativa");
                return buscarEstatisticasAlternativo();
            }
            
            log.info("Array de estatísticas recebido com {} elementos: {}", stats.length, java.util.Arrays.toString(stats));
            
            
            if (stats.length < 6) {
                log.warn("Array de estatísticas tem apenas {} elementos, esperado 6. Usando método alternativo.", stats.length);
                return buscarEstatisticasAlternativo();
            }
            
            UsuarioStatsDTO statsDTO = new UsuarioStatsDTO();
            
            
            statsDTO.setTotalUsuarios(convertToLong(stats[0]));
            statsDTO.setUsuariosAtivos(convertToLong(stats[1]));
            statsDTO.setUsuariosInativos(convertToLong(stats[2]));
            statsDTO.setAdministradores(convertToLong(stats[3]));
            statsDTO.setVendedores(convertToLong(stats[4]));
            statsDTO.setOutrosPerfis(convertToLong(stats[5]));
            
            log.info("Estatísticas encontradas: Total={}, Ativos={}, Inativos={}, Admins={}, Vendedores={}, Outros={}", 
                    statsDTO.getTotalUsuarios(), statsDTO.getUsuariosAtivos(), statsDTO.getUsuariosInativos(),
                    statsDTO.getAdministradores(), statsDTO.getVendedores(), statsDTO.getOutrosPerfis());
            
            return statsDTO;
            
        } catch (Exception e) {
            log.error("Erro ao buscar estatísticas via query nativa: {}", e.getMessage());
            return buscarEstatisticasAlternativo();
        }
    }
    
    @Transactional(readOnly = true)
    private UsuarioStatsDTO buscarEstatisticasAlternativo() {
        log.info("Usando método alternativo para buscar estatísticas");
        
        try {
            
            List<Usuario> usuarios = usuarioRepository.findAllNotDeleted();
            
            UsuarioStatsDTO statsDTO = new UsuarioStatsDTO();
            statsDTO.setTotalUsuarios((long) usuarios.size());
            
            
            long usuariosAtivos = usuarios.stream().mapToLong(u -> u.getAtivo() ? 1 : 0).sum();
            statsDTO.setUsuariosAtivos(usuariosAtivos);
            statsDTO.setUsuariosInativos(statsDTO.getTotalUsuarios() - usuariosAtivos);
            
            
            long administradores = usuarios.stream()
                    .filter(u -> u.getPerfil() != null && "admin".equals(u.getPerfil().getCodigo()))
                    .count();
            statsDTO.setAdministradores(administradores);
            
            long vendedores = usuarios.stream()
                    .filter(u -> u.getPerfil() != null && "seller".equals(u.getPerfil().getCodigo()))
                    .count();
            statsDTO.setVendedores(vendedores);
            
            long outrosPerfis = usuarios.stream()
                    .filter(u -> u.getPerfil() != null && 
                            !"admin".equals(u.getPerfil().getCodigo()) && 
                            !"seller".equals(u.getPerfil().getCodigo()))
                    .count();
            statsDTO.setOutrosPerfis(outrosPerfis);
            
            log.info("Estatísticas alternativas encontradas: Total={}, Ativos={}, Inativos={}, Admins={}, Vendedores={}, Outros={}", 
                    statsDTO.getTotalUsuarios(), statsDTO.getUsuariosAtivos(), statsDTO.getUsuariosInativos(),
                    statsDTO.getAdministradores(), statsDTO.getVendedores(), statsDTO.getOutrosPerfis());
            
            return statsDTO;
            
        } catch (Exception e) {
            log.error("Erro ao buscar estatísticas alternativas: {}", e.getMessage());
            return new UsuarioStatsDTO(0L, 0L, 0L, 0L, 0L, 0L);
        }
    }

    private Usuario mapearCreateDTOParaEntity(UsuarioCreateDTO dto, Perfil perfil, Empresa empresa, Loja loja) {
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCracha(dto.getCracha());
        usuario.setPerfil(perfil);
        usuario.setEmpresa(empresa);
        usuario.setLoja(loja);
        usuario.setAtivo(true);
        usuario.setDeleted(false);
        usuario.setRoot(false);
        usuario.setContaBloqueada(false);
        usuario.setTentativasLoginFalhadas(0);
        
        
        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            atualizarPermissoesDoPerfil(perfil, dto.getPermissions());
        }
        
        return usuario;
    }

    private void mapearUpdateDTOParaEntity(UsuarioUpdateDTO dto, Usuario usuario, Perfil perfil, Empresa empresa, Loja loja) {
        usuario.setUsername(dto.getUsername());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCracha(dto.getCracha());
        usuario.setPerfil(perfil);
        usuario.setEmpresa(empresa);
        usuario.setLoja(loja);
        usuario.setAtivo(dto.getAtivo());
        
        
        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            atualizarPermissoesDoPerfil(perfil, dto.getPermissions());
        }
    }

    private UsuarioDTO mapearEntityParaDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setCracha(usuario.getCracha());
        dto.setAtivo(usuario.getAtivo());
        dto.setRoot(usuario.getRoot());
        dto.setContaBloqueada(usuario.getContaBloqueada());
        dto.setTentativasLoginFalhadas(usuario.getTentativasLoginFalhadas());
        dto.setUltimoLogin(usuario.getUltimoLogin());
        dto.setCreatedAt(usuario.getCreatedAt());
        dto.setUpdatedAt(usuario.getUpdatedAt());
        
        
        if (usuario.getPerfil() != null) {
            dto.setPerfilId(usuario.getPerfil().getId());
            dto.setPerfilNome(usuario.getPerfil().getNome());
            
            
            if (usuario.getPerfil().getPermissoes() != null) {
                dto.setPermissions(usuario.getPerfil().getPermissoes().stream()
                        .map(Permissao::getChave)
                        .collect(Collectors.toList()));
            }
        }
        
        
        if (usuario.getEmpresa() != null) {
            dto.setEmpresaId(usuario.getEmpresa().getId());
            dto.setEmpresaNome(usuario.getEmpresa().getRazaoSocial());
        }
        
        
        if (usuario.getLoja() != null) {
            dto.setLojaId(usuario.getLoja().getId());
            dto.setLojaNome(usuario.getLoja().getNome());
        }
        
        return dto;
    }

    private void atualizarPermissoesDoPerfil(Perfil perfil, List<String> chavesPermissoes) {
        log.info("Atualizando permissões do perfil {}: {}", perfil.getId(), chavesPermissoes);
        
        
        List<Permissao> novasPermissoes = permissaoRepository.findByChaveIn(chavesPermissoes);
        
        if (novasPermissoes.size() != chavesPermissoes.size()) {
            log.warn("Algumas permissões não foram encontradas. Esperadas: {}, Encontradas: {}", 
                    chavesPermissoes.size(), novasPermissoes.size());
        }
        
        
        perfil.setPermissoes(new java.util.HashSet<>(novasPermissoes));
        perfilRepository.save(perfil);
        
        log.info("Permissões do perfil {} atualizadas com sucesso", perfil.getId());
    }

    @Transactional(readOnly = true)
    public boolean podeSerDeletado(Long id) {
        log.info("Verificando se usuário {} pode ser deletado", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        
        
        if (usuario.getAtivo()) {
            long usuariosAtivos = usuarioRepository.countByAtivoTrueAndDeletedFalse();
            if (usuariosAtivos <= 1) {
                log.warn("Usuário {} é o último usuário ativo, não pode ser deletado", id);
                return false;
            }
        }
        
        
        boolean temVendas = usuarioRepository.hasVendasAssociadas(id);
        if (temVendas) {
            log.warn("Usuário {} tem vendas associadas, não pode ser deletado", id);
            return false;
        }
        
        
        boolean temMovimentacoes = usuarioRepository.hasMovimentacoesEstoque(id);
        if (temMovimentacoes) {
            log.warn("Usuário {} tem movimentações de estoque, não pode ser deletado", id);
            return false;
        }
        
        log.info("Usuário {} pode ser deletado", id);
        return true;
    }
    
    @Transactional(readOnly = true)
    public List<String> getRazoesNaoPodeDeletar(Long id) {
        log.info("Obtendo razões para não deletar usuário {}", id);
        
        List<String> razoes = new ArrayList<>();
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        
        
        if (usuario.getAtivo()) {
            long usuariosAtivos = usuarioRepository.countByAtivoTrueAndDeletedFalse();
            if (usuariosAtivos <= 1) {
                razoes.add("É o último usuário ativo do sistema");
            }
        }
        
        
        boolean temVendas = usuarioRepository.hasVendasAssociadas(id);
        if (temVendas) {
            razoes.add("Possui vendas associadas");
        }
        
        
        boolean temMovimentacoes = usuarioRepository.hasMovimentacoesEstoque(id);
        if (temMovimentacoes) {
            razoes.add("Possui movimentações de estoque");
        }
        
        return razoes;
    }

    private Long convertToLong(Object obj) {
        if (obj == null) {
            return 0L;
        }
        
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                log.warn("Erro ao converter string para Long: {}", obj);
                return 0L;
            }
        }
        
        log.warn("Tipo não esperado para conversão: {}", obj.getClass().getSimpleName());
        return 0L;
    }
}
