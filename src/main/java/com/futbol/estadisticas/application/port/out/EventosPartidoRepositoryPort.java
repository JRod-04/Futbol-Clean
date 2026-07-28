package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;

public interface EventosPartidoRepositoryPort {
    EventosPartido save(EventosPartido evento);

    List<EventosPartido> saveAll(List<EventosPartido> eventosPartidos);

    Optional<EventosPartido> findById(UUID idEvento);
 
    List<EventosPartido> findByPartido(UUID idPartido);

    List<EventosPartido> findByPersonalConCompeticion(UUID idPersonal);

    List<EventosPartido> findByPartidoAndTipo(UUID idPartido, TipoEvento tipoEvento);
 
    List<EventosPartido> findByPersonal(UUID idPersonal);
 
    List<EventosPartido> findGolesByPartido(UUID idPartido);
 
    List<EventosPartido> findTarjetasByPartido(UUID idPartido);

    void delete(EventosPartido evento);

    boolean existsById(UUID idEvento);
 
    void deleteById(UUID idEvento);
}
