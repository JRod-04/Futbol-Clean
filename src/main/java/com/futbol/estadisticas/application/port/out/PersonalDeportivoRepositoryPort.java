package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;

public interface PersonalDeportivoRepositoryPort {
  PersonalDeportivo save(PersonalDeportivo personal);
 
    Optional<PersonalDeportivo> findById(UUID idPersonal);
 
    List<PersonalDeportivo> findAll();
  
    List<PersonalDeportivo> findByNombreOrApellido(String termino);
 
    boolean existsById(UUID idPersonal);
 
    void deleteById(UUID idPersonal);
}
