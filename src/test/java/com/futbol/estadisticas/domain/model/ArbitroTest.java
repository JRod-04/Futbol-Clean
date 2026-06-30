package com.futbol.estadisticas.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArbitroTest {

    private Arbitro arbitro;
    private static final UUID ID_ARBITRO = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        arbitro = Arbitro.builder()
                .idArbitro(ID_ARBITRO)
                .nombre("Michael")
                .apellido("Oliver")
                .fechaNacimiento(LocalDate.of(1985, 2, 20))
                .build();
    }

    @Test
    @DisplayName("getNombreCompleto: debe retornar nombre y apellido concatenados")
    void testGetNombreCompleto() {
        assertThat(arbitro.getNombreCompleto()).isEqualTo("Michael Oliver");
    }

    @Test
    @DisplayName("getEdad: debe calcular la edad correctamente")
    void testGetEdad() {
        int edad = arbitro.getEdad();
        assertThat(edad).isGreaterThan(0);
    }

    @Test
    @DisplayName("getEdad: debe retornar 0 cuando fechaNacimiento es null")
    void testGetEdad_CuandoFechaNacimientoNull() {
        arbitro.setFechaNacimiento(null);
        assertThat(arbitro.getEdad()).isZero();
    }

    @Test
    @DisplayName("agregarPartido: debe agregar un partido y establecer la relación bidireccional")
    void testAgregarPartido() {
        Partido partido = Partido.builder()
                .idPartido(UUID.randomUUID())
                .estado(EstadoPartido.PROGRAMADO)
                .build();

        arbitro.agregarPartido(partido);

        assertThat(arbitro.getPartidosArbitrados()).hasSize(1);
        assertThat(partido.getArbitro()).isEqualTo(arbitro);
    }

    @Test
    @DisplayName("agregarPartido: no debe agregar partido nulo")
    void testAgregarPartido_Nulo() {
        arbitro.agregarPartido(null);
        assertThat(arbitro.getPartidosArbitrados()).isEmpty();
    }

    @Test
    @DisplayName("getCantidadPartidos: debe retornar la cantidad de partidos arbitrados")
    void testGetCantidadPartidos() {
        Partido partido1 = Partido.builder().idPartido(UUID.randomUUID()).build();
        Partido partido2 = Partido.builder().idPartido(UUID.randomUUID()).build();

        arbitro.agregarPartido(partido1);
        arbitro.agregarPartido(partido2);

        assertThat(arbitro.getCantidadPartidos()).isEqualTo(2);
    }

    @Test
    @DisplayName("getPartidosPorTemporada: debe filtrar partidos por año")
    void testGetPartidosPorTemporada() {
        Partido partido2024 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .fechaYHora(LocalDateTime.of(2024, 5, 20, 16, 0))
                .build();
        
        Partido partido2025 = Partido.builder()
                .idPartido(UUID.randomUUID())
                .fechaYHora(LocalDateTime.of(2025, 3, 15, 16, 0))
                .build();

        arbitro.agregarPartido(partido2024);
        arbitro.agregarPartido(partido2025);

        assertThat(arbitro.getPartidosPorTemporada(2024)).hasSize(1);
        assertThat(arbitro.getPartidosPorTemporada(2025)).hasSize(1);
        assertThat(arbitro.getPartidosPorTemporada(2023)).isEmpty();
    }
}