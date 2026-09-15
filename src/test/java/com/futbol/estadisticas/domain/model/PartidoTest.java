package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Partido")
class PartidoTest {

    @Mock private Equipo equipoLocal;
    @Mock private Equipo equipoVisitante;
    @Mock private Estadio estadio;
    @Mock private Arbitro arbitro;
    @Mock private Competicion competicion;
    @Mock private Jugador jugador;
    @Mock private Tecnico tecnico;

    private static final UUID ID_PARTIDO = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ID_EQUIPO_LOCAL = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_EQUIPO_VISITANTE = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private static LocalTime minuto(int m) {
        return LocalTime.of(m / 60, m % 60, 0);
    }

    private Partido.PartidoBuilder base() {
        return Partido.builder()
                .idPartido(ID_PARTIDO)
                .equipoLocal(equipoLocal)
                .equipoVisitante(equipoVisitante)
                .golesLocal(0)
                .golesVisitante(0);
    }

    @BeforeEach
    void setUp() {
        lenient().when(equipoLocal.getIdEquipo()).thenReturn(ID_EQUIPO_LOCAL);
        lenient().when(equipoLocal.getNombre()).thenReturn("Arsenal");
        lenient().when(equipoLocal.getNombreCorto()).thenReturn("ARS");

        lenient().when(equipoVisitante.getIdEquipo()).thenReturn(ID_EQUIPO_VISITANTE);
        lenient().when(equipoVisitante.getNombre()).thenReturn("Chelsea");
        lenient().when(equipoVisitante.getNombreCorto()).thenReturn("CHE");

        lenient().when(jugador.getNombreCompleto()).thenReturn("Bukayo Saka");
    }

    private EventosPartido golDe(Equipo equipo) {
        return EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .tipoEvento(TipoEvento.GOL)
                .equipoFavorecido(equipo)
                .minuto(minuto(20))
                .build();
    }

    @Nested
    @DisplayName("iniciarPartido")
    class IniciarPartido {

        @Test
        @DisplayName("lanza si el estado no es PROGRAMADO")
        void estadoNoProgramado() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            assertThatThrownBy(p::iniciarPartido)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ya ha sido iniciado");
        }

        @Test
        @DisplayName("lanza si falta algún equipo")
        void faltaEquipo() {
            Partido p = Partido.builder()
                    .idPartido(ID_PARTIDO)
                    .estado(EstadoPartido.PROGRAMADO)
                    .equipoLocal(equipoLocal)
                    .build();

            assertThatThrownBy(p::iniciarPartido)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("equipo local y visitante");
        }

        @Test
        @DisplayName("lanza si el local no tiene 11 titulares")
        void localSinOnce() {
            when(equipoLocal.getJugadoresTitulares()).thenReturn(List.of(mock(Jugador.class)));
            when(equipoVisitante.getJugadoresTitulares()).thenReturn(onceTitulares());

            Partido p = base().estado(EstadoPartido.PROGRAMADO).build();

            assertThatThrownBy(p::iniciarPartido)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("equipo local")
                    .hasMessageContaining("11 titulares");
        }

        @Test
        @DisplayName("lanza si el visitante no tiene 11 titulares")
        void visitanteSinOnce() {
            when(equipoLocal.getJugadoresTitulares()).thenReturn(onceTitulares());
            when(equipoVisitante.getJugadoresTitulares()).thenReturn(List.of(mock(Jugador.class)));

            Partido p = base().estado(EstadoPartido.PROGRAMADO).build();

            assertThatThrownBy(p::iniciarPartido)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("equipo visitante");
        }

