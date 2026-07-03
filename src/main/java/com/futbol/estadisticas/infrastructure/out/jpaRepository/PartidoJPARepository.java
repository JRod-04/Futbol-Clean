package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PartidoJPAEntity;

public interface PartidoJPARepository extends JpaRepository<PartidoJPAEntity, UUID>{
 @Query("""
           SELECT p FROM PartidoJPAEntity p
           WHERE p.equipoLocal.idEquipo = :idClub
              OR p.equipoVisitante.idEquipo = :idClub
           """)
    List<PartidoJPAEntity> findByClub(@Param("idClub") UUID idClub);
 
    List<PartidoJPAEntity> findByCompeticionIdCompeticion(UUID idCompeticion);
 
    List<PartidoJPAEntity> findByEstado(EstadoPartido estado);
 
    List<PartidoJPAEntity> findByFechaYHoraBetween(LocalDateTime desde, LocalDateTime hasta);
 
    List<PartidoJPAEntity> findByArbitroIdArbitro(UUID idArbitro);

   @Query("""
           SELECT p FROM PartidoJPAEntity p
           WHERE p.competicion.idCompeticion = :idCompeticion
             AND p.estado IN (
                 'FINALIZADO',
                 'PRIMER_TIEMPO',
                 'SEGUNDO_TIEMPO',
                 'ENTRETIEMPO',
                 'PRORROGA',
                 'PENALTIS'
             )
           """)
   List<PartidoJPAEntity> findClasificacion(@Param("idCompeticion") UUID idCompeticion);
}
