package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.Competicion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipoRepositoryPort {

    Page<Equipo> buscarEquipoPorNombre(String nombre, Pageable pageable);

    Equipo save(Equipo equipo);
 
    Optional<Equipo> findById(UUID idEquipo);
 
    List<Equipo> findAll();

    Optional<Equipo> findByIdWithContratos(UUID id);

    List<Competicion> findCompeticionesByEquipo(UUID idEquipo);

    boolean existsById(UUID idEquipo);

    void actualizarTecnicoActual(UUID idEquipo, UUID idTecnico);

    void deleteById(UUID idEquipo);
}
