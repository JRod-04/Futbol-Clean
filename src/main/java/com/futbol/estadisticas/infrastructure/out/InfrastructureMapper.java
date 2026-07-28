package com.futbol.estadisticas.infrastructure.out;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.futbol.estadisticas.infrastructure.out.jpaRepository.ClubJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.CompeticionJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.PersonalDeportivoJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.futbol.estadisticas.domain.model.Arbitro;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Estadio;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Lesion;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.Tecnico;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ArbitroJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.CompeticionJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ContratoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.DatosDeportivosJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EstadioJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.EventosPartidoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.LesionJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PartidoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PersonalDeportivoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.TecnicoJPAEntity;

@Component
@RequiredArgsConstructor
public class InfrastructureMapper {

    private final PersonalDeportivoJPARepository personalRepo;
    private final ClubJPARepository clubRepo;


    // ──────────────────────────── ESTADIO ────────────────────────────

    public Estadio EstadiotoDomain(EstadioJPAEntity e) {
        if (e == null) return null;
        return Estadio.builder()
                .idEstadio(e.getIdEstadio())
                .nombre(e.getNombre())
                .direccion(e.getDireccion())
                .capacidad(e.getCapacidad())
                .fechaFundacion(e.getFechaFundacion())
                .build();
    }

    public EstadioJPAEntity toJpa(Estadio d) {
        if (d == null) return null;
        return EstadioJPAEntity.builder()
                .idEstadio(d.getIdEstadio())
                .nombre(d.getNombre())
                .direccion(d.getDireccion())
                .capacidad(d.getCapacidad())
                .fechaFundacion(d.getFechaFundacion())
                .build();
    }

    // ──────────────────────────── ÁRBITRO ────────────────────────────

    public Arbitro ArbitrotoDomain(ArbitroJPAEntity e) {
        if (e == null) return null;
        return Arbitro.builder()
                .idArbitro(e.getIdArbitro())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .fechaNacimiento(e.getFechaNacimiento())
                .partidosArbitrados(new ArrayList<>())
                .build();
    }

    public ArbitroJPAEntity toJpa(Arbitro d) {
        if (d == null) return null;
        return ArbitroJPAEntity.builder()
                .idArbitro(d.getIdArbitro())
                .nombre(d.getNombre())
                .apellido(d.getApellido())
                .fechaNacimiento(d.getFechaNacimiento())
                .build();
    }

    // ──────────────────────────── DATOS DEPORTIVOS ────────────────────────────

    public DatosDeportivos DatostoDomain(DatosDeportivosJPAEntity entity) {
        if (entity == null) return null;

        return DatosDeportivos.builder()
                .idHistorialDeportivo(entity.getIdHistorialDeportivo())
                .fechaActualizacion(entity.getFechaActualizacion())
                .estadoJugador(entity.getEstadoJugador())
                .valorMercado(entity.getValorMercado())
                .posiciones(entity.getPosiciones() != null ? entity.getPosiciones() : new ArrayList<>())
                .dorsal(entity.getDorsal())
                .build();
    }

    // ── DATOS DEPORTIVOS: DOMAIN → JPA ──

    public DatosDeportivosJPAEntity toJpa(DatosDeportivos domain, JugadorJPAEntity jugadorJPA) {
        if (domain == null) return null;

        DatosDeportivosJPAEntity entity = DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(domain.getIdHistorialDeportivo())
                .fechaActualizacion(domain.getFechaActualizacion())
                .estadoJugador(domain.getEstadoJugador())
                .valorMercado(domain.getValorMercado())
                .posiciones(domain.getPosiciones() != null ? domain.getPosiciones() : new ArrayList<>())
                .dorsal(domain.getDorsal())
                .jugador(jugadorJPA)
                .build();

        if (jugadorJPA == null && domain.getJugador() != null) {
            System.out.println("WARNING: JugadorJPA es null pero domain tiene jugador: " + domain.getJugador().getIdPersonal());
        }

        return entity;
    }

    // ──────────────────────────── LESIÓN ────────────────────────────

    public Lesion LesiontoDomain(LesionJPAEntity e) {
        if (e == null) return null;
        return Lesion.builder()
                .idLesion(e.getIdLesion())
                .nombreLesion(e.getNombreLesion())
                .gravedad(e.getGravedad())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .curada(e.isCurada())
                .jugadorLesionado(toJugadorBasico(e.getJugador()))
                .build();
    }

