package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartidoTest {

    private Partido partido;
    private Club equipoLocal;
    private Club equipoVisitante;
    private Estadio estadio;

    @BeforeEach
    void setUp() {
        equipoLocal = Club.builder()
                .idEquipo(UUID.fromString("44444444-5555-6666-7777-888888888888"))
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .build();

        equipoVisitante = Club.builder()
                .idEquipo(UUID.fromString("55555555-6666-7777-8888-999999999999"))
                .nombre("Manchester City")
                .nombreCorto("MCI")
                .build();

        estadio = Estadio.builder()
                .idEstadio(UUID.randomUUID())
                .nombre("Emirates Stadium")
                .build();

        partido = Partido.builder()
                .idPartido(UUID.randomUUID())
                .equipoLocal(equipoLocal)
                .equipoVisitante(equipoVisitante)
                .estadio(estadio)
                .estado(EstadoPartido.PROGRAMADO)
                .fechaYHora(LocalDateTime.now().plusDays(2))
                .jornada(5)
                .build();
    }

    @Test
    @DisplayName("iniciarPartido: debe cambiar estado y agregar evento de inicio")
    void testIniciarPartido() {
        partido.iniciarPartido();

        assertThat(partido.getEstado()).isEqualTo(EstadoPartido.PRIMER_TIEMPO);
        assertThat(partido.getEventos()).hasSize(1);
        assertThat(partido.getEventos().get(0).getTipoEvento())
                .isEqualTo(TipoEvento.INICIO_PARTIDO);
    }

    @Test
    @DisplayName("iniciarPartido: debe lanzar excepción cuando ya está iniciado")
    void testIniciarPartido_YaIniciado() {
        partido.setEstado(EstadoPartido.PRIMER_TIEMPO);
        assertThatThrownBy(() -> partido.iniciarPartido())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El partido ya ha sido iniciado");
    }

    @Test
    @DisplayName("finalizarPartido: debe cambiar estado y agregar evento de fin")
    void testFinalizarPartido() {
        partido.iniciarPartido();
        partido.finalizarPartido();

        assertThat(partido.getEstado()).isEqualTo(EstadoPartido.FINALIZADO);
        assertThat(partido.getEventos()).hasSize(2);
        assertThat(partido.getEventos().get(1).getTipoEvento())
                .isEqualTo(TipoEvento.FIN_PARTIDO);
    }

    @Test
    @DisplayName("finalizarPartido: debe lanzar excepción cuando ya está finalizado")
    void testFinalizarPartido_YaFinalizado() {
        partido.setEstado(EstadoPartido.FINALIZADO);
        assertThatThrownBy(() -> partido.finalizarPartido())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El partido ya ha finalizado");
    }

    @Test
    @DisplayName("agregarEvento: debe agregar un evento y actualizar el marcador si es gol")
    void testAgregarEvento_Gol() {
        partido.iniciarPartido();

        EventosPartido gol = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .tipoEvento(TipoEvento.GOL)
                .equipoFavorecido(equipoLocal)
                .minuto(LocalTime.of(0, 15))
                .build();

        partido.agregarEvento(gol);

        assertThat(partido.getEventos()).hasSize(2);
        assertThat(partido.getGolesLocal()).isEqualTo(1);
    }

    @Test
    @DisplayName("agregarEvento: debe lanzar excepción cuando el evento es nulo")
    void testAgregarEvento_Nulo() {
        assertThatThrownBy(() -> partido.agregarEvento(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El evento no puede ser nulo");
    }

    @Test
    @DisplayName("agregarEvento: debe lanzar excepción cuando el partido está finalizado")
    void testAgregarEvento_PartidoFinalizado() {
        partido.setEstado(EstadoPartido.FINALIZADO);
        EventosPartido evento = EventosPartido.builder().idEvento(UUID.randomUUID()).build();

        assertThatThrownBy(() -> partido.agregarEvento(evento))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se pueden agregar eventos a un partido finalizado");
    }

    @Test
    @DisplayName("estaEnCurso: debe retornar true para partidos en curso")
    void testEstaEnCurso() {
        partido.setEstado(EstadoPartido.PRIMER_TIEMPO);
        assertThat(partido.estaEnCurso()).isTrue();

        partido.setEstado(EstadoPartido.ENTRETIEMPO);
        assertThat(partido.estaEnCurso()).isTrue();

        partido.setEstado(EstadoPartido.SEGUNDO_TIEMPO);
        assertThat(partido.estaEnCurso()).isTrue();

        partido.setEstado(EstadoPartido.FINALIZADO);
        assertThat(partido.estaEnCurso()).isFalse();
    }

    @Test
    @DisplayName("haFinalizado: debe retornar true para partidos finalizados")
    void testHaFinalizado() {
        partido.setEstado(EstadoPartido.FINALIZADO);
        assertThat(partido.haFinalizado()).isTrue();

        partido.setEstado(EstadoPartido.CANCELADO);
        assertThat(partido.haFinalizado()).isTrue();

        partido.setEstado(EstadoPartido.PROGRAMADO);
        assertThat(partido.haFinalizado()).isFalse();
    }

    @Test
    @DisplayName("getResultado: debe retornar el marcador para partidos finalizados")
    void testGetResultado() {
        partido.setEstado(EstadoPartido.FINALIZADO);
        partido.setGolesLocal(2);
        partido.setGolesVisitante(1);
        assertThat(partido.getResultado()).isEqualTo("2 - 1");

        partido.setEstado(EstadoPartido.PROGRAMADO);
        assertThat(partido.getResultado()).isEqualTo("En curso");
    }

    @Test
    @DisplayName("getGanador: debe retornar el equipo ganador")
    void testGetGanador() {
        partido.setEstado(EstadoPartido.FINALIZADO);
        partido.setGolesLocal(2);
        partido.setGolesVisitante(1);
        assertThat(partido.getGanador()).isEqualTo(equipoLocal);

        partido.setGolesLocal(0);
        partido.setGolesVisitante(1);
        assertThat(partido.getGanador()).isEqualTo(equipoVisitante);

        partido.setGolesLocal(1);
        partido.setGolesVisitante(1);
        assertThat(partido.getGanador()).isNull();
    }

    @Test
    @DisplayName("hayEmpate: debe retornar true cuando hay empate")
    void testHayEmpate() {
        partido.setEstado(EstadoPartido.FINALIZADO);
        partido.setGolesLocal(1);
        partido.setGolesVisitante(1);
        assertThat(partido.hayEmpate()).isTrue();

        partido.setGolesLocal(2);
        partido.setGolesVisitante(1);
        assertThat(partido.hayEmpate()).isFalse();
    }

    @Test
    @DisplayName("esFuturo: debe retornar true cuando la fecha es futura")
    void testEsFuturo() {
        partido.setFechaYHora(LocalDateTime.now().plusDays(1));
        assertThat(partido.esFuturo()).isTrue();

        partido.setFechaYHora(LocalDateTime.now().minusDays(1));
        assertThat(partido.esFuturo()).isFalse();
    }

    @Test
    @DisplayName("esHoy: debe retornar true cuando el partido es hoy")
    void testEsHoy() {
        partido.setFechaYHora(LocalDateTime.now());
        assertThat(partido.esHoy()).isTrue();

        partido.setFechaYHora(LocalDateTime.now().plusDays(1));
        assertThat(partido.esHoy()).isFalse();
    }
}