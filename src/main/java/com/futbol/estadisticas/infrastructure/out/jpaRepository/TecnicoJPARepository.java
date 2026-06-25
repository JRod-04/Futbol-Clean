package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.TecnicoJPAEntity;

public interface TecnicoJPARepository extends JpaRepository<TecnicoJPAEntity,UUID>{
@Query("SELECT t FROM TecnicoJPAEntity t WHERE t.clubActual.idEquipo = :idClub")
    List<TecnicoJPAEntity> findByClub(@Param("idClub") UUID idClub);
 
    @Query("SELECT t FROM TecnicoJPAEntity t WHERE t.clubActual.idEquipo = :idClub")
    Optional<TecnicoJPAEntity> findTecnicoActualByClub(@Param("idClub") UUID idClub);
}
