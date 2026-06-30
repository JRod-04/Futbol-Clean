package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CompeticionTest {

    private Competicion competicion;

    @BeforeEach
    void setUp() {
        competicion = Competicion.builder()
                .idCompeticion(UUID.randomUUID())
                .nombre("Premier League")
                .fechaInicio(LocalDateTime.of(2025, 8, 16, 0, 0))
                .fechaFin(LocalDateTime.of(2027, 5, 25, 23, 59))
                .build();
    }

    @Test
    @DisplayName("agregarPartido: debe agregar un partido y establecer relación bidireccional")
    void testAgregarPartido() {
        Partido partido = Partido.builder()
                .idPartido(UUID.randomUUID())
                .build();

        competicion.agregarPartido(partido);

        assertThat(competicion.getPartidos()).hasSize(1);
        assertThat(partido.getCompeticion()).isEqualTo(competicion);
    }

    @Test
    @DisplayName("estaActiva: debe retornar true cuando la competición está activa")
    void testEstaActiva() {
        assertThat(competicion.estaActiva()).isTrue();
    }

    @Test
    @DisplayName("estaActiva: debe retornar false cuando no ha comenzado")
    void testEstaActiva_NoHaComenzado() {
        competicion.setFechaInicio(LocalDateTime.now().plusMonths(1));
        competicion.setFechaFin(LocalDateTime.now().plusMonths(2));
        assertThat(competicion.estaActiva()).isFalse();
    }

    @Test
    @DisplayName("haFinalizado: debe retornar true cuando la competición ha finalizado")
    void testHaFinalizado() {
        competicion.setFechaFin(LocalDateTime.now().minusDays(1));
        assertThat(competicion.haFinalizado()).isTrue();
    }

    @Test
    @DisplayName("noHaComenzado: debe retornar true cuando no ha comenzado")
    void testNoHaComenzado() {
        competicion.setFechaInicio(LocalDateTime.now().plusDays(1));
        assertThat(competicion.noHaComenzado()).isTrue();
    }

    @Test
    @DisplayName("getPartidosJugados: debe retornar solo partidos finalizados")
    void testGetPartidosJugados() {
        Partido partido1 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .estado(EstadoPartido.FINALIZADO)
                .build();

        Partido partido2 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .estado(EstadoPartido.PROGRAMADO)
                .build();

        competicion.agregarPartido(partido1);
        competicion.agregarPartido(partido2);

        assertThat(competicion.getPartidosJugados()).hasSize(1);
        assertThat(competicion.getPartidosJugados().get(0).getIdPartido())
                .isEqualTo(partido1.getIdPartido());
    }

    @Test
    @DisplayName("getPorcentajePartidosJugados: debe calcular el porcentaje correctamente")
    void testGetPorcentajePartidosJugados() {
        Partido partido1 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .estado(EstadoPartido.FINALIZADO)
                .build();

        Partido partido2 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .estado(EstadoPartido.FINALIZADO)
                .build();

        Partido partido3 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .estado(EstadoPartido.PROGRAMADO)
                .build();

        competicion.agregarPartido(partido1);
        competicion.agregarPartido(partido2);
        competicion.agregarPartido(partido3);

        assertThat(competicion.getPorcentajePartidosJugados()).isEqualTo(66.66666666666666);
    }

    @Test
    @DisplayName("getPorcentajePartidosJugados: debe retornar 0 cuando no hay partidos")
    void testGetPorcentajePartidosJugados_Vacio() {
        assertThat(competicion.getPorcentajePartidosJugados()).isZero();
    }
}