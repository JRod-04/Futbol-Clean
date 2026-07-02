package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TecnicoTest {

    private Tecnico tecnico;
    private static final UUID ID_TECNICO = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        tecnico = Tecnico.builder()
                .idPersonal(ID_TECNICO)
                .nombre("Mikel")
                .apellido("Arteta")
                .fechaNacimiento(LocalDate.of(1982, 3, 26))
                .nacionalidad(Nacion.ESPAÑA)
                .estiloJuego("Presión alta y posesión")
                .alineacionFavorita("4-3-3")
                .build();
    }

    @Test
    @DisplayName("asignarClub: debe asignar un club al técnico")
    void testAsignarClub() {
        Club club = Club.builder()
                .idEquipo(UUID.randomUUID())
                .nombre("Arsenal FC")
                .build();

        tecnico.asignarClub(club);
        assertThat(tecnico.getClubActualAsignado()).isEqualTo(club);
    }

    @Test
    @DisplayName("asignarClub: debe lanzar excepción cuando el club es nulo")
    void testAsignarClub_Nulo() {
        assertThatThrownBy(() -> tecnico.asignarClub(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El club no puede ser nulo");
    }

    @Test
    @DisplayName("desvincularClub: debe desvincular al técnico del club")
    void testDesvincularClub() {
        Club club = Club.builder()
                .idEquipo(UUID.randomUUID())
                .nombre("Arsenal FC")
                .build();

        tecnico.asignarClub(club);
        tecnico.desvincularClub();
        assertThat(tecnico.getClubActualAsignado()).isNull();
    }

    @Test
    @DisplayName("actualizarEstiloJuego: debe actualizar el estilo de juego")
    void testActualizarEstiloJuego() {
        tecnico.actualizarEstiloJuego("Fútbol posicional");
        assertThat(tecnico.getEstiloJuego()).isEqualTo("Fútbol posicional");
    }

    @Test
    @DisplayName("actualizarEstiloJuego: debe lanzar excepción cuando el estilo es vacío")
    void testActualizarEstiloJuego_Vacio() {
        assertThatThrownBy(() -> tecnico.actualizarEstiloJuego(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estilo de juego no puede ser vacío");

        assertThatThrownBy(() -> tecnico.actualizarEstiloJuego(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El estilo de juego no puede ser vacío");
    }

    @Test
    @DisplayName("actualizarAlineacion: debe actualizar la alineación favorita")
    void testActualizarAlineacion() {
        tecnico.actualizarAlineacion("4-2-3-1");
        assertThat(tecnico.getAlineacionFavorita()).isEqualTo("4-2-3-1");
    }

    @Test
    @DisplayName("actualizarAlineacion: debe lanzar excepción cuando la alineación es vacía")
    void testActualizarAlineacion_Vacia() {
        assertThatThrownBy(() -> tecnico.actualizarAlineacion(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La alineación no puede ser vacía");

        assertThatThrownBy(() -> tecnico.actualizarAlineacion(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La alineación no puede ser vacía");
    }

    @Test
    @DisplayName("getClubActual: debe retornar el club actual si tiene contrato vigente")
    void testGetClubActual() {
        // No hay contrato, debería retornar clubActualAsignado
        Club club = Club.builder()
                .idEquipo(UUID.randomUUID())
                .nombre("Arsenal FC")
                .build();
        tecnico.asignarClub(club);
        assertThat(tecnico.getClubActual()).isEqualTo(club);
    }
}