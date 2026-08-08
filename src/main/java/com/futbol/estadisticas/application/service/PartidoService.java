package com.futbol.estadisticas.application.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.futbol.estadisticas.application.port.dto.request.RealizarSustitucionRequest;
import com.futbol.estadisticas.application.port.dto.response.SustitucionResponse;
import com.futbol.estadisticas.application.port.dto.response.TandaPenalesResponse;
import com.futbol.estadisticas.application.port.mapper.TandaPenalesMapper;
import com.futbol.estadisticas.application.port.out.*;
import com.futbol.estadisticas.domain.model.*;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.CrearPartidoRequest;
import com.futbol.estadisticas.application.port.dto.request.RegistrarEventoRequest;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.in.PartidoUseCase;
import com.futbol.estadisticas.application.port.mapper.EventosPartidoMapper;
import com.futbol.estadisticas.application.port.mapper.PartidoMapper;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PartidoService implements PartidoUseCase {

    private final PartidoRepositoryPort           partidoRepository;
    private final EquipoRepositoryPort clubRepository;
    private final CompeticionRepositoryPort       competicionRepository;
    private final ArbitroRepositoryPort           arbitroRepository;
    private final EstadioRepositoryPort           estadioRepository;
    private final JugadorRepositoryPort           jugadorRepository;
    private final EventosPartidoRepositoryPort    eventosRepository;
    private final PersonalDeportivoRepositoryPort personalRepository;
    private final PartidoMapper                   partidoMapper;
    private final EventosPartidoMapper            eventosMapper;
    private final TandaPenalesMapper              tandapenalesMapper;
 
    @Override
    public PartidoResponse programarPartido(CrearPartidoRequest request) {
        
        Equipo local      = findClubOrThrow(request.idEquipoLocal());
        Equipo visitante  = findClubOrThrow(request.idEquipoVisitante());
        Competicion competicion = findCompeticionOrThrow(request.idCompeticion());
        Arbitro arbitro = findArbitroOrThrow(request.idArbitro());
 
        if (local.getIdEquipo().equals(visitante.getIdEquipo())) {
            throw new IllegalArgumentException("Un club no puede jugar contra sí mismo");
        }
        if (competicion.haFinalizado()) {
            throw new IllegalStateException(
                    "No se puede programar un partido en una competición finalizada");
        }
 
        Partido partido = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(local)
                .equipoVisitante(visitante)
                .competicion(competicion)
                .arbitro(arbitro)
                .fechaYHora(request.fechaYHora())
                .jornada(request.jornadaTorneo())
                .fase(request.fase())
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .build();
 
        if (request.idEstadio() != null) {
            Estadio estadio = estadioRepository.findById(request.idEstadio())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Estadio no encontrado con id: " + request.idEstadio()));
            partido.setEstadio(estadio);
        }
 
        competicion.agregarPartido(partido);
        arbitro.agregarPartido(partido);
 
        return partidoMapper.toResponse(partidoRepository.save(partido));
    }

    @Override
    @Transactional
    public List<PartidoResponse> programarPartidosBatch(List<CrearPartidoRequest> requests) {
        List<Partido> partidos = new ArrayList<>();

        for (CrearPartidoRequest request : requests) {
            // Validar y obtener dependencias
            Equipo local = findClubOrThrow(request.idEquipoLocal());
            Equipo visitante = findClubOrThrow(request.idEquipoVisitante());
            Competicion competicion = findCompeticionOrThrow(request.idCompeticion());
            Arbitro arbitro = findArbitroOrThrow(request.idArbitro());

            if (local.getIdEquipo().equals(visitante.getIdEquipo())) {
                throw new IllegalArgumentException("Un club no puede jugar contra sí mismo");
            }
            if (competicion.haFinalizado()) {
                throw new IllegalStateException("No se puede programar un partido en una competición finalizada");
            }

            Partido partido = Partido.builder()
                    .idPartido(UUID.randomUUID())
                    .equipoLocal(local)
                    .equipoVisitante(visitante)
                    .competicion(competicion)
                    .arbitro(arbitro)
                    .fechaYHora(request.fechaYHora())
                    .jornada(request.jornadaTorneo())
                    .fase(request.fase())
                    .estado(EstadoPartido.PROGRAMADO)
                    .golesLocal(0)
                    .golesVisitante(0)
                    .build();

            if (request.idEstadio() != null) {
                Estadio estadio = estadioRepository.findById(request.idEstadio())
                        .orElseThrow(() -> new IllegalArgumentException("Estadio no encontrado"));
                partido.setEstadio(estadio);
            }

            // Agregar a las colecciones de las entidades relacionadas (opcional)
            competicion.agregarPartido(partido);
            arbitro.agregarPartido(partido);

            partidos.add(partido);
        }

        List<Partido> saved = partidoRepository.saveAll(partidos);

        return saved.stream()
                .map(partidoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PartidoResponse> obtenerPartidosPorFecha(LocalDate fecha, int page, int size) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        Pageable pageable = PageRequest.of(page, size);

        return partidoRepository.findByFecha(fecha, pageable)
                .map(partido -> {
                    PartidoResponse response = partidoMapper.toResponse(partido);
                    return response;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PartidoResponse obtenerPartidoPorId(UUID idPartido) {
        return findPartidoOrThrow(idPartido, partidoMapper);
    }

    @Override
    public TandaPenalesResponse obtenerTandaPenales(UUID idPartido) {
        Partido partido = getPartidoOrThrow(idPartido);

        if (!partido.haFinalizado()) {
            throw new IllegalStateException("El partido aún no ha finalizado");
        }

        EstadoPartido finalizadoEn = obtenerEstadoFinalizacion(partido);
        if (finalizadoEn != EstadoPartido.PENALTIS) {
            throw new IllegalStateException("El partido no terminó en tanda de penaltis");
        }

        return tandapenalesMapper.toResponse(partido);
    }


    private EstadoPartido obtenerEstadoFinalizacion(Partido partido) {
        if (partido == null || partido.getEventos() == null) {
            return null;
        }
        return partido.getEventos().stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.FIN_PARTIDO)
                .findFirst()
                .map(EventosPartido::getEstadoEvento)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> obtenerPartidosPorCompeticion(UUID idCompeticion) {
        return partidoRepository.findByCompeticion(idCompeticion).stream()
                .map(partidoMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> obtenerPartidosPorEquipo(UUID idEquipo) {
        return partidoRepository.findByEquipo(idEquipo).stream()
                .map(partidoMapper::toResponse)
                .toList();
    }

    @Override
    public SustitucionResponse realizarSustitucion(UUID idPartido, RealizarSustitucionRequest request) {
        Partido partido = getPartidoOrThrow(idPartido);

        Jugador jugadorEntrante = findJugadorOrThrow(request.idJugadorEntrante());
        Jugador jugadorSaliente = findJugadorOrThrow(request.idJugadorSaliente());
        Equipo club = findClubOrThrow(request.idEquipo());

        List<EventosPartido> eventosSustitucion =
                partido.realizarSustitucion(jugadorEntrante, jugadorSaliente, club, request.minuto());

        eventosRepository.saveAll(eventosSustitucion);
        partidoRepository.save(partido);

        EventosPartido eventoSalida = eventosSustitucion.get(0);
        EventosPartido eventoEntrada = eventosSustitucion.get(1);

        return SustitucionResponse.builder()
                .eventoSalida(eventosMapper.toResponse(eventoSalida))
                .eventoEntrada(eventosMapper.toResponse(eventoEntrada))
                .build();
    }

    private Jugador findJugadorOrThrow(UUID idJugador) {
        return jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Jugador no encontrado con id: " + idJugador));
    }

    @Override
    public PartidoResponse iniciarPartido(UUID idPartido) {
        Partido partido = getPartidoOrThrow(idPartido);
        partido.iniciarPartido();
        return partidoMapper.toResponse(partidoRepository.save(partido));
    }
 
    @Override
    public PartidoResponse cambiarEstadoPartido(UUID idPartido, EstadoPartido nuevoEstado) {
        Partido partido = getPartidoOrThrow(idPartido);
        partido.cambiarEstado(nuevoEstado);
        return partidoMapper.toResponse(partidoRepository.save(partido));
    }

    @Override
    public PartidoResponse avanzarPartido(UUID idPartido) {
        Partido partido = getPartidoOrThrow(idPartido);
        partido.reanudarPartido();
        return partidoMapper.toResponse(partidoRepository.save(partido));
    }

    @Override
    public EventoPartidoResponse agregarTiempoAgregado(UUID idPartido, int minutos, String descripcion) {
        Partido partido = getPartidoOrThrow(idPartido);
        partido.agregarTiempoAgregado(minutos);
        Partido saved = partidoRepository.save(partido);

        EventosPartido agregado = saved.getEventos().stream()
                .filter(e -> e.getTipoEvento() == TipoEvento.AGREGADO)
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("No se encontró el evento agregado"));

        return eventosMapper.toResponse(agregado);    }

    @Override
    public PartidoResponse finalizarTiempo(UUID idPartido, LocalTime minutoFin) {
        Partido partido = getPartidoOrThrow(idPartido);
        partido.finalizarTiempo(minutoFin);
        return partidoMapper.toResponse(partidoRepository.save(partido));
    }

    @Override
    public PartidoResponse finalizarPartido(UUID idPartido) {
        Partido partido = getPartidoOrThrow(idPartido);
        partido.finalizarPartido();
        return partidoMapper.toResponse(partidoRepository.save(partido));
    }




    @Override
    public EventoPartidoResponse registrarEvento(UUID idPartido, RegistrarEventoRequest request) {
        Partido partido = getPartidoOrThrow(idPartido);

        EventosPartido eventoValidacion = EventosPartido.builder()
                .minuto(request.minuto())
                .build();
        eventoValidacion.validarMinutoCreate(partido);

        PersonalDeportivo personal = null;
        if (request.idPersonal() != null) {
            personal = personalRepository.findById(request.idPersonal())
                    .orElseThrow(() -> new PersonalNotFoundException(
                            "Personal no encontrado con id: " + request.idPersonal()));
        }

        Equipo equipoFavorecido = null;
        if (request.idEquipoFavorecido() != null) {
            equipoFavorecido = findClubOrThrow(request.idEquipoFavorecido());
        }

        EventosPartido evento = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .tipoEvento(request.tipoEvento())
                .minuto(request.minuto())
                .descripcion(request.descripcion())
                .personal(personal)
                .equipoFavorecido(equipoFavorecido)
                .partido(partido)
                .build();

        partido.agregarEvento(evento);

        eventosRepository.save(evento);

        partidoRepository.save(partido);

        return eventosMapper.toResponse(evento);
    }

    @Override
    public List<EventoPartidoResponse> registrarEventosBatch(UUID idPartido, List<RegistrarEventoRequest> requests) {
        Partido partido = getPartidoOrThrow(idPartido);
        List<EventosPartido> eventos = new ArrayList<>();

        for (RegistrarEventoRequest request : requests) {
            EventosPartido eventoValidacion = EventosPartido.builder()
                    .minuto(request.minuto())
                    .build();
            eventoValidacion.validarMinutoCreate(partido);

            PersonalDeportivo personal = null;
            if (request.idPersonal() != null) {
                personal = personalRepository.findById(request.idPersonal())
                        .orElseThrow(() -> new PersonalNotFoundException("Personal no encontrado"));
            }

            Equipo equipoFavorecido = null;
            if (request.idEquipoFavorecido() != null) {
                equipoFavorecido = findClubOrThrow(request.idEquipoFavorecido());
            }

            EventosPartido evento = EventosPartido.builder()
                    .idEvento(UUID.randomUUID())
                    .tipoEvento(request.tipoEvento())
                    .minuto(request.minuto())
                    .descripcion(request.descripcion())
                    .personal(personal)
                    .equipoFavorecido(equipoFavorecido)
                    .partido(partido)
                    .build();

            partido.agregarEvento(evento);
            eventos.add(evento);
        }

        eventosRepository.saveAll(eventos);
        partidoRepository.save(partido);

        return eventos.stream()
                .map(eventosMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoPartidoResponse> obtenerEventosDePartido(UUID idPartido) {
        return eventosRepository.findByPartido(idPartido).stream()
                .map(eventosMapper::toResponse)
                .toList();
    }
 
    @Override
    public void cancelarPartido(UUID idPartido) {
        Partido partido = getPartidoOrThrow(idPartido);
        if (partido.haFinalizado()) {
            throw new IllegalStateException("No se puede cancelar un partido ya finalizado");
        }
        partido.cambiarEstado(EstadoPartido.CANCELADO);
        partidoRepository.save(partido);
    }

    @Override
    @Transactional
    public void eliminarPartido(UUID idPartido) {
            Partido partido = getPartidoOrThrow(idPartido);

            if (partido.estaEnCurso()) {
                throw new IllegalStateException("No se puede eliminar un partido en curso");
            }

            partidoRepository.deleteById(idPartido);
        }

    @Override
    public void eliminarEvento(UUID idPartido, UUID idEvento) {
        Partido partido = getPartidoOrThrow(idPartido);

        if (!eventosRepository.existsById(idEvento)) {
            throw new IllegalArgumentException("Evento no encontrado con id: " + idEvento);
        }

        if (partido.haFinalizado()) {
            throw new IllegalStateException("No se puede eliminar eventos de un partido finalizado");
        }

        Optional<EventosPartido> eventoOpt = partido.getEventos().stream()
                .filter(e -> e.getIdEvento().equals(idEvento))
                .findFirst();

        if (eventoOpt.isPresent()) {
            EventosPartido evento = eventoOpt.get();

            if (evento.getTipoEvento().afectaMarcador()) {
                if (evento.getTipoEvento().esGolValido()) {
                    restarGolAlEliminar(partido, evento);
                } else if (evento.getTipoEvento() == TipoEvento.GOL_ANULADO) {
                    restaurarGolAnulado(partido, evento);
                }
            }

            partido.getEventos().remove(evento);
        }

        eventosRepository.deleteById(idEvento);

        partidoRepository.save(partido);
    }

    private void restarGolAlEliminar(Partido partido, EventosPartido evento) {
        if (evento.getEquipoFavorecido() == null) return;

        boolean esLocal = evento.getEquipoFavorecido().getIdEquipo()
                .equals(partido.getEquipoLocal().getIdEquipo());

        if (esLocal) {
            partido.setGolesLocal(partido.getGolesLocal() - 1);
        } else {
            partido.setGolesVisitante(partido.getGolesVisitante() - 1);
        }
    }


    private void restaurarGolAnulado(Partido partido, EventosPartido evento) {
        if (evento.getEquipoFavorecido() == null) return;

        EventosPartido golAnulado = partido.getEventos().stream()
                .filter(e -> e.getEquipoFavorecido() != null)
                .filter(e -> e.getEquipoFavorecido().getIdEquipo()
                        .equals(evento.getEquipoFavorecido().getIdEquipo()))
                .filter(e -> e.getTipoEvento().esGolValido())
                .filter(e -> e.getIdEvento().compareTo(evento.getIdEvento()) < 0) // Antes del GOL_ANULADO
                .reduce((first, second) -> second) // El más reciente
                .orElse(null);

        if (golAnulado != null) {
            boolean esLocal = evento.getEquipoFavorecido().getIdEquipo()
                    .equals(partido.getEquipoLocal().getIdEquipo());

            if (esLocal) {
                partido.setGolesLocal(partido.getGolesLocal() + 1);
            } else {
                partido.setGolesVisitante(partido.getGolesVisitante() + 1);
            }
        }
    }




    private Partido getPartidoOrThrow(UUID idPartido) {
        return partidoRepository.findById(idPartido)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Partido no encontrado con id: " + idPartido));
    }
 
    private PartidoResponse findPartidoOrThrow(UUID idPartido, PartidoMapper mapper) {
        return partidoRepository.findById(idPartido)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Partido no encontrado con id: " + idPartido));
    }
 
    private Equipo findClubOrThrow(UUID idClub) {
        return clubRepository.findById(idClub)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
    }
 
    private Competicion findCompeticionOrThrow(UUID idCompeticion) {
        return competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));
    }
 
    private Arbitro findArbitroOrThrow(UUID idArbitro) {
        return arbitroRepository.findById(idArbitro)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Árbitro no encontrado con id: " + idArbitro));
    }
}
