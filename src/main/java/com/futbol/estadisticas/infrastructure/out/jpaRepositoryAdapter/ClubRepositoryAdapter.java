package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Competicion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Club> buscarClubPorNombre(String texto, Pageable pageable) {
        if (texto == null || texto.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        return repository.buscarClubPorTexto(texto.trim(), pageable)
                .map(mapper::DatostoDomain);
    }

    @Override
    public Club save(Club club) {
        ClubJPAEntity entity = mapper.toJpa(club);
 
        if (club.getEstadio() != null) {
            EstadioJPAEntity estadioJPA = estadioRepo
                    .findById(club.getEstadio().getIdEstadio()).orElse(null);
            entity.setEstadio(estadioJPA);
        }
 
        if (club.getTecnicoActual() != null) {
            TecnicoJPAEntity tecnicoJPA = tecnicoRepo
                    .findById(club.getTecnicoActual().getIdPersonal()).orElse(null);
            entity.setTecnicoActual(tecnicoJPA);
        }
 
        return mapper.DatostoDomain(repository.save(entity));
    }
 
    @Override
    public Optional<Club> findById(UUID idEquipo) {
        return repository.findByIdWithDetails(idEquipo)
                .map(mapper::toDomainConClubYBásicos)
                .map(this::cargarLesiones);
    }

    private Club cargarLesiones(Club club) {
        if (club == null || club.getContratos() == null) return club;
        return club;

    }
 
    @Override
    public List<Club> findAll() {
        return repository.findAll().stream().map(mapper::DatostoDomain).toList();
    }

    @Override
    public Optional<Club> findByIdWithContratos(UUID id) {
        return repository.findByIdWithContratos(id).map(mapper::DatostoDomain);
            }

    @Override
    public List<Competicion> findCompeticionesByClub(UUID idClub) {
        return repository.findCompeticionesByClub(idClub).stream()
                .map(mapper::CompeticiontoDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID idEquipo) {
        return repository.existsById(idEquipo);
    }

    @Override
    public void actualizarTecnicoActual(UUID idClub, UUID idTecnico) {
        if (idTecnico == null) {
            repository.actualizarTecnicoActual(idClub, null);
        } else {
            tecnicoRepo.findById(idTecnico)
                    .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado: " + idTecnico));
            repository.actualizarTecnicoActual(idClub, idTecnico);
        }
    }

    @Override
    public void deleteById(UUID idEquipo) {
        repository.deleteById(idEquipo);
    }
}
