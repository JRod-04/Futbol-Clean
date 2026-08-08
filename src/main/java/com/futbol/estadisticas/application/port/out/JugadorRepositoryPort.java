package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JugadorRepositoryPort {

    Page<Jugador> buscarJugadorPorTexto(String texto, Pageable pageable);

    Jugador save(Jugador jugador);

    List<Jugador> saveAll(List<Jugador> jugadores);

    Optional<Jugador> findById(UUID idPersonal);
 
    List<Jugador> findAll();
 
    List<Jugador> findByEquipo(UUID idEquipo);
 
    List<Jugador> findByEstado(EstadoJugador estado);
 
    List<Jugador> findByPosicion(PosicionJugador posicion);
 
    List<Jugador> findDisponibles();
 
    List<Jugador> findLesionados();
 
    boolean existsById(UUID idPersonal);
 
    void deleteById(UUID idPersonal);
}
