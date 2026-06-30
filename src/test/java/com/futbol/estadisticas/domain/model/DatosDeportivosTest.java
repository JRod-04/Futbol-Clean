package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatosDeportivosTest {

    private DatosDeportivos datosDeportivos;

    @BeforeEach
    void setUp() {
        datosDeportivos = DatosDeportivos.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(85_000_000.0)
                .posicion(PosicionJugador.EXTREMO_DERECHO)
                .fechaActualizacion(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("actualizarEstado: debe cambiar el estado y actualizar la fecha")
    void testActualizarEstado() {
        datosDeportivos.actualizarEstado(EstadoJugador.SUPLENTE);
        assertThat(datosDeportivos.getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);
        assertThat(datosDeportivos.getFechaActualizacion()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("actualizarEstado: debe lanzar excepción cuando el estado es nulo")
    void testActualizarEstado_Nulo() {
        assertThatThrownBy(() -> datosDeportivos.actualizarEstado(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estado no puede ser nulo");
    }

    @Test
    @DisplayName("actualizarValorMercado: debe actualizar el valor")
    void testActualizarValorMercado() {
        datosDeportivos.actualizarValorMercado(100_000_000.0);
        assertThat(datosDeportivos.getValorMercado()).isEqualTo(100_000_000.0);
    }

    @Test
    @DisplayName("actualizarValorMercado: debe lanzar excepción cuando el valor es negativo")
    void testActualizarValorMercado_Negativo() {
        assertThatThrownBy(() -> datosDeportivos.actualizarValorMercado(-1000.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El valor de mercado debe ser positivo");
    }

    @Test
    @DisplayName("esTitular: debe retornar true cuando el estado es TITULAR")
    void testEsTitular() {
        assertThat(datosDeportivos.esTitular()).isTrue();
        datosDeportivos.setEstadoJugador(EstadoJugador.SUPLENTE);
        assertThat(datosDeportivos.esTitular()).isFalse();
    }

    @Test
    @DisplayName("esSuplente: debe retornar true cuando el estado es SUPLENTE")
    void testEsSuplente() {
        datosDeportivos.setEstadoJugador(EstadoJugador.SUPLENTE);
        assertThat(datosDeportivos.esSuplente()).isTrue();
    }

    @Test
    @DisplayName("estaLesionado: debe retornar true cuando el estado es LESIONADO")
    void testEstaLesionado() {
        datosDeportivos.setEstadoJugador(EstadoJugador.LESIONADO);
        assertThat(datosDeportivos.estaLesionado()).isTrue();
    }

    @Test
    @DisplayName("estaDisponible: debe retornar true para TITULAR o SUPLENTE")
    void testEstaDisponible() {
        assertThat(datosDeportivos.estaDisponible()).isTrue();
        datosDeportivos.setEstadoJugador(EstadoJugador.SUPLENTE);
        assertThat(datosDeportivos.estaDisponible()).isTrue();
        datosDeportivos.setEstadoJugador(EstadoJugador.LESIONADO);
        assertThat(datosDeportivos.estaDisponible()).isFalse();
    }

    @Test
    @DisplayName("getValorMercadoEnMillones: debe convertir el valor a millones")
    void testGetValorMercadoEnMillones() {
        assertThat(datosDeportivos.getValorMercadoEnMillones()).isEqualTo(85.0);
    }

    @Test
    @DisplayName("promoverATitular: debe cambiar a TITULAR")
    void testPromoverATitular() {
        datosDeportivos.setEstadoJugador(EstadoJugador.SUPLENTE);
        datosDeportivos.promoverATitular();
        assertThat(datosDeportivos.getEstadoJugador()).isEqualTo(EstadoJugador.TITULAR);
    }

    @Test
    @DisplayName("cambiarASuplente: debe cambiar a SUPLENTE")
    void testCambiarASuplente() {
        datosDeportivos.cambiarASuplente();
        assertThat(datosDeportivos.getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);
    }
}