package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Estadio;

public interface EstadioRepositoryPort {
    
    Estadio save(Estadio estadio);
 
    Optional<Estadio> findById(UUID idEstadio);
 
    List<Estadio> findAll();
 
    Optional<Estadio> findByEquipoPrincipal(UUID idEquipo);
 
    boolean existsById(UUID idEstadio);
 
    void deleteById(UUID idEstadio);
}
