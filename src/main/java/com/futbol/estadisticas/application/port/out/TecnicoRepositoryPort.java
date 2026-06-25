package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Tecnico;

public interface TecnicoRepositoryPort {
    Tecnico save(Tecnico tecnico);
 
    Optional<Tecnico> findById(UUID idPersonal);
 
    List<Tecnico> findAll();
 
    List<Tecnico> findByClub(UUID idClub);
 
    Optional<Tecnico> findTecnicoActualByClub(UUID idClub);
 
    boolean existsById(UUID idPersonal);
 
    void deleteById(UUID idPersonal);

}
