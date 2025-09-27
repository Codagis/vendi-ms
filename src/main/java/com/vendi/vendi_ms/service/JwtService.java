package com.vendi.vendi_ms.service;

import com.vendi.vendi_ms.dto.UserTokenData;
import com.vendi.vendi_ms.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service responsável pela geração e validação de tokens JWT.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration:28800000}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    public String generateToken(Usuario usuario) {
        return generateToken(new HashMap<>(), usuario);
    }

    public String generateToken(Map<String, Object> extraClaims, Usuario usuario) {
        return buildToken(extraClaims, usuario, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, Usuario usuario, long expiration) {
        UserTokenData userData = UserTokenData.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .root(usuario.getRoot())
                .ativo(usuario.getAtivo())
                .contaBloqueada(usuario.getContaBloqueada())
                .perfilNome(usuario.getPerfil() != null ? usuario.getPerfil().getNome() : null)
                .permissoes(usuario.getPerfil() != null && usuario.getPerfil().getPermissoes() != null 
                    ? usuario.getPerfil().getPermissoes().stream()
                        .map(p -> p.getChave())
                        .collect(Collectors.toList())
                    : List.of())
                .empresaId(usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null)
                .empresaNome(usuario.getEmpresa() != null ? usuario.getEmpresa().getRazaoSocial() : null)
                .lojaId(usuario.getLoja() != null ? usuario.getLoja().getId() : null)
                .lojaNome(usuario.getLoja() != null ? usuario.getLoja().getNome() : null)
                .build();

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(usuario.getUsername())
                .claim("userData", userData)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public UserTokenData extractUserData(String token) {
        return extractClaim(token, claims -> {
            Map<String, Object> userDataMap = (Map<String, Object>) claims.get("userData");
            if (userDataMap == null) {
                return null;
            }
            
            return UserTokenData.builder()
                    .id(((Number) userDataMap.get("id")).longValue())
                    .username((String) userDataMap.get("username"))
                    .email((String) userDataMap.get("email"))
                    .nome((String) userDataMap.get("nome"))
                    .root((Boolean) userDataMap.get("root"))
                    .ativo((Boolean) userDataMap.get("ativo"))
                    .contaBloqueada((Boolean) userDataMap.get("contaBloqueada"))
                    .perfilNome((String) userDataMap.get("perfilNome"))
                    .permissoes((List<String>) userDataMap.get("permissoes"))
                    .empresaId(userDataMap.get("empresaId") != null ? ((Number) userDataMap.get("empresaId")).longValue() : null)
                    .empresaNome((String) userDataMap.get("empresaNome"))
                    .lojaId(userDataMap.get("lojaId") != null ? ((Number) userDataMap.get("lojaId")).longValue() : null)
                    .lojaNome((String) userDataMap.get("lojaNome"))
                    .build();
        });
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateRefreshToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("userId", usuario.getId());
        return buildToken(claims, usuario, refreshTokenExpiration);
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractUserIdFromRefreshToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            return ((Number) claims.get("userId")).longValue();
        } catch (Exception e) {
            log.error("Erro ao extrair userId do refresh token: {}", e.getMessage());
            return null;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
