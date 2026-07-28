package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EventosPartidoJPAEntity;

public interface EventosPartidoJPARepository extends JpaRepository<EventosPartidoJPAEntity, UUID> {
    List<EventosPartidoJPAEntity> findByPartidoIdPartido(UUID idPartido);

    List<EventosPartidoJPAEntity> findByPartidoIdPartidoAndTipoEvento(UUID idPartido, TipoEvento tipoEvento);

    List<EventosPartidoJPAEntity> findByPersonalIdPersonal(UUID idPersonal);

    @Query("""
            SELECT e FROM EventosPartidoJPAEntity e
            WHERE e.partido.idPartido = :idPartido
              AND e.tipoEvento IN ('GOL','AUTOGOL','PENALTI_ANOTADO')
            """)
    List<EventosPartidoJPAEntity> findGolesByPartido(@Param("idPartido") UUID idPartido);

    @Query("""
            SELECT e FROM EventosPartidoJPAEntity e
            WHERE e.partido.idPartido = :idPartido
              AND e.tipoEvento IN ('AMARILLA','ROJA')
            """)
    List<EventosPartidoJPAEntity> findTarjetasByPartido(@Param("idPartido") UUID idPartido);

    @Modifying
    @Query("DELETE FROM EventosPartidoJPAEntity e WHERE e.idEvento = :idEvento")
    void deleteEventoById(@Param("idEvento") UUID idEvento);

    @Query("""
       SELECT e FROM EventosPartidoJPAEntity e
       JOIN FETCH e.partido p
       JOIN FETCH p.competicion
       WHERE e.personal.idPersonal = :idPersonal
       """)
    List<EventosPartidoJPAEntity> findByPersonalIdPersonalConCompeticion(@Param("idPersonal") UUID idPersonal);
}
