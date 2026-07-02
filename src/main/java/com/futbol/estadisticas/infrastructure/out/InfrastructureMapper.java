package com.futbol.estadisticas.infrastructure.out;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;
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
public class InfrastructureMapper {

    // ──────────────────────────── ESTADIO ────────────────────────────
 
    public Estadio toDomain(EstadioJPAEntity e) {
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
 
    public Arbitro toDomain(ArbitroJPAEntity e) {
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
 
    public DatosDeportivos toDomain(DatosDeportivosJPAEntity entity) {
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

        return DatosDeportivosJPAEntity.builder()
                .idHistorialDeportivo(domain.getIdHistorialDeportivo())
                .fechaActualizacion(domain.getFechaActualizacion())
                .estadoJugador(domain.getEstadoJugador())
                .valorMercado(domain.getValorMercado())
                .posiciones(domain.getPosiciones() != null ? domain.getPosiciones() : new ArrayList<>())
                .dorsal(domain.getDorsal())
                .jugador(jugadorJPA)
                .build();
    }
    
    // ──────────────────────────── LESIÓN ────────────────────────────
 
    public Lesion toDomain(LesionJPAEntity e) {
        if (e == null) return null;
        return Lesion.builder()
                .idLesion(e.getIdLesion())
                .nombreLesion(e.getNombreLesion())
                .gravedad(e.getGravedad())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .curada(e.isCurada())
                .build();
    }
 
    public LesionJPAEntity toJpa(Lesion d, JugadorJPAEntity jugadorJPA) {
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
 
    public Jugador toDomain(JugadorJPAEntity entity) {
        if (entity == null) return null;
        
        Jugador.JugadorBuilder builder = Jugador.builder()
                .idPersonal(entity.getIdPersonal())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .fechaNacimiento(entity.getFechaNacimiento())
                .nacionalidad(entity.getNacionalidad())
                .tipoPersonal(entity.getTipoPersonal())
                .pieHabil(entity.getPieHabil())
                .altura(entity.getAltura())
                .peso(entity.getPeso())
                .fechaActualizacion(entity.getFechaActualizacion());

        // Mapear datos deportivos - AHORA SIN RELACIÓN CIRCULAR
        if (entity.getDatosDeportivos() != null) {
            DatosDeportivos datosDeportivos = toDomain(entity.getDatosDeportivos());
            builder.datosDeportivos(datosDeportivos);
        }

        // Mapear lesiones
        if (entity.getLesiones() != null && !entity.getLesiones().isEmpty()) {
            builder.lesiones(entity.getLesiones().stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList()));
        }

        return builder.build();
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

        // Crear la entidad temporal para establecer relaciones
        JugadorJPAEntity entity = builder.build();

        // Mapear DATOS DEPORTIVOS
        if (d.getDatosDeportivos() != null) {
            DatosDeportivosJPAEntity datosJPA = toJpa(d.getDatosDeportivos(), entity);
            entity.setDatosDeportivos(datosJPA);
        }

        // Mapear LESIONES
        if (d.getLesiones() != null && !d.getLesiones().isEmpty()) {
            java.util.List<LesionJPAEntity> lesionesJPA = d.getLesiones().stream()
                    .map(lesion -> toJpa(lesion, entity))
                    .collect(Collectors.toList());
            entity.setLesiones(lesionesJPA);
        }

        return entity;
    }
 
    // ──────────────────────────── TÉCNICO ────────────────────────────
 
    public Tecnico toDomain(TecnicoJPAEntity e) {
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
 
    public Club toDomain(ClubJPAEntity e) {
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

    // Solo acceder al estadio si está inicializado (sesión abierta)
    if (e.getEstadio() != null && org.hibernate.Hibernate.isInitialized(e.getEstadio())) {
        club.setEstadio(toDomain(e.getEstadio()));
    }

    // Solo acceder al técnico si está inicializado
    if (e.getTecnicoActual() != null && org.hibernate.Hibernate.isInitialized(e.getTecnicoActual())) {
        club.setTecnicoActual(toDomain(e.getTecnicoActual()));
    }

    if (e.getContratos() != null && org.hibernate.Hibernate.isInitialized(e.getContratos())) {
        e.getContratos().forEach(c -> {
            Contrato contrato = toDomainSinRelaciones(c);
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
 
    // ──────────────────────────── CONTRATO ────────────────────────────
 
    /** Mapea contrato sin rellenar relaciones para evitar ciclos. */
    public Contrato toDomainSinRelaciones(ContratoJPAEntity e) {
        if (e == null) return null;
        return Contrato.builder()
                .idContrato(e.getIdContrato())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .sueldo(e.getSueldo())
                .estado(e.getEstado())
                .build();
    }
 
    public Contrato toDomain(ContratoJPAEntity e) {
        if (e == null) return null;
        Contrato c = toDomainSinRelaciones(e);
        if (e.getPersonal() instanceof JugadorJPAEntity jugadorJPA) {
            c.setPersonal(toDomain(jugadorJPA));
        } else if (e.getPersonal() instanceof TecnicoJPAEntity tecnicoJPA) {
            c.setPersonal(toDomain(tecnicoJPA));
        }
        if (e.getClub() != null) {
            c.setClub(toDomain(e.getClub()));
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
 
    // ──────────────────────────── COMPETICIÓN ────────────────────────────
 
    public Competicion toDomain(CompeticionJPAEntity e) {
        if (e == null) return null;
        return Competicion.builder()
                .idCompeticion(e.getIdCompeticion())
                .nombre(e.getNombre())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .partidos(new ArrayList<>())
                .build();
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
 
    public Partido toDomain(PartidoJPAEntity e) {
        if (e == null) return null;
        return Partido.builder()
                .idPartido(e.getIdPartido())
                .fechaYHora(e.getFechaYHora())
                .jornada(e.getJornada())
                .estado(e.getEstado())
                .golesLocal(e.getGolesLocal())
                .golesVisitante(e.getGolesVisitante())
                .equipoLocal(toDomain(e.getEquipoLocal()))
                .equipoVisitante(toDomain(e.getEquipoVisitante()))
                .estadio(toDomain(e.getEstadio()))
                .arbitro(toDomain(e.getArbitro()))
                .competicion(toDomain(e.getCompeticion()))
                .eventos(new ArrayList<>())
                .build();
    }
 
    public PartidoJPAEntity toJpa(Partido d,
                                  ClubJPAEntity local,
                                  ClubJPAEntity visitante,
                                  EstadioJPAEntity estadio,
                                  ArbitroJPAEntity arbitro,
                                  CompeticionJPAEntity competicion) {
        if (d == null) return null;
        return PartidoJPAEntity.builder()
                .idPartido(d.getIdPartido())
                .fechaYHora(d.getFechaYHora())
                .jornada(d.getJornada())
                .estado(d.getEstado())
                .golesLocal(d.getGolesLocal())
                .golesVisitante(d.getGolesVisitante())
                .equipoLocal(local)
                .equipoVisitante(visitante)
                .estadio(estadio)
                .arbitro(arbitro)
                .competicion(competicion)
                .build();
    }
 
    // ──────────────────────────── EVENTO ────────────────────────────
 
    public EventosPartido toDomain(EventosPartidoJPAEntity e) {
        if (e == null) return null;
        EventosPartido ev = EventosPartido.builder()
                .idEvento(e.getIdEvento())
                .minuto(e.getMinuto())
                .descripcion(e.getDescripcion())
                .tipoEvento(e.getTipoEvento())
                .build();
 
        if (e.getPersonal() instanceof JugadorJPAEntity jugadorJPA) {
            ev.setPersonal(toDomain(jugadorJPA));
        } else if (e.getPersonal() instanceof TecnicoJPAEntity tecnicoJPA) {
            ev.setPersonal(toDomain(tecnicoJPA));
        }
        if (e.getEquipoFavorecido() != null) {
            ev.setEquipoFavorecido(toDomain(e.getEquipoFavorecido()));
        }
        return ev;
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
                .partido(partidoJPA)
                .personal(personalJPA)
                .equipoFavorecido(equipoFavorecidoJPA)
                .build();
    }
 
    // ──────────────────────────── PERSONAL GENÉRICO ────────────────────────────
 
    public PersonalDeportivo toDomain(PersonalDeportivoJPAEntity entity) {
    if (entity == null) return null;
    
    if (entity instanceof JugadorJPAEntity) {
        return toDomain((JugadorJPAEntity) entity);
    } else if (entity instanceof TecnicoJPAEntity) {
        return toDomain((TecnicoJPAEntity) entity);
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
}