    public LesionJPAEntity LesiontoJpa(Lesion d, JugadorJPAEntity jugadorJPA) {
        if (d == null) return null;
        return LesionJPAEntity.builder()
                .idLesion(d.getIdLesion())
                .nombreLesion(d.getNombreLesion())
                .gravedad(d.getGravedad())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .curada(d.isCurada())
                .jugador(jugadorJPA)
                .build();
    }

    // ──────────────────────────── JUGADOR: JPA → DOMAIN ────────────────────────────

    public Jugador toJugadorBasico(JugadorJPAEntity entity) {
        if (entity == null) return null;
        return Jugador.builder()
                .idPersonal(entity.getIdPersonal())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .fechaNacimiento(entity.getFechaNacimiento())
                .nacionalidad(entity.getNacionalidad())
                .tipoPersonal(entity.getTipoPersonal())
                .pieHabil(entity.getPieHabil())
                .altura(entity.getAltura())
                .peso(entity.getPeso())
                .fechaActualizacion(entity.getFechaActualizacion())
                .contratos(new ArrayList<>())
                .build();
    }
    public Jugador JugadortoDomain(JugadorJPAEntity entity) {
        if (entity == null) return null;

        Jugador jugador = Jugador.builder()
                .idPersonal(entity.getIdPersonal())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .fechaNacimiento(entity.getFechaNacimiento())
                .nacionalidad(entity.getNacionalidad())
                .tipoPersonal(entity.getTipoPersonal())
                .pieHabil(entity.getPieHabil())
                .altura(entity.getAltura())
                .peso(entity.getPeso())
                .fechaActualizacion(entity.getFechaActualizacion())
                .contratos(new ArrayList<>())
                .build();

        if (entity.getDatosDeportivos() != null) {
            jugador.setDatosDeportivos(DatostoDomain(entity.getDatosDeportivos()));
        }

        if (entity.getLesiones() != null && !entity.getLesiones().isEmpty()) {
            jugador.setLesiones(entity.getLesiones().stream()
                    .map(this::LesiontoDomain)
                    .collect(Collectors.toList()));
        }

        if (entity.getContratos() != null && !entity.getContratos().isEmpty()) {
            List<Contrato> contratos = entity.getContratos().stream()
                    .map(c -> {
                        Contrato contrato = ContratotoDomain(c);
                        contrato.setPersonal(jugador);
                        return contrato;
                    })
                    .collect(Collectors.toList());
            jugador.setContratos(contratos);
        }

        return jugador;
    }

    // ──────────────────────────── JUGADOR: DOMAIN → JPA ────────────────────────────

    public JugadorJPAEntity toJpa(Jugador d) {
        if (d == null) return null;

        JugadorJPAEntity.JugadorJPAEntityBuilder builder = JugadorJPAEntity.builder()
                .idPersonal(d.getIdPersonal())
                .nombre(d.getNombre())
                .apellido(d.getApellido())
                .fechaNacimiento(d.getFechaNacimiento())
                .nacionalidad(d.getNacionalidad())
                .tipoPersonal(d.getTipoPersonal())
                .pieHabil(d.getPieHabil())
                .altura(d.getAltura())
                .peso(d.getPeso())
                .fechaActualizacion(d.getFechaActualizacion());

        JugadorJPAEntity entity = builder.build();

        if (d.getDatosDeportivos() != null) {
            DatosDeportivosJPAEntity datosJPA = toJpa(d.getDatosDeportivos(), entity);
            entity.setDatosDeportivos(datosJPA);
        }

        if (d.getLesiones() != null && !d.getLesiones().isEmpty()) {
            java.util.List<LesionJPAEntity> lesionesJPA = d.getLesiones().stream()
                    .map(lesion -> LesiontoJpa(lesion, entity))
                    .collect(Collectors.toList());
            entity.setLesiones(lesionesJPA);
        }

        return entity;
    }

    // ──────────────────────────── TÉCNICO ────────────────────────────

