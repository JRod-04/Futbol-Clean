package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContratoTest {

    private Contrato contrato;
    private static final UUID ID_CONTRATO = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        contrato = Contrato.builder()
                .idContrato(ID_CONTRATO)
                .fechaInicio(LocalDateTime.now().minusMonths(6))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .sueldo(250000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("estaVigente: debe retornar true cuando el contrato está activo y dentro del rango")
    void testEstaVigente() {
        assertThat(contrato.estaVigente()).isTrue();
    }

    @Test
    @DisplayName("estaVigente: debe retornar false cuando el contrato no está activo")
    void testEstaVigente_NoActivo() {
        contrato.setEstado(EstadoContrato.FINALIZADO);
        assertThat(contrato.estaVigente()).isFalse();
    }

    @Test
    @DisplayName("finalizar: debe finalizar el contrato y establecer fechaFin")
    void testFinalizar() {
        LocalDateTime fechaAntes = LocalDateTime.now();
        contrato.finalizar();

        assertThat(contrato.getEstado()).isEqualTo(EstadoContrato.FINALIZADO);
        assertThat(contrato.getFechaFin()).isAfterOrEqualTo(fechaAntes);
    }

    @Test
    @DisplayName("finalizar: debe lanzar excepción cuando el contrato ya está finalizado")
    void testFinalizar_YaFinalizado() {
        contrato.setEstado(EstadoContrato.FINALIZADO);
        assertThatThrownBy(() -> contrato.finalizar())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El contrato ya está finalizado");
    }

    @Test
    @DisplayName("renovar: debe extender la fechaFin")
    void testRenovar() {
        LocalDateTime fechaFinOriginal = contrato.getFechaFin();
        contrato.renovar(12);

        assertThat(contrato.getFechaFin()).isAfter(fechaFinOriginal);
        assertThat(contrato.getFechaFin()).isEqualTo(fechaFinOriginal.plusMonths(12));
    }

    @Test
    @DisplayName("renovar: debe lanzar excepción cuando los meses son negativos")
    void testRenovar_MesesNegativos() {
        assertThatThrownBy(() -> contrato.renovar(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los meses deben ser positivos");
    }

    @Test
    @DisplayName("rescindir: debe rescindir el contrato")
    void testRescindir() {
        contrato.rescindir();

        assertThat(contrato.getEstado()).isEqualTo(EstadoContrato.RESCINDIDO);
        assertThat(contrato.getFechaFin()).isNotNull();
    }

    @Test
    @DisplayName("rescindir: debe lanzar excepción cuando el contrato ya está finalizado")
    void testRescindir_YaFinalizado() {
        contrato.setEstado(EstadoContrato.FINALIZADO);
        assertThatThrownBy(() -> contrato.rescindir())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se puede Rescindir un contrato Finalizado");
    }
}