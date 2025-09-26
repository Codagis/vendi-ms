package com.vendi.vendi_ms.repository;

import com.vendi.vendi_ms.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Usuario.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.username = :username AND u.deleted = false")
    Optional<Usuario> findByUsernameAndNotDeleted(@Param("username") String username);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfil WHERE u.username = :username AND u.deleted = false")
    Optional<Usuario> findByUsernameWithPerfil(@Param("username") String username);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfil p LEFT JOIN FETCH p.permissoes WHERE u.id = :id AND u.deleted = false")
    Optional<Usuario> findByIdWithPerfilAndPermissoes(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.deleted = false")
    Optional<Usuario> findByEmailAndNotDeleted(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.cracha = :cracha AND u.deleted = false")
    Optional<Usuario> findByCrachaAndNotDeleted(@Param("cracha") String cracha);

    @Query("SELECT u FROM Usuario u WHERE u.empresa.id = :empresaId AND u.ativo = true AND u.deleted = false")
    List<Usuario> findAtivosByEmpresa(@Param("empresaId") Long empresaId);

    @Query("SELECT u FROM Usuario u WHERE u.loja.id = :lojaId AND u.ativo = true AND u.deleted = false")
    List<Usuario> findAtivosByLoja(@Param("lojaId") Long lojaId);

    @Query("SELECT u FROM Usuario u WHERE u.perfil.id = :perfilId AND u.deleted = false")
    List<Usuario> findByPerfil(@Param("perfilId") Long perfilId);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.username = :username AND u.id != :id AND u.deleted = false")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("id") Long id);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.email = :email AND u.id != :id AND u.deleted = false")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.cracha = :cracha AND u.id != :id AND u.deleted = false")
    boolean existsByCrachaAndIdNot(@Param("cracha") String cracha, @Param("id") Long id);

    @Query(value = "SELECT u.* FROM usuarios u WHERE " +
           "((:nome IS NULL OR u.nome ILIKE CONCAT('%', :nome, '%')) OR " +
           "(:username IS NULL OR u.username ILIKE CONCAT('%', :username, '%')) OR " +
           "(:email IS NULL OR u.email ILIKE CONCAT('%', :email, '%'))) AND " +
           "(:perfilId IS NULL OR u.perfil_id = :perfilId) AND " +
           "(:empresaId IS NULL OR u.empresa_id = :empresaId) AND " +
           "(:lojaId IS NULL OR u.loja_id = :lojaId) AND " +
           "(:ativo IS NULL OR u.ativo = :ativo) AND " +
           "u.deleted = false " +
           "ORDER BY u.nome", nativeQuery = true)
    List<Usuario> findWithFilters(@Param("nome") String nome,
                                  @Param("username") String username,
                                  @Param("email") String email,
                                  @Param("perfilId") Long perfilId,
                                  @Param("empresaId") Long empresaId,
                                  @Param("lojaId") Long lojaId,
                                  @Param("ativo") Boolean ativo);

    @Query("SELECT u FROM Usuario u WHERE u.deleted = false ORDER BY u.nome")
    List<Usuario> findAllNotDeleted();

    @Query(value = "SELECT " +
           "COALESCE(COUNT(*), 0) as total_usuarios, " +
           "COALESCE(COUNT(CASE WHEN u.ativo = true THEN 1 END), 0) as usuarios_ativos, " +
           "COALESCE(COUNT(CASE WHEN u.ativo = false THEN 1 END), 0) as usuarios_inativos, " +
           "COALESCE(COUNT(CASE WHEN p.codigo = 'admin' THEN 1 END), 0) as administradores, " +
           "COALESCE(COUNT(CASE WHEN p.codigo = 'seller' THEN 1 END), 0) as vendedores, " +
           "COALESCE(COUNT(CASE WHEN p.codigo NOT IN ('admin', 'seller') OR p.codigo IS NULL THEN 1 END), 0) as outros_perfis " +
           "FROM usuarios u " +
           "LEFT JOIN perfis p ON u.perfil_id = p.id " +
           "WHERE u.deleted = false", nativeQuery = true)
    Object[] findUsuarioStats();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.ativo = true AND u.deleted = false")
    long countByAtivoTrueAndDeletedFalse();

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
           "FROM vendas v WHERE v.usuario_id = :usuarioId AND v.deleted = false", nativeQuery = true)
    boolean hasVendasAssociadas(@Param("usuarioId") Long usuarioId);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
           "FROM movimentacoes_estoque m WHERE m.usuario_id = :usuarioId AND m.deleted = false", nativeQuery = true)
    boolean hasMovimentacoesEstoque(@Param("usuarioId") Long usuarioId);
}
