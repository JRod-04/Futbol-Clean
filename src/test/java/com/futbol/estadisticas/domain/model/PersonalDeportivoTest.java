package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalDeportivoTest {

    private PersonalDeportivo personal;

    @BeforeEach
    void setUp() {
        personal = PersonalDeportivo.builder()
                .idPersonal(UUID.randomUUID())
                .nombre("Mikel")
                .apellido("Arteta")
                .fechaNacimiento(LocalDate.of(1982, 3, 26))
                .nacionalidad(Nacion.ESPANA)
                .tipoPersonal(TipoPersonal.TECNICO)
                .build();
    }

    @Test
    @DisplayName("getNombreCompleto: debe retornar nombre y apellido concatenados")
    void testGetNombreCompleto() {
        assertThat(personal.getNombreCompleto()).isEqualTo("Mikel Arteta");
    }

    @Test
    @DisplayName("getEdad: debe calcular la edad correctamente")
    void testGetEdad() {
        int edad = personal.getEdad();
        assertThat(edad).isGreaterThan(0);
    }

    @Test
    @DisplayName("getEdad: debe retornar 0 cuando fechaNacimiento es null")
    void testGetEdad_FechaNacimientoNull() {
        personal.setFechaNacimiento(null);
        assertThat(personal.getEdad()).isZero();
    }

    @Test
    @DisplayName("getContratoVigente: debe retornar el contrato vigente")
    void testGetContratoVigente() {
        Contrato contratoActivo = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .estado(EstadoContrato.ACTIVO)
                .build();

        Contrato contratoFinalizado = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .fechaInicio(LocalDateTime.now().minusMonths(12))
                .fechaFin(LocalDateTime.now().minusMonths(1))
                .estado(EstadoContrato.FINALIZADO)
                .build();

        personal.agregarContrato(contratoActivo);
        personal.agregarContrato(contratoFinalizado);

        Contrato vigente = personal.getContratoVigente();
        assertThat(vigente).isNotNull();
        assertThat(vigente.getEstado()).isEqualTo(EstadoContrato.ACTIVO);
    }

    @Test
    @DisplayName("getContratoVigente: debe retornar null cuando no hay contrato vigente")
    void testGetContratoVigente_NoVigente() {
        Contrato contratoFinalizado = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .fechaInicio(LocalDateTime.now().minusMonths(12))
                .fechaFin(LocalDateTime.now().minusMonths(1))
                .estado(EstadoContrato.FINALIZADO)
                .build();

        personal.agregarContrato(contratoFinalizado);
        assertThat(personal.getContratoVigente()).isNull();
    }

    @Test
    @DisplayName("agregarContrato: debe agregar un contrato y establecer relación bidireccional")
    void testAgregarContrato() {
        Contrato contrato = Contrato.builder()
                .idContrato(UUID.randomUUID())
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .estado(EstadoContrato.ACTIVO)
                .build();

        personal.agregarContrato(contrato);

        assertThat(personal.getContratos()).hasSize(1);
        assertThat(contrato.getPersonal()).isEqualTo(personal);
    }
}