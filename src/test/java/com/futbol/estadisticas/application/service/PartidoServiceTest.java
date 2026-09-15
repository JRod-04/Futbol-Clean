package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.CrearPartidoRequest;
import com.futbol.estadisticas.application.port.dto.request.RealizarSustitucionRequest;
import com.futbol.estadisticas.application.port.dto.request.RegistrarEventoRequest;
import com.futbol.estadisticas.application.port.dto.response.*;
import com.futbol.estadisticas.application.port.mapper.AlineacionMapper;
import com.futbol.estadisticas.application.port.mapper.EventosPartidoMapper;
import com.futbol.estadisticas.application.port.mapper.PartidoMapper;
import com.futbol.estadisticas.application.port.mapper.TandaPenalesMapper;
import com.futbol.estadisticas.application.port.out.*;
import com.futbol.estadisticas.domain.model.*;
import com.futbol.estadisticas.domain.model.enums.*;
import com.futbol.estadisticas.domain.model.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartidoServiceTest {

    @Mock private PartidoRepositoryPort partidoRepository;
    @Mock private EquipoRepositoryPort clubRepository;
    @Mock private CompeticionRepositoryPort competicionRepository;
    @Mock private ArbitroRepositoryPort arbitroRepository;
    @Mock private EstadioRepositoryPort estadioRepository;
    @Mock private JugadorRepositoryPort jugadorRepository;
    @Mock private EventosPartidoRepositoryPort eventosRepository;
    @Mock private PersonalDeportivoRepositoryPort personalRepository;
    @Mock private PartidoMapper partidoMapper;
    @Mock private EventosPartidoMapper eventosMapper;
    @Mock private AlineacionMapper alineacionMapper;
    @Mock private TandaPenalesMapper tandaPenalesMapper;

    @InjectMocks
    private PartidoService partidoService;

    private static final UUID ID_PARTIDO = UUID.randomUUID();
    private static final UUID ID_LOCAL = UUID.randomUUID();
    private static final UUID ID_VISITANTE = UUID.randomUUID();
    private static final UUID ID_COMPETICION = UUID.randomUUID();
    private static final UUID ID_ARBITRO = UUID.randomUUID();
    private static final UUID ID_ESTADIO = UUID.randomUUID();
    private static final UUID ID_JUGADOR_1 = UUID.randomUUID();
    private static final UUID ID_JUGADOR_2 = UUID.randomUUID();

    private Equipo local;
    private Equipo visitante;
    private Competicion competicion;
    private Arbitro arbitro;
    private Estadio estadio;
    private Jugador jugador1;
    private Jugador jugador2;
    private Partido partido;
    private PartidoResponse responsePartido;
    private EventoPartidoResponse responseEvento;

    private static LocalTime minuto(int m) {
        return LocalTime.of(m / 60, m % 60, 0);
    }

    @BeforeEach
    void setUp() {
        local = Equipo.builder().idEquipo(ID_LOCAL).nombre("Arsenal").nombreCorto("ARS").build();
        visitante = Equipo.builder().idEquipo(ID_VISITANTE).nombre("Chelsea").nombreCorto("CHE").build();
        arbitro = Arbitro.builder().idArbitro(ID_ARBITRO).nombre("Michael").apellido("Oliver").build();
        estadio = Estadio.builder().idEstadio(ID_ESTADIO).nombre("Emirates").build();

        competicion = Competicion.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("Premier League")
                .estado(EstadoCompeticion.EN_CURSO)
                .fechaInicio(LocalDateTime.now().minusMonths(2))
                .fechaFin(LocalDateTime.now().plusMonths(2))
                .build();

        jugador1 = Jugador.builder()
                .idPersonal(ID_JUGADOR_1)
                .nombre("Bukayo").apellido("Saka")
                .datosDeportivos(DatosDeportivos.builder()
                        .idHistorialDeportivo(UUID.randomUUID())
                        .dorsal(7)
                        .estadoJugador(EstadoJugador.TITULAR)
                        .posiciones(new ArrayDeque<>(List.of(PosicionJugador.EXTREMO_DERECHO)))
                        .build())
                .build();

        jugador2 = Jugador.builder()
                .idPersonal(ID_JUGADOR_2)
                .nombre("Cole").apellido("Palmer")
                .datosDeportivos(DatosDeportivos.builder()
                        .idHistorialDeportivo(UUID.randomUUID())
                        .dorsal(10)
                        .estadoJugador(EstadoJugador.SUPLENTE)
                        .posiciones(new ArrayDeque<>(List.of(PosicionJugador.MEDIOCAMPISTA_OFENSIVO)))
                        .build())
                .build();

        partido = Partido.builder()
                .idPartido(ID_PARTIDO)
                .equipoLocal(local)
                .equipoVisitante(visitante)
                .competicion(competicion)
                .arbitro(arbitro)
                .estadio(estadio)
                .fechaYHora(LocalDateTime.now().plusDays(3))
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .build();

        responsePartido = PartidoResponse.builder()
                .idPartido(ID_PARTIDO)
                .fechaYHora(partido.getFechaYHora())
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .build();

        responseEvento = EventoPartidoResponse.builder()
                .idEvento(UUID.randomUUID())
                .tipoEvento(TipoEvento.GOL)
                .minuto(minuto(20))
                .build();
    }

    private CrearPartidoRequest requestCrear() {
        return CrearPartidoRequest.builder()
                .idEquipoLocal(ID_LOCAL)
                .idEquipoVisitante(ID_VISITANTE)
                .idCompeticion(ID_COMPETICION)
                .idArbitro(ID_ARBITRO)
                .idEstadio(ID_ESTADIO)
                .fechaYHora(LocalDateTime.now().plusDays(3))
                .jornadaTorneo(JornadaPartido.JORNADA_1)
                .fase(FaseTorneo.LIGA)
                .build();
    }

    // ──────────────────────────── PROGRAMAR ────────────────────────────

    @Nested
    @DisplayName("programarPartido")
    class Programar {

        @Test
        @DisplayName("crea y guarda el partido correctamente")
        void ok() {
            when(clubRepository.findById(ID_LOCAL)).thenReturn(Optional.of(local));
            when(clubRepository.findById(ID_VISITANTE)).thenReturn(Optional.of(visitante));
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
            when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));
            when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));
            when(partidoRepository.save(any(Partido.class))).thenAnswer(inv -> inv.getArgument(0));
            when(partidoMapper.toResponse(any(Partido.class))).thenReturn(responsePartido);

            PartidoResponse result = partidoService.programarPartido(requestCrear());

            assertThat(result).isSameAs(responsePartido);
            verify(partidoRepository).save(any(Partido.class));
        }

        @Test
        @DisplayName("lanza si local y visitante son el mismo equipo")
        void mismoEquipo() {
            CrearPartidoRequest req = CrearPartidoRequest.builder()
                    .idEquipoLocal(ID_LOCAL)
                    .idEquipoVisitante(ID_LOCAL)
                    .idCompeticion(ID_COMPETICION)
                    .idArbitro(ID_ARBITRO)
                    .fechaYHora(LocalDateTime.now())
                    .build();

            when(clubRepository.findById(ID_LOCAL)).thenReturn(Optional.of(local));
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
            when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));

            assertThatThrownBy(() -> partidoService.programarPartido(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sí mismo");
        }

        @Test
        @DisplayName("lanza si la competición ya finalizó")
        void competicionFinalizada() {
            competicion.setEstado(EstadoCompeticion.FINALIZADA);

            when(clubRepository.findById(ID_LOCAL)).thenReturn(Optional.of(local));
            when(clubRepository.findById(ID_VISITANTE)).thenReturn(Optional.of(visitante));
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
            when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));

            assertThatThrownBy(() -> partidoService.programarPartido(requestCrear()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizada");
        }

        @Test
        @DisplayName("lanza si el club no existe")
        void clubNoExiste() {
            when(clubRepository.findById(ID_LOCAL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> partidoService.programarPartido(requestCrear()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Club no encontrado");
        }
    }

    // ──────────────────────────── PROGRAMAR BATCH ────────────────────────────

    @Nested
    @DisplayName("programarPartidosBatch")
    class ProgramarBatch {

        @Test
        @DisplayName("guarda todos los partidos")
        void ok() {
            when(clubRepository.findById(ID_LOCAL)).thenReturn(Optional.of(local));
            when(clubRepository.findById(ID_VISITANTE)).thenReturn(Optional.of(visitante));
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
            when(arbitroRepository.findById(ID_ARBITRO)).thenReturn(Optional.of(arbitro));
            when(estadioRepository.findById(ID_ESTADIO)).thenReturn(Optional.of(estadio));
            when(partidoRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
            when(partidoMapper.toResponse(any(Partido.class))).thenReturn(responsePartido);

            List<PartidoResponse> result =
                    partidoService.programarPartidosBatch(List.of(requestCrear(), requestCrear()));

            assertThat(result).hasSize(2);
        }
    }

    // ──────────────────────────── CONSULTAS ────────────────────────────

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("obtenerPartidosPorFecha: lanza si fecha es null")
        void fechaNull() {
            assertThatThrownBy(() -> partidoService.obtenerPartidosPorFecha(null, 0, 10))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("obtenerPartidosPorFecha: mapea la página")
        void porFecha() {
            Page<Partido> page = new PageImpl<>(List.of(partido));
            when(partidoRepository.findByFecha(eq(LocalDate.now()), any(Pageable.class))).thenReturn(page);
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            Page<PartidoResponse> result =
                    partidoService.obtenerPartidosPorFecha(LocalDate.now(), 0, 10);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("listarTodos: mapea la página")
        void listarTodos() {
            Pageable pageable = PageRequest.of(0, 10);
            when(partidoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(partido)));
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            assertThat(partidoService.listarTodos(pageable).getContent()).hasSize(1);
        }

        @Test
        @DisplayName("obtenerPartidoPorId: retorna response")
        void porId() {
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            assertThat(partidoService.obtenerPartidoPorId(ID_PARTIDO)).isSameAs(responsePartido);
        }

        @Test
        @DisplayName("obtenerPartidoPorId: lanza si no existe")
        void porIdNoExiste() {
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> partidoService.obtenerPartidoPorId(ID_PARTIDO))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("obtenerPartidosPorCompeticion: mapea lista")
        void porCompeticion() {
            when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of(partido));
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            assertThat(partidoService.obtenerPartidosPorCompeticion(ID_COMPETICION)).hasSize(1);
        }

        @Test
        @DisplayName("obtenerPartidosPorEquipo: mapea lista")
        void porEquipo() {
            when(partidoRepository.findByEquipo(ID_LOCAL)).thenReturn(List.of(partido));
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            assertThat(partidoService.obtenerPartidosPorEquipo(ID_LOCAL)).hasSize(1);
        }
    }

    // ──────────────────────────── TANDA PENALES ────────────────────────────

    @Nested
    @DisplayName("obtenerTandaPenales")
    class TandaPenales {

        @Test
        @DisplayName("lanza si el partido no ha finalizado")
        void noFinalizado() {
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));

            assertThatThrownBy(() -> partidoService.obtenerTandaPenales(ID_PARTIDO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no ha finalizado");
        }

        @Test
        @DisplayName("lanza si no terminó en penaltis")
        void noTerminoEnPenaltis() {
            partido.setEstado(EstadoPartido.FINALIZADO);
            partido.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.FIN_PARTIDO)
                    .estadoEvento(EstadoPartido.SEGUNDO_TIEMPO)
                    .build());
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));

            assertThatThrownBy(() -> partidoService.obtenerTandaPenales(ID_PARTIDO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no terminó en tanda de penaltis");
        }

        @Test
        @DisplayName("devuelve la tanda si terminó en penaltis")
        void ok() {
            partido.setEstado(EstadoPartido.FINALIZADO);
            partido.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.FIN_PARTIDO)
                    .estadoEvento(EstadoPartido.PENALTIS)
                    .build());
            TandaPenalesResponse tanda = TandaPenalesResponse.builder().build();
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(tandaPenalesMapper.toResponse(partido)).thenReturn(tanda);

            assertThat(partidoService.obtenerTandaPenales(ID_PARTIDO)).isSameAs(tanda);
        }
    }

    // ──────────────────────────── ALINEACIÓN ────────────────────────────

    @Nested
    @DisplayName("obtenerPartidoConAlineacion")
    class Alineacion {

        @Test
        @DisplayName("devuelve el partido con alineaciones")
        void ok() {
            partido.setAlineacionLocal(com.futbol.estadisticas.domain.model.enums.Alineacion.ALINEACION_433);
            partido.setAlineacionVisitante(com.futbol.estadisticas.domain.model.enums.Alineacion.ALINEACION_4231);

            EventosPartido titularLocal = EventosPartido.builder()
                    .tipoEvento(TipoEvento.TITULAR)
                    .personal(jugador1)
                    .equipoFavorecido(local)
                    .descripcion("RW")
                    .build();

            partido.getEventos().add(titularLocal);

            PartidoConAlineacionResponse response = PartidoConAlineacionResponse.builder().build();

            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(alineacionMapper.toPartidoWithAlineacion(any(), any(), any(), any()))
                    .thenReturn(response);

            assertThat(partidoService.obtenerPartidoConAlineacion(ID_PARTIDO)).isSameAs(response);
        }

        @Test
        @DisplayName("lanza si el partido no existe")
        void noExiste() {
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> partidoService.obtenerPartidoConAlineacion(ID_PARTIDO))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ──────────────────────────── ESTADO / AVANCE ────────────────────────────

    @Nested
    @DisplayName("iniciar / avanzar / cambiar estado")
    class Estado {

        @Test
        @DisplayName("cambiarEstadoPartido: cambia el estado")
        void cambiarEstado() {
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(partidoRepository.save(partido)).thenReturn(partido);
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            partidoService.cambiarEstadoPartido(ID_PARTIDO, EstadoPartido.SUSPENDIDO);

            assertThat(partido.getEstado()).isEqualTo(EstadoPartido.SUSPENDIDO);
        }

        @Test
        @DisplayName("avanzarPartido: delega en reanudarPartido")
        void avanzar() {
            partido.setEstado(EstadoPartido.ENTRETIEMPO);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(partidoRepository.save(partido)).thenReturn(partido);
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            partidoService.avanzarPartido(ID_PARTIDO);

            assertThat(partido.getEstado()).isEqualTo(EstadoPartido.SEGUNDO_TIEMPO);
        }

        @Test
        @DisplayName("agregarTiempoAgregado: agrega y devuelve el evento")
        void agregarTiempo() {
            partido.setEstado(EstadoPartido.PRIMER_TIEMPO);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(partidoRepository.save(partido)).thenReturn(partido);
            when(eventosMapper.toResponse(any(EventosPartido.class))).thenReturn(responseEvento);

            EventoPartidoResponse result =
                    partidoService.agregarTiempoAgregado(ID_PARTIDO, 3, "+3");

            assertThat(result).isSameAs(responseEvento);
        }

        @Test
        @DisplayName("finalizarTiempo: delega en el dominio")
        void finalizarTiempo() {
            partido.setEstado(EstadoPartido.SEGUNDO_TIEMPO);
            partido.setGolesLocal(1);
            partido.setGolesVisitante(0);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(partidoRepository.save(partido)).thenReturn(partido);
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            partidoService.finalizarTiempo(ID_PARTIDO, minuto(90));

            assertThat(partido.getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        }

        @Test
        @DisplayName("finalizarPartido: delega en el dominio")
        void finalizarPartido() {
            partido.setEstado(EstadoPartido.SEGUNDO_TIEMPO);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(partidoRepository.save(partido)).thenReturn(partido);
            when(partidoMapper.toResponse(partido)).thenReturn(responsePartido);

            partidoService.finalizarPartido(ID_PARTIDO, minuto(90));

            assertThat(partido.getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        }
    }

    // ──────────────────────────── EVENTOS ────────────────────────────

    @Nested
    @DisplayName("registrarEvento")
    class RegistrarEvento {

        private RegistrarEventoRequest request() {
            return RegistrarEventoRequest.builder()
                    .tipoEvento(TipoEvento.GOL)
                    .minuto(minuto(20))
                    .descripcion("Gol")
                    .idPersonal(ID_JUGADOR_1)
                    .idEquipoFavorecido(ID_LOCAL)
                    .build();
        }

        @Test
        @DisplayName("registra el evento y guarda")
        void ok() {
            partido.setEstado(EstadoPartido.PRIMER_TIEMPO);
            partido.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.TITULAR)
                    .personal(jugador1)
                    .equipoFavorecido(local)
                    .build());

            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(personalRepository.findById(ID_JUGADOR_1)).thenReturn(Optional.of(jugador1));
            when(clubRepository.findById(ID_LOCAL)).thenReturn(Optional.of(local));
            when(eventosRepository.save(any(EventosPartido.class))).thenAnswer(inv -> inv.getArgument(0));
            when(partidoRepository.save(partido)).thenReturn(partido);
            when(eventosMapper.toResponse(any(EventosPartido.class))).thenReturn(responseEvento);

            EventoPartidoResponse result =
                    partidoService.registrarEvento(ID_PARTIDO, request());

            assertThat(result).isSameAs(responseEvento);
            verify(eventosRepository).save(any(EventosPartido.class));
        }

        @Test
        @DisplayName("lanza si el personal no existe")
        void personalNoExiste() {
            partido.setEstado(EstadoPartido.PRIMER_TIEMPO);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(personalRepository.findById(ID_JUGADOR_1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> partidoService.registrarEvento(ID_PARTIDO, request()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("registrarEventosBatch")
    class RegistrarEventosBatch {

        @Test
        @DisplayName("registra todos y guarda")
        void ok() {
            partido.setEstado(EstadoPartido.PRIMER_TIEMPO);
            RegistrarEventoRequest r = RegistrarEventoRequest.builder()
                    .tipoEvento(TipoEvento.GOL)
                    .minuto(minuto(20))
                    .build();
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(eventosRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
            when(partidoRepository.save(partido)).thenReturn(partido);
            when(eventosMapper.toResponse(any(EventosPartido.class))).thenReturn(responseEvento);

            List<EventoPartidoResponse> result =
                    partidoService.registrarEventosBatch(ID_PARTIDO, List.of(r));

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("obtenerEventosDePartido / eliminarEvento")
    class Eventos {

        @Test
        @DisplayName("obtenerEventosDePartido: mapea lista")
        void obtener() {
            EventosPartido evento = EventosPartido.builder().idEvento(UUID.randomUUID()).build();
            when(eventosRepository.findByPartido(ID_PARTIDO)).thenReturn(List.of(evento));
            when(eventosMapper.toResponse(evento)).thenReturn(responseEvento);

            assertThat(partidoService.obtenerEventosDePartido(ID_PARTIDO)).hasSize(1);
        }

        @Test
        @DisplayName("eliminarEvento: lanza si no existe")
        void noExiste() {
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(eventosRepository.existsById(any())).thenReturn(false);

            assertThatThrownBy(() -> partidoService.eliminarEvento(ID_PARTIDO, UUID.randomUUID()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Evento no encontrado");
        }

        @Test
        @DisplayName("eliminarEvento: lanza si el partido finalizó")
        void partidoFinalizado() {
            partido.setEstado(EstadoPartido.FINALIZADO);
            UUID idEvento = UUID.randomUUID();
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(eventosRepository.existsById(idEvento)).thenReturn(true);

            assertThatThrownBy(() -> partidoService.eliminarEvento(ID_PARTIDO, idEvento))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizado");
        }
    }

    // ──────────────────────────── SUSTITUCIÓN ────────────────────────────

    @Nested
    @DisplayName("realizarSustitucion")
    class Sustitucion {

        @Test
        @DisplayName("lanza si el jugador no existe")
        void jugadorNoExiste() {
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));
            when(jugadorRepository.findById(any())).thenReturn(Optional.empty());

            RealizarSustitucionRequest req = RealizarSustitucionRequest.builder()
                    .idJugadorEntrante(UUID.randomUUID())
                    .idJugadorSaliente(UUID.randomUUID())
                    .idEquipo(ID_LOCAL)
                    .minuto(minuto(60))
                    .build();

            assertThatThrownBy(() -> partidoService.realizarSustitucion(ID_PARTIDO, req))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ──────────────────────────── CANCELAR / ELIMINAR ────────────────────────────

    @Nested
    @DisplayName("cancelarPartido / eliminarPartido")
    class CancelarEliminar {

        @Test
        @DisplayName("cancelarPartido: lanza si ya finalizó")
        void cancelarFinalizado() {
            partido.setEstado(EstadoPartido.FINALIZADO);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));

            assertThatThrownBy(() -> partidoService.cancelarPartido(ID_PARTIDO))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("cancelarPartido: cambia estado y guarda")
        void cancelarOk() {
            partido.setEstado(EstadoPartido.PROGRAMADO);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));

            partidoService.cancelarPartido(ID_PARTIDO);

            assertThat(partido.getEstado()).isEqualTo(EstadoPartido.CANCELADO);
            verify(partidoRepository).save(partido);
        }

        @Test
        @DisplayName("eliminarPartido: lanza si está en curso")
        void eliminarEnCurso() {
            partido.setEstado(EstadoPartido.SEGUNDO_TIEMPO);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));

            assertThatThrownBy(() -> partidoService.eliminarPartido(ID_PARTIDO))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("eliminarPartido: elimina si no está en curso")
        void eliminarOk() {
            partido.setEstado(EstadoPartido.PROGRAMADO);
            when(partidoRepository.findById(ID_PARTIDO)).thenReturn(Optional.of(partido));

            partidoService.eliminarPartido(ID_PARTIDO);

            verify(partidoRepository).deleteById(ID_PARTIDO);
        }
    }
}