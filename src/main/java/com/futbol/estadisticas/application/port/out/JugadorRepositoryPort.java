package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

public interface JugadorRepositoryPort {
    Jugador save(Jugador jugador);
 
    Optional<Jugador> findById(UUID idPersonal);
 
    List<Jugador> findAll();
 
    List<Jugador> findByClub(UUID idClub);
 
    List<Jugador> findByEstado(EstadoJugador estado);
 
    List<Jugador> findByPosicion(PosicionJugador posicion);
 
    List<Jugador> findDisponibles();
 
    List<Jugador> findLesionados();
 
    boolean existsById(UUID idPersonal);
 
    void deleteById(UUID idPersonal);
}