    public Tecnico TecnicotoDomain(TecnicoJPAEntity e) {
        if (e == null) return null;
        return Tecnico.builder()
                .idPersonal(e.getIdPersonal())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .fechaNacimiento(e.getFechaNacimiento())
                .nacionalidad(e.getNacionalidad())
                .tipoPersonal(e.getTipoPersonal())
                .estiloJuego(e.getEstiloJuego())
                .alineacionFavorita(e.getAlineacionFavorita())
                .contratos(new ArrayList<>())
                .eventos(new ArrayList<>())
                .build();
    }

    public TecnicoJPAEntity toJpa(Tecnico d) {
        if (d == null) return null;
        return TecnicoJPAEntity.builder()
                .idPersonal(d.getIdPersonal())
                .nombre(d.getNombre())
                .apellido(d.getApellido())
                .fechaNacimiento(d.getFechaNacimiento())
                .nacionalidad(d.getNacionalidad())
                .tipoPersonal(d.getTipoPersonal())
                .estiloJuego(d.getEstiloJuego())
                .alineacionFavorita(d.getAlineacionFavorita())
                .build();
    }

    // ──────────────────────────── CLUB ────────────────────────────

    public Club DatostoDomain(ClubJPAEntity e) {
        if (e == null) return null;
        Club club = Club.builder()
                .idEquipo(e.getIdEquipo())
                .nombre(e.getNombre())
                .nombreCorto(e.getNombreCorto())
                .fechaFundacion(e.getFechaFundacion())
                .contratos(new ArrayList<>())
                .partidosLocal(new ArrayList<>())
                .partidosVisitante(new ArrayList<>())
                .tecnicos(new ArrayList<>())
                .build();

        if (e.getEstadio() != null && org.hibernate.Hibernate.isInitialized(e.getEstadio())) {
            club.setEstadio(EstadiotoDomain(e.getEstadio()));
        }

        if (e.getTecnicoActual() != null && org.hibernate.Hibernate.isInitialized(e.getTecnicoActual())) {
            club.setTecnicoActual(TecnicotoDomain(e.getTecnicoActual()));
        }

        if (e.getContratos() != null && org.hibernate.Hibernate.isInitialized(e.getContratos())) {
            e.getContratos().forEach(c -> {
                Contrato contrato = toDomainConPersonal(c);
                contrato.setClub(club);
                club.getContratos().add(contrato);
            });
        }
        return club;
    }

    public ClubJPAEntity toJpa(Club d) {
        if (d == null) return null;
        return ClubJPAEntity.builder()
                .idEquipo(d.getIdEquipo())
                .nombre(d.getNombre())
                .nombreCorto(d.getNombreCorto())
                .fechaFundacion(d.getFechaFundacion())
                .build();
    }


    public Club toDomainConClubYBásicos(ClubJPAEntity e) {
        if (e == null) return null;

        Club club = Club.builder()
                .idEquipo(e.getIdEquipo())
                .nombre(e.getNombre())
                .nombreCorto(e.getNombreCorto())
                .fechaFundacion(e.getFechaFundacion())
                .contratos(new ArrayList<>())
                .partidosLocal(new ArrayList<>())
                .partidosVisitante(new ArrayList<>())
                .tecnicos(new ArrayList<>())
                .build();

        if (e.getEstadio() != null && org.hibernate.Hibernate.isInitialized(e.getEstadio())) {
            club.setEstadio(EstadiotoDomain(e.getEstadio()));
        }

        if (e.getTecnicoActual() != null && org.hibernate.Hibernate.isInitialized(e.getTecnicoActual())) {
            club.setTecnicoActual(TecnicotoDomain(e.getTecnicoActual()));
        }

        if (e.getContratos() != null && org.hibernate.Hibernate.isInitialized(e.getContratos())) {
            e.getContratos().forEach(c -> {
                Contrato contrato = toDomainConPersonalSinClub(c);
                contrato.setClub(club);
                club.getContratos().add(contrato);
            });
        }

        if (e.getTecnicoActual() != null && org.hibernate.Hibernate.isInitialized(e.getTecnicoActual())) {
            club.setTecnicoActual(TecnicotoDomain(e.getTecnicoActual()));
        }

        return club;
    }
    // ──────────────────────────── CONTRATO ────────────────────────────

