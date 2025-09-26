package com.vendi.vendi_ms.config;

import com.vendi.vendi_ms.model.Usuario;
import com.vendi.vendi_ms.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementação do UserDetailsService para autenticação.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Carregando usuário: {}", username);
        
        Usuario usuario = usuarioRepository.findByUsernameWithPerfil(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        String role = usuario.getPerfil() != null ? 
            "ROLE_" + usuario.getPerfil().getCodigo().toUpperCase() : 
            "ROLE_USER";

        return new User(
            usuario.getUsername(),
            usuario.getSenha(),
            usuario.isAtivo(),
            true,
            true,
            !usuario.isContaBloqueada(),
            Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
