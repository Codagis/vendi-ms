package com.vendi.vendi_ms.repository;

import com.vendi.vendi_ms.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Empresa.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Query("SELECT e FROM Empresa e WHERE e.cnpj = :cnpj AND e.deleted = false")
    Optional<Empresa> findByCnpjAndNotDeleted(@Param("cnpj") String cnpj);

    @Query("SELECT e FROM Empresa e WHERE e.ativo = true AND e.deleted = false ORDER BY e.razaoSocial")
    List<Empresa> findAtivas();

    @Query("SELECT e FROM Empresa e WHERE LOWER(e.razaoSocial) LIKE LOWER(CONCAT('%', :razaoSocial, '%')) AND e.deleted = false")
    List<Empresa> findByRazaoSocialContaining(@Param("razaoSocial") String razaoSocial);

    @Query("SELECT COUNT(e) > 0 FROM Empresa e WHERE e.cnpj = :cnpj AND e.id != :id AND e.deleted = false")
    boolean existsByCnpjAndIdNot(@Param("cnpj") String cnpj, @Param("id") Long id);
}
