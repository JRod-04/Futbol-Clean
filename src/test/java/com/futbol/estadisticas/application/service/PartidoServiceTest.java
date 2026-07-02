package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.CrearPartidoRequest;
import com.futbol.estadisticas.application.port.dto.request.RegistrarEventoRequest;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.mapper.EventosPartidoMapper;
import com.futbol.estadisticas.application.port.mapper.PartidoMapper;
import com.futbol.estadisticas.application.port.out.ArbitroRepositoryPort;
import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.application.port.out.EstadioRepositoryPort;
import com.futbol.estadisticas.application.port.out.EventosPartidoRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.application.port.out.PersonalDeportivoRepositoryPort;
import com.futbol.estadisticas.domain.model.Arbitro;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.Estadio;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidoServiceTest {

    @Mock
    private PartidoRepositoryPort partidoRepository;

    @Mock
    private ClubRepositoryPort clubRepository;

    @Mock
    private CompeticionRepositoryPort competicionRepository;

    @Mock
    private ArbitroRepositoryPort arbitroRepository;

    @Mock
    private EstadioRepositoryPort estadioRepository;

    @Mock
    private EventosPartidoRepositoryPort eventosRepository;

    @Mock
    private PersonalDeportivoRepositoryPort personalRepository;

    @Mock
    private PartidoMapper partidoMapper;

    @Mock
    private EventosPartidoMapper eventosMapper;

    @InjectMocks
    private PartidoService partidoService;

    private static final UUID ID_PARTIDO = UUID.randomUUID();
    private static final UUID ID_CLUB_LOCAL = UUID.randomUUID();
    private static final UUID ID_CLUB_VISITANTE = UUID.randomUUID();
    private static final UUID ID_COMPETICION = UUID.randomUUID();
    private static final UUID ID_ARBITRO = UUID.randomUUID();
    private static final UUID ID_ESTADIO = UUID.randomUUID();
    private static final UUID ID_PERSONAL = UUID.randomUUID();

    private Club clubLocal;
    private Club clubVisitante;
    private Competicion competicion;
    private Arbitro arbitro;
    private Estadio estadio;
    private Partido partido;
    private Partido partidoFinalizado;
    private Partido partidoEnCurso;
    private Jugador jugador;
    private EventosPartido evento;
    private PartidoResponse partidoResponse;
    private EventoPartidoResponse eventoResponse;
    private CrearPartidoRequest crearRequest;
    private RegistrarEventoRequest registrarEventoRequest;

    @BeforeEach
    void setUp() {
        clubLocal = Club.builder()
                .idEquipo(ID_CLUB_LOCAL)
                .nombre("FC Barcelona")
                .nombreCorto("Barça")
                .fechaFundacion(LocalDate.of(1899, 11, 29))
                .build();

        clubVisitante = Club.builder()
                .idEquipo(ID_CLUB_VISITANTE)
                .nombre("Real Madrid")
                .nombreCorto("Madrid")
                .fechaFundacion(LocalDate.of(1902, 3, 6))
                .build();

        competicion = Competicion.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("La Liga")
                .fechaInicio(LocalDateTime.now().minusMonths(2))
                .fechaFin(LocalDateTime.now().plusMonths(2))
                .partidos(new ArrayList<>())
                .build();

        arbitro = Arbitro.builder()
                .idArbitro(ID_ARBITRO)
                .nombre("Juan")
                .apellido("Martínez")
                .fechaNacimiento(LocalDate.of(1980, 5, 15))
                .partidosArbitrados(new ArrayList<>())
                .build();

        estadio = Estadio.builder()
                .idEstadio(ID_ESTADIO)
                .nombre("Camp Nou")
                .direccion("C/ Arístides Maillol, 12")
                .capacidad(99354)
                .fechaFundacion(LocalDate.of(1957, 9, 24))
                .build();

        jugador = Jugador.builder()
                .idPersonal(ID_PERSONAL)
                .nombre("Lionel")
                .apellido("Messi")
                .fechaNacimiento(LocalDate.of(1987, 6, 24))
                .nacionalidad(Nacion.ARGENTINA)
                .tipoPersonal(TipoPersonal.JUGADOR)
                .pieHabil(JuegoPies.ZURDO)
                .altura(170)
                .peso(72)
                .build();

        partido = Partido.builder()
                .idPartido(ID_PARTIDO)
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .competicion(competicion)
                .arbitro(arbitro)
                .estadio(estadio)
                .fechaYHora(LocalDateTime.now().plusDays(7))
                .jornada(10)
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .eventos(new ArrayList<>())
                .build();

        partidoFinalizado = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .competicion(competicion)
                .arbitro(arbitro)
                .estadio(estadio)
                .fechaYHora(LocalDateTime.now().minusDays(7))
                .jornada(9)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(2)
                .golesVisitante(1)
                .eventos(new ArrayList<>())
                .build();

        partidoEnCurso = Partido.builder()
                .idPartido(ID_PARTIDO)
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .competicion(competicion)
                .arbitro(arbitro)
                .estadio(estadio)
                .fechaYHora(LocalDateTime.now())
                .jornada(10)
                .estado(EstadoPartido.PRIMER_TIEMPO)
                .golesLocal(2)
                .golesVisitante(1)
                .eventos(new ArrayList<>())
                .build();

        evento = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .tipoEvento(TipoEvento.GOL)
                .minuto(LocalTime.of(0, 30))
                .descripcion("Gol de Messi")
                .personal(jugador)
                .equipoFavorecido(clubLocal)
                .partido(partido)
                .build();

        partidoResponse = PartidoResponse.builder()
                .idPartido(ID_PARTIDO)
                .fechaYHora(LocalDateTime.now().plusDays(7))
                .jornada(10)
                .estado(EstadoPartido.PROGRAMADO)
                .estadoDisplayName("Programado")
                .idEquipoLocal(ID_CLUB_LOCAL)
                .nombreEquipoLocal("FC Barcelona")
                .idEquipoVisitante(ID_CLUB_VISITANTE)
                .nombreEquipoVisitante("Real Madrid")
                .golesLocal(0)
                .golesVisitante(0)
                .resultado("0-0")
                .nombreArbitro("Juan Martínez")
                .nombreEstadio("Camp Nou")
                .nombreCompeticion("La Liga")
                .idCompeticion(ID_COMPETICION)
                .enCurso(false)
                .finalizado(false)
                .esFuturo(true)
                .esHoy(false)
                .build();

        eventoResponse = EventoPartidoResponse.builder()
                .idEvento(evento.getIdEvento())
                .minuto(LocalTime.of(0, 30))
                .minutoFormateado("30'")
                .tipoEvento(TipoEvento.GOL)
                .descripcionCompleta("30' - GOL - Lionel Messi (Barça)")
                .idPersonal(ID_PERSONAL)
                .nombreJugador("Lionel Messi")
                .nombreEquipoFavorecido("Barça")
                .esGol(true)
                .esTarjeta(false)
                .esSustitucion(false)
                .esPenalti(false)
                .colorTarjeta(null)
                .build();

        crearRequest = new CrearPartidoRequest(
                ID_CLUB_LOCAL,
                ID_CLUB_VISITANTE,
                ID_COMPETICION,
                ID_ARBITRO,
                LocalDateTime.now().plusDays(7),
                10,
                ID_ESTADIO
        );

        registrarEventoRequest = new RegistrarEventoRequest(
                TipoEvento.GOL,
                LocalTime.of(0, 30),
                ID_PERSONAL,
                ID_CLUB_LOCAL,
                "Gol de Messi"
        );
    }

    @Test
    @DisplayName("programarPartido: debe programar un partido correctamente")
    void testProgramarPartido() {
        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
        when(clubRepository.findById(ID_CLUB_VISITANTE)).thenReturn(Optional.of(clubVisitante));
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
        when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));
        when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));
        when(partidoRepository.save(any(Partido.class))).thenReturn(partido);
        when(partidoMapper.toResponse(partido)).thenReturn(partidoResponse);

        PartidoResponse result = partidoService.programarPartido(crearRequest);

        assertThat(result).isNotNull();
        assertThat(result.idPartido()).isEqualTo(ID_PARTIDO);
        assertThat(result.nombreEquipoLocal()).isEqualTo("FC Barcelona");
        assertThat(result.nombreEquipoVisitante()).isEqualTo("Real Madrid");
        assertThat(result.estado()).isEqualTo(EstadoPartido.PROGRAMADO);
        assertThat(result.estadoDisplayName()).isEqualTo("Programado");
        assertThat(result.resultado()).isEqualTo("0-0");
        assertThat(result.esFuturo()).isTrue();
        assertThat(result.finalizado()).isFalse();

        verify(clubRepository).findById(ID_CLUB_LOCAL);
        verify(clubRepository).findById(ID_CLUB_VISITANTE);
        verify(competicionRepository).findById(ID_COMPETICION);
        verify(arbitroRepository).findById(ID_ARBITRO);
        verify(estadioRepository).findById(ID_ESTADIO);
        verify(partidoRepository).save(any(Partido.class));
        verify(partidoMapper).toResponse(partido);
    }

    @Test
    @DisplayName("programarPartido: debe lanzar excepción cuando el club local no existe")
    void testProgramarPartido_ClubLocalNoExiste() {
        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.programarPartido(crearRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Club no encontrado con id: " + ID_CLUB_LOCAL);

        verify(clubRepository).findById(ID_CLUB_LOCAL);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("programarPartido: debe lanzar excepción cuando el club visitante no existe")
    void testProgramarPartido_ClubVisitanteNoExiste() {
        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
        when(clubRepository.findById(ID_CLUB_VISITANTE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.programarPartido(crearRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Club no encontrado con id: " + ID_CLUB_VISITANTE);

        verify(clubRepository).findById(ID_CLUB_LOCAL);
        verify(clubRepository).findById(ID_CLUB_VISITANTE);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("programarPartido: debe lanzar excepción cuando el club local y visitante son el mismo")
    void testProgramarPartido_ClubesIguales() {
        UUID mismoClub = UUID.randomUUID();
        CrearPartidoRequest requestMismoClub = new CrearPartidoRequest(
                mismoClub,
                mismoClub,
                ID_COMPETICION,
                ID_ARBITRO,
                LocalDateTime.now().plusDays(7),
                10,
                ID_ESTADIO
        );

        Club club = Club.builder().idEquipo(mismoClub).build();

        when(clubRepository.findById(mismoClub)).thenReturn(Optional.of(club));
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
        when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));

        assertThatThrownBy(() -> partidoService.programarPartido(requestMismoClub))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Un club no puede jugar contra sí mismo");

        verify(clubRepository, times(2)).findById(mismoClub);
        verify(competicionRepository).findById(ID_COMPETICION);
        verify(arbitroRepository).findById(ID_ARBITRO);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("programarPartido: debe lanzar excepción cuando la competición no existe")
    void testProgramarPartido_CompeticionNoExiste() {
        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
        when(clubRepository.findById(ID_CLUB_VISITANTE)).thenReturn(Optional.of(clubVisitante));
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.programarPartido(crearRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Competición no encontrada con id: " + ID_COMPETICION);

        verify(clubRepository).findById(ID_CLUB_LOCAL);
        verify(clubRepository).findById(ID_CLUB_VISITANTE);
        verify(competicionRepository).findById(ID_COMPETICION);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("programarPartido: debe lanzar excepción cuando la competición ya finalizó")
    void testProgramarPartido_CompeticionFinalizada() {
        Competicion competicionFinalizada = Competicion.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("La Liga")
                .fechaInicio(LocalDateTime.now().minusMonths(4))
                .fechaFin(LocalDateTime.now().minusMonths(1))
                .partidos(new ArrayList<>())
                .build();

        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
        when(clubRepository.findById(ID_CLUB_VISITANTE)).thenReturn(Optional.of(clubVisitante));
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicionFinalizada));
        when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));

        assertThatThrownBy(() -> partidoService.programarPartido(crearRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se puede programar un partido en una competición finalizada");

        verify(competicionRepository).findById(ID_COMPETICION);
        verify(arbitroRepository).findById(ID_ARBITRO);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("programarPartido: debe lanzar excepción cuando el árbitro no existe")
    void testProgramarPartido_ArbitroNoExiste() {
        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
        when(clubRepository.findById(ID_CLUB_VISITANTE)).thenReturn(Optional.of(clubVisitante));
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
        when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.programarPartido(crearRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Árbitro no encontrado con id: " + ID_ARBITRO);

        verify(clubRepository).findById(ID_CLUB_LOCAL);
        verify(clubRepository).findById(ID_CLUB_VISITANTE);
        verify(competicionRepository).findById(ID_COMPETICION);
        verify(arbitroRepository).findById(ID_ARBITRO);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("programarPartido: debe permitir crear partido sin estadio")
    void testProgramarPartido_SinEstadio() {
        CrearPartidoRequest requestSinEstadio = new CrearPartidoRequest(
                ID_CLUB_LOCAL,
                ID_CLUB_VISITANTE,
                ID_COMPETICION,
                ID_ARBITRO,
                LocalDateTime.now().plusDays(7),
                10,
                null
        );

        Partido partidoSinEstadio = Partido.builder()
                .idPartido(ID_PARTIDO)
                .equipoLocal(clubLocal)
                .equipoVisitante(clubVisitante)
                .competicion(competicion)
                .arbitro(arbitro)
                .estadio(null)
                .fechaYHora(LocalDateTime.now().plusDays(7))
                .jornada(10)
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .eventos(new ArrayList<>())
                .build();

        PartidoResponse responseSinEstadio = PartidoResponse.builder()
                .idPartido(ID_PARTIDO)
                .fechaYHora(LocalDateTime.now().plusDays(7))
                .jornada(10)
                .estado(EstadoPartido.PROGRAMADO)
                .estadoDisplayName("Programado")
                .idEquipoLocal(ID_CLUB_LOCAL)
                .nombreEquipoLocal("FC Barcelona")
                .idEquipoVisitante(ID_CLUB_VISITANTE)
                .nombreEquipoVisitante("Real Madrid")
                .golesLocal(0)
                .golesVisitante(0)
                .resultado("0-0")
                .nombreArbitro("Juan Martínez")
                .nombreEstadio(null)
                .nombreCompeticion("La Liga")
                .idCompeticion(ID_COMPETICION)
                .enCurso(false)
                .finalizado(false)
                .esFuturo(true)
                .esHoy(false)
                .build();

        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
        when(clubRepository.findById(ID_CLUB_VISITANTE)).thenReturn(Optional.of(clubVisitante));
        when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
        when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));
        when(partidoRepository.save(any(Partido.class))).thenReturn(partidoSinEstadio);
        when(partidoMapper.toResponse(partidoSinEstadio)).thenReturn(responseSinEstadio);

        PartidoResponse result = partidoService.programarPartido(requestSinEstadio);

        assertThat(result).isNotNull();
        assertThat(result.nombreEstadio()).isNull();
        verify(estadioRepository, never()).findById(any());
        verify(partidoRepository).save(any(Partido.class));
    }

    @Test
    @DisplayName("obtenerPartidoPorId: debe retornar el partido cuando existe")
    void testObtenerPartidoPorId_Existe() {
        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(partidoMapper.toResponse(partido)).thenReturn(partidoResponse);

        PartidoResponse result = partidoService.obtenerPartidoPorId(ID_PARTIDO);

        assertThat(result).isNotNull();
        assertThat(result.idPartido()).isEqualTo(ID_PARTIDO);
        assertThat(result.nombreEquipoLocal()).isEqualTo("FC Barcelona");
        assertThat(result.nombreEquipoVisitante()).isEqualTo("Real Madrid");
        assertThat(result.estadoDisplayName()).isEqualTo("Programado");

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(partidoMapper).toResponse(partido);
    }

    @Test
    @DisplayName("obtenerPartidoPorId: debe lanzar excepción cuando el partido no existe")
    void testObtenerPartidoPorId_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(partidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.obtenerPartidoPorId(idInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Partido no encontrado con id: " + idInexistente);

        verify(partidoRepository).findById(idInexistente);
        verify(partidoMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("obtenerPartidosPorCompeticion: debe retornar partidos de una competición")
    void testObtenerPartidosPorCompeticion() {
        List<Partido> partidos = List.of(partido);
        List<PartidoResponse> responses = List.of(partidoResponse);

        when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(partidos);
        when(partidoMapper.toResponse(partido)).thenReturn(partidoResponse);

        List<PartidoResponse> result = partidoService.obtenerPartidosPorCompeticion(ID_COMPETICION);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombreCompeticion()).isEqualTo("La Liga");
        assertThat(result.get(0).idCompeticion()).isEqualTo(ID_COMPETICION);

        verify(partidoRepository).findByCompeticion(ID_COMPETICION);
        verify(partidoMapper).toResponse(partido);
    }

    @Test
    @DisplayName("obtenerPartidosPorCompeticion: debe retornar lista vacía cuando no hay partidos")
    void testObtenerPartidosPorCompeticion_Vacio() {
        when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of());

        List<PartidoResponse> result = partidoService.obtenerPartidosPorCompeticion(ID_COMPETICION);

        assertThat(result).isEmpty();
        verify(partidoRepository).findByCompeticion(ID_COMPETICION);
        verify(partidoMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("obtenerPartidosPorClub: debe retornar partidos de un club")
    void testObtenerPartidosPorClub() {
        List<Partido> partidos = List.of(partido);
        List<PartidoResponse> responses = List.of(partidoResponse);

        when(partidoRepository.findByClub(ID_CLUB_LOCAL)).thenReturn(partidos);
        when(partidoMapper.toResponse(partido)).thenReturn(partidoResponse);

        List<PartidoResponse> result = partidoService.obtenerPartidosPorClub(ID_CLUB_LOCAL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).idEquipoLocal()).isEqualTo(ID_CLUB_LOCAL);
        assertThat(result.get(0).nombreEquipoLocal()).isEqualTo("FC Barcelona");

        verify(partidoRepository).findByClub(ID_CLUB_LOCAL);
        verify(partidoMapper).toResponse(partido);
    }

    @Test
    @DisplayName("obtenerPartidosPorClub: debe retornar lista vacía cuando el club no tiene partidos")
    void testObtenerPartidosPorClub_Vacio() {
        UUID clubSinPartidos = UUID.randomUUID();
        when(partidoRepository.findByClub(clubSinPartidos)).thenReturn(List.of());

        List<PartidoResponse> result = partidoService.obtenerPartidosPorClub(clubSinPartidos);

        assertThat(result).isEmpty();
        verify(partidoRepository).findByClub(clubSinPartidos);
        verify(partidoMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("iniciarPartido: debe iniciar un partido correctamente")
    void testIniciarPartido() {
        PartidoResponse responseEnCurso = PartidoResponse.builder()
                .idPartido(ID_PARTIDO)
                .fechaYHora(LocalDateTime.now())
                .jornada(10)
                .estado(EstadoPartido.PRIMER_TIEMPO)
                .estadoDisplayName("1er Tiempo")
                .idEquipoLocal(ID_CLUB_LOCAL)
                .nombreEquipoLocal("FC Barcelona")
                .idEquipoVisitante(ID_CLUB_VISITANTE)
                .nombreEquipoVisitante("Real Madrid")
                .golesLocal(0)
                .golesVisitante(0)
                .resultado("0-0")
                .nombreArbitro("Juan Martínez")
                .nombreEstadio("Camp Nou")
                .nombreCompeticion("La Liga")
                .idCompeticion(ID_COMPETICION)
                .enCurso(true)
                .finalizado(false)
                .esFuturo(false)
                .esHoy(true)
                .build();

        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(partido)).thenReturn(partido);
        when(partidoMapper.toResponse(partido)).thenReturn(responseEnCurso);

        PartidoResponse result = partidoService.iniciarPartido(ID_PARTIDO);

        assertThat(result).isNotNull();
        assertThat(result.estado()).isEqualTo(EstadoPartido.PRIMER_TIEMPO);
        assertThat(result.estadoDisplayName()).isEqualTo("1er Tiempo");
        assertThat(result.enCurso()).isTrue();

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(partidoRepository).save(partido);
        verify(partidoMapper).toResponse(partido);
    }

    @Test
    @DisplayName("iniciarPartido: debe lanzar excepción cuando el partido no existe")
    void testIniciarPartido_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(partidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.iniciarPartido(idInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Partido no encontrado con id: " + idInexistente);

        verify(partidoRepository).findById(idInexistente);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("iniciarPartido: debe lanzar excepción si el partido ya está en curso")
    void testIniciarPartido_YaEnCurso() {
        Partido partidoEnCurso = Partido.builder()
                .idPartido(ID_PARTIDO)
                .estado(EstadoPartido.PRIMER_TIEMPO)
                .build();

        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partidoEnCurso));

        assertThatThrownBy(() -> partidoService.iniciarPartido(ID_PARTIDO))
                .isInstanceOf(IllegalStateException.class);

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("cambiarEstadoPartido: debe cambiar el estado de un partido")
    void testCambiarEstadoPartido() {
        PartidoResponse responseSuspendido = PartidoResponse.builder()
                .idPartido(ID_PARTIDO)
                .fechaYHora(LocalDateTime.now().plusDays(7))
                .jornada(10)
                .estado(EstadoPartido.SUSPENDIDO)
                .estadoDisplayName("Suspendido")
                .idEquipoLocal(ID_CLUB_LOCAL)
                .nombreEquipoLocal("FC Barcelona")
                .idEquipoVisitante(ID_CLUB_VISITANTE)
                .nombreEquipoVisitante("Real Madrid")
                .golesLocal(0)
                .golesVisitante(0)
                .resultado("0-0")
                .nombreArbitro("Juan Martínez")
                .nombreEstadio("Camp Nou")
                .nombreCompeticion("La Liga")
                .idCompeticion(ID_COMPETICION)
                .enCurso(false)
                .finalizado(false)
                .esFuturo(true)
                .esHoy(false)
                .build();

        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(partido)).thenReturn(partido);
        when(partidoMapper.toResponse(partido)).thenReturn(responseSuspendido);

        PartidoResponse result = partidoService.cambiarEstadoPartido(ID_PARTIDO, EstadoPartido.SUSPENDIDO);

        assertThat(result).isNotNull();
        assertThat(result.estado()).isEqualTo(EstadoPartido.SUSPENDIDO);
        assertThat(result.estadoDisplayName()).isEqualTo("Suspendido");

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(partidoRepository).save(partido);
        verify(partidoMapper).toResponse(partido);
    }

    @Test
    @DisplayName("cambiarEstadoPartido: debe lanzar excepción cuando el partido no existe")
    void testCambiarEstadoPartido_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(partidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.cambiarEstadoPartido(idInexistente, EstadoPartido.SUSPENDIDO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Partido no encontrado con id: " + idInexistente);

        verify(partidoRepository).findById(idInexistente);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("finalizarPartido: debe finalizar un partido correctamente")
    void testFinalizarPartido() {
        PartidoResponse responseFinalizado = PartidoResponse.builder()
                .idPartido(ID_PARTIDO)
                .fechaYHora(LocalDateTime.now())
                .jornada(10)
                .estado(EstadoPartido.FINALIZADO)
                .estadoDisplayName("Finalizado")
                .idEquipoLocal(ID_CLUB_LOCAL)
                .nombreEquipoLocal("FC Barcelona")
                .idEquipoVisitante(ID_CLUB_VISITANTE)
                .nombreEquipoVisitante("Real Madrid")
                .golesLocal(2)
                .golesVisitante(1)
                .resultado("2-1")
                .nombreArbitro("Juan Martínez")
                .nombreEstadio("Camp Nou")
                .nombreCompeticion("La Liga")
                .idCompeticion(ID_COMPETICION)
                .enCurso(false)
                .finalizado(true)
                .esFuturo(false)
                .esHoy(true)
                .build();

        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partidoEnCurso));
        when(partidoRepository.save(partidoEnCurso)).thenReturn(partidoEnCurso);
        when(partidoMapper.toResponse(partidoEnCurso)).thenReturn(responseFinalizado);

        PartidoResponse result = partidoService.finalizarPartido(ID_PARTIDO);

        assertThat(result).isNotNull();
        assertThat(result.estado()).isEqualTo(EstadoPartido.FINALIZADO);
        assertThat(result.estadoDisplayName()).isEqualTo("Finalizado");
        assertThat(result.finalizado()).isTrue();
        assertThat(result.resultado()).isEqualTo("2-1");

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(partidoRepository).save(partidoEnCurso);
        verify(partidoMapper).toResponse(partidoEnCurso);
    }

    @Test
    @DisplayName("finalizarPartido: debe lanzar excepción cuando el partido no existe")
    void testFinalizarPartido_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(partidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.finalizarPartido(idInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Partido no encontrado con id: " + idInexistente);

        verify(partidoRepository).findById(idInexistente);
        verify(partidoRepository, never()).save(any());
    }

@Test
@DisplayName("finalizarPartido: debe lanzar excepción si el partido ya está finalizado, suspendido o cancelado")
void testFinalizarPartido_YaFinalizado() {
    Partido partidoYaFinalizado = Partido.builder()
            .idPartido(ID_PARTIDO)
            .estado(EstadoPartido.FINALIZADO)
            .build();

    when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partidoYaFinalizado));

    assertThatThrownBy(() -> partidoService.finalizarPartido(ID_PARTIDO))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("El partido ya ha finalizado");

    verify(partidoRepository).findById(ID_PARTIDO);
    verify(partidoRepository, never()).save(any());
}

    @Test
    @DisplayName("registrarEvento: debe registrar un evento en un partido")
    void testRegistrarEvento() {
        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.of(jugador));
        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
        when(partidoRepository.save(partido)).thenReturn(partido);
        when(eventosRepository.save(any(EventosPartido.class))).thenReturn(evento);
        when(eventosMapper.toResponse(evento)).thenReturn(eventoResponse);

        EventoPartidoResponse result = partidoService.registrarEvento(ID_PARTIDO, registrarEventoRequest);

        assertThat(result).isNotNull();
        assertThat(result.tipoEvento()).isEqualTo(TipoEvento.GOL);
        assertThat(result.minuto()).isEqualTo(LocalTime.of(0, 30));
        assertThat(result.minutoFormateado()).isEqualTo("30'");
        assertThat(result.descripcionCompleta()).isEqualTo("30' - GOL - Lionel Messi (Barça)");
        assertThat(result.nombreJugador()).isEqualTo("Lionel Messi");
        assertThat(result.nombreEquipoFavorecido()).isEqualTo("Barça");
        assertThat(result.esGol()).isTrue();
        assertThat(result.esTarjeta()).isFalse();

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(personalRepository).findById(ID_PERSONAL);
        verify(clubRepository).findById(ID_CLUB_LOCAL);
        verify(partidoRepository).save(partido);
        verify(eventosRepository).save(any(EventosPartido.class));
        verify(eventosMapper).toResponse(evento);
    }

    @Test
    @DisplayName("registrarEvento: debe lanzar excepción cuando el partido no existe")
    void testRegistrarEvento_PartidoNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(partidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.registrarEvento(idInexistente, registrarEventoRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Partido no encontrado con id: " + idInexistente);

        verify(partidoRepository).findById(idInexistente);
        verify(personalRepository, never()).findById(any());
        verify(eventosRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarEvento: debe lanzar excepción cuando el personal no existe")
    void testRegistrarEvento_PersonalNoExiste() {
        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.registrarEvento(ID_PARTIDO, registrarEventoRequest))
                .isInstanceOf(PersonalNotFoundException.class)
                .hasMessageContaining("Personal no encontrado con id: " + ID_PERSONAL);

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(personalRepository).findById(ID_PERSONAL);
        verify(eventosRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarEvento: debe permitir registrar evento sin personal")
    void testRegistrarEvento_SinPersonal() {
        RegistrarEventoRequest requestSinPersonal = new RegistrarEventoRequest(
                TipoEvento.GOL,
                LocalTime.of(0, 30),
                null,
                ID_CLUB_LOCAL,
                "Gol"
        );

        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
        when(partidoRepository.save(partido)).thenReturn(partido);
        when(eventosRepository.save(any(EventosPartido.class))).thenReturn(evento);
        when(eventosMapper.toResponse(evento)).thenReturn(eventoResponse);

        EventoPartidoResponse result = partidoService.registrarEvento(ID_PARTIDO, requestSinPersonal);

        assertThat(result).isNotNull();
        verify(personalRepository, never()).findById(any());
        verify(eventosRepository).save(any(EventosPartido.class));
    }

    @Test
    @DisplayName("registrarEvento: debe permitir registrar evento sin equipo favorecido")
    void testRegistrarEvento_SinEquipoFavorecido() {
        RegistrarEventoRequest requestSinEquipo = new RegistrarEventoRequest(
                TipoEvento.AMARILLA,
                LocalTime.of(0, 45),
                ID_PERSONAL,
                null,
                "Tarjeta amarilla"
        );

        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.of(jugador));
        when(partidoRepository.save(partido)).thenReturn(partido);
        when(eventosRepository.save(any(EventosPartido.class))).thenReturn(evento);
        when(eventosMapper.toResponse(evento)).thenReturn(eventoResponse);

        EventoPartidoResponse result = partidoService.registrarEvento(ID_PARTIDO, requestSinEquipo);

        assertThat(result).isNotNull();
        verify(clubRepository, never()).findById(any());
        verify(eventosRepository).save(any(EventosPartido.class));
    }

    @Test
    @DisplayName("obtenerEventosDePartido: debe retornar eventos de un partido")
    void testObtenerEventosDePartido() {
        List<EventosPartido> eventos = List.of(evento);
        List<EventoPartidoResponse> responses = List.of(eventoResponse);

        when(eventosRepository.findByPartido(ID_PARTIDO)).thenReturn(eventos);
        when(eventosMapper.toResponse(evento)).thenReturn(eventoResponse);

        List<EventoPartidoResponse> result = partidoService.obtenerEventosDePartido(ID_PARTIDO);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).tipoEvento()).isEqualTo(TipoEvento.GOL);

        verify(eventosRepository).findByPartido(ID_PARTIDO);
        verify(eventosMapper).toResponse(evento);
    }

    @Test
    @DisplayName("obtenerEventosDePartido: debe retornar lista vacía cuando no hay eventos")
    void testObtenerEventosDePartido_Vacio() {
        when(eventosRepository.findByPartido(ID_PARTIDO)).thenReturn(List.of());

        List<EventoPartidoResponse> result = partidoService.obtenerEventosDePartido(ID_PARTIDO);

        assertThat(result).isEmpty();
        verify(eventosRepository).findByPartido(ID_PARTIDO);
        verify(eventosMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("cancelarPartido: debe cancelar un partido correctamente")
    void testCancelarPartido() {
        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(partido)).thenReturn(partido);

        partidoService.cancelarPartido(ID_PARTIDO);

        assertThat(partido.getEstado()).isEqualTo(EstadoPartido.CANCELADO);

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(partidoRepository).save(partido);
    }

    @Test
    @DisplayName("cancelarPartido: debe lanzar excepción cuando el partido no existe")
    void testCancelarPartido_NoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(partidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidoService.cancelarPartido(idInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Partido no encontrado con id: " + idInexistente);

        verify(partidoRepository).findById(idInexistente);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelarPartido: debe lanzar excepción cuando el partido ya finalizó")
    void testCancelarPartido_YaFinalizado() {
        when(partidoRepository.findById(partidoFinalizado.getIdPartido())).thenReturn(Optional.of(partidoFinalizado));

        assertThatThrownBy(() -> partidoService.cancelarPartido(partidoFinalizado.getIdPartido()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se puede cancelar un partido ya finalizado");

        verify(partidoRepository).findById(partidoFinalizado.getIdPartido());
        verify(partidoRepository, never()).save(any());
    }

   @Test
@DisplayName("programarPartido: debe agregar el partido a la competición y al árbitro")
void testProgramarPartido_AgregaRelaciones() {
    when(clubRepository.findById(ID_CLUB_LOCAL)).thenReturn(Optional.of(clubLocal));
    when(clubRepository.findById(ID_CLUB_VISITANTE)).thenReturn(Optional.of(clubVisitante));
    when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
    when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));
    when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));
    
    // Capturar el partido que se guarda
    when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> {
        Partido partidoGuardado = invocation.getArgument(0);
        // Verificar que la competición y el árbitro tienen el partido
        assertThat(competicion.getPartidos()).contains(partidoGuardado);
        assertThat(arbitro.getPartidosArbitrados()).contains(partidoGuardado);
        return partidoGuardado;
    });
    
    when(partidoMapper.toResponse(any(Partido.class))).thenReturn(partidoResponse);

    partidoService.programarPartido(crearRequest);

    verify(competicionRepository).findById(ID_COMPETICION);
    verify(arbitroRepository).findById(ID_ARBITRO);
    verify(partidoRepository).save(any(Partido.class));
}

    @Test
    @DisplayName("obtenerPartidoPorId: debe retornar información completa del partido")
    void testObtenerPartidoPorId_InformacionCompleta() {
        PartidoResponse responseCompleta = PartidoResponse.builder()
                .idPartido(ID_PARTIDO)
                .fechaYHora(LocalDateTime.now().plusDays(7))
                .jornada(10)
                .estado(EstadoPartido.PROGRAMADO)
                .estadoDisplayName("Programado")
                .idEquipoLocal(ID_CLUB_LOCAL)
                .nombreEquipoLocal("FC Barcelona")
                .idEquipoVisitante(ID_CLUB_VISITANTE)
                .nombreEquipoVisitante("Real Madrid")
                .golesLocal(0)
                .golesVisitante(0)
                .resultado("0-0")
                .nombreArbitro("Juan Martínez")
                .nombreEstadio("Camp Nou")
                .nombreCompeticion("La Liga")
                .idCompeticion(ID_COMPETICION)
                .enCurso(false)
                .finalizado(false)
                .esFuturo(true)
                .esHoy(false)
                .build();

        when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
        when(partidoMapper.toResponse(partido)).thenReturn(responseCompleta);

        PartidoResponse result = partidoService.obtenerPartidoPorId(ID_PARTIDO);

        assertThat(result.idPartido()).isEqualTo(ID_PARTIDO);
        assertThat(result.nombreEquipoLocal()).isEqualTo("FC Barcelona");
        assertThat(result.nombreEquipoVisitante()).isEqualTo("Real Madrid");
        assertThat(result.nombreArbitro()).isEqualTo("Juan Martínez");
        assertThat(result.nombreEstadio()).isEqualTo("Camp Nou");
        assertThat(result.nombreCompeticion()).isEqualTo("La Liga");
        assertThat(result.idCompeticion()).isEqualTo(ID_COMPETICION);
        assertThat(result.resultado()).isEqualTo("0-0");
        assertThat(result.estadoDisplayName()).isEqualTo("Programado");
        assertThat(result.esFuturo()).isTrue();
        assertThat(result.finalizado()).isFalse();

        verify(partidoRepository).findById(ID_PARTIDO);
        verify(partidoMapper).toResponse(partido);
    }
}