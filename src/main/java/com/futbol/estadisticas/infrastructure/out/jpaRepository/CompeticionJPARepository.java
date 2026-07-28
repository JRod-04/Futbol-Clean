package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.CompeticionJPAEntity;

public interface CompeticionJPARepository extends JpaRepository <CompeticionJPAEntity, UUID> {
    @Query("SELECT c FROM CompeticionJPAEntity c WHERE :ahora BETWEEN c.fechaInicio AND c.fechaFin")
    List<CompeticionJPAEntity> findActivas(@Param("ahora") LocalDateTime ahora);
 
    @Query("SELECT c FROM CompeticionJPAEntity c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%',:nombre,'%'))")
    List<CompeticionJPAEntity> findByNombre(@Param("nombre") String nombre);

    @Query("SELECT c FROM CompeticionJPAEntity c " +
            "WHERE LOWER(COALESCE(c.nombre, '')) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<CompeticionJPAEntity> buscarCompeticionPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT c FROM CompeticionJPAEntity c " +
            "LEFT JOIN FETCH c.partidos p " +
            "LEFT JOIN FETCH p.equipoLocal " +
            "LEFT JOIN FETCH p.equipoVisitante " +
            "WHERE c.idCompeticion = :id")
    Optional<CompeticionJPAEntity> findByIdWithPartidosAndEquipos(@Param("id") UUID id);

}
