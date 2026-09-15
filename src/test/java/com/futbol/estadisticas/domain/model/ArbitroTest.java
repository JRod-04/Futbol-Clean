package com.futbol.estadisticas.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArbitroTest {

    private static final UUID ID_ARBITRO = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private Arbitro arbitroBase() {
        return Arbitro.builder()
                .idArbitro(ID_ARBITRO)
                .nombre("Michael")
                .apellido("Oliver")
                .fechaNacimiento(LocalDate.of(1985, 2, 20))
                .build();
    }

    @Nested
    @DisplayName("Construcción")
    class Construccion {

        @Test
        @DisplayName("builder crea árbitro con todos los campos")
        void builderCreaArbitroCompleto() {
            Arbitro arbitro = arbitroBase();

            assertThat(arbitro.getIdArbitro()).isEqualTo(ID_ARBITRO);
            assertThat(arbitro.getNombre()).isEqualTo("Michael");
            assertThat(arbitro.getApellido()).isEqualTo("Oliver");
            assertThat(arbitro.getFechaNacimiento()).isEqualTo(LocalDate.of(1985, 2, 20));
        }

        @Test
        @DisplayName("partidosArbitrados se inicializa vacío por defecto")
        void partidosArbitradosVacioPorDefecto() {
            Arbitro arbitro = arbitroBase();

            assertThat(arbitro.getPartidosArbitrados()).isNotNull();
            assertThat(arbitro.getPartidosArbitrados()).isEmpty();
        }

        @Test
        @DisplayName("constructor vacío inicializa partidosArbitrados como lista vacía")
        void constructorVacio() {
            Arbitro arbitro = new Arbitro();

            assertThat(arbitro.getPartidosArbitrados()).isNotNull();
            assertThat(arbitro.getPartidosArbitrados()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getNombreCompleto")
    class NombreCompleto {

        @Test
        @DisplayName("devuelve nombre y apellido separados por espacio")
        void nombreCompleto() {
            Arbitro arbitro = arbitroBase();

            assertThat(arbitro.getNombreCompleto()).isEqualTo("Michael Oliver");
        }

        @Test
        @DisplayName("con nombre null devuelve 'null apellido'")
        void nombreNull() {
            Arbitro arbitro = Arbitro.builder()
                    .idArbitro(ID_ARBITRO)
                    .apellido("Oliver")
                    .build();

            assertThat(arbitro.getNombreCompleto()).isEqualTo("null Oliver");
        }

        @Test
        @DisplayName("con apellido null devuelve 'nombre null'")
        void apellidoNull() {
            Arbitro arbitro = Arbitro.builder()
                    .idArbitro(ID_ARBITRO)
                    .nombre("Michael")
                    .build();

            assertThat(arbitro.getNombreCompleto()).isEqualTo("Michael null");
        }
    }

    @Nested
    @DisplayName("getEdad")
    class Edad {

        @Test
        @DisplayName("calcula la edad correctamente")
        void edadCorrecta() {
            Arbitro arbitro = Arbitro.builder()
                    .idArbitro(ID_ARBITRO)
                    .fechaNacimiento(LocalDate.now().minusYears(40))
                    .build();

            assertThat(arbitro.getEdad()).isEqualTo(40);
        }

        @Test
        @DisplayName("devuelve 0 si fechaNacimiento es null")
        void edadNull() {
            Arbitro arbitro = Arbitro.builder()
                    .idArbitro(ID_ARBITRO)
                    .fechaNacimiento(null)
                    .build();

            assertThat(arbitro.getEdad()).isZero();
        }

        @Test
        @DisplayName("devuelve 0 si nació hoy")
        void edadRecienNacido() {
            Arbitro arbitro = Arbitro.builder()
                    .idArbitro(ID_ARBITRO)
                    .fechaNacimiento(LocalDate.now())
                    .build();

            assertThat(arbitro.getEdad()).isZero();
        }

        @Test
        @DisplayName("no suma un año si aún no ha llegado el cumpleaños")
        void edadAntesDeCumpleanios() {
            LocalDate hace40Anios = LocalDate.now().minusYears(40).plusDays(1);
            Arbitro arbitro = Arbitro.builder()
                    .idArbitro(ID_ARBITRO)
                    .fechaNacimiento(hace40Anios)
                    .build();

            assertThat(arbitro.getEdad()).isEqualTo(39);
        }

        @Test
        @DisplayName("suma el año en el día exacto del cumpleaños")
        void edadEnCumpleanios() {
            LocalDate hace40Anios = LocalDate.now().minusYears(40);
            Arbitro arbitro = Arbitro.builder()
                    .idArbitro(ID_ARBITRO)
                    .fechaNacimiento(hace40Anios)
                    .build();

            assertThat(arbitro.getEdad()).isEqualTo(40);
        }
    }

    @Nested
    @DisplayName("agregarPartido")
    class AgregarPartido {

        @Test
        @DisplayName("agrega partido a la lista y establece el árbitro en el partido")
        void agregaPartidoYEstableceArbitro() {
            Arbitro arbitro = arbitroBase();
            Partido partido = new Partido();

            arbitro.agregarPartido(partido);

            assertThat(arbitro.getPartidosArbitrados()).hasSize(1);
            assertThat(arbitro.getPartidosArbitrados()).contains(partido);
            assertThat(partido.getArbitro()).isSameAs(arbitro);
        }

        @Test
        @DisplayName("ignora partido null")
        void ignoraPartidoNull() {
            Arbitro arbitro = arbitroBase();

            arbitro.agregarPartido(null);

            assertThat(arbitro.getPartidosArbitrados()).isEmpty();
        }

        @Test
        @DisplayName("permite agregar varios partidos")
        void agregaVariosPartidos() {
            Arbitro arbitro = arbitroBase();
            Partido p1 = new Partido();
            Partido p2 = new Partido();

            arbitro.agregarPartido(p1);
            arbitro.agregarPartido(p2);

            assertThat(arbitro.getPartidosArbitrados()).containsExactly(p1, p2);
            assertThat(p1.getArbitro()).isSameAs(arbitro);
            assertThat(p2.getArbitro()).isSameAs(arbitro);
        }

        @Test
        @DisplayName("permite agregar el mismo partido dos veces (sin deduplicación)")
        void permiteDuplicados() {
            Arbitro arbitro = arbitroBase();
            Partido partido = new Partido();

            arbitro.agregarPartido(partido);
            arbitro.agregarPartido(partido);

            assertThat(arbitro.getPartidosArbitrados()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("getCantidadPartidos")
    class CantidadPartidos {

        @Test
        @DisplayName("devuelve 0 sin partidos")
        void cantidadCero() {
            Arbitro arbitro = arbitroBase();

            assertThat(arbitro.getCantidadPartidos()).isZero();
        }

        @Test
        @DisplayName("devuelve el número de partidos agregados")
        void cantidadVarios() {
            Arbitro arbitro = arbitroBase();
            arbitro.agregarPartido(new Partido());
            arbitro.agregarPartido(new Partido());
            arbitro.agregarPartido(new Partido());

            assertThat(arbitro.getCantidadPartidos()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("dos árbitros con el mismo ID son iguales")
        void mismoIdIguales() {
            Arbitro a1 = Arbitro.builder().idArbitro(ID_ARBITRO).nombre("Michael").build();
            Arbitro a2 = Arbitro.builder().idArbitro(ID_ARBITRO).nombre("Otro").build();

            assertThat(a1).isEqualTo(a2);
            assertThat(a1).hasSameHashCodeAs(a2);
        }

        @Test
        @DisplayName("dos árbitros con distinto ID no son iguales")
        void distintoIdNoIguales() {
            Arbitro a1 = Arbitro.builder().idArbitro(ID_ARBITRO).build();
            Arbitro a2 = Arbitro.builder().idArbitro(UUID.randomUUID()).build();

            assertThat(a1).isNotEqualTo(a2);
        }

        @Test
        @DisplayName("dos árbitros con ID null son iguales entre sí")
        void idNullIguales() {
            Arbitro a1 = Arbitro.builder().nombre("A").build();
            Arbitro a2 = Arbitro.builder().nombre("B").build();

            assertThat(a1).isEqualTo(a2);
            assertThat(a1).hasSameHashCodeAs(a2);
        }
    }
}