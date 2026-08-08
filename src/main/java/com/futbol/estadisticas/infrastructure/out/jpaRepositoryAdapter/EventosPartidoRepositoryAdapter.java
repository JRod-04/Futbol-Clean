package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.EquipoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EventosPartidoJPAEntity;
import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.EventosPartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PartidoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PersonalDeportivoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.EquipoJPARepository;
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
    private final EquipoJPARepository clubRepo;
    private final InfrastructureMapper           mapper;
 
    @Override
    public EventosPartido save(EventosPartido evento) {
        if (evento.getPartido() == null) {
            throw new IllegalStateException("No se puede guardar un evento sin partido");
        }

        PartidoJPAEntity partidoJPA = partidoRepo.findById(evento.getPartido().getIdPartido())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Partido no encontrado con id: " + evento.getPartido().getIdPartido()));

        PersonalDeportivoJPAEntity personalJPA = null;
        if (evento.getPersonal() != null) {
            personalJPA = personalRepo.findById(evento.getPersonal().getIdPersonal())
                    .orElse(null);
        }

        EquipoJPAEntity equipoJPA = null;
        if (evento.getEquipoFavorecido() != null) {
            equipoJPA = clubRepo.findById(evento.getEquipoFavorecido().getIdEquipo())
                    .orElse(null);
        }

        EventosPartidoJPAEntity entity = mapper.toJpa(evento, partidoJPA, personalJPA, equipoJPA);
        EventosPartidoJPAEntity saved = repository.save(entity);

        return mapper.EventotoDomain(saved);
    }

    @Override
    public List<EventosPartido> saveAll(List<EventosPartido> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return List.of();
        }

        UUID idPartido = eventos.get(0).getPartido().getIdPartido();
        PartidoJPAEntity partidoJPA = partidoRepo.findById(idPartido)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));

        List<EventosPartidoJPAEntity> entities = eventos.stream()
                .map(e -> {
                    PersonalDeportivoJPAEntity personalJPA = null;
                    if (e.getPersonal() != null) {
                        personalJPA = personalRepo.findById(e.getPersonal().getIdPersonal()).orElse(null);
                    }
                    EquipoJPAEntity equipoJPA = null;
                    if (e.getEquipoFavorecido() != null) {
                        equipoJPA = clubRepo.findById(e.getEquipoFavorecido().getIdEquipo()).orElse(null);
                    }
                    return mapper.toJpa(e, partidoJPA, personalJPA, equipoJPA);
                })
                .toList();

        List<EventosPartidoJPAEntity> saved = repository.saveAll(entities);
        return saved.stream().map(mapper::EventotoDomain).toList();
    }

    @Override
    public Optional<EventosPartido> findById(UUID idEvento) {
        return repository.findById(idEvento).map(mapper::EventotoDomain);
    }
 
    @Override
    public List<EventosPartido> findByPartido(UUID idPartido) {
        return repository.findByPartidoIdPartido(idPartido).stream()
                .map(mapper::EventotoDomain).toList();
    }

    @Override
    public List<EventosPartido> findByPersonalConCompeticion(UUID idPersonal) {
        return repository.findByPersonalIdPersonalConCompeticion(idPersonal).stream()
                .map(mapper::EventotoDomain).toList();
    }

    @Override
    public List<EventosPartido> findByPartidoAndTipo(UUID idPartido, TipoEvento tipoEvento) {
        return repository.findByPartidoIdPartidoAndTipoEvento(idPartido, tipoEvento).stream()
                .map(mapper::EventotoDomain).toList();
    }
 
    @Override
    public List<EventosPartido> findByPersonal(UUID idPersonal) {
        return repository.findByPersonalIdPersonal(idPersonal).stream()
                .map(mapper::EventotoDomain).toList();
    }
 
    @Override
    public List<EventosPartido> findGolesByPartido(UUID idPartido) {
        return repository.findGolesByPartido(idPartido).stream().map(mapper::EventotoDomain).toList();
    }
 
    @Override
    public List<EventosPartido> findTarjetasByPartido(UUID idPartido) {
        return repository.findTarjetasByPartido(idPartido).stream().map(mapper::EventotoDomain).toList();
    }

    @Override
    public void delete(EventosPartido evento) {
        if (evento == null || evento.getIdEvento() == null) {
            throw new IllegalArgumentException("Evento inválido para eliminar");
        }

        EventosPartidoJPAEntity entity = repository.findById(evento.getIdEvento())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evento no encontrado con id: " + evento.getIdEvento()));

        repository.delete(entity);
    }

    @Override
    public boolean existsById(UUID idEvento) {
        return repository.existsById(idEvento);
    }
 
    @Override
    public void deleteById(UUID idEvento) {
        repository.deleteEventoById(idEvento);
    }
}