    public Contrato toDomainConPersonal(ContratoJPAEntity e) {
        if (e == null) return null;

        Contrato c = Contrato.builder()
                .idContrato(e.getIdContrato())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .sueldo(e.getSueldo())
                .estado(e.getEstado())
                .build();

        if (e.getPersonal() != null) {
            PersonalDeportivoJPAEntity personalJPA = unwrapProxy(e.getPersonal());
            if (personalJPA instanceof JugadorJPAEntity) {
                Jugador jugador = toDomainSinContratos((JugadorJPAEntity) personalJPA);
                c.setPersonal(jugador);
            } else if (personalJPA instanceof TecnicoJPAEntity) {
                c.setPersonal(TecnicotoDomain((TecnicoJPAEntity) personalJPA));
            }
        }

        return c;
    }

    public Jugador toDomainSinContratos(JugadorJPAEntity entity) {
        if (entity == null) return null;

        Jugador jugador = Jugador.builder()
                .idPersonal(entity.getIdPersonal())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .fechaNacimiento(entity.getFechaNacimiento())
                .nacionalidad(entity.getNacionalidad())
                .tipoPersonal(entity.getTipoPersonal())
                .pieHabil(entity.getPieHabil())
                .altura(entity.getAltura())
                .peso(entity.getPeso())
                .fechaActualizacion(entity.getFechaActualizacion())
                .contratos(new ArrayList<>())
                .build();

        if (entity.getDatosDeportivos() != null) {
            jugador.setDatosDeportivos(DatostoDomain(entity.getDatosDeportivos()));
        }
        if (entity.getLesiones() != null && !entity.getLesiones().isEmpty()) {
            jugador.setLesiones(entity.getLesiones().stream()
                    .map(this::LesiontoDomain)
                    .collect(Collectors.toList()));
        }
        return jugador;
    }

    public Contrato ContratotoDomain(ContratoJPAEntity e) {
        if (e == null) return null;

        Contrato c = Contrato.builder()
                .idContrato(e.getIdContrato())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .sueldo(e.getSueldo())
                .estado(e.getEstado())
                .build();

        if (e.getPersonal() != null) {
            PersonalDeportivoJPAEntity personalJPA = unwrapProxy(e.getPersonal());
            if (personalJPA instanceof JugadorJPAEntity) {
                c.setPersonal(toDomainSinContratos((JugadorJPAEntity) personalJPA));
            } else if (personalJPA instanceof TecnicoJPAEntity) {
                c.setPersonal(TecnicotoDomain((TecnicoJPAEntity) personalJPA));
            }
        }

        if (e.getClub() != null) {
            c.setClub(DatostoDomain(e.getClub()));
        }

        return c;
    }

    public ContratoJPAEntity toJpa(Contrato d,
                                   PersonalDeportivoJPAEntity personalJPA,
                                   ClubJPAEntity clubJPA) {
        if (d == null) return null;
        return ContratoJPAEntity.builder()
                .idContrato(d.getIdContrato())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .sueldo(d.getSueldo())
                .estado(d.getEstado())
                .personal(personalJPA)
                .club(clubJPA)
                .build();
    }


    public Contrato toDomainConPersonalSinClub(ContratoJPAEntity e) {
        if (e == null) return null;

        Contrato c = Contrato.builder()
                .idContrato(e.getIdContrato())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .sueldo(e.getSueldo())
                .estado(e.getEstado())
                .build();

        if (e.getPersonal() != null) {
            PersonalDeportivoJPAEntity personalJPA = unwrapProxy(e.getPersonal());
            if (personalJPA instanceof JugadorJPAEntity) {
                Jugador jugador = toDomainSinContratos((JugadorJPAEntity) personalJPA);
                c.setPersonal(jugador);
            } else if (personalJPA instanceof TecnicoJPAEntity) {
                c.setPersonal(TecnicotoDomain((TecnicoJPAEntity) personalJPA));
            }
        }

        return c;
    }
    // ──────────────────────────── COMPETICIÓN ────────────────────────────

    public Competicion CompeticiontoDomain(CompeticionJPAEntity e) {
        if (e == null) return null;

        Competicion competicion = Competicion.builder()
                .idCompeticion(e.getIdCompeticion())
                .nombre(e.getNombre())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .partidos(new ArrayList<>())
                .build();

        if (e.getPartidos() != null && org.hibernate.Hibernate.isInitialized(e.getPartidos())) {
            List<Partido> partidos = e.getPartidos().stream()
                    .map(this::toDomainSinEventos)
                    .collect(Collectors.toList());
            competicion.setPartidos(partidos);

            partidos.forEach(p -> p.setCompeticion(competicion));
        }

        return competicion;
    }

