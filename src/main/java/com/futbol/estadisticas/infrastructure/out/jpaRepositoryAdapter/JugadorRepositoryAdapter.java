package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.JugadorJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JugadorRepositoryAdapter implements JugadorRepositoryPort {

    private final JugadorJPARepository repository;
    private final InfrastructureMapper mapper;

    @Override
    public List<Jugador> saveAll(List<Jugador> jugadores) {
        List<JugadorJPAEntity> entities = jugadores.stream()
                .map(mapper::toJpa)
                .toList();
        List<JugadorJPAEntity> saved = repository.saveAll(entities);
        return saved.stream()
                .map(mapper::JugadortoDomain)
                .toList();
    }

    @Override
    public Page<Jugador> buscarJugadorPorTexto(String texto, Pageable pageable) {
        return repository.buscarJugadorPorTexto(texto, pageable)
                .map(mapper::JugadortoDomain);
    }

    @Override
    public Jugador save(Jugador jugador) {
        JugadorJPAEntity entity = mapper.toJpa(jugador);
        
        // Establecer relaciones bidireccionales
        if (entity.getDatosDeportivos() != null) {
            entity.getDatosDeportivos().setJugador(entity);
        }
        
        if (entity.getLesiones() != null && !entity.getLesiones().isEmpty()) {
            entity.getLesiones().forEach(lesion -> lesion.setJugador(entity));
        }
        
        return mapper.JugadortoDomain(repository.save(entity));
    }

    @Override
    public Optional<Jugador> findById(UUID idPersonal) {
        return repository.findByIdWithContratos(idPersonal).map(mapper::JugadortoDomain);
    }

    @Override
    public List<Jugador> findAll() {
        return repository.findAll().stream().map(mapper::JugadortoDomain).toList();
    }

    @Override
    public List<Jugador> findByClub(UUID idClub) {
        return repository.findByClub(idClub).stream().map(mapper::JugadortoDomain).toList();
    }

    @Override
    public List<Jugador> findByEstado(EstadoJugador estado) {
        return repository.findByEstado(estado).stream().map(mapper::JugadortoDomain).toList();
    }

    @Override
    public List<Jugador> findByPosicion(PosicionJugador posicion) {
        return repository.findByPosicion(posicion).stream().map(mapper::JugadortoDomain).toList();
    }

    @Override
    public List<Jugador> findDisponibles() {
        return repository.findDisponibles().stream().map(mapper::JugadortoDomain).toList();
    }

    @Override
    public List<Jugador> findLesionados() {
        return repository.findLesionados().stream().map(mapper::JugadortoDomain).toList();
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