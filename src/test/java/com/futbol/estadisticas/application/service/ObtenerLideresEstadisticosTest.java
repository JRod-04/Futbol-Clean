package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.response.LideresEstadisticos.LideresEstadisticosResponse;
import com.futbol.estadisticas.application.port.dto.response.RankingEstadisticasDTO;
import com.futbol.estadisticas.application.port.mapper.LideresEstadisticosMapper;
import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoCompeticion;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.enums.Temporada;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerLideresEstadisticosTest {

    @Mock private PartidoRepositoryPort partidoRepository;
    @Mock private CompeticionRepositoryPort competicionRepository;
    @Mock private LideresEstadisticosMapper lideresEstadisticosMapper;

    @InjectMocks
    private ClasificacionService clasificacionService;

    private static final UUID ID_COMPETICION = UUID.randomUUID();
    private static final UUID ID_LOCAL = UUID.randomUUID();
    private static final UUID ID_VISITANTE = UUID.randomUUID();
    private static final UUID ID_JUGADOR_1 = UUID.randomUUID();
    private static final UUID ID_JUGADOR_2 = UUID.randomUUID();

    private Competicion competicion;
    private Equipo local;
    private Equipo visitante;
    private Jugador jugador1;
    private Jugador jugador2;

    private static LocalTime minuto(int m) {
        return LocalTime.of(m / 60, m % 60, 0);
    }

    @BeforeEach
    void setUp() {
        competicion = Competicion.builder()
                .idCompeticion(ID_COMPETICION)
                .nombre("Premier League")
                .temporada(Temporada.T2024_25)
                .estado(EstadoCompeticion.EN_CURSO)
                .fechaInicio(LocalDateTime.now().minusMonths(2))
                .fechaFin(LocalDateTime.now().plusMonths(8))
                .build();

        local = Equipo.builder().idEquipo(ID_LOCAL).nombre("Arsenal").nombreCorto("ARS").build();
        visitante = Equipo.builder().idEquipo(ID_VISITANTE).nombre("Chelsea").nombreCorto("CHE").build();

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
                        .estadoJugador(EstadoJugador.TITULAR)
                        .posiciones(new ArrayDeque<>(List.of(PosicionJugador.MEDIOCAMPISTA_OFENSIVO)))
                        .build())
                .build();
    }

    // ──────────────────────────── HELPERS ────────────────────────────

    private EventosPartido evento(Jugador jugador, TipoEvento tipo, LocalTime min, Equipo equipo) {
        return EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .tipoEvento(tipo)
                .minuto(min)
                .personal(jugador)
                .equipoFavorecido(equipo)
                .estadoEvento(EstadoPartido.PRIMER_TIEMPO)
                .build();
    }

    private Partido partidoFinalizado(List<EventosPartido> eventos) {
        Partido p = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(local)
                .equipoVisitante(visitante)
                .estado(EstadoPartido.FINALIZADO)
                .golesLocal(2)
                .golesVisitante(1)
                .fechaYHora(LocalDateTime.now().minusDays(1))
                .build();
        p.setEventos(eventos);
        eventos.forEach(e -> e.setPartido(p));
        return p;
    }

    // ──────────────────────────── TESTS ────────────────────────────

    @Nested
    @DisplayName("obtenerLideresEstadisticos")
    class Lideres {

        @Test
        @DisplayName("lanza si la competición no existe")
        void competicionNoExiste() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Competición no encontrada");
        }

        @Test
        @DisplayName("devuelve response vacío si no hay partidos")
        void sinPartidos() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));
            when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of());

            LideresEstadisticosResponse result =
                    clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10);

            assertThat(result).isNotNull();
            assertThat(result.idCompeticion()).isEqualTo(ID_COMPETICION);
            assertThat(result.nombreCompeticion()).isEqualTo("Premier League");
            assertThat(result.estadisticas()).isEmpty();
        }

        @Test
        @DisplayName("ignora partidos no finalizados")
        void ignoraPartidosNoFinalizados() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));

            Partido enCurso = Partido.builder()
                    .idPartido(UUID.randomUUID())
                    .equipoLocal(local)
                    .equipoVisitante(visitante)
                    .estado(EstadoPartido.SEGUNDO_TIEMPO)
                    .build();
            enCurso.setEventos(List.of(evento(jugador1, TipoEvento.GOL, minuto(20), local)));

            when(partidoRepository.findByCompeticion(ID_COMPETICION))
                    .thenReturn(List.of(enCurso));

            LideresEstadisticosResponse expected = mock(LideresEstadisticosResponse.class);
            when(lideresEstadisticosMapper.toResponse(eq(ID_COMPETICION), eq("Premier League"),
                    anyMap(), anyInt())).thenReturn(expected);

            LideresEstadisticosResponse result =
                    clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10);

            assertThat(result).isSameAs(expected);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<UUID, RankingEstadisticasDTO>> captor =
                    ArgumentCaptor.forClass(Map.class);
            verify(lideresEstadisticosMapper)
                    .toResponse(eq(ID_COMPETICION), eq("Premier League"), captor.capture(), eq(10));

            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("agrega goles por jugador")
        void agregaGoles() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));

            Partido p = partidoFinalizado(List.of(
                    evento(jugador1, TipoEvento.TITULAR, minuto(0), local),
                    evento(jugador1, TipoEvento.GOL, minuto(23), local),
                    evento(jugador1, TipoEvento.GOL, minuto(45), local),
                    evento(jugador1, TipoEvento.FIN_PARTIDO, minuto(90), null)
            ));

            when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of(p));

            LideresEstadisticosResponse expected = mock(LideresEstadisticosResponse.class);
            when(lideresEstadisticosMapper.toResponse(eq(ID_COMPETICION), eq("Premier League"),
                    anyMap(), anyInt())).thenReturn(expected);

            clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<UUID, RankingEstadisticasDTO>> captor =
                    ArgumentCaptor.forClass(Map.class);
            verify(lideresEstadisticosMapper)
                    .toResponse(eq(ID_COMPETICION), eq("Premier League"), captor.capture(), eq(10));

            Map<UUID, RankingEstadisticasDTO> stats = captor.getValue();
            assertThat(stats).containsKey(ID_JUGADOR_1);

            RankingEstadisticasDTO dto = stats.get(ID_JUGADOR_1);
            assertThat(dto.goles()).isEqualTo(2);
            assertThat(dto.minutosJugados()).isEqualTo(90);
        }

        @Test
        @DisplayName("agrega todas las estadísticas de un partido completo")
        void agregaMultiplesTipos() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));

            Partido p = partidoFinalizado(List.of(
                    evento(jugador1, TipoEvento.TITULAR, minuto(0), local),
                    evento(jugador1, TipoEvento.GOL, minuto(23), local),
                    evento(jugador1, TipoEvento.ASISTENCIA, minuto(30), local),
                    evento(jugador1, TipoEvento.AMARILLA, minuto(45), local),
                    evento(jugador1, TipoEvento.PENALTI_ANOTADO, minuto(60), local),
                    evento(jugador1, TipoEvento.PENALTI_FALLADO, minuto(75), local),
                    evento(jugador1, TipoEvento.TIRO_A_PUERTA, minuto(80), local),
                    evento(jugador1, TipoEvento.TIRO_FUERA, minuto(85), local),
                    evento(jugador1, TipoEvento.FIN_PARTIDO, minuto(90), null)
            ));

            when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of(p));

            when(lideresEstadisticosMapper.toResponse(eq(ID_COMPETICION), eq("Premier League"),
                    anyMap(), anyInt())).thenReturn(mock(LideresEstadisticosResponse.class));

            clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<UUID, RankingEstadisticasDTO>> captor =
                    ArgumentCaptor.forClass(Map.class);
            verify(lideresEstadisticosMapper)
                    .toResponse(eq(ID_COMPETICION), eq("Premier League"), captor.capture(), eq(10));

            RankingEstadisticasDTO dto = captor.getValue().get(ID_JUGADOR_1);
            assertThat(dto.goles()).isEqualTo(2);          // GOL + PENALTI_ANOTADO
            assertThat(dto.golesPenal()).isEqualTo(1);
            assertThat(dto.penalesAnotados()).isEqualTo(1);
            assertThat(dto.penalesFallados()).isEqualTo(1);
            assertThat(dto.asistencias()).isEqualTo(1);
            assertThat(dto.tarjetasAmarillas()).isEqualTo(1);
            assertThat(dto.tirosAPuerta()).isEqualTo(1);
            assertThat(dto.tirosFuera()).isEqualTo(1);
            assertThat(dto.minutosJugados()).isEqualTo(90);
        }

        @Test
        @DisplayName("calcula minutos desde SUB_IN hasta SUB_OUT")
        void minutosConSustitucion() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));

            Partido p = partidoFinalizado(List.of(
                    evento(jugador1, TipoEvento.SUB_IN, minuto(60), local),
                    evento(jugador1, TipoEvento.SUB_OUT, minuto(85), local),
                    evento(jugador1, TipoEvento.FIN_PARTIDO, minuto(90), null)
            ));

            when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of(p));
            when(lideresEstadisticosMapper.toResponse(eq(ID_COMPETICION), eq("Premier League"),
                    anyMap(), anyInt())).thenReturn(mock(LideresEstadisticosResponse.class));

            clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<UUID, RankingEstadisticasDTO>> captor =
                    ArgumentCaptor.forClass(Map.class);
            verify(lideresEstadisticosMapper)
                    .toResponse(eq(ID_COMPETICION), eq("Premier League"), captor.capture(), eq(10));

            assertThat(captor.getValue().get(ID_JUGADOR_1).minutosJugados()).isEqualTo(25);
        }

        @Test
        @DisplayName("acumula estadísticas de varios partidos")
        void acumulaVariosPartidos() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));

            Partido p1 = partidoFinalizado(List.of(
                    evento(jugador1, TipoEvento.TITULAR, minuto(0), local),
                    evento(jugador1, TipoEvento.GOL, minuto(20), local),
                    evento(jugador1, TipoEvento.FIN_PARTIDO, minuto(90), null)
            ));
            Partido p2 = partidoFinalizado(List.of(
                    evento(jugador1, TipoEvento.TITULAR, minuto(0), local),
                    evento(jugador1, TipoEvento.GOL, minuto(30), local),
                    evento(jugador1, TipoEvento.GOL, minuto(70), local),
                    evento(jugador1, TipoEvento.FIN_PARTIDO, minuto(90), null)
            ));

            when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of(p1, p2));
            when(lideresEstadisticosMapper.toResponse(eq(ID_COMPETICION), eq("Premier League"),
                    anyMap(), anyInt())).thenReturn(mock(LideresEstadisticosResponse.class));

            clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<UUID, RankingEstadisticasDTO>> captor =
                    ArgumentCaptor.forClass(Map.class);
            verify(lideresEstadisticosMapper)
                    .toResponse(eq(ID_COMPETICION), eq("Premier League"), captor.capture(), eq(10));

            RankingEstadisticasDTO dto = captor.getValue().get(ID_JUGADOR_1);
            assertThat(dto.goles()).isEqualTo(3);
            assertThat(dto.minutosJugados()).isEqualTo(180);
        }

        @Test
        @DisplayName("agrupa varios jugadores en el mismo partido")
        void agrupaVariosJugadores() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));

            Partido p = partidoFinalizado(List.of(
                    evento(jugador1, TipoEvento.TITULAR, minuto(0), local),
                    evento(jugador1, TipoEvento.GOL, minuto(23), local),
                    evento(jugador1, TipoEvento.FIN_PARTIDO, minuto(90), null),
                    evento(jugador2, TipoEvento.TITULAR, minuto(0), visitante),
                    evento(jugador2, TipoEvento.ASISTENCIA, minuto(45), visitante),
                    evento(jugador2, TipoEvento.FIN_PARTIDO, minuto(90), null)
            ));

            when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of(p));
            when(lideresEstadisticosMapper.toResponse(eq(ID_COMPETICION), eq("Premier League"),
                    anyMap(), anyInt())).thenReturn(mock(LideresEstadisticosResponse.class));

            clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<UUID, RankingEstadisticasDTO>> captor =
                    ArgumentCaptor.forClass(Map.class);
            verify(lideresEstadisticosMapper)
                    .toResponse(eq(ID_COMPETICION), eq("Premier League"), captor.capture(), eq(10));

            Map<UUID, RankingEstadisticasDTO> stats = captor.getValue();
            assertThat(stats).containsKeys(ID_JUGADOR_1, ID_JUGADOR_2);
            assertThat(stats.get(ID_JUGADOR_1).goles()).isEqualTo(1);
            assertThat(stats.get(ID_JUGADOR_2).asistencias()).isEqualTo(1);
        }

        @Test
        @DisplayName("ignora eventos sin personal o con personal no Jugador")
        void ignoraEventosSinPersonal() {
            when(competicionRepository.findById(ID_COMPETICION)).thenReturn(Optional.of(competicion));

            EventosPartido sinPersonal = EventosPartido.builder()
                    .idEvento(UUID.randomUUID())
                    .tipoEvento(TipoEvento.GOL)
                    .minuto(minuto(20))
                    .equipoFavorecido(local)
                    .build();

            Partido p = partidoFinalizado(List.of(sinPersonal));

            when(partidoRepository.findByCompeticion(ID_COMPETICION)).thenReturn(List.of(p));
            when(lideresEstadisticosMapper.toResponse(eq(ID_COMPETICION), eq("Premier League"),
                    anyMap(), anyInt())).thenReturn(mock(LideresEstadisticosResponse.class));

            clasificacionService.obtenerLideresEstadisticos(ID_COMPETICION, 10);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<UUID, RankingEstadisticasDTO>> captor =
                    ArgumentCaptor.forClass(Map.class);
            verify(lideresEstadisticosMapper)
                    .toResponse(eq(ID_COMPETICION), eq("Premier League"), captor.capture(), eq(10));

            assertThat(captor.getValue()).isEmpty();
        }
    }
}