    public Competicion toDomainWithPartidos(CompeticionJPAEntity e) {
        if (e == null) return null;

        Competicion competicion = Competicion.builder()
                .idCompeticion(e.getIdCompeticion())
                .nombre(e.getNombre())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .equipoGanador(e.getEquipoGanador() != null ? DatostoDomain(e.getEquipoGanador()) : null)
                .partidos(new ArrayList<>())
                .build();

        if (e.getPartidos() != null && !e.getPartidos().isEmpty()) {
            List<Partido> partidos = e.getPartidos().stream()
                    .map(this::toDomainSinEventos)
                    .collect(Collectors.toList());
            competicion.setPartidos(partidos);
            partidos.forEach(p -> p.setCompeticion(competicion));
        }

        return competicion;
    }

    public CompeticionJPAEntity toJpa(Competicion d) {
        if (d == null) return null;
        return CompeticionJPAEntity.builder()
                .idCompeticion(d.getIdCompeticion())
                .nombre(d.getNombre())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .build();
    }

    // ──────────────────────────── PARTIDO ────────────────────────────

    public Partido PartidotoDomain(PartidoJPAEntity e) {
        if (e == null) return null;
        Partido partido = Partido.builder()
                .idPartido(e.getIdPartido())
                .fechaYHora(e.getFechaYHora())
                .fase(e.getFase())
                .jornada(e.getJornadaTorneo())
                .estado(e.getEstado())
                .golesLocal(e.getGolesLocal())
                .golesVisitante(e.getGolesVisitante())
                .equipoLocal(DatostoDomain(e.getEquipoLocal()))
                .equipoVisitante(DatostoDomain(e.getEquipoVisitante()))
                .estadio(EstadiotoDomain(e.getEstadio()))
                .arbitro(ArbitrotoDomain(e.getArbitro()))
                .competicion(CompeticiontoDomain(e.getCompeticion()))
                .eventos(new ArrayList<>())
                .build();

        if (e.getEventos() != null && !e.getEventos().isEmpty()) {
            List<EventosPartido> eventos = e.getEventos().stream()
                    .map(this::EventotoDomain)
                    .collect(Collectors.toList());
            partido.setEventos(eventos);
        }
        return partido;
    }

    public PartidoJPAEntity toJpa(Partido d,
                                  ClubJPAEntity local,
                                  ClubJPAEntity visitante,
                                  EstadioJPAEntity estadio,
                                  ArbitroJPAEntity arbitro,
                                  CompeticionJPAEntity competicion) {
        if (d == null) return null;
        PartidoJPAEntity entity = PartidoJPAEntity.builder()
                .idPartido(d.getIdPartido())
                .fechaYHora(d.getFechaYHora())
                .jornadaTorneo(d.getJornada())
                .fase(d.getFase())
                .estado(d.getEstado())
                .golesLocal(d.getGolesLocal())
                .golesVisitante(d.getGolesVisitante())
                .equipoLocal(local)
                .equipoVisitante(visitante)
                .estadio(estadio)
                .arbitro(arbitro)
                .competicion(competicion)
                .build();

        if (d.getEventos() != null && !d.getEventos().isEmpty()) {
            List<EventosPartidoJPAEntity> eventosJPA = d.getEventos().stream()
                    .map(evento -> {
                        PersonalDeportivoJPAEntity personalJPA = null;
                        if (evento.getPersonal() != null) {
                            personalJPA = personalRepo.findById(evento.getPersonal().getIdPersonal()).orElse(null);
                        }

                        ClubJPAEntity equipoFavorecidoJPA = null;
                        if (evento.getEquipoFavorecido() != null) {
                            equipoFavorecidoJPA = clubRepo.findById(evento.getEquipoFavorecido().getIdEquipo()).orElse(null);
                        }

                        return toJpa(evento, entity, personalJPA, equipoFavorecidoJPA);
                    })
                    .collect(Collectors.toList());
            entity.setEventos(eventosJPA);
        }

        return entity;
    }

    // ──────────────────────────── EVENTO ────────────────────────────

