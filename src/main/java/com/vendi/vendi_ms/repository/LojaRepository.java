package com.vendi.vendi_ms.repository;

import com.vendi.vendi_ms.model.Loja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Loja.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {

    @Query("SELECT l FROM Loja l WHERE l.codigo = :codigo AND l.empresa.id = :empresaId AND l.deleted = false")
    Optional<Loja> findByCodigoAndEmpresa(@Param("codigo") String codigo, @Param("empresaId") Long empresaId);

    @Query("SELECT l FROM Loja l WHERE l.empresa.id = :empresaId AND l.ativo = true AND l.deleted = false ORDER BY l.nome")
    List<Loja> findAtivasByEmpresa(@Param("empresaId") Long empresaId);

    @Query("SELECT l FROM Loja l WHERE l.ativo = true AND l.deleted = false ORDER BY l.empresa.razaoSocial, l.nome")
    List<Loja> findAtivas();

    @Query("SELECT l FROM Loja l WHERE LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND l.deleted = false")
    List<Loja> findByNomeContaining(@Param("nome") String nome);

    @Query("SELECT COUNT(l) > 0 FROM Loja l WHERE l.codigo = :codigo AND l.empresa.id = :empresaId AND l.id != :id AND l.deleted = false")
    boolean existsByCodigoAndEmpresaAndIdNot(@Param("codigo") String codigo, @Param("empresaId") Long empresaId, @Param("id") Long id);

    @Query("SELECT l FROM Loja l WHERE l.deleted = false")
    Page<Loja> findByDeletedFalse(Pageable pageable);

    @Query("SELECT l FROM Loja l WHERE l.id = :id AND l.deleted = false")
    Optional<Loja> findByIdAndDeletedFalse(@Param("id") Long id);

    @Query("SELECT COUNT(l) FROM Loja l WHERE l.deleted = false")
    Long countByDeletedFalse();

    @Query("SELECT COUNT(l) FROM Loja l WHERE l.ativo = true AND l.deleted = false")
    Long countByAtivoTrueAndDeletedFalse();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.loja IS NOT NULL AND u.deleted = false")
    Long countUsuariosByDeletedFalse();

    @Query("SELECT l FROM Loja l WHERE l.deleted = false ORDER BY l.empresa.razaoSocial, l.nome")
    List<Loja> findByDeletedFalse();

    @Query("SELECT l FROM Loja l WHERE (LOWER(l.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(l.empresa.razaoSocial) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND l.deleted = false ORDER BY l.empresa.razaoSocial, l.nome")
    List<Loja> findByNomeContainingOrEmpresaRazaoSocialContaining(@Param("searchTerm") String searchTerm);
}
