package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;

public interface DatosDeportivosRepositoryPort {

    DatosDeportivos save(DatosDeportivos datosDeportivos);
 
    Optional<DatosDeportivos> findById(UUID idHistorialDeportivo);
 
    Optional<DatosDeportivos> findByJugador(UUID idJugador);
 
    List<DatosDeportivos> findByEstado(EstadoJugador estado);
 
    boolean existsByJugador(UUID idJugador);

    void deleteById(UUID idHistorialDeportivo);
}
