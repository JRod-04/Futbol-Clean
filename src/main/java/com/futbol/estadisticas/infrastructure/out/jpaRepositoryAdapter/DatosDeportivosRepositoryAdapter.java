package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.DatosDeportivosRepositoryPort;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.DatosDeportivosJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.DatosDeportivosJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.JugadorJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatosDeportivosRepositoryAdapter implements DatosDeportivosRepositoryPort {

    private final DatosDeportivosJPARepository repository;
    private final JugadorJPARepository jugadorRepo;
    private final InfrastructureMapper mapper;

    @Override
    public DatosDeportivos save(DatosDeportivos datos) {
        JugadorJPAEntity jugadorJPA = null;
        if (datos.getJugador() != null) {
            jugadorJPA = jugadorRepo
                    .findById(datos.getJugador().getIdPersonal())
                    .orElse(null);
        }
        DatosDeportivosJPAEntity entity = mapper.toJpa(datos, jugadorJPA);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<DatosDeportivos> findById(UUID idHistorialDeportivo) {
        return repository.findById(idHistorialDeportivo).map(mapper::toDomain);
    }

    @Override
    public Optional<DatosDeportivos> findByJugador(UUID idJugador) {
        return repository.findByJugadorIdPersonal(idJugador).map(mapper::toDomain);
    }

    @Override
    public List<DatosDeportivos> findByEstado(EstadoJugador estado) {
        return repository.findByEstadoJugador(estado).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByJugador(UUID idJugador) {
        return repository.existsByJugadorIdPersonal(idJugador);
    }

    @Override
    public void deleteById(UUID idJugador) {
        repository.deleteById(idJugador);
    }
}