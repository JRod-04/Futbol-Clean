package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.TecnicoRepositoryPort;
import com.futbol.estadisticas.domain.model.Tecnico;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.TecnicoJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TecnicoRepositoryAdapter implements TecnicoRepositoryPort {

    private final TecnicoJPARepository repository;
    private final InfrastructureMapper  mapper;
 
    @Override
    public Tecnico save(Tecnico tecnico) {
        return mapper.toDomain(repository.save(mapper.toJpa(tecnico)));
    }
 
    @Override
    public Optional<Tecnico> findById(UUID idPersonal) {
        return repository.findById(idPersonal).map(mapper::toDomain);
    }
 
    @Override
    public List<Tecnico> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Tecnico> findByClub(UUID idClub) {
        return repository.findByClub(idClub).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public Optional<Tecnico> findTecnicoActualByClub(UUID idClub) {
        return repository.findTecnicoActualByClub(idClub).map(mapper::toDomain);
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
