package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.Gravedad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Lesion")
class LesionTest {

    private Lesion.LesionBuilder base() {
        return Lesion.builder().idLesion(UUID.randomUUID());
    }

    @Nested
    @DisplayName("esActiva")
    class EsActiva {

        @Test
        @DisplayName("es false si está curada")
        void falseSiCurada() {
            Lesion lesion = base()
                    .curada(true)
                    .fechaInicio(LocalDate.now().minusDays(5))
                    .build();

            assertThat(lesion.esActiva()).isFalse();
        }

        @Test
        @DisplayName("es false si no tiene fecha de inicio")
        void falseSinFechaInicio() {
            Lesion lesion = base().build();

            assertThat(lesion.esActiva()).isFalse();
        }

        @Test
        @DisplayName("es false si la fecha de inicio es hoy o en el futuro")
        void falseSiInicioNoEsAnteriorAHoy() {
            Lesion lesion = base().fechaInicio(LocalDate.now()).build();

            assertThat(lesion.esActiva()).isFalse();
        }

        @Test
        @DisplayName("es true si empezó antes de hoy y no tiene fecha fin")
        void trueSinFechaFin() {
            Lesion lesion = base().fechaInicio(LocalDate.now().minusDays(3)).build();

            assertThat(lesion.esActiva()).isTrue();
        }

        @Test
        @DisplayName("es true si hoy está entre inicio y fin")
        void trueSiHoyEstaEnRango() {
            Lesion lesion = base()
                    .fechaInicio(LocalDate.now().minusDays(3))
                    .fechaFin(LocalDate.now().plusDays(3))
                    .build();

            assertThat(lesion.esActiva()).isTrue();
        }

        @Test
        @DisplayName("es false si la fecha fin ya pasó")
        void falseSiFechaFinYaPaso() {
            Lesion lesion = base()
                    .fechaInicio(LocalDate.now().minusDays(10))
                    .fechaFin(LocalDate.now().minusDays(1))
                    .build();

            assertThat(lesion.esActiva()).isFalse();
        }
    }

    @Nested
    @DisplayName("curar")
    class Curar {

        @Test
        @DisplayName("lanza excepción si ya está curada")
        void lanzaExcepcionSiYaCurada() {
            Lesion lesion = base().curada(true).build();

            assertThatThrownBy(lesion::curar)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ya está curada");
        }

        @Test
        @DisplayName("marca la lesión como curada y fija la fecha fin de hoy")
        void marcaComoCurada() {
            Lesion lesion = base().fechaInicio(LocalDate.now().minusDays(5)).build();

            lesion.curar();

            assertThat(lesion.isCurada()).isTrue();
            assertThat(lesion.getFechaFin()).isEqualTo(LocalDate.now());
        }
    }

    @ParameterizedTest
    @EnumSource(value = Gravedad.class, names = {"GRAVE", "CRITICA"})
    @DisplayName("esGrave es true para GRAVE y CRITICA")
    void esGrave_trueParaGraveYCritica(Gravedad gravedad) {
        Lesion lesion = base().gravedad(gravedad).build();

        assertThat(lesion.esGrave()).isTrue();
        assertThat(lesion.necesitaAtencionUrgente()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = Gravedad.class, names = {"LEVE", "MODERADA"})
    @DisplayName("esGrave es false para LEVE y MODERADA")
    void esGrave_falseParaLeveYModerada(Gravedad gravedad) {
        Lesion lesion = base().gravedad(gravedad).build();

        assertThat(lesion.esGrave()).isFalse();
        assertThat(lesion.necesitaAtencionUrgente()).isFalse();
    }

    @Nested
    @DisplayName("getDuracionDias")
    class GetDuracionDias {

        @Test
        @DisplayName("devuelve 0 si no hay fecha de inicio")
        void devuelveCeroSinFechaInicio() {
            Lesion lesion = base().build();

            assertThat(lesion.getDuracionDias()).isZero();
        }

        @Test
        @DisplayName("calcula los días entre inicio y fin cuando ambas existen")
        void calculaDiasEntreInicioYFin() {
            Lesion lesion = base()
                    .fechaInicio(LocalDate.now().minusDays(10))
                    .fechaFin(LocalDate.now().minusDays(2))
                    .build();

            assertThat(lesion.getDuracionDias()).isEqualTo(8);
        }

        @Test
        @DisplayName("usa hoy como fecha de cálculo si no hay fecha fin")
        void usaHoySiNoHayFechaFin() {
            Lesion lesion = base().fechaInicio(LocalDate.now().minusDays(4)).build();

            assertThat(lesion.getDuracionDias()).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("getEstadoLesion")
    class GetEstadoLesion {

        @Test
        @DisplayName("devuelve 'Curada' si está curada")
        void devuelveCurada() {
            Lesion lesion = base().curada(true).build();

            assertThat(lesion.getEstadoLesion()).isEqualTo("Curada");
        }

        @Test
        @DisplayName("devuelve 'Activa' si está activa y no curada")
        void devuelveActiva() {
            Lesion lesion = base().fechaInicio(LocalDate.now().minusDays(2)).build();

            assertThat(lesion.getEstadoLesion()).isEqualTo("Activa");
        }

        @Test
        @DisplayName("devuelve 'Inactiva' si no está curada ni activa")
        void devuelveInactiva() {
            Lesion lesion = base().build();

            assertThat(lesion.getEstadoLesion()).isEqualTo("Inactiva");
        }
    }

    @Nested
    @DisplayName("getDiasRestantesRecuperacion")
    class GetDiasRestantesRecuperacion {

        @Test
        @DisplayName("devuelve 0 si no hay fecha fin")
        void devuelveCeroSinFechaFin() {
            Lesion lesion = base().fechaInicio(LocalDate.now().minusDays(2)).build();

            assertThat(lesion.getDiasRestantesRecuperacion()).isZero();
        }

        @Test
        @DisplayName("devuelve 0 si ya está curada")
        void devuelveCeroSiCurada() {
            Lesion lesion = base()
                    .fechaInicio(LocalDate.now().minusDays(2))
                    .fechaFin(LocalDate.now().plusDays(3))
                    .curada(true)
                    .build();

            assertThat(lesion.getDiasRestantesRecuperacion()).isZero();
        }

        @Test
        @DisplayName("calcula los días restantes cuando la lesión está activa")
        void calculaDiasRestantes() {
            Lesion lesion = base()
                    .fechaInicio(LocalDate.now().minusDays(2))
                    .fechaFin(LocalDate.now().plusDays(5))
                    .build();

            assertThat(lesion.getDiasRestantesRecuperacion()).isEqualTo(5);
        }
    }

    @Test
    @DisplayName("equals/hashCode se basan únicamente en idLesion")
    void equalsSeBasaEnId() {
        UUID id = UUID.randomUUID();
        Lesion l1 = base().idLesion(id).nombreLesion("Esguince").build();
        Lesion l2 = base().idLesion(id).nombreLesion("Fractura").build();

        assertThat(l1).isEqualTo(l2);
        assertThat(l1).hasSameHashCodeAs(l2);
    }
}
