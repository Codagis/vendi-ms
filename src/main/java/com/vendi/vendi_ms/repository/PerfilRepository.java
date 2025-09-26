package com.vendi.vendi_ms.repository;

import com.vendi.vendi_ms.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Perfil.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    @Query("SELECT p FROM Perfil p WHERE p.codigo = :codigo AND p.deleted = false")
    Optional<Perfil> findByCodigoAndNotDeleted(@Param("codigo") String codigo);

    @Query("SELECT p FROM Perfil p WHERE p.ativo = true AND p.deleted = false ORDER BY p.nome")
    List<Perfil> findAtivos();

    List<Perfil> findByAtivoTrue();

    @Query("SELECT p FROM Perfil p WHERE p.sistema = true AND p.deleted = false")
    List<Perfil> findSistema();

    @Query("SELECT COUNT(p) > 0 FROM Perfil p WHERE p.codigo = :codigo AND p.id != :id AND p.deleted = false")
    boolean existsByCodigoAndIdNot(@Param("codigo") String codigo, @Param("id") Long id);

    @Query("SELECT p FROM Perfil p WHERE p.deleted = false ORDER BY p.nome")
    List<Perfil> findAllNotDeleted();
}
