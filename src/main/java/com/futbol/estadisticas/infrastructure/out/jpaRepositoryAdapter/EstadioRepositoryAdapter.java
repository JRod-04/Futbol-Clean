package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.EstadioRepositoryPort;
import com.futbol.estadisticas.domain.model.Estadio;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EstadioJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EstadioRepositoryAdapter implements EstadioRepositoryPort { 
    private final EstadioJPARepository repository;
    private final InfrastructureMapper  mapper;
 
    @Override
    public Estadio save(Estadio estadio) {
        return mapper.EstadiotoDomain(repository.save(mapper.toJpa(estadio)));
    }
 
    @Override
    public Optional<Estadio> findById(UUID idEstadio) {
        return repository.findById(idEstadio).map(mapper::EstadiotoDomain);
    }
 
    @Override
    public List<Estadio> findAll() {
        return repository.findAll().stream().map(mapper::EstadiotoDomain).toList();
    }
 
    @Override
    public Optional<Estadio> findByEquipoPrincipal(UUID idEquipo) {
        return repository.findByEquipoPrincipal(idEquipo).map(mapper::EstadiotoDomain);
    }
 
    @Override
    public boolean existsById(UUID idEstadio) {
        return repository.existsById(idEstadio);
    }
 
    @Override
    public void deleteById(UUID idEstadio) {
        repository.deleteById(idEstadio);
    }
}
