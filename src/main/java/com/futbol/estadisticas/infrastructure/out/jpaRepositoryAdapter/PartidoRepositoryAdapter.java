package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ArbitroJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.CompeticionJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PartidoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.ArbitroJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.ClubJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.CompeticionJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EstadioJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.PartidoJPARepository;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PartidoRepositoryAdapter implements PartidoRepositoryPort {

    private final PartidoJPARepository     repository;
    private final ClubJPARepository        clubRepo;
    private final EstadioJPARepository     estadioRepo;
    private final ArbitroJPARepository     arbitroRepo;
    private final CompeticionJPARepository competicionRepo;
    private final InfrastructureMapper     mapper;
 
    @Override
    public Partido save(Partido partido) {
        ClubJPAEntity local = clubRepo.findById(
                partido.getEquipoLocal().getIdEquipo()).orElseThrow();
        ClubJPAEntity visitante = clubRepo.findById(
                partido.getEquipoVisitante().getIdEquipo()).orElseThrow();
        EstadioJPAEntity estadio = partido.getEstadio() != null
                ? estadioRepo.findById(partido.getEstadio().getIdEstadio()).orElse(null) : null;
        ArbitroJPAEntity arbitro = partido.getArbitro() != null
                ? arbitroRepo.findById(partido.getArbitro().getIdArbitro()).orElse(null) : null;
        CompeticionJPAEntity competicion = partido.getCompeticion() != null
                ? competicionRepo.findById(partido.getCompeticion().getIdCompeticion()).orElse(null) : null;
 
        PartidoJPAEntity entity = mapper.toJpa(partido, local, visitante, estadio, arbitro, competicion);
        return mapper.toDomain(repository.save(entity));
    }
 
    @Override
    public Optional<Partido> findById(UUID idPartido) {
        return repository.findById(idPartido).map(mapper::toDomain);
    }
 
    @Override
    public List<Partido> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Partido> findByClub(UUID idClub) {
        return repository.findByClub(idClub).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Partido> findByCompeticion(UUID idCompeticion) {
        return repository.findByCompeticionIdCompeticion(idCompeticion).stream()
                .map(mapper::toDomain).toList();
    }
    @Override
    public List<Partido> findClasificacion(UUID idCompeticion) {
        return repository.findClasificacion(idCompeticion).stream()
                .map(mapper::toDomain)
                .toList();
    }
    @Override
    public List<Partido> findByEstado(EstadoPartido estado) {
        return repository.findByEstado(estado).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Partido> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByFechaYHoraBetween(desde, hasta).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Partido> findByArbitro(UUID idArbitro) {
        return repository.findByArbitroIdArbitro(idArbitro).stream()
                .map(mapper::toDomain).toList();
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
