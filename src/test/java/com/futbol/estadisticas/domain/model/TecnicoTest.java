package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tecnico")
class TecnicoTest {

    private Tecnico tecnico;
    private Equipo equipo;

    @BeforeEach
    void setUp() {
        tecnico = new Tecnico(UUID.randomUUID(), "Marcelo", "Gallardo",
                LocalDate.now().minusYears(48), Nacion.ARGENTINA,
                "Posesión ofensiva", "4-3-3");

        equipo = Equipo.builder()
                .idEquipo(UUID.randomUUID())
                .nombre("River Plate")
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .build();
    }

    @Nested
    @DisplayName("getClubActual")
    class GetClubActual {

        @Test
        @DisplayName("devuelve el equipo asignado manualmente si no hay contrato vigente")
        void devuelveEquipoAsignadoSinContrato() {
            tecnico.asignarClub(equipo);

            assertThat(tecnico.getClubActual()).isEqualTo(equipo);
        }

        @Test
        @DisplayName("prioriza el equipo del contrato vigente sobre el asignado manualmente")
        void priorizaContratoVigente() {
            Equipo equipoDelContrato = Equipo.builder()
                    .idEquipo(UUID.randomUUID())
                    .tipo(TipoEquipo.CLUB_PROFESIONAL)
                    .build();
            Contrato contrato = Contrato.builder()
                    .idContrato(UUID.randomUUID())
                    .tipoContrato(TipoContrato.PROFESIONAL)
                    .equipo(equipoDelContrato)
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .fechaFin(LocalDateTime.now().plusDays(1))
                    .build();
            tecnico.getContratos().add(contrato);
            tecnico.asignarClub(equipo);

            assertThat(tecnico.getClubActual()).isEqualTo(equipoDelContrato);
        }

        @Test
        @DisplayName("devuelve null si no hay contrato vigente ni club asignado")
        void devuelveNullSinNada() {
            assertThat(tecnico.getClubActual()).isNull();
        }
    }

    @Nested
    @DisplayName("asignarClub / desvincularClub")
    class AsignarYDesvincularClub {

        @Test
        @DisplayName("asignarClub lanza excepción si el club es nulo")
        void asignarClubLanzaExcepcionSiEsNulo() {
            assertThatThrownBy(() -> tecnico.asignarClub(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("asignarClub establece el equipo actual asignado")
        void asignarClubEstableceEquipo() {
            tecnico.asignarClub(equipo);

            assertThat(tecnico.getEquipoActualAsignado()).isEqualTo(equipo);
        }

        @Test
        @DisplayName("desvincularClub limpia el equipo asignado")
        void desvincularClubLimpiaEquipo() {
            tecnico.asignarClub(equipo);

            tecnico.desvincularClub();

            assertThat(tecnico.getEquipoActualAsignado()).isNull();
        }
    }

    @Nested
    @DisplayName("actualizarEstiloJuego")
    class ActualizarEstiloJuego {

        @Test
        @DisplayName("lanza excepción si el nuevo estilo es nulo o vacío")
        void lanzaExcepcionSiEsInvalido() {
            assertThatThrownBy(() -> tecnico.actualizarEstiloJuego(null))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tecnico.actualizarEstiloJuego("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("actualiza el estilo de juego cuando es válido")
        void actualizaEstiloJuego() {
            tecnico.actualizarEstiloJuego("Contragolpe");

            assertThat(tecnico.getEstiloJuego()).isEqualTo("Contragolpe");
        }
    }

    @Nested
    @DisplayName("actualizarAlineacion")
    class ActualizarAlineacion {

        @Test
        @DisplayName("lanza excepción si la nueva alineación es nula o vacía")
        void lanzaExcepcionSiEsInvalida() {
            assertThatThrownBy(() -> tecnico.actualizarAlineacion(null))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tecnico.actualizarAlineacion(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("actualiza la alineación cuando es válida")
        void actualizaAlineacion() {
            tecnico.actualizarAlineacion("4-4-2");

            assertThat(tecnico.getAlineacionFavorita()).isEqualTo("4-4-2");
        }
    }
}
