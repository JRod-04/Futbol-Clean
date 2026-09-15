package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.Gravedad;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Jugador")
class JugadorTest {

    private Jugador jugador;

    @BeforeEach
    void setUp() {
        jugador = new Jugador(UUID.randomUUID(), "Lautaro", "Martínez",
                LocalDate.now().minusYears(26), Nacion.ARGENTINA,
                JuegoPies.DERECHO, 174, 72);
    }

    @Test
    @DisplayName("el constructor de conveniencia inicializa tipoPersonal, contratos y lesiones")
    void constructorInicializaCamposBase() {
        assertThat(jugador.getContratos()).isEmpty();
        assertThat(jugador.getEventos()).isEmpty();
        assertThat(jugador.getLesiones()).isEmpty();
        assertThat(jugador.getFechaActualizacion()).isEqualTo(LocalDate.now());
    }

    @Nested
    @DisplayName("getEquipoActual")
    class GetEquipoActual {

        @Test
        @DisplayName("devuelve null si no tiene contrato vigente")
        void devuelveNullSinContrato() {
            assertThat(jugador.getEquipoActual()).isNull();
        }

        @Test
        @DisplayName("devuelve el equipo del contrato vigente")
        void devuelveElEquipoDelContratoVigente() {
            Equipo equipo = Equipo.builder()
                    .idEquipo(UUID.randomUUID())
                    .tipo(TipoEquipo.CLUB_PROFESIONAL)
                    .build();
            Contrato contrato = Contrato.builder()
                    .idContrato(UUID.randomUUID())
                    .tipoContrato(TipoContrato.PROFESIONAL)
                    .equipo(equipo)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            jugador.getContratos().add(contrato);

            assertThat(jugador.getEquipoActual()).isEqualTo(equipo);
        }
    }

    @Nested
    @DisplayName("registrarLesion")
    class RegistrarLesion {

        @Test
        @DisplayName("lanza excepción si la lesión es nula")
        void lanzaExcepcionSiLesionEsNula() {
            assertThatThrownBy(() -> jugador.registrarLesion(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no puede ser nula");
        }

        @Test
        @DisplayName("lanza excepción si la lesión ya está curada")
        void lanzaExcepcionSiLesionYaCurada() {
            Lesion curada = Lesion.builder()
                    .idLesion(UUID.randomUUID())
                    .curada(true)
                    .build();

            assertThatThrownBy(() -> jugador.registrarLesion(curada))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ya está curada");
        }

        @Test
        @DisplayName("agrega la lesión y actualiza el estado del jugador a LESIONADO")
        void agregaLesionYActualizaEstado() {
            DatosDeportivos datos = DatosDeportivos.builder()
                    .idHistorialDeportivo(UUID.randomUUID())
                    .estadoJugador(EstadoJugador.TITULAR)
                    .build();
            jugador.setDatosDeportivos(datos);

            Lesion lesion = Lesion.builder()
                    .idLesion(UUID.randomUUID())
                    .gravedad(Gravedad.MODERADA)
                    .fechaInicio(LocalDate.now().minusDays(1))
                    .build();

            jugador.registrarLesion(lesion);

            assertThat(jugador.getLesiones()).containsExactly(lesion);
            assertThat(datos.getEstadoJugador()).isEqualTo(EstadoJugador.LESIONADO);
            assertThat(lesion.getJugadorLesionado()).isEqualTo(jugador);
        }

        @Test
        @DisplayName("no falla si el jugador no tiene DatosDeportivos todavía")
        void noFallaSinDatosDeportivos() {
            Lesion lesion = Lesion.builder()
                    .idLesion(UUID.randomUUID())
                    .gravedad(Gravedad.LEVE)
                    .fechaInicio(LocalDate.now().minusDays(1))
                    .build();

            jugador.registrarLesion(lesion);

            assertThat(jugador.getLesiones()).containsExactly(lesion);
        }
    }

    @Nested
    @DisplayName("estaLesionado")
    class EstaLesionado {

        @Test
        @DisplayName("es false sin lesiones")
        void falseSinLesiones() {
            assertThat(jugador.estaLesionado()).isFalse();
        }

        @Test
        @DisplayName("es true si tiene al menos una lesión activa")
        void trueConLesionActiva() {
            Lesion activa = Lesion.builder()
                    .idLesion(UUID.randomUUID())
                    .fechaInicio(LocalDate.now().minusDays(2))
                    .fechaFin(LocalDate.now().plusDays(5))
                    .curada(false)
                    .build();
            jugador.getLesiones().add(activa);

            assertThat(jugador.estaLesionado()).isTrue();
        }

        @Test
        @DisplayName("es false si todas las lesiones están curadas")
        void falseSiTodasCuradas() {
            Lesion curada = Lesion.builder()
                    .idLesion(UUID.randomUUID())
                    .fechaInicio(LocalDate.now().minusDays(10))
                    .curada(true)
                    .build();
            jugador.getLesiones().add(curada);

            assertThat(jugador.estaLesionado()).isFalse();
        }
    }

    @Nested
    @DisplayName("estaDisponible")
    class EstaDisponible {

        @Test
        @DisplayName("es false si no tiene DatosDeportivos")
        void falseSinDatosDeportivos() {
            assertThat(jugador.estaDisponible()).isFalse();
        }

        @ParameterizedTest
        @EnumSource(value = EstadoJugador.class, names = {"SUSPENDIDO", "RETIRADO", "APARTADO"})
        @DisplayName("es false para estados SUSPENDIDO, RETIRADO o APARTADO")
        void falseParaEstadosNoDisponibles(EstadoJugador estado) {
            jugador.setDatosDeportivos(DatosDeportivos.builder()
                    .idHistorialDeportivo(UUID.randomUUID())
                    .estadoJugador(estado)
                    .build());

            assertThat(jugador.estaDisponible()).isFalse();
        }

        @Test
        @DisplayName("es true si el estado es válido y no está lesionado")
        void trueSiEstadoValidoYSinLesion() {
            jugador.setDatosDeportivos(DatosDeportivos.builder()
                    .idHistorialDeportivo(UUID.randomUUID())
                    .estadoJugador(EstadoJugador.TITULAR)
                    .build());

            assertThat(jugador.estaDisponible()).isTrue();
        }

        @Test
        @DisplayName("es false si está lesionado aunque el estado sea válido")
        void falseSiEstaLesionado() {
            jugador.setDatosDeportivos(DatosDeportivos.builder()
                    .idHistorialDeportivo(UUID.randomUUID())
                    .estadoJugador(EstadoJugador.TITULAR)
                    .build());
            jugador.getLesiones().add(Lesion.builder()
                    .idLesion(UUID.randomUUID())
                    .fechaInicio(LocalDate.now().minusDays(1))
                    .fechaFin(LocalDate.now().plusDays(5))
                    .build());

            assertThat(jugador.estaDisponible()).isFalse();
        }
    }
}
