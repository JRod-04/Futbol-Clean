package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClubRepositoryPort {

    Page<Club> buscarClubPorNombre(String nombre, Pageable pageable);

    Club save(Club club);
 
    Optional<Club> findById(UUID idEquipo);
 
    List<Club> findAll();

    Optional<Club> findByIdWithContratos(UUID id);

    List<Club> findByNombre(String nombre);
 
    boolean existsById(UUID idEquipo);

    void actualizarTecnicoActual(UUID idClub, UUID idTecnico);

    void deleteById(UUID idEquipo);
}
