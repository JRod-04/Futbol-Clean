/*package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventosPartidoTest {

    private EventosPartido evento;

    @BeforeEach
    void setUp() {
        evento = EventosPartido.builder()
                .idEvento(UUID.randomUUID())
                .minuto(LocalTime.of(0, 15))
                .descripcion("Gol de Saka")
                .tipoEvento(TipoEvento.GOL)
                .build();
    }

    @Test
    @DisplayName("getMinutoFormateado: debe formatear el minuto correctamente")
    void testGetMinutoFormateado() {
        assertThat(evento.getMinutoFormateado()).isEqualTo("15'");
    }

    @Test
    @DisplayName("getMinutoFormateado: debe manejar minutos nulos")
    void testGetMinutoFormateado_Nulo() {
        evento.setMinuto(null);
        assertThat(evento.getMinutoFormateado()).isEqualTo("0:00");
    }

    @Test
    @DisplayName("esGol: debe retornar true para eventos de gol")
    void testEsGol() {
        assertThat(evento.esGol()).isTrue();

        evento.setTipoEvento(TipoEvento.AUTOGOL);
        assertThat(evento.esGol()).isTrue();

        evento.setTipoEvento(TipoEvento.PENALTI_ANOTADO);
        assertThat(evento.esGol()).isTrue();

        evento.setTipoEvento(TipoEvento.AMARILLA);
        assertThat(evento.esGol()).isFalse();
    }

    @Test
    @DisplayName("esTarjeta: debe retornar true para tarjetas")
    void testEsTarjeta() {
        evento.setTipoEvento(TipoEvento.AMARILLA);
        assertThat(evento.esTarjeta()).isTrue();

        evento.setTipoEvento(TipoEvento.ROJA);
        assertThat(evento.esTarjeta()).isTrue();

        evento.setTipoEvento(TipoEvento.GOL);
        assertThat(evento.esTarjeta()).isFalse();
    }

    @Test
    @DisplayName("getColorTarjeta: debe retornar el color de la tarjeta")
    void testGetColorTarjeta() {
        evento.setTipoEvento(TipoEvento.AMARILLA);
        assertThat(evento.getColorTarjeta()).isEqualTo("Amarilla");

        evento.setTipoEvento(TipoEvento.ROJA);
        assertThat(evento.getColorTarjeta()).isEqualTo("Roja");

        evento.setTipoEvento(TipoEvento.GOL);
        assertThat(evento.getColorTarjeta()).isNull();
    }

    @Test
    @DisplayName("esPenalti: debe retornar true para eventos de penalti")
    void testEsPenalti() {
        evento.setTipoEvento(TipoEvento.PENALTI_CONCEDIDO);
        assertThat(evento.esPenalti()).isTrue();

        evento.setTipoEvento(TipoEvento.PENALTI_ANOTADO);
        assertThat(evento.esPenalti()).isTrue();

        evento.setTipoEvento(TipoEvento.PENALTI_FALLADO);
        assertThat(evento.esPenalti()).isTrue();

        evento.setTipoEvento(TipoEvento.GOL);
        assertThat(evento.esPenalti()).isFalse();
    }

    @Test
    @DisplayName("getNombreJugador: debe retornar el nombre del jugador")
    void testGetNombreJugador() {
        PersonalDeportivo jugador = PersonalDeportivo.builder()
                .nombre("Bukayo")
                .apellido("Saka")
                .build();
        evento.setPersonal(jugador);
        assertThat(evento.getNombreJugador()).isEqualTo("Bukayo Saka");

        evento.setPersonal(null);
        assertThat(evento.getNombreJugador()).isEqualTo("Desconocido");
    }

    @Test
    @DisplayName("esEstadisticable: debe retornar true para eventos relevantes")
    void testEsEstadisticable() {
        assertThat(evento.esEstadisticable()).isTrue();

        evento.setTipoEvento(TipoEvento.INICIO_PARTIDO);
        assertThat(evento.esEstadisticable()).isFalse();

        evento.setTipoEvento(TipoEvento.FIN_PARTIDO);
        assertThat(evento.esEstadisticable()).isFalse();
    }
}*/