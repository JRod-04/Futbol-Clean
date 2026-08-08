package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EquipoJPAEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.EquipoRepositoryPort;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.TecnicoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EquipoJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EstadioJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.TecnicoJPARepository;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class EquipoRepositoryAdapter implements EquipoRepositoryPort {

    private final EquipoJPARepository repository;
    private final EstadioJPARepository estadioRepo;
    private final TecnicoJPARepository tecnicoRepo;
    private final InfrastructureMapper mapper;

    @Override
    public Page<Equipo> buscarEquipoPorNombre(String texto, Pageable pageable) {
        if (texto == null || texto.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        return repository.buscarEquipoPorTexto(texto.trim(), pageable)
                .map(mapper::DatostoDomain);
    }

    @Override
    public Equipo save(Equipo equipo) {
        EquipoJPAEntity entity = mapper.toJpa(equipo);
 
        if (equipo.getEstadio() != null) {
            EstadioJPAEntity estadioJPA = estadioRepo
                    .findById(equipo.getEstadio().getIdEstadio()).orElse(null);
            entity.setEstadio(estadioJPA);
        }
 
        if (equipo.getTecnicoActual() != null) {
            TecnicoJPAEntity tecnicoJPA = tecnicoRepo
                    .findById(equipo.getTecnicoActual().getIdPersonal()).orElse(null);
            entity.setTecnicoActual(tecnicoJPA);
        }
 
        return mapper.DatostoDomain(repository.save(entity));
    }
 
    @Override
    public Optional<Equipo> findById(UUID idEquipo) {
        return repository.findByIdWithDetails(idEquipo)
                .map(mapper::toDomainConClubYBásicos)
                .map(this::cargarLesiones);
    }

    private Equipo cargarLesiones(Equipo club) {
        if (club == null || club.getContratos() == null) return club;
        return club;

    }
 
    @Override
    public List<Equipo> findAll() {
        return repository.findAll().stream().map(mapper::DatostoDomain).toList();
    }

    @Override
    public Optional<Equipo> findByIdWithContratos(UUID id) {
        return repository.findByIdWithContratos(id).map(mapper::DatostoDomain);
            }

    @Override
    public List<Competicion> findCompeticionesByEquipo(UUID idEquipo) {
        return repository.findCompeticionesByEquipo(idEquipo).stream()
                .map(mapper::CompeticiontoDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID idEquipo) {
        return repository.existsById(idEquipo);
    }

    @Override
    public void actualizarTecnicoActual(UUID idEquipo, UUID idTecnico) {
        if (idTecnico == null) {
            repository.actualizarTecnicoActual(idEquipo, null);
        } else {
            tecnicoRepo.findById(idTecnico)
                    .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado: " + idTecnico));
            repository.actualizarTecnicoActual(idEquipo, idTecnico);
        }
    }

    @Override
    public void deleteById(UUID idEquipo) {
        repository.deleteById(idEquipo);
    }
}