        @Test
        @DisplayName("inicia el partido, cambia a PRIMER_TIEMPO y registra eventos")
        void iniciaCorrectamente() {
            when(equipoLocal.getJugadoresTitulares()).thenReturn(onceTitulares());
            when(equipoVisitante.getJugadoresTitulares()).thenReturn(onceTitulares());
            when(equipoLocal.getJugadoresDisponibles()).thenReturn(List.of());
            when(equipoVisitante.getJugadoresDisponibles()).thenReturn(List.of());
            when(equipoLocal.getTecnicoActual()).thenReturn(null);
            when(equipoVisitante.getTecnicoActual()).thenReturn(null);

            Partido p = base().estado(EstadoPartido.PROGRAMADO).build();
            p.iniciarPartido();

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.PRIMER_TIEMPO);
            assertThat(p.getEventos()).isNotEmpty();
            assertThat(p.getEventos().stream()
                    .filter(e -> e.getTipoEvento() == TipoEvento.INICIO_PARTIDO))
                    .hasSize(1);
            assertThat(p.getEventos().stream()
                    .filter(e -> e.getTipoEvento() == TipoEvento.TITULAR))
                    .hasSize(22);
        }

        private List<Jugador> onceTitulares() {
            List<Jugador> titulares = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                DatosDeportivos dd = DatosDeportivos.builder()
                        .idHistorialDeportivo(UUID.randomUUID())
                        .posiciones(new ArrayDeque<>(List.of(PosicionJugador.PORTERO)))
                        .estadoJugador(EstadoJugador.TITULAR)
                        .build();

                Jugador j = Jugador.builder()
                        .idPersonal(UUID.randomUUID())
                        .nombre("Jugador " + i)
                        .apellido("Apellido")
                        .datosDeportivos(dd)
                        .build();

                titulares.add(j);
            }
            return titulares;
        }
    }

    @Nested
    @DisplayName("reanudarPartido")
    class ReanudarPartido {

        @Test
        @DisplayName("lanza si FINALIZADO o CANCELADO")
        void finalizadoOCancelado() {
            Partido p1 = base().estado(EstadoPartido.FINALIZADO).build();
            assertThatThrownBy(p1::reanudarPartido)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizado");

            Partido p2 = base().estado(EstadoPartido.CANCELADO).build();
            assertThatThrownBy(p2::reanudarPartido)
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("lanza si PROGRAMADO")
        void programado() {
            Partido p = base().estado(EstadoPartido.PROGRAMADO).build();

            assertThatThrownBy(p::reanudarPartido)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("programado");
        }

        @Test
        @DisplayName("PRIMER_TIEMPO → AGREGADO_PRIMER_TIEMPO sin evento extra")
        void primerTiempoAAgregado() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            p.reanudarPartido();

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.AGREGADO_PRIMER_TIEMPO);
        }

        @Test
        @DisplayName("ENTRETIEMPO → SEGUNDO_TIEMPO con evento INICIO_SEGUNDO")
        void entretiempoASegundo() {
            Partido p = base().estado(EstadoPartido.ENTRETIEMPO).build();
            p.reanudarPartido();

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.SEGUNDO_TIEMPO);
            assertThat(p.getEventos())
                    .anyMatch(e -> e.getTipoEvento() == TipoEvento.INICIO_SEGUNDO);
        }

        @Test
        @DisplayName("ESPERANDO_PRORROGA → PRIMER_TIEMPO_PRORROGA con evento")
        void esperandoProrroga() {
            Partido p = base().estado(EstadoPartido.ESPERANDO_PRORROGA).build();
            p.reanudarPartido();

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.PRIMER_TIEMPO_PRORROGA);
            assertThat(p.getEventos())
                    .anyMatch(e -> e.getTipoEvento() == TipoEvento.INICIO_PRIMERO_EXTRA);
        }

        @Test
        @DisplayName("ENTRETIEMPO_PRORROGA → SEGUNDO_TIEMPO_PRORROGA con evento")
        void entretiempoProrroga() {
            Partido p = base().estado(EstadoPartido.ENTRETIEMPO_PRORROGA).build();
            p.reanudarPartido();

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.SEGUNDO_TIEMPO_PRORROGA);
            assertThat(p.getEventos())
                    .anyMatch(e -> e.getTipoEvento() == TipoEvento.INICIO_SEGUNDO_EXTRA);
        }

        @Test
        @DisplayName("ESPERANDO_PENALTIS → PENALTIS con evento")
        void esperandoPenaltis() {
            Partido p = base().estado(EstadoPartido.ESPERANDO_PENALTIS).build();
            p.reanudarPartido();

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.PENALTIS);
            assertThat(p.getEventos())
                    .anyMatch(e -> e.getTipoEvento() == TipoEvento.INICIO_PENALTIS);
        }
    }

    @Nested
    @DisplayName("finalizarPartido")
    class FinalizarPartido {

        @Test
        @DisplayName("lanza si minuto es null")
        void minutoNull() {
            Partido p = base().estado(EstadoPartido.SEGUNDO_TIEMPO).build();

            assertThatThrownBy(() -> p.finalizarPartido(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("lanza si ya está finalizado, cancelado o suspendido")
        void yaFinalizado() {
            Partido p = base().estado(EstadoPartido.FINALIZADO).build();

            assertThatThrownBy(() -> p.finalizarPartido(minuto(90)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ya ha finalizado");
        }

        @Test
        @DisplayName("lanza si el estado no es Finalizable()")
        void estadoNoFinalizable() {
            Partido p = base().estado(EstadoPartido.ENTRETIEMPO).build();

            assertThatThrownBy(() -> p.finalizarPartido(minuto(45)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no se puede finalizar");
        }

        @Test
        @DisplayName("SEGUNDO_TIEMPO debe finalizar exactamente en 90")
        void segundoTiempoExacto() {
            Partido p = base().estado(EstadoPartido.SEGUNDO_TIEMPO).build();

            assertThatThrownBy(() -> p.finalizarPartido(minuto(89)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("exactamente en el minuto 90");
        }

        @Test
        @DisplayName("AGREGADO_SEGUNDO_TIEMPO debe ser >= 90")
        void agregadoSegundoTiempo() {
            Partido p = base().estado(EstadoPartido.AGREGADO_SEGUNDO_TIEMPO).build();

            assertThatThrownBy(() -> p.finalizarPartido(minuto(89)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("antes del minuto 90");
        }

        @Test
        @DisplayName("finaliza correctamente y añade evento FIN_PARTIDO")
        void finalizaCorrectamente() {
            Partido p = base().estado(EstadoPartido.SEGUNDO_TIEMPO).build();
            p.finalizarPartido(minuto(90));

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
            assertThat(p.getEventos())
                    .anyMatch(e -> e.getTipoEvento() == TipoEvento.FIN_PARTIDO);
        }
    }

    @Nested
    @DisplayName("agregarEvento")
    class AgregarEvento {

        @Test
        @DisplayName("lanza si el evento es null")
        void eventoNull() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            assertThatThrownBy(() -> p.agregarEvento(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("lanza si el partido está finalizado")
        void partidoFinalizado() {
            Partido p = base().estado(EstadoPartido.FINALIZADO).build();
            EventosPartido e = golDe(equipoLocal);

            assertThatThrownBy(() -> p.agregarEvento(e))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("finalizado");
        }

        @Test
        @DisplayName("en PENALTIS solo se permiten tarjetas y penaltis")
        void restriccionPenaltis() {
            Partido p = base().estado(EstadoPartido.PENALTIS).build();
            EventosPartido e = EventosPartido.builder()
                    .tipoEvento(TipoEvento.CORNER)
                    .build();

            assertThatThrownBy(() -> p.agregarEvento(e))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("solo se permiten tarjetas");
        }

        @Test
        @DisplayName("lanza si intenta agregar evento no-transición en ENTRETIEMPO")
        void eventoEnEntretiempo() {
            Partido p = base().estado(EstadoPartido.ENTRETIEMPO).build();
            EventosPartido e = EventosPartido.builder()
                    .tipoEvento(TipoEvento.GOL)
                    .equipoFavorecido(equipoLocal)
                    .minuto(minuto(45))
                    .build();

            assertThatThrownBy(() -> p.agregarEvento(e))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("lanza si el personal no está en campo")
        void personalNoEnCampo() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            EventosPartido e = EventosPartido.builder()
                    .tipoEvento(TipoEvento.GOL)
                    .personal(jugador)
                    .equipoFavorecido(equipoLocal)
                    .minuto(minuto(20))
                    .build();

            assertThatThrownBy(() -> p.agregarEvento(e))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no está actualmente en el campo");
        }

        @Test
        @DisplayName("actualiza el marcador local con gol")
        void golLocal() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            EventosPartido e = golDe(equipoLocal);

            p.agregarEvento(e);

            assertThat(p.getGolesLocal()).isEqualTo(1);
            assertThat(p.getGolesVisitante()).isZero();
        }

        @Test
        @DisplayName("actualiza el marcador visitante con gol")
        void golVisitante() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            EventosPartido e = golDe(equipoVisitante);

            p.agregarEvento(e);

            assertThat(p.getGolesVisitante()).isEqualTo(1);
            assertThat(p.getGolesLocal()).isZero();
        }

        @Test
        @DisplayName("lanza si el minuto excede el límite del estado")
        void minutoExcedido() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            EventosPartido e = EventosPartido.builder()
                    .tipoEvento(TipoEvento.GOL)
                    .equipoFavorecido(equipoLocal)
                    .minuto(minuto(50))
                    .build();

            assertThatThrownBy(() -> p.agregarEvento(e))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("sin tiempo agregado");
        }

        @Test
        @DisplayName("GOL_ANULADO resta el último gol del equipo")
        void golAnulado() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            p.agregarEvento(golDe(equipoLocal));
            p.agregarEvento(golDe(equipoLocal));
            assertThat(p.getGolesLocal()).isEqualTo(2);

            EventosPartido anulado = EventosPartido.builder()
                    .tipoEvento(TipoEvento.GOL_ANULADO)
                    .equipoFavorecido(equipoLocal)
                    .minuto(minuto(25))
                    .build();
            p.agregarEvento(anulado);

            assertThat(p.getGolesLocal()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("agregarTiempoAgregado")
    class AgregarTiempoAgregado {

        @Test
        @DisplayName("lanza si el partido está finalizado")
        void finalizado() {
            Partido p = base().estado(EstadoPartido.FINALIZADO).build();

            assertThatThrownBy(() -> p.agregarTiempoAgregado(3))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("lanza si los minutos no son positivos")
        void minutosNoPositivos() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            assertThatThrownBy(() -> p.agregarTiempoAgregado(0))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> p.agregarTiempoAgregado(-3))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("PRIMER_TIEMPO → AGREGADO_PRIMER_TIEMPO")
        void primerTiempo() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            p.agregarTiempoAgregado(3);

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.AGREGADO_PRIMER_TIEMPO);
            assertThat(p.getEventos()).anyMatch(e -> e.getTipoEvento() == TipoEvento.AGREGADO);
        }

        @Test
        @DisplayName("SEGUNDO_TIEMPO → AGREGADO_SEGUNDO_TIEMPO")
        void segundoTiempo() {
            Partido p = base().estado(EstadoPartido.SEGUNDO_TIEMPO).build();

            p.agregarTiempoAgregado(5);

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.AGREGADO_SEGUNDO_TIEMPO);
        }

        @Test
        @DisplayName("PRIMER_TIEMPO_PRORROGA → AGREGADO_PRORROGA_PRIMER")
        void primeraProrroga() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO_PRORROGA).build();

            p.agregarTiempoAgregado(2);

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.AGREGADO_PRORROGA_PRIMER);
        }

        @Test
        @DisplayName("SEGUNDO_TIEMPO_PRORROGA → AGREGADO_PRORROGA_SEGUNDO")
        void segundaProrroga() {
            Partido p = base().estado(EstadoPartido.SEGUNDO_TIEMPO_PRORROGA).build();

            p.agregarTiempoAgregado(2);

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.AGREGADO_PRORROGA_SEGUNDO);
        }

        @Test
        @DisplayName("lanza si el estado no admite agregado")
        void estadoNoAdmite() {
            Partido p = base().estado(EstadoPartido.ENTRETIEMPO).build();

            assertThatThrownBy(() -> p.agregarTiempoAgregado(3))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("finalizarTiempo")
    class FinalizarTiempo {

        @Test
        @DisplayName("lanza si el partido ya finalizó")
        void yaFinalizado() {
            Partido p = base().estado(EstadoPartido.FINALIZADO).build();

            assertThatThrownBy(() -> p.finalizarTiempo(minuto(90)))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("lanza si minutoFin es null")
        void minutoNull() {
            Partido p = base().estado(EstadoPartido.SEGUNDO_TIEMPO).build();

            assertThatThrownBy(() -> p.finalizarTiempo(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("PRIMER_TIEMPO antes del 45 lanza")
        void primerTiempoAntes() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            assertThatThrownBy(() -> p.finalizarTiempo(minuto(40)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("antes del minuto 45");
        }

        @Test
        @DisplayName("PRIMER_TIEMPO → ENTRETIEMPO")
        void primerTiempoCorrecto() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            p.finalizarTiempo(minuto(45));

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.ENTRETIEMPO);
            assertThat(p.getEventos())
                    .anyMatch(e -> e.getTipoEvento() == TipoEvento.FIN_PRIMERO);
        }

        @Test
        @DisplayName("SEGUNDO_TIEMPO con empate → ESPERANDO_PRORROGA")
        void segundoTiempoCorrecto() {
            Partido p = base().estado(EstadoPartido.SEGUNDO_TIEMPO).build();

            p.finalizarTiempo(minuto(90));

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.ESPERANDO_PRORROGA);
        }

        @Test
        @DisplayName("SEGUNDO_TIEMPO sin empate finaliza automáticamente")
        void sinEmpateFinaliza() {
            Partido p = base()
                    .estado(EstadoPartido.SEGUNDO_TIEMPO)
                    .golesLocal(2)
                    .golesVisitante(1)
                    .build();

            p.finalizarTiempo(minuto(90));

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        }

        @Test
        @DisplayName("SEGUNDO_TIEMPO con empate no finaliza automáticamente")
        void conEmpateNoFinaliza() {
            Partido p = base()
                    .estado(EstadoPartido.SEGUNDO_TIEMPO)
                    .golesLocal(1)
                    .golesVisitante(1)
                    .build();

            p.finalizarTiempo(minuto(90));

            assertThat(p.getEstado()).isEqualTo(EstadoPartido.ESPERANDO_PRORROGA);
        }
    }

    @Nested
    @DisplayName("estado / curso / finalizado")
    class EstadoPartidoMethods {

        @Test
        @DisplayName("estaEnCurso: true para estados en juego")
        void estaEnCurso() {
            assertThat(base().estado(EstadoPartido.PRIMER_TIEMPO).build().estaEnCurso()).isTrue();
            assertThat(base().estado(EstadoPartido.SEGUNDO_TIEMPO).build().estaEnCurso()).isTrue();
            assertThat(base().estado(EstadoPartido.ENTRETIEMPO).build().estaEnCurso()).isTrue();
            assertThat(base().estado(EstadoPartido.PENALTIS).build().estaEnCurso()).isTrue();
            assertThat(base().estado(EstadoPartido.PROGRAMADO).build().estaEnCurso()).isFalse();
            assertThat(base().estado(EstadoPartido.FINALIZADO).build().estaEnCurso()).isFalse();
        }

        @Test
        @DisplayName("haFinalizado: true para FINALIZADO, CANCELADO, SUSPENDIDO")
        void haFinalizado() {
            assertThat(base().estado(EstadoPartido.FINALIZADO).build().haFinalizado()).isTrue();
            assertThat(base().estado(EstadoPartido.CANCELADO).build().haFinalizado()).isTrue();
            assertThat(base().estado(EstadoPartido.SUSPENDIDO).build().haFinalizado()).isTrue();
            assertThat(base().estado(EstadoPartido.PRIMER_TIEMPO).build().haFinalizado()).isFalse();
        }
    }

    @Nested
    @DisplayName("getResultado")
    class GetResultado {

        @Test
        @DisplayName("devuelve 'En curso' si no ha finalizado")
        void enCurso() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            assertThat(p.getResultado()).isEqualTo("En curso");
        }

        @Test
        @DisplayName("devuelve 'L - V' si ha finalizado")
        void finalizado() {
            Partido p = base()
                    .estado(EstadoPartido.FINALIZADO)
                    .golesLocal(3)
                    .golesVisitante(1)
                    .build();

            assertThat(p.getResultado()).isEqualTo("3 - 1");
        }
    }

    @Nested
    @DisplayName("estaEnCampo")
    class EstaEnCampo {

        @Test
        @DisplayName("false si jugador es null")
        void jugadorNull() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            assertThat(p.estaEnCampo(null)).isFalse();
        }

        @Test
        @DisplayName("false si no hay eventos para el jugador")
        void sinEventos() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            assertThat(p.estaEnCampo(jugador)).isFalse();
        }

        @Test
        @DisplayName("true si el último evento es TITULAR")
        void ultimoTitular() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            p.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.TITULAR)
                    .personal(jugador)
                    .build());

            assertThat(p.estaEnCampo(jugador)).isTrue();
        }

        @Test
        @DisplayName("true si el último evento es SUB_IN")
        void ultimoSubIn() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            p.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.SUB_IN)
                    .personal(jugador)
                    .build());

            assertThat(p.estaEnCampo(jugador)).isTrue();
        }

        @Test
        @DisplayName("false si el último evento es SUB_OUT")
        void ultimoSubOut() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            p.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.TITULAR)
                    .personal(jugador)
                    .build());
            p.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.SUB_OUT)
                    .personal(jugador)
                    .build());

            assertThat(p.estaEnCampo(jugador)).isFalse();
        }
    }

    @Nested
    @DisplayName("hayEmpate")
    class HayEmpate {

        @Test
        @DisplayName("true si el marcador está igualado, sin importar el estado")
        void marcadorIgualado() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO)
                    .golesLocal(1).golesVisitante(1).build();

            assertThat(p.hayEmpate()).isTrue();
        }

        @Test
        @DisplayName("true si finalizado con marcador igualado")
        void empateFinalizado() {
            Partido p = base().estado(EstadoPartido.FINALIZADO)
                    .golesLocal(2).golesVisitante(2).build();

            assertThat(p.hayEmpate()).isTrue();
        }

        @Test
        @DisplayName("false si el marcador es distinto")
        void sinEmpate() {
            Partido p = base().estado(EstadoPartido.FINALIZADO)
                    .golesLocal(3).golesVisitante(1).build();

            assertThat(p.hayEmpate()).isFalse();
        }
    }

    @Nested
    @DisplayName("esFuturo / esHoy")
    class FechaMethods {

        @Test
        @DisplayName("esFuturo: true si fechaYHora está en el futuro")
        void esFuturo() {
            Partido p = base().fechaYHora(LocalDateTime.now().plusDays(1)).build();
            assertThat(p.esFuturo()).isTrue();

            Partido p2 = base().fechaYHora(LocalDateTime.now().minusDays(1)).build();
            assertThat(p2.esFuturo()).isFalse();
        }

        @Test
        @DisplayName("esFuturo: false si fechaYHora es null")
        void esFuturoNull() {
            assertThat(base().build().esFuturo()).isFalse();
        }

        @Test
        @DisplayName("esHoy: true si la fecha es hoy")
        void esHoy() {
            Partido p = base().fechaYHora(LocalDateTime.now()).build();
            assertThat(p.esHoy()).isTrue();
        }

        @Test
        @DisplayName("esHoy: false si fechaYHora es null")
        void esHoyNull() {
            assertThat(base().build().esHoy()).isFalse();
        }
    }

    @Nested
    @DisplayName("realizarSustitucion")
    class RealizarSustitucion {

        @Test
        @DisplayName("lanza si algún jugador es null")
        void jugadorNull() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            Jugador otro = mock(Jugador.class);

            assertThatThrownBy(() -> p.realizarSustitucion(null, otro, equipoLocal, minuto(60)))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> p.realizarSustitucion(otro, null, equipoLocal, minuto(60)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("lanza si entrante y saliente son el mismo")
        void mismoJugador() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();

            assertThatThrownBy(() -> p.realizarSustitucion(jugador, jugador, equipoLocal, minuto(60)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("mismo");
        }

        @Test
        @DisplayName("lanza si el equipo es null")
        void equipoNull() {
            Partido p = base().estado(EstadoPartido.PRIMER_TIEMPO).build();
            Jugador entrante = mock(Jugador.class);

            assertThatThrownBy(() -> p.realizarSustitucion(entrante, jugador, null, minuto(60)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("genera dos eventos y actualiza datos deportivos")
        void sustitucionCorrecta() {
            Partido p = base().estado(EstadoPartido.SEGUNDO_TIEMPO).build();

            Jugador saliente = mock(Jugador.class);
            DatosDeportivos ddSaliente = mock(DatosDeportivos.class);
            lenient().when(saliente.getNombreCompleto()).thenReturn("Saliente");
            lenient().when(saliente.getDatosDeportivos()).thenReturn(ddSaliente);
            p.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.TITULAR)
                    .personal(saliente)
                    .build());

            Jugador entrante = mock(Jugador.class);
            DatosDeportivos ddEntrante = mock(DatosDeportivos.class);
            lenient().when(entrante.getNombreCompleto()).thenReturn("Entrante");
            lenient().when(entrante.getDatosDeportivos()).thenReturn(ddEntrante);
            p.getEventos().add(EventosPartido.builder()
                    .tipoEvento(TipoEvento.CONVOCADO)
                    .personal(entrante)
                    .build());

            List<EventosPartido> eventos = p.realizarSustitucion(
                    entrante, saliente, equipoLocal, minuto(60));

            assertThat(eventos).hasSize(2);
            assertThat(eventos)
                    .extracting(EventosPartido::getTipoEvento)
                    .containsExactly(TipoEvento.SUB_OUT, TipoEvento.SUB_IN);
        }
    }

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mismo ID son iguales")
        void mismoId() {
            Partido p1 = base().golesLocal(1).build();
            Partido p2 = base().golesLocal(5).build();

            assertThat(p1).isEqualTo(p2);
            assertThat(p1).hasSameHashCodeAs(p2);
        }

        @Test
        @DisplayName("distinto ID no son iguales")
        void distintoId() {
            Partido p1 = base().idPartido(UUID.randomUUID()).build();
            Partido p2 = base().idPartido(UUID.randomUUID()).build();

            assertThat(p1).isNotEqualTo(p2);
        }
    }
}