    public EventosPartido EventotoDomain(EventosPartidoJPAEntity e) {
        if (e == null) return null;

        EventosPartido ev = EventosPartido.builder()
                .idEvento(e.getIdEvento())
                .minuto(e.getMinuto())
                .descripcion(e.getDescripcion())
                .tipoEvento(e.getTipoEvento())
                .estadoEvento(e.getEstadoEvento())
                .build();

        if (e.getPartido() != null) {
            Partido partido = toDomainSinEventos(e.getPartido());
            ev.setPartido(partido);
        }

        if (e.getPersonal() instanceof JugadorJPAEntity jugadorJPA) {
            ev.setPersonal(JugadortoDomain(jugadorJPA));
        } else if (e.getPersonal() instanceof TecnicoJPAEntity tecnicoJPA) {
            ev.setPersonal(TecnicotoDomain(tecnicoJPA));
        } else if (e.getPersonal() != null) {
            ev.setPersonal(toDomainPersonalGenerico(e.getPersonal()));
        }

        if (e.getEquipoFavorecido() != null) {
            ev.setEquipoFavorecido(DatostoDomain(e.getEquipoFavorecido()));
        }

        return ev;
    }

    private PersonalDeportivo toDomainPersonalGenerico(PersonalDeportivoJPAEntity entity) {
        if (entity == null) return null;
        return PersonalDeportivo.builder()
                .idPersonal(entity.getIdPersonal())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .fechaNacimiento(entity.getFechaNacimiento())
                .nacionalidad(entity.getNacionalidad())
                .tipoPersonal(entity.getTipoPersonal())
                .build();
    }
    public EventosPartidoJPAEntity toJpa(EventosPartido d,
                                         PartidoJPAEntity partidoJPA,
                                         PersonalDeportivoJPAEntity personalJPA,
                                         ClubJPAEntity equipoFavorecidoJPA) {
        if (d == null) return null;
        return EventosPartidoJPAEntity.builder()
                .idEvento(d.getIdEvento())
                .minuto(d.getMinuto())
                .descripcion(d.getDescripcion())
                .tipoEvento(d.getTipoEvento())
                .estadoEvento(d.getEstadoEvento())
                .partido(partidoJPA)
                .personal(personalJPA)
                .equipoFavorecido(equipoFavorecidoJPA)
                .build();
    }


    public Partido toDomainSinEventos(PartidoJPAEntity e) {
        if (e == null) return null;

        return Partido.builder()
                .idPartido(e.getIdPartido())
                .fechaYHora(e.getFechaYHora())
                .jornada(e.getJornadaTorneo())
                .fase(e.getFase())
                .estado(e.getEstado())
                .golesLocal(e.getGolesLocal())
                .golesVisitante(e.getGolesVisitante())
                .equipoLocal(DatostoDomain(e.getEquipoLocal()))
                .equipoVisitante(DatostoDomain(e.getEquipoVisitante()))
                .estadio(EstadiotoDomain(e.getEstadio()))
                .arbitro(ArbitrotoDomain(e.getArbitro()))
                .competicion(null)
                .eventos(new ArrayList<>())
                .build();
    }


    // ──────────────────────────── PERSONAL GENÉRICO ────────────────────────────

    public PersonalDeportivo PersonaltoDomain(PersonalDeportivoJPAEntity entity) {
    if (entity == null) return null;

    if (entity instanceof JugadorJPAEntity) {
        return JugadortoDomain((JugadorJPAEntity) entity);
    } else if (entity instanceof TecnicoJPAEntity) {
        return TecnicotoDomain((TecnicoJPAEntity) entity);
    }

    return PersonalDeportivo.builder()
            .idPersonal(entity.getIdPersonal())
            .nombre(entity.getNombre())
            .apellido(entity.getApellido())
            .fechaNacimiento(entity.getFechaNacimiento())
            .nacionalidad(entity.getNacionalidad())
            .tipoPersonal(entity.getTipoPersonal())
            .build();
}


    private PersonalDeportivoJPAEntity unwrapProxy(PersonalDeportivoJPAEntity entity) {
        if (entity instanceof org.hibernate.proxy.HibernateProxy) {
            return (PersonalDeportivoJPAEntity) ((org.hibernate.proxy.HibernateProxy) entity)
                    .getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }
}