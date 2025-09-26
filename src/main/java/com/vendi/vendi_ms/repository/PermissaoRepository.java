package com.vendi.vendi_ms.repository;

import com.vendi.vendi_ms.model.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Permissao.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

    @Query("SELECT p FROM Permissao p WHERE p.chave = :chave AND p.deleted = false")
    Optional<Permissao> findByChaveAndNotDeleted(@Param("chave") String chave);

    @Query("SELECT p FROM Permissao p WHERE p.ativo = true AND p.deleted = false ORDER BY p.categoria, p.nome")
    List<Permissao> findAtivas();

    @Query("SELECT p FROM Permissao p WHERE p.categoria = :categoria AND p.ativo = true AND p.deleted = false ORDER BY p.nome")
    List<Permissao> findByCategoria(@Param("categoria") String categoria);

    @Query("SELECT p FROM Permissao p JOIN p.perfis pf WHERE pf.id = :perfilId AND p.ativo = true AND p.deleted = false ORDER BY p.categoria, p.nome")
    List<Permissao> findByPerfil(@Param("perfilId") Long perfilId);

    @Query("SELECT COUNT(p) > 0 FROM Permissao p WHERE p.chave = :chave AND p.id != :id AND p.deleted = false")
    boolean existsByChaveAndIdNot(@Param("chave") String chave, @Param("id") Long id);

    @Query("SELECT p FROM Permissao p WHERE p.chave IN :chaves AND p.ativo = true AND p.deleted = false")
    List<Permissao> findByChaveIn(@Param("chaves") List<String> chaves);

    @Query("SELECT p FROM Permissao p WHERE p.deleted = false ORDER BY p.categoria, p.nome")
    List<Permissao> findAllNotDeleted();
}
