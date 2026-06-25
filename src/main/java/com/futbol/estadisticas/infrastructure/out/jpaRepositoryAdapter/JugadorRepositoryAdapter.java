package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.DatosDeportivosJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.LesionJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.JugadorJPARepository;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JugadorRepositoryAdapter implements JugadorRepositoryPort {

    private final JugadorJPARepository repository;
    private final InfrastructureMapper  mapper;
 
    @Override
    public Jugador save(Jugador jugador) {
        JugadorJPAEntity entity = mapper.toJpa(jugador);
 
        // Guardar DatosDeportivos embebidos
        if (jugador.getDatosDeportivos() != null) {
            DatosDeportivosJPAEntity datosJPA =
                    mapper.toJpa(jugador.getDatosDeportivos(), entity);
            entity.setDatosDeportivos(datosJPA);
        }
 
        // Guardar lesiones
        if (jugador.getLesiones() != null) {
            List<LesionJPAEntity> lesionesJPA = jugador.getLesiones().stream()
                    .map(l -> mapper.toJpa(l, entity))
                    .toList();
            entity.setLesiones(lesionesJPA);
        }
 
        return mapper.toDomain(repository.save(entity));
    }
 
    @Override
    public Optional<Jugador> findById(UUID idPersonal) {
        return repository.findById(idPersonal).map(mapper::toDomain);
    }
 
    @Override
    public List<Jugador> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Jugador> findByClub(UUID idClub) {
        return repository.findByClub(idClub).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Jugador> findByEstado(EstadoJugador estado) {
        return repository.findByEstado(estado).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Jugador> findByPosicion(PosicionJugador posicion) {
        return repository.findByPosicion(posicion).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Jugador> findDisponibles() {
        return repository.findDisponibles().stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Jugador> findLesionados() {
        return repository.findLesionados().stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public boolean existsById(UUID idPersonal) {
        return repository.existsById(idPersonal);
    }
 
    @Override
    public void deleteById(UUID idPersonal) {
        repository.deleteById(idPersonal);
    }
}
