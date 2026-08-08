package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.ArbitroJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EquipoJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.CompeticionJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EstadioJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.PartidoJPARepository;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PartidoRepositoryAdapter implements PartidoRepositoryPort {

    private final PartidoJPARepository     repository;
    private final EquipoJPARepository clubRepo;
    private final EstadioJPARepository     estadioRepo;
    private final ArbitroJPARepository     arbitroRepo;
    private final CompeticionJPARepository competicionRepo;
    private final InfrastructureMapper     mapper;
 
    @Override
    public Partido save(Partido partido) {
        EquipoJPAEntity local = clubRepo.findById(
                partido.getEquipoLocal().getIdEquipo()).orElseThrow();
        EquipoJPAEntity visitante = clubRepo.findById(
                partido.getEquipoVisitante().getIdEquipo()).orElseThrow();
        EstadioJPAEntity estadio = partido.getEstadio() != null
                ? estadioRepo.findById(partido.getEstadio().getIdEstadio()).orElse(null) : null;
        ArbitroJPAEntity arbitro = partido.getArbitro() != null
                ? arbitroRepo.findById(partido.getArbitro().getIdArbitro()).orElse(null) : null;
        CompeticionJPAEntity competicion = partido.getCompeticion() != null
                ? competicionRepo.findById(partido.getCompeticion().getIdCompeticion()).orElse(null) : null;
 
        PartidoJPAEntity entity = mapper.toJpa(partido, local, visitante, estadio, arbitro, competicion);
        return mapper.PartidotoDomain(repository.save(entity));
    }

    @Override
    public List<Partido> saveAll(List<Partido> partidos) {
        List<PartidoJPAEntity> entities = partidos.stream()
                .map(p -> mapper.toJpa(p,
                        clubRepo.findById(p.getEquipoLocal().getIdEquipo()).orElseThrow(),
                        clubRepo.findById(p.getEquipoVisitante().getIdEquipo()).orElseThrow(),
                        p.getEstadio() != null ? estadioRepo.findById(p.getEstadio().getIdEstadio()).orElse(null) : null,
                        p.getArbitro() != null ? arbitroRepo.findById(p.getArbitro().getIdArbitro()).orElse(null) : null,
                        p.getCompeticion() != null ? competicionRepo.findById(p.getCompeticion().getIdCompeticion()).orElse(null) : null
                ))
                .toList();
        List<PartidoJPAEntity> saved = repository.saveAll(entities);
        return saved.stream().map(mapper::PartidotoDomain).toList();
    }

    @Override
    public Optional<Partido> findById(UUID idPartido) {
        return repository.findById(idPartido).map(mapper::PartidotoDomain);
    }

    @Override
    public Page<Partido> findByFecha(LocalDate fecha, Pageable pageable) {
        return repository.findByFecha(fecha, pageable)
                .map(mapper::PartidotoDomain);
    }

    @Override
    public List<Partido> findAll() {
        return repository.findAll().stream().map(mapper::PartidotoDomain).toList();
    }
 
    @Override
    public List<Partido> findByEquipo(UUID idEquipo) {
        return repository.findByEquipo(idEquipo).stream().map(mapper::PartidotoDomain).toList();
    }
 
    @Override
    public List<Partido> findByCompeticion(UUID idCompeticion) {
        return repository.findByCompeticionIdCompeticion(idCompeticion).stream()
                .map(mapper::PartidotoDomain).toList();
    }
    @Override
    public List<Partido> findClasificacion(UUID idCompeticion) {
        return repository.findClasificacion(idCompeticion).stream()
                .map(mapper::PartidotoDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID idPartido) {
        return repository.existsById(idPartido);
    }

    @Override
    public void deleteById(UUID idPartido) {
        repository.deleteById(idPartido);
    }
}
