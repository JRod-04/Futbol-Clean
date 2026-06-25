package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.EventosPartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PartidoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PersonalDeportivoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.ClubJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EventosPartidoJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.PartidoJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.PersonalDeportivoJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventosPartidoRepositoryAdapter implements EventosPartidoRepositoryPort {

    private final EventosPartidoJPARepository    repository;
    private final PartidoJPARepository           partidoRepo;
    private final PersonalDeportivoJPARepository personalRepo;
    private final ClubJPARepository              clubRepo;
    private final InfrastructureMapper           mapper;
 
    @Override
    public EventosPartido save(EventosPartido evento) {
        PartidoJPAEntity partidoJPA = evento.getPartido() != null
                ? partidoRepo.findById(evento.getPartido().getIdPartido()).orElse(null) : null;
        PersonalDeportivoJPAEntity personalJPA = evento.getPersonal() != null
                ? personalRepo.findById(evento.getPersonal().getIdPersonal()).orElse(null) : null;
        ClubJPAEntity equipoJPA = evento.getEquipoFavorecido() != null
                ? clubRepo.findById(evento.getEquipoFavorecido().getIdEquipo()).orElse(null) : null;
 
        return mapper.toDomain(
                repository.save(mapper.toJpa(evento, partidoJPA, personalJPA, equipoJPA)));
    }
 
    @Override
    public Optional<EventosPartido> findById(UUID idEvento) {
        return repository.findById(idEvento).map(mapper::toDomain);
    }
 
    @Override
    public List<EventosPartido> findByPartido(UUID idPartido) {
        return repository.findByPartidoIdPartido(idPartido).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<EventosPartido> findByPartidoAndTipo(UUID idPartido, TipoEvento tipoEvento) {
        return repository.findByPartidoIdPartidoAndTipoEvento(idPartido, tipoEvento).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<EventosPartido> findByPersonal(UUID idPersonal) {
        return repository.findByPersonalIdPersonal(idPersonal).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<EventosPartido> findGolesByPartido(UUID idPartido) {
        return repository.findGolesByPartido(idPartido).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<EventosPartido> findTarjetasByPartido(UUID idPartido) {
        return repository.findTarjetasByPartido(idPartido).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public boolean existsById(UUID idEvento) {
        return repository.existsById(idEvento);
    }
 
    @Override
    public void deleteById(UUID idEvento) {
        repository.deleteById(idEvento);
    }
}
