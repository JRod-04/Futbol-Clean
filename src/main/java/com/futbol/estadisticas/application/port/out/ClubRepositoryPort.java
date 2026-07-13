package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Club;

public interface ClubRepositoryPort {
    Club save(Club club);
 
    Optional<Club> findById(UUID idEquipo);
 
    List<Club> findAll();

    Optional<Club> findByIdWithContratos(UUID id);

    List<Club> findByNombre(String nombre);
 
    boolean existsById(UUID idEquipo);
 
    void deleteById(UUID idEquipo);
}
