package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Arbitro;

public interface ArbitroRepositoryPort {
    Arbitro save(Arbitro arbitro);
 
    Optional<Arbitro> findById(UUID idArbitro);
 
    List<Arbitro> findAll();
 
    List<Arbitro> findByNombreOrApellido(String termino);
 
    boolean existsById(UUID idArbitro);
 
    void deleteById(UUID idArbitro);
}
