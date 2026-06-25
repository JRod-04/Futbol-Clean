package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.CompeticionJPARepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompeticionRepositoryAdapter implements CompeticionRepositoryPort{
 
    private final CompeticionJPARepository repository;
    private final InfrastructureMapper     mapper;
 
    @Override
    public Competicion save(Competicion competicion) {
        return mapper.toDomain(repository.save(mapper.toJpa(competicion)));
    }
 
    @Override
    public Optional<Competicion> findById(UUID idCompeticion) {
        return repository.findById(idCompeticion).map(mapper::toDomain);
    }
 
    @Override
    public List<Competicion> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Competicion> findActivas() {
        return repository.findActivas(LocalDateTime.now()).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Competicion> findByNombre(String nombre) {
        return repository.findByNombre(nombre).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public boolean existsById(UUID idCompeticion) {
        return repository.existsById(idCompeticion);
    }
 
    @Override
    public void deleteById(UUID idCompeticion) {
        repository.deleteById(idCompeticion);
    }
}
