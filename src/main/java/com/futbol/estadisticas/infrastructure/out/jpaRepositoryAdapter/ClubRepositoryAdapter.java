package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.TecnicoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.ClubJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EstadioJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.TecnicoJPARepository;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class ClubRepositoryAdapter implements ClubRepositoryPort{

    private final ClubJPARepository    repository;
    private final EstadioJPARepository estadioRepo;
    private final TecnicoJPARepository tecnicoRepo;
    private final InfrastructureMapper mapper;
 
    @Override
    public Club save(Club club) {
        ClubJPAEntity entity = mapper.toJpa(club);
 
        // Resolver FK estadio
        if (club.getEstadio() != null) {
            EstadioJPAEntity estadioJPA = estadioRepo
                    .findById(club.getEstadio().getIdEstadio()).orElse(null);
            entity.setEstadio(estadioJPA);
        }
 
        // Resolver FK técnico actual
        if (club.getTecnicoActual() != null) {
            TecnicoJPAEntity tecnicoJPA = tecnicoRepo
                    .findById(club.getTecnicoActual().getIdPersonal()).orElse(null);
            entity.setTecnicoActual(tecnicoJPA);
        }
 
        return mapper.toDomain(repository.save(entity));
    }
 
    @Override
    public Optional<Club> findById(UUID idEquipo) {
        return repository.findById(idEquipo).map(mapper::toDomain);
    }
 
    @Override
    public List<Club> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Club> findByIdWithContratos(UUID id) {
        return repository.findByIdWithContratos(id).map(mapper::toDomain);
            }

    @Override
    public List<Club> findByNombre(String nombre) {
        return repository.findByNombre(nombre).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public boolean existsById(UUID idEquipo) {
        return repository.existsById(idEquipo);
    }
 
    @Override
    public void deleteById(UUID idEquipo) {
        repository.deleteById(idEquipo);
    }
}
