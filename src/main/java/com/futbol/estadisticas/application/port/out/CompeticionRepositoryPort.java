package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Competicion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompeticionRepositoryPort {

    Page<Competicion> buscarCompeticionesPorNombre(String nombre, Pageable pageable);

    Competicion save(Competicion competicion);

    Optional<Competicion> findByIdWithPartidosAndEquipos(UUID idCompeticion);


    Optional<Competicion> findById(UUID idCompeticion);
 
    List<Competicion> findAll();
 
    List<Competicion> findActivas();
 
    List<Competicion> findByNombre(String nombre);
 
    boolean existsById(UUID idCompeticion);
 
    void deleteById(UUID idCompeticion);
}
