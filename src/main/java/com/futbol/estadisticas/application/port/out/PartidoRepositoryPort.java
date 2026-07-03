package com.futbol.estadisticas.application.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;

public interface PartidoRepositoryPort {
    
    Partido save(Partido partido);
 
    Optional<Partido> findById(UUID idPartido);
 
    List<Partido> findAll();
 
    List<Partido> findByClub(UUID idClub);
 
    List<Partido> findByCompeticion(UUID idCompeticion);
 
    List<Partido> findByEstado(EstadoPartido estado);

    List<Partido> findClasificacion(UUID idCcompeticion);

    List<Partido> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
 
    List<Partido> findByArbitro(UUID idArbitro);
 
    boolean existsById(UUID idPartido);
 
    void deleteById(UUID idPartido);
}
