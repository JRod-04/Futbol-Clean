package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.ContratoJPAEntity;

public interface ContratoJPARepository extends JpaRepository <ContratoJPAEntity, UUID>{
    List<ContratoJPAEntity> findByPersonalIdPersonal(UUID idPersonal);
 
    List<ContratoJPAEntity> findByEquipoIdEquipo(UUID idEquipo);

    @Query("SELECT c FROM ContratoJPAEntity c " +
            "LEFT JOIN FETCH c.personal " +
            "LEFT JOIN FETCH c.equipo " +
            "WHERE c.idContrato = :id")
    Optional<ContratoJPAEntity> findByIdWithRelations(@Param("id") UUID id);

    @Query("""
           SELECT c FROM ContratoJPAEntity c
           WHERE c.personal.idPersonal = :idPersonal
             AND c.estado = 'ACTIVO'
             AND c.fechaInicio <= CURRENT_TIMESTAMP
             AND c.fechaFin >= CURRENT_TIMESTAMP
           """)
    Optional<ContratoJPAEntity> findVigenteByPersonal(@Param("idPersonal") UUID idPersonal);
 
    @Query("""
           SELECT c FROM ContratoJPAEntity c
           WHERE c.equipo.idEquipo = :idEquipo
             AND c.estado = 'ACTIVO'
             AND c.fechaInicio <= CURRENT_TIMESTAMP
             AND c.fechaFin >= CURRENT_TIMESTAMP
           """)
    List<ContratoJPAEntity> findVigentesByEquipo(@Param("idEquipo") UUID idEquipo);
}
