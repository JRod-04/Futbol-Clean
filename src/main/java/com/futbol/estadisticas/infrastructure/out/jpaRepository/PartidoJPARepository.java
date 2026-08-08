package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PartidoJPAEntity;

public interface PartidoJPARepository extends JpaRepository<PartidoJPAEntity, UUID>{
 @Query("""
           SELECT p FROM PartidoJPAEntity p
           WHERE p.equipoLocal.idEquipo = :idEquipo
              OR p.equipoVisitante.idEquipo = :idEquipo
           """)
    List<PartidoJPAEntity> findByEquipo(@Param("idEquipo") UUID idEquipo);
 
    List<PartidoJPAEntity> findByCompeticionIdCompeticion(UUID idCompeticion);
 

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

    @Modifying
    @Query("UPDATE PartidoJPAEntity p SET p.golesLocal = :golesLocal, p.golesVisitante = :golesVisitante WHERE p.idPartido = :id")
    void updateGoles(@Param("id") UUID id, @Param("golesLocal") int golesLocal, @Param("golesVisitante") int golesVisitante);

    @Query("SELECT p FROM PartidoJPAEntity p " +
            "WHERE DATE(p.fechaYHora) = :fecha " +
            "ORDER BY p.fechaYHora ASC")
    Page<PartidoJPAEntity> findByFecha(@Param("fecha") LocalDate fecha, Pageable pageable);
}
