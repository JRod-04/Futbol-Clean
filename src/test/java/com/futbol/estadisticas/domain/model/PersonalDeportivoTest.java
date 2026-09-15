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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PersonalDeportivo")
class PersonalDeportivoTest {

    private PersonalDeportivo personal;
    private Equipo clubProfesional;

    @BeforeEach
    void setUp() {
        personal = PersonalDeportivo.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Lionel")
                .apellido("Messi")
                .fechaNacimiento(LocalDate.of(1987, 6, 24))
                .nacionalidad(Nacion.ARGENTINA)
                .build();

        clubProfesional = Equipo.builder()
                .idEquipo(UUID.randomUUID())
                .nombre("Inter Miami")
                .tipo(TipoEquipo.CLUB_PROFESIONAL)
                .pais(Nacion.ESTADOS_UNIDOS)
                .build();
    }

    private Contrato contratoVigente(TipoContrato tipo, Equipo equipo) {
        return Contrato.builder()
                .idContrato(UUID.randomUUID())
                .tipoContrato(tipo)
                .equipo(equipo)
                .fechaInicio(LocalDateTime.now().minusDays(10))
                .fechaFin(LocalDateTime.now().plusDays(10))
                .build();
    }

    private Contrato contratoVencido(TipoContrato tipo, Equipo equipo) {
        return Contrato.builder()
                .idContrato(UUID.randomUUID())
                .tipoContrato(tipo)
                .equipo(equipo)
                .fechaInicio(LocalDateTime.now().minusDays(30))
                .fechaFin(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("getNombreCompleto concatena nombre y apellido")
    void getNombreCompleto_concatenaNombreYApellido() {
        assertThat(personal.getNombreCompleto()).isEqualTo("Lionel Messi");
    }

    @Nested
    @DisplayName("getEdad")
    class GetEdad {

        @Test
        @DisplayName("calcula la edad a partir de la fecha de nacimiento")
        void calculaEdadCorrectamente() {
            LocalDate haceVeinteAnios = LocalDate.now().minusYears(20);
            personal.setFechaNacimiento(haceVeinteAnios);

            assertThat(personal.getEdad()).isEqualTo(20);
        }

        @Test
        @DisplayName("devuelve 0 si no hay fecha de nacimiento")
        void devuelveCeroSiFechaNacimientoEsNula() {
            personal.setFechaNacimiento(null);

            assertThat(personal.getEdad()).isZero();
        }
    }

    @Nested
    @DisplayName("getContratoVigente")
    class GetContratoVigente {

        @Test
        @DisplayName("devuelve null si no tiene contratos")
        void devuelveNullSinContratos() {
            assertThat(personal.getContratoVigente()).isNull();
        }

        @Test
        @DisplayName("devuelve null si todos los contratos están vencidos")
        void devuelveNullSiTodosVencidos() {
            personal.getContratos().add(contratoVencido(TipoContrato.PROFESIONAL, clubProfesional));

            assertThat(personal.getContratoVigente()).isNull();
        }

        @Test
        @DisplayName("devuelve el contrato vigente cuando existe")
        void devuelveElContratoVigente() {
            Contrato vigente = contratoVigente(TipoContrato.PROFESIONAL, clubProfesional);
            personal.getContratos().add(contratoVencido(TipoContrato.PROFESIONAL, clubProfesional));
            personal.getContratos().add(vigente);

            assertThat(personal.getContratoVigente()).isEqualTo(vigente);
        }
    }

    @Nested
    @DisplayName("agregarContrato")
    class AgregarContrato {

        @Test
        @DisplayName("lanza excepción si el contrato es nulo")
        void lanzaExcepcionSiContratoEsNulo() {
            assertThatThrownBy(() -> personal.agregarContrato(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no puede ser nulo");
        }

        @Test
        @DisplayName("agrega el contrato y establece la relación bidireccional")
        void agregaContratoYEstableceRelacion() {
            Contrato contrato = contratoVigente(TipoContrato.PROFESIONAL, clubProfesional);

            personal.agregarContrato(contrato);

            assertThat(personal.getContratos()).containsExactly(contrato);
            assertThat(contrato.getPersonal()).isEqualTo(personal);
        }

        @Test
        @DisplayName("propaga la excepción de validación si el contrato no es compatible con el equipo")
        void propagaExcepcionDeValidacion() {
            Equipo clubAmateur = Equipo.builder()
                    .idEquipo(UUID.randomUUID())
                    .tipo(TipoEquipo.CLUB_AMATEUR)
                    .build();
            Contrato contratoProfesionalEnAmateur = contratoVigente(TipoContrato.PROFESIONAL, clubAmateur);

            assertThatThrownBy(() -> personal.agregarContrato(contratoProfesionalEnAmateur))
                    .isInstanceOf(IllegalStateException.class);

            assertThat(personal.getContratos()).isEmpty();
        }
    }

    @Nested
    @DisplayName("puedeRegistrarseEnEquipo")
    class PuedeRegistrarseEnEquipo {

        @Test
        @DisplayName("devuelve false si el equipo es nulo")
        void devuelveFalseSiEquipoEsNulo() {
            assertThat(personal.puedeRegistrarseEnEquipo(null)).isFalse();
        }

        @Test
        @DisplayName("devuelve true si un contrato profesional sería válido para el equipo")
        void devuelveTrueSiEsValido() {
            assertThat(personal.puedeRegistrarseEnEquipo(clubProfesional)).isTrue();
        }

        @Test
        @DisplayName("devuelve false si ya existe un contrato incompatible vigente")
        void devuelveFalseSiHayConflicto() {
            personal.getContratos().add(contratoVigente(TipoContrato.PROFESIONAL, clubProfesional));

            // CLUB_PROFESIONAL + CLUB_AMATEUR es una combinación incompatible
            // según validarCompatibilidadTiposEquipo.
            Equipo clubAmateur = Equipo.builder()
                    .idEquipo(UUID.randomUUID())
                    .tipo(TipoEquipo.CLUB_AMATEUR)
                    .build();

            assertThat(personal.puedeRegistrarseEnEquipo(clubAmateur)).isFalse();
        }
    }

    @Nested
    @DisplayName("getContratosPorTipo")
    class GetContratosPorTipo {

        @Test
        @DisplayName("filtra únicamente los contratos del tipo pedido")
        void filtraPorTipo() {
            Contrato profesional = contratoVigente(TipoContrato.PROFESIONAL, clubProfesional);
            Equipo seleccion = Equipo.builder()
                    .idEquipo(UUID.randomUUID())
                    .tipo(TipoEquipo.SELECCION_ABSOLUTA)
                    .pais(Nacion.ARGENTINA)
                    .build();
            Contrato convocatoria = contratoVigente(TipoContrato.CONVOCATORIA, seleccion);

            personal.getContratos().add(profesional);
            personal.getContratos().add(convocatoria);

            assertThat(personal.getContratosPorTipo(TipoContrato.PROFESIONAL))
                    .containsExactly(profesional);
            assertThat(personal.getContratosPorTipo(TipoContrato.CONVOCATORIA))
                    .containsExactly(convocatoria);
        }

        @Test
        @DisplayName("devuelve lista vacía si no hay coincidencias")
        void devuelveListaVaciaSinCoincidencias() {
            assertThat(personal.getContratosPorTipo(TipoContrato.AMATEUR)).isEmpty();
        }
    }

    @Nested
    @DisplayName("tieneContratoProfesionalVigente / tieneContratoConvocatoriaVigente")
    class BanderasDeContrato {

        @Test
        @DisplayName("tieneContratoProfesionalVigente es true solo con un profesional vigente")
        void tieneContratoProfesionalVigente() {
            assertThat(personal.tieneContratoProfesionalVigente()).isFalse();

            personal.getContratos().add(contratoVigente(TipoContrato.PROFESIONAL, clubProfesional));

            assertThat(personal.tieneContratoProfesionalVigente()).isTrue();
        }

        @Test
        @DisplayName("un contrato profesional vencido no cuenta como vigente")
        void contratoVencidoNoCuenta() {
            personal.getContratos().add(contratoVencido(TipoContrato.PROFESIONAL, clubProfesional));

            assertThat(personal.tieneContratoProfesionalVigente()).isFalse();
        }

        @Test
        @DisplayName("tieneContratoConvocatoriaVigente es true solo con una convocatoria vigente")
        void tieneContratoConvocatoriaVigente() {
            Equipo seleccion = Equipo.builder()
                    .idEquipo(UUID.randomUUID())
                    .tipo(TipoEquipo.SELECCION_ABSOLUTA)
                    .pais(Nacion.ARGENTINA)
                    .build();

            assertThat(personal.tieneContratoConvocatoriaVigente()).isFalse();

            personal.getContratos().add(contratoVigente(TipoContrato.CONVOCATORIA, seleccion));

            assertThat(personal.tieneContratoConvocatoriaVigente()).isTrue();
        }
    }

    @Nested
    @DisplayName("getEquipoActual")
    class GetEquipoActual {

        @Test
        @DisplayName("devuelve null si no hay contrato vigente")
        void devuelveNullSinContratoVigente() {
            assertThat(personal.getEquipoActual()).isNull();
        }

        @Test
        @DisplayName("devuelve el equipo del contrato vigente")
        void devuelveElEquipoDelContratoVigente() {
            personal.getContratos().add(contratoVigente(TipoContrato.PROFESIONAL, clubProfesional));

            assertThat(personal.getEquipoActual()).isEqualTo(clubProfesional);
        }
    }

    @Test
    @DisplayName("equals/hashCode se basan únicamente en idPersonal")
    void equalsSeBasaEnId() {
        UUID id = UUID.randomUUID();
        PersonalDeportivo p1 = PersonalDeportivo.builder().idPersonal(id).nombre("A").build();
        PersonalDeportivo p2 = PersonalDeportivo.builder().idPersonal(id).nombre("B").build();

        assertThat(p1).isEqualTo(p2);
        assertThat(p1).hasSameHashCodeAs(p2);
    }
}
