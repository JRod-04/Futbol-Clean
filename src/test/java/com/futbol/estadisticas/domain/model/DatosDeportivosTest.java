package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DatosDeportivos")
class DatosDeportivosTest {

    private DatosDeportivos datos;

    @BeforeEach
    void setUp() {
        datos = DatosDeportivos.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .build();
    }

    @Nested
    @DisplayName("actualizarEstado")
    class ActualizarEstado {

        @Test
        @DisplayName("lanza excepción si el nuevo estado es nulo")
        void lanzaExcepcionSiEstadoEsNulo() {
            assertThatThrownBy(() -> datos.actualizarEstado(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("un jugador RETIRADO no puede cambiar de estado")
        void retiradoNoPuedeCambiar() {
            datos.setEstadoJugador(EstadoJugador.RETIRADO);

            assertThatThrownBy(() -> datos.actualizarEstado(EstadoJugador.TITULAR))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("retirado no puede cambiar");
        }

        @Test
        @DisplayName("permite mantenerse en RETIRADO")
        void permiteMantenerseRetirado() {
            datos.setEstadoJugador(EstadoJugador.RETIRADO);

            datos.actualizarEstado(EstadoJugador.RETIRADO);

            assertThat(datos.getEstadoJugador()).isEqualTo(EstadoJugador.RETIRADO);
        }

        @Test
        @DisplayName("actualiza el estado y la fecha de actualización")
        void actualizaEstadoYFecha() {
            datos.actualizarEstado(EstadoJugador.SUPLENTE);

            assertThat(datos.getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);
            assertThat(datos.getFechaActualizacion()).isEqualTo(LocalDate.now());
        }
    }

    @Nested
    @DisplayName("actualizarValorMercado")
    class ActualizarValorMercado {

        @Test
        @DisplayName("lanza excepción si el valor es nulo o negativo")
        void lanzaExcepcionSiValorInvalido() {
            assertThatThrownBy(() -> datos.actualizarValorMercado(null))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> datos.actualizarValorMercado(-1.0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("acepta valor cero y lo asigna")
        void aceptaValorCero() {
            datos.actualizarValorMercado(0.0);

            assertThat(datos.getValorMercado()).isZero();
        }

        @Test
        @DisplayName("actualiza el valor de mercado")
        void actualizaValor() {
            datos.actualizarValorMercado(5_000_000.0);

            assertThat(datos.getValorMercado()).isEqualTo(5_000_000.0);
            assertThat(datos.getFechaActualizacion()).isEqualTo(LocalDate.now());
        }
    }

    @Nested
    @DisplayName("posiciones (agregarPosicion / eliminarPosicion / getPosicionActual)")
    class Posiciones {

        @Test
        @DisplayName("agregarPosicion lanza excepción si la posición es nula")
        void agregarPosicionLanzaExcepcionSiNula() {
            assertThatThrownBy(() -> datos.agregarPosicion(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("la posición agregada más recientemente es la posición actual")
        void ultimaPosicionEsLaActual() {
            datos.agregarPosicion(PosicionJugador.LATERAL_DERECHO);
            datos.agregarPosicion(PosicionJugador.DELANTERO_CENTRO);

            assertThat(datos.getPosicionActual()).isEqualTo(PosicionJugador.DELANTERO_CENTRO);
        }

        @Test
        @DisplayName("agregar una posición ya existente la mueve al frente sin duplicarla")
        void agregarPosicionExistenteLaMueveAlFrente() {
            datos.agregarPosicion(PosicionJugador.LATERAL_DERECHO);
            datos.agregarPosicion(PosicionJugador.DELANTERO_CENTRO);
            datos.agregarPosicion(PosicionJugador.LATERAL_DERECHO);

            assertThat(datos.getPosiciones()).containsExactly(
                    PosicionJugador.LATERAL_DERECHO, PosicionJugador.DELANTERO_CENTRO);
        }

        @Test
        @DisplayName("getPosicionActual devuelve null si no hay posiciones")
        void posicionActualNullSinPosiciones() {
            assertThat(datos.getPosicionActual()).isNull();
        }

        @Test
        @DisplayName("eliminarPosicion lanza excepción si la posición es nula")
        void eliminarPosicionLanzaExcepcionSiNula() {
            assertThatThrownBy(() -> datos.eliminarPosicion(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("eliminarPosicion lanza excepción si la posición no está en la lista")
        void eliminarPosicionLanzaExcepcionSiNoExiste() {
            assertThatThrownBy(() -> datos.eliminarPosicion(PosicionJugador.PORTERO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no está en la lista");
        }

        @Test
        @DisplayName("eliminarPosicion quita la posición de la lista")
        void eliminarPosicionQuitaLaPosicion() {
            datos.agregarPosicion(PosicionJugador.LATERAL_DERECHO);
            datos.agregarPosicion(PosicionJugador.DELANTERO_CENTRO);

            datos.eliminarPosicion(PosicionJugador.LATERAL_DERECHO);

            assertThat(datos.getPosiciones()).containsExactly(PosicionJugador.DELANTERO_CENTRO);
        }
    }

    @Nested
    @DisplayName("actualizarDorsal")
    class ActualizarDorsal {

        @Test
        @DisplayName("lanza excepción si el dorsal es nulo")
        void lanzaExcepcionSiEsNulo() {
            assertThatThrownBy(() -> datos.actualizarDorsal(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("lanza excepción si el dorsal no es positivo")
        void lanzaExcepcionSiNoEsPositivo() {
            assertThatThrownBy(() -> datos.actualizarDorsal(0))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> datos.actualizarDorsal(-5))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("asigna el dorsal cuando es válido")
        void asignaDorsalValido() {
            datos.actualizarDorsal(10);

            assertThat(datos.getDorsal()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("consultas de estado (esTitular, esSuplente, estaLesionado, estaDisponible)")
    class ConsultasDeEstado {

        @Test
        @DisplayName("esTitular es true solo en estado TITULAR")
        void esTitular() {
            datos.setEstadoJugador(EstadoJugador.TITULAR);
            assertThat(datos.esTitular()).isTrue();

            datos.setEstadoJugador(EstadoJugador.SUPLENTE);
            assertThat(datos.esTitular()).isFalse();
        }

        @Test
        @DisplayName("esSuplente es true solo en estado SUPLENTE")
        void esSuplente() {
            datos.setEstadoJugador(EstadoJugador.SUPLENTE);
            assertThat(datos.esSuplente()).isTrue();

            datos.setEstadoJugador(EstadoJugador.TITULAR);
            assertThat(datos.esSuplente()).isFalse();
        }

        @Test
        @DisplayName("estaDisponible es true solo para TITULAR o SUPLENTE")
        void estaDisponible() {
            datos.setEstadoJugador(EstadoJugador.TITULAR);
            assertThat(datos.estaDisponible()).isTrue();

            datos.setEstadoJugador(EstadoJugador.SUPLENTE);
            assertThat(datos.estaDisponible()).isTrue();

            datos.setEstadoJugador(EstadoJugador.LESIONADO);
            assertThat(datos.estaDisponible()).isFalse();
        }
    }

    @Nested
    @DisplayName("getValorMercadoEnMillones")
    class GetValorMercadoEnMillones {

        @Test
        @DisplayName("devuelve 0 si no hay valor de mercado")
        void devuelveCeroSinValor() {
            assertThat(datos.getValorMercadoEnMillones()).isZero();
        }

        @Test
        @DisplayName("convierte el valor de mercado a millones")
        void convierteAMillones() {
            datos.setValorMercado(45_000_000.0);

            assertThat(datos.getValorMercadoEnMillones()).isEqualTo(45.0);
        }
    }

    @Nested
    @DisplayName("promoverATitular")
    class PromoverATitular {

        @Test
        @DisplayName("lanza excepción si el jugador está RETIRADO")
        void lanzaExcepcionSiRetirado() {
            datos.setEstadoJugador(EstadoJugador.RETIRADO);

            assertThatThrownBy(() -> datos.promoverATitular())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("retirado no puede ser titular");
        }

        @Test
        @DisplayName("lanza excepción si el jugador está LESIONADO")
        void lanzaExcepcionSiLesionado() {
            Jugador jugador = new Jugador(UUID.randomUUID(), "Nombre", "Apellido",
                    LocalDate.now().minusYears(20), Nacion.ARGENTINA, JuegoPies.DERECHO, 180, 75);
            datos.setJugador(jugador);
            datos.setEstadoJugador(EstadoJugador.LESIONADO);

            assertThatThrownBy(() -> datos.promoverATitular())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no puede ser titular, está lesionado");
        }

        @Test
        @DisplayName("promueve al jugador a TITULAR cuando es válido")
        void promueveATitular() {
            datos.setEstadoJugador(EstadoJugador.SUPLENTE);

            datos.promoverATitular();

            assertThat(datos.getEstadoJugador()).isEqualTo(EstadoJugador.TITULAR);
        }
    }

    @Nested
    @DisplayName("cambiarASuplente")
    class CambiarASuplente {

        @Test
        @DisplayName("lanza excepción si el jugador está RETIRADO")
        void lanzaExcepcionSiRetirado() {
            datos.setEstadoJugador(EstadoJugador.RETIRADO);

            assertThatThrownBy(() -> datos.cambiarASuplente())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("retirado no puede ser suplente");
        }

        @Test
        @DisplayName("cambia al jugador a SUPLENTE cuando es válido")
        void cambiaASuplente() {
            datos.setEstadoJugador(EstadoJugador.TITULAR);

            datos.cambiarASuplente();

            assertThat(datos.getEstadoJugador()).isEqualTo(EstadoJugador.SUPLENTE);
        }
    }
}
