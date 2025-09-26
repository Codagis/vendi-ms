package com.vendi.vendi_ms.service;

import com.vendi.vendi_ms.dto.LoginRequest;
import com.vendi.vendi_ms.dto.LoginResponse;
import com.vendi.vendi_ms.dto.RefreshTokenRequest;
import com.vendi.vendi_ms.dto.RefreshTokenResponse;
import com.vendi.vendi_ms.exception.AccountInactiveException;
import com.vendi.vendi_ms.exception.EntityNotFoundException;
import com.vendi.vendi_ms.exception.InvalidCredentialsException;
import com.vendi.vendi_ms.model.Usuario;
import com.vendi.vendi_ms.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsável pela autenticação de usuários.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse authenticate(LoginRequest request) {
        log.info("Tentativa de login para username: {}", request.getUsername());
        
        Usuario usuario = usuarioRepository.findByUsernameAndNotDeleted(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!usuario.podeFazerLogin()) {
            log.warn("Usuário {} não pode fazer login - conta inativa ou bloqueada", request.getUsername());
            throw new AccountInactiveException("Conta inativa ou bloqueada");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getSenha())) {
            log.warn("Senha incorreta para usuário: {}", request.getUsername());
            usuario.incrementarTentativasFalhadas();
            usuarioRepository.save(usuario);
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        
        usuario.resetarTentativasFalhadas();
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        log.info("Login bem-sucedido para usuário: {}", request.getUsername());

        return buildLoginResponse(usuario);
    }

    private LoginResponse buildLoginResponse(Usuario usuario) {
        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Renovando token com refresh token");

        
        if (!jwtService.isRefreshToken(request.getRefreshToken())) {
            throw new InvalidCredentialsException("Refresh token inválido");
        }

        if (!jwtService.isTokenValid(request.getRefreshToken())) {
            throw new InvalidCredentialsException("Refresh token expirado");
        }

        
        Long userId = jwtService.extractUserIdFromRefreshToken(request.getRefreshToken());
        if (userId == null) {
            throw new InvalidCredentialsException("Refresh token inválido");
        }

        
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        
        if (!usuario.getAtivo()) {
            throw new AccountInactiveException("Conta inativa");
        }

        
        String newAccessToken = jwtService.generateToken(usuario);
        String newRefreshToken = jwtService.generateRefreshToken(usuario);

        log.info("Token renovado com sucesso para usuário: {}", usuario.getUsername());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(28800L) // 8 horas em segundos
                .build();
    }
}
