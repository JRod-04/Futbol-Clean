package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Lesion;
import com.futbol.estadisticas.domain.model.enums.Gravedad;

public interface LesionRepositoryPort {
    
    Lesion save(Lesion lesion);
 
    Optional<Lesion> findById(UUID idLesion);
 
    List<Lesion> findByJugador(UUID idJugador);
 
    List<Lesion> findActivasByJugador(UUID idJugador);
 
    List<Lesion> findByGravedad(Gravedad gravedad);
 
    List<Lesion> findActivas();
 
    boolean existsById(UUID idLesion);
 
    void deleteById(UUID idLesion);
}
