package com.futbol.estadisticas.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

@DisplayName("Estadio")
class EstadioTest {

    private static final UUID ID_ESTADIO = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private Estadio estadioBase() {
        return Estadio.builder()
                .idEstadio(ID_ESTADIO)
                .nombre("Emirates Stadium")
                .direccion("Highbury, Londres")
                .capacidad(60704)
                .fechaFundacion(LocalDate.of(2006, 7, 22))
                .build();
    }

    @Nested
    @DisplayName("Construcción")
    class Construccion {

        @Test
        @DisplayName("builder crea estadio con todos los campos")
        void builderCompleto() {
            Estadio e = estadioBase();

            assertThat(e.getIdEstadio()).isEqualTo(ID_ESTADIO);
            assertThat(e.getNombre()).isEqualTo("Emirates Stadium");
            assertThat(e.getDireccion()).isEqualTo("Highbury, Londres");
            assertThat(e.getCapacidad()).isEqualTo(60704);
            assertThat(e.getFechaFundacion()).isEqualTo(LocalDate.of(2006, 7, 22));
            assertThat(e.getEquipoPrincipal()).isNull();
        }

        @Test
        @DisplayName("constructor vacío deja todos los campos null")
        void constructorVacio() {
            Estadio e = new Estadio();

            assertThat(e.getIdEstadio()).isNull();
            assertThat(e.getNombre()).isNull();
            assertThat(e.getCapacidad()).isNull();
            assertThat(e.getFechaFundacion()).isNull();
            assertThat(e.getEquipoPrincipal()).isNull();
        }
    }

    @Nested
    @DisplayName("getPorcentajeOcupacion")
    class PorcentajeOcupacion {

        @Test
        @DisplayName("calcula el porcentaje correctamente")
        void porcentajeCorrecto() {
            Estadio e = estadioBase();

            assertThat(e.getPorcentajeOcupacion(30352)).isEqualTo(50.0);
            assertThat(e.getPorcentajeOcupacion(60704)).isEqualTo(100.0);
            assertThat(e.getPorcentajeOcupacion(0)).isZero();
        }

        @Test
        @DisplayName("devuelve 0 si la capacidad es null")
        void capacidadNull() {
            Estadio e = Estadio.builder()
                    .idEstadio(ID_ESTADIO)
                    .capacidad(null)
                    .build();

            assertThat(e.getPorcentajeOcupacion(100)).isZero();
        }

        @Test
        @DisplayName("devuelve 0 si la capacidad es 0")
        void capacidadCero() {
            Estadio e = Estadio.builder()
                    .idEstadio(ID_ESTADIO)
                    .capacidad(0)
                    .build();

            assertThat(e.getPorcentajeOcupacion(100)).isZero();
        }

        @Test
        @DisplayName("devuelve 0 si espectadores es null")
        void espectadoresNull() {
            Estadio e = estadioBase();

            assertThat(e.getPorcentajeOcupacion(null)).isZero();
        }

        @Test
        @DisplayName("permite sobreocupación (más espectadores que capacidad)")
        void sobreocupacion() {
            Estadio e = estadioBase();

            assertThat(e.getPorcentajeOcupacion(121408)).isEqualTo(200.0);
        }

        @Test
        @DisplayName("maneja decimales con precisión double")
        void decimales() {
            Estadio e = Estadio.builder()
                    .idEstadio(ID_ESTADIO)
                    .capacidad(3)
                    .build();

            assertThat(e.getPorcentajeOcupacion(1)).isCloseTo(33.33, within(0.01));
        }
    }

    @Nested
    @DisplayName("getDescripcionCompleta")
    class DescripcionCompleta {

        @Test
        @DisplayName("formato correcto con todos los campos")
        void formatoCompleto() {
            Estadio e = estadioBase();

            assertThat(e.getDescripcionCompleta())
                    .isEqualTo("Emirates Stadium - Capacidad: 60704 espectadores - Fundado: 2006");
        }

        @Test
        @DisplayName("usa 0 cuando capacidad es null")
        void capacidadNull() {
            Estadio e = Estadio.builder()
                    .idEstadio(ID_ESTADIO)
                    .nombre("Sin Capacidad")
                    .capacidad(null)
                    .fechaFundacion(LocalDate.of(2000, 1, 1))
                    .build();

            assertThat(e.getDescripcionCompleta())
                    .isEqualTo("Sin Capacidad - Capacidad: 0 espectadores - Fundado: 2000");
        }

        @Test
        @DisplayName("usa 0 cuando fechaFundacion es null")
        void fechaNull() {
            Estadio e = Estadio.builder()
                    .idEstadio(ID_ESTADIO)
                    .nombre("Sin Fecha")
                    .capacidad(50000)
                    .fechaFundacion(null)
                    .build();

            assertThat(e.getDescripcionCompleta())
                    .isEqualTo("Sin Fecha - Capacidad: 50000 espectadores - Fundado: 0");
        }

        @Test
        @DisplayName("usa null y 0 cuando nombre, capacidad y fecha son null")
        void todosNull() {
            Estadio e = Estadio.builder()
                    .idEstadio(ID_ESTADIO)
                    .build();

            assertThat(e.getDescripcionCompleta())
                    .isEqualTo("null - Capacidad: 0 espectadores - Fundado: 0");
        }
    }

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mismo ID son iguales aunque el resto difiera")
        void mismoId() {
            Estadio e1 = Estadio.builder().idEstadio(ID_ESTADIO).nombre("A").build();
            Estadio e2 = Estadio.builder().idEstadio(ID_ESTADIO).nombre("B").build();

            assertThat(e1).isEqualTo(e2);
            assertThat(e1).hasSameHashCodeAs(e2);
        }

        @Test
        @DisplayName("distinto ID no son iguales")
        void distintoId() {
            Estadio e1 = Estadio.builder().idEstadio(ID_ESTADIO).build();
            Estadio e2 = Estadio.builder().idEstadio(UUID.randomUUID()).build();

            assertThat(e1).isNotEqualTo(e2);
        }

        @Test
        @DisplayName("dos estadios con ID null son iguales entre sí")
        void idNull() {
            Estadio e1 = Estadio.builder().nombre("A").build();
            Estadio e2 = Estadio.builder().nombre("B").build();

            assertThat(e1).isEqualTo(e2);
            assertThat(e1).hasSameHashCodeAs(e2);
        }
    }
}