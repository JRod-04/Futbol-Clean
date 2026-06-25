package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.CompeticionJPAEntity;

public interface CompeticionJPARepository extends JpaRepository <CompeticionJPAEntity, UUID> {
    @Query("SELECT c FROM CompeticionJPAEntity c WHERE :ahora BETWEEN c.fechaInicio AND c.fechaFin")
    List<CompeticionJPAEntity> findActivas(@Param("ahora") LocalDateTime ahora);
 
    @Query("SELECT c FROM CompeticionJPAEntity c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%',:nombre,'%'))")
    List<CompeticionJPAEntity> findByNombre(@Param("nombre") String nombre);
}
