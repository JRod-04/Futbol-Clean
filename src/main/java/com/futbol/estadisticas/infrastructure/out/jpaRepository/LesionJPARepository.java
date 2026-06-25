package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.domain.model.enums.Gravedad;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.LesionJPAEntity;

public interface LesionJPARepository extends JpaRepository<LesionJPAEntity, UUID>{
    List<LesionJPAEntity> findByJugadorIdPersonal(UUID idJugador);
 
    @Query("""
           SELECT l FROM LesionJPAEntity l
           WHERE l.jugador.idPersonal = :idJugador
             AND l.curada = false
             AND l.fechaInicio < :hoy
             AND (l.fechaFin IS NULL OR l.fechaFin > :hoy)
           """)
    List<LesionJPAEntity> findActivasByJugador(@Param("idJugador") UUID idJugador,
                                               @Param("hoy") LocalDate hoy);
 
    List<LesionJPAEntity> findByGravedad(Gravedad gravedad);
 
    @Query("""
           SELECT l FROM LesionJPAEntity l
           WHERE l.curada = false
             AND l.fechaInicio < :hoy
             AND (l.fechaFin IS NULL OR l.fechaFin > :hoy)
           """)
    List<LesionJPAEntity> findActivas(@Param("hoy") LocalDate hoy);
}
