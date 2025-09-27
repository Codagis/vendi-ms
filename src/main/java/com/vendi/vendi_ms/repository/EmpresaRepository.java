package com.vendi.vendi_ms.repository;

import com.vendi.vendi_ms.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Query("SELECT e FROM Empresa e WHERE e.cnpj = :cnpj AND e.deleted = false")
    Optional<Empresa> findByCnpjAndNotDeleted(@Param("cnpj") String cnpj);

    @Query("SELECT e FROM Empresa e WHERE e.razaoSocial ILIKE %:razaoSocial% AND e.deleted = false")
    List<Empresa> findByRazaoSocialContainingIgnoreCase(@Param("razaoSocial") String razaoSocial);

    @Query("SELECT e FROM Empresa e WHERE e.nomeFantasia ILIKE %:nomeFantasia% AND e.deleted = false")
    List<Empresa> findByNomeFantasiaContainingIgnoreCase(@Param("nomeFantasia") String nomeFantasia);

    @Query("SELECT e FROM Empresa e WHERE e.email = :email AND e.deleted = false")
    Optional<Empresa> findByEmailAndNotDeleted(@Param("email") String email);

    @Query("SELECT e FROM Empresa e WHERE e.ativo = :ativo AND e.deleted = false")
    List<Empresa> findByAtivoAndNotDeleted(@Param("ativo") Boolean ativo);

    @Query("SELECT e FROM Empresa e WHERE e.deleted = false ORDER BY e.razaoSocial")
    List<Empresa> findAllNotDeleted();

    @Query(value = "SELECT e.* FROM empresas e WHERE " +
           "((:razaoSocial IS NULL OR e.razao_social ILIKE CONCAT('%', :razaoSocial, '%')) AND " +
           "(:nomeFantasia IS NULL OR e.nome_fantasia ILIKE CONCAT('%', :nomeFantasia, '%')) AND " +
           "(:cnpj IS NULL OR e.cnpj ILIKE CONCAT('%', :cnpj, '%')) AND " +
           "(:email IS NULL OR e.email ILIKE CONCAT('%', :email, '%'))) AND " +
           "(:ativo IS NULL OR e.ativo = :ativo) AND " +
           "e.deleted = false " +
           "ORDER BY e.razao_social", nativeQuery = true)
    List<Empresa> findWithFilters(@Param("razaoSocial") String razaoSocial,
                                  @Param("nomeFantasia") String nomeFantasia,
                                  @Param("cnpj") String cnpj,
                                  @Param("email") String email,
                                  @Param("ativo") Boolean ativo);

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.ativo = true AND e.deleted = false")
    long countByAtivoTrueAndDeletedFalse();

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.deleted = false")
    long countByDeletedFalse();


    @Query("SELECT COUNT(e) > 0 FROM Empresa e WHERE e.cnpj = :cnpj AND e.id != :id AND e.deleted = false")
    boolean existsByCnpjAndIdNot(@Param("cnpj") String cnpj, @Param("id") Long id);

    @Query("SELECT COUNT(e) > 0 FROM Empresa e WHERE e.email = :email AND e.id != :id AND e.deleted = false")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);

    @Query("SELECT e FROM Empresa e WHERE e.id = :id AND e.deleted = false")
    Optional<Empresa> findByIdAndDeletedFalse(@Param("id") Long id);
}