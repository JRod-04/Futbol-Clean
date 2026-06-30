package com.futbol.estadisticas.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadioTest {

    private Estadio estadio;

    @BeforeEach
    void setUp() {
        estadio = Estadio.builder()
                .idEstadio(UUID.randomUUID())
                .nombre("Emirates Stadium")
                .direccion("Highbury, Londres")
                .capacidad(60704)
                .fechaFundacion(LocalDate.of(2006, 7, 22))
                .build();
    }

    @Test
    @DisplayName("getPorcentajeOcupacion: debe calcular el porcentaje correctamente")
    void testGetPorcentajeOcupacion() {
        assertThat(estadio.getPorcentajeOcupacion(60000)).isEqualTo(98.84027411702688);
        assertThat(estadio.getPorcentajeOcupacion(30000)).isEqualTo(49.42013705851344);
    }

    @Test
    @DisplayName("getPorcentajeOcupacion: debe retornar 0 cuando capacidad es 0")
    void testGetPorcentajeOcupacion_CapacidadCero() {
        estadio.setCapacidad(0);
        assertThat(estadio.getPorcentajeOcupacion(10000)).isZero();
    }

    @Test
    @DisplayName("getPorcentajeOcupacion: debe retornar 0 cuando espectadores es null")
    void testGetPorcentajeOcupacion_EspectadoresNull() {
        assertThat(estadio.getPorcentajeOcupacion(null)).isZero();
    }

    @Test
    @DisplayName("getDescripcionCompleta: debe retornar la descripción formateada")
    void testGetDescripcionCompleta() {
        String descripcion = estadio.getDescripcionCompleta();
        assertThat(descripcion).contains("Emirates Stadium");
        assertThat(descripcion).contains("Capacidad: 60704");
        assertThat(descripcion).contains("Fundado: 2006");
    }

    @Test
    @DisplayName("getDescripcionCompleta: debe manejar valores nulos")
    void testGetDescripcionCompleta_ValoresNulos() {
        estadio.setCapacidad(null);
        estadio.setFechaFundacion(null);

        String descripcion = estadio.getDescripcionCompleta();
        assertThat(descripcion).contains("Emirates Stadium");
        assertThat(descripcion).contains("Capacidad: 0");
        assertThat(descripcion).contains("Fundado: 0");
    }
}