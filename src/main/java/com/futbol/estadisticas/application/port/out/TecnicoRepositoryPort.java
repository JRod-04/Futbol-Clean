package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Tecnico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TecnicoRepositoryPort {

    Page<Tecnico> buscarTecnicoPorNombre(String nombre, Pageable pageable);

    Tecnico save(Tecnico tecnico);
 
    Optional<Tecnico> findById(UUID idPersonal);
 
    List<Tecnico> findAll();
 
    List<Tecnico> findByEquipo(UUID idEquipo);
 
    Optional<Tecnico> findTecnicoActualByEquipo(UUID idEquipo);
 
    boolean existsById(UUID idPersonal);
 
    void deleteById(UUID idPersonal);

}
