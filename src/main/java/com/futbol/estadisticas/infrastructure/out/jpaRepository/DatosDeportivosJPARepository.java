package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.DatosDeportivosJPAEntity;

public interface DatosDeportivosJPARepository extends JpaRepository<DatosDeportivosJPAEntity, UUID>{
    Optional<DatosDeportivosJPAEntity> findByJugadorIdPersonal(UUID idJugador);
 
    List<DatosDeportivosJPAEntity> findByEstadoJugador(EstadoJugador estadoJugador);
 
    boolean existsByJugadorIdPersonal(UUID idJugador);
}
