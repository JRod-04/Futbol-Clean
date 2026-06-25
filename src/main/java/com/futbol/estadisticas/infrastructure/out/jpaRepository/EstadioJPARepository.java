package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;

public interface EstadioJPARepository extends JpaRepository<EstadioJPAEntity, UUID>{
@Query("SELECT e FROM EstadioJPAEntity e JOIN e.clubPrincipal c WHERE c.idEquipo = :idClub")
    Optional<EstadioJPAEntity> findByClubPrincipal(@Param("idClub") UUID idClub);
}
