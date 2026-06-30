package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.Gravedad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LesionTest {

    private Lesion lesion;

    @BeforeEach
    void setUp() {
        lesion = Lesion.builder()
                .idLesion(UUID.randomUUID())
                .nombreLesion("Rotura de ligamento cruzado anterior")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.of(2024, 11, 15))
                .fechaFin(LocalDate.of(2025, 4, 15))
                .curada(false)
                .build();
    }

    @Test
    @DisplayName("esActiva: debe retornar true cuando la lesión está activa")
    void testEsActiva() {
        lesion.setFechaInicio(LocalDate.now().minusDays(5));
        lesion.setFechaFin(LocalDate.now().plusDays(5));
        lesion.setCurada(false);
        assertThat(lesion.esActiva()).isTrue();
    }

    @Test
    @DisplayName("esActiva: debe retornar false cuando está curada")
    void testEsActiva_Curada() {
        lesion.setCurada(true);
        assertThat(lesion.esActiva()).isFalse();
    }

    @Test
    @DisplayName("esActiva: debe retornar false cuando la fechaInicio es null")
    void testEsActiva_FechaInicioNull() {
        lesion.setFechaInicio(null);
        assertThat(lesion.esActiva()).isFalse();
    }

    @Test
    @DisplayName("curar: debe curar la lesión y establecer fechaFin")
    void testCurar() {
        lesion.curar();
        assertThat(lesion.isCurada()).isTrue();
        assertThat(lesion.getFechaFin()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("curar: debe lanzar excepción cuando ya está curada")
    void testCurar_YaCurada() {
        lesion.setCurada(true);
        assertThatThrownBy(() -> lesion.curar())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La lesión ya está curada");
    }

    @Test
    @DisplayName("esGrave: debe retornar true para lesiones GRAVE o CRITICA")
    void testEsGrave() {
        assertThat(lesion.esGrave()).isTrue();

        lesion.setGravedad(Gravedad.CRITICA);
        assertThat(lesion.esGrave()).isTrue();

        lesion.setGravedad(Gravedad.MODERADA);
        assertThat(lesion.esGrave()).isFalse();

        lesion.setGravedad(Gravedad.LEVE);
        assertThat(lesion.esGrave()).isFalse();
    }

    @Test
    @DisplayName("getDuracionDias: debe calcular la duración en días")
    void testGetDuracionDias() {
        lesion.setFechaInicio(LocalDate.of(2024, 11, 15));
        lesion.setFechaFin(LocalDate.of(2024, 11, 20));
        assertThat(lesion.getDuracionDias()).isEqualTo(5);
    }

    @Test
    @DisplayName("getDuracionDias: debe usar fecha actual si fechaFin es null")
    void testGetDuracionDias_SinFechaFin() {
        lesion.setFechaFin(null);
        long duracion = lesion.getDuracionDias();
        assertThat(duracion).isGreaterThan(0);
    }

    @Test
    @DisplayName("getDuracionDias: debe retornar 0 cuando fechaInicio es null")
    void testGetDuracionDias_FechaInicioNull() {
        lesion.setFechaInicio(null);
        assertThat(lesion.getDuracionDias()).isZero();
    }

    @Test
    @DisplayName("necesitaAtencionUrgente: debe retornar true para GRAVE o CRITICA")
    void testNecesitaAtencionUrgente() {
        assertThat(lesion.necesitaAtencionUrgente()).isTrue();
        lesion.setGravedad(Gravedad.CRITICA);
        assertThat(lesion.necesitaAtencionUrgente()).isTrue();
        lesion.setGravedad(Gravedad.LEVE);
        assertThat(lesion.necesitaAtencionUrgente()).isFalse();
    }

    @Test
    @DisplayName("getEstadoLesion: debe retornar el estado de la lesión")
    void testGetEstadoLesion() {
        lesion.setCurada(false);
        lesion.setFechaInicio(LocalDate.now().minusDays(1));
        lesion.setFechaFin(LocalDate.now().plusDays(5));
        assertThat(lesion.getEstadoLesion()).isEqualTo("Activa");

        lesion.setCurada(true);
        assertThat(lesion.getEstadoLesion()).isEqualTo("Curada");

        lesion.setCurada(false);
        lesion.setFechaInicio(LocalDate.now().plusDays(5));
        assertThat(lesion.getEstadoLesion()).isEqualTo("Inactiva");
    }

    @Test
    @DisplayName("getDiasRestantesRecuperacion: debe calcular los días restantes")
    void testGetDiasRestantesRecuperacion() {
        lesion.setFechaInicio(LocalDate.now().minusDays(3));
        lesion.setFechaFin(LocalDate.now().plusDays(7));
        lesion.setCurada(false);
        assertThat(lesion.getDiasRestantesRecuperacion()).isEqualTo(7);
    }
}