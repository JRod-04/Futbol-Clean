package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JugadorTest {

    private Jugador jugador;
    private static final UUID ID_JUGADOR = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        jugador = Jugador.builder()
                .idPersonal(ID_JUGADOR)
                .nombre("Bukayo")
                .apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178)
                .peso(70)
                .build();

        DatosDeportivos datos = DatosDeportivos.builder()
                .estadoJugador(EstadoJugador.TITULAR)
                .posicion(PosicionJugador.EXTREMO_DERECHO)
                .valorMercado(85_000_000.0)
                .build();

        jugador.setDatosDeportivos(datos);

        // Agregar contrato vigente
        Club club = Club.builder()
                .idEquipo(UUID.randomUUID())
                .nombre("Arsenal FC")
                .build();

        Contrato contrato = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .club(club)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .estado(EstadoContrato.ACTIVO)
                .build();

        jugador.agregarContrato(contrato);
    }

    @Test
    @DisplayName("getClubActual: debe retornar el club del contrato vigente")
    void testGetClubActual() {
        assertThat(jugador.getClubActual()).isNotNull();
        assertThat(jugador.getClubActual().getNombre()).isEqualTo("Arsenal FC");
    }

    @Test
    @DisplayName("getClubActual: debe retornar null cuando no hay contrato vigente")
    void testGetClubActual_SinContrato() {
        jugador.getContratos().clear();
        assertThat(jugador.getClubActual()).isNull();
    }

    @Test
    @DisplayName("registrarLesion: debe registrar una lesión y cambiar estado a LESIONADO")
    void testRegistrarLesion() {
        Lesion lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Rotura de ligamento")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now())
                .curada(false)
                .build();

        jugador.registrarLesion(lesion);

        assertThat(jugador.getLesiones()).hasSize(1);
        assertThat(jugador.getDatosDeportivos().getEstadoJugador())
                .isEqualTo(EstadoJugador.LESIONADO);
    }

    @Test
    @DisplayName("registrarLesion: debe lanzar excepción cuando la lesión es nula")
    void testRegistrarLesion_Nula() {
        assertThatThrownBy(() -> jugador.registrarLesion(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La lesión no puede ser nula");
    }

    @Test
    @DisplayName("registrarLesion: debe lanzar excepción cuando la lesión ya está curada")
    void testRegistrarLesion_YaCurada() {
        Lesion lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión curada")
                .gravedad(Gravedad.LEVE)
                .fechaInicio(LocalDate.now().minusMonths(1))
                .curada(true)
                .build();

        assertThatThrownBy(() -> jugador.registrarLesion(lesion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No se puede registrar una lesión que ya está curada");
    }

    @Test
    @DisplayName("estaLesionado: debe retornar true cuando tiene lesiones activas")
    void testEstaLesionado() {
        Lesion lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión activa")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(1))
                .curada(false)
                .build();

        jugador.registrarLesion(lesion);
        assertThat(jugador.estaLesionado()).isTrue();
    }

    @Test
    @DisplayName("estaDisponible: debe retornar true cuando no tiene lesiones activas")
    void testEstaDisponible() {
        assertThat(jugador.estaDisponible()).isTrue();

        // Agregar lesión
        Lesion lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Lesión activa")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(1))
                .curada(false)
                .build();

        jugador.registrarLesion(lesion);
        assertThat(jugador.estaDisponible()).isFalse();
    }

    @Test
    @DisplayName("estaDisponible: debe retornar false cuando está suspendido")
    void testEstaDisponible_Suspendido() {
        jugador.getDatosDeportivos().setEstadoJugador(EstadoJugador.SUSPENDIDO);
        assertThat(jugador.estaDisponible()).isFalse();
    }

    @Test
    @DisplayName("estaDisponible: debe retornar false cuando está retirado")
    void testEstaDisponible_Retirado() {
        jugador.getDatosDeportivos().setEstadoJugador(EstadoJugador.RETIRADO);
        assertThat(jugador.estaDisponible()).isFalse();
    }
}