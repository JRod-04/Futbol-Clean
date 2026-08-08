package com.futbol.estadisticas.application.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Partido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartidoRepositoryPort {
    
    Partido save(Partido partido);

    List<Partido> saveAll(List<Partido> partidos);

    Optional<Partido> findById(UUID idPartido);

    Page<Partido> findByFecha(LocalDate fecha, Pageable pageable);

    List<Partido> findAll();
 
    List<Partido> findByEquipo(UUID idEquipo);
 
    List<Partido> findByCompeticion(UUID idCompeticion);

    List<Partido> findClasificacion(UUID idCcompeticion);

    boolean existsById(UUID idPartido);

    void deleteById(UUID idPartido);
}
