package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.CrearContratoRequest;
import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import com.futbol.estadisticas.application.port.mapper.ContratoMapper;
import com.futbol.estadisticas.application.port.out.ContratoRepositoryPort;
import com.futbol.estadisticas.application.port.out.DatosDeportivosRepositoryPort;
import com.futbol.estadisticas.application.port.out.EquipoRepositoryPort;
import com.futbol.estadisticas.application.port.out.PersonalDeportivoRepositoryPort;
import com.futbol.estadisticas.application.port.out.TecnicoRepositoryPort;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import com.futbol.estadisticas.domain.model.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {

    @Mock private ContratoRepositoryPort contratoRepository;
    @Mock private PersonalDeportivoRepositoryPort personalRepository;
    @Mock private EquipoRepositoryPort equipoRepository;
    @Mock private ContratoMapper contratoMapper;

    @InjectMocks private ContratoService contratoService;

    private static final UUID ID_CONTRATO = UUID.randomUUID();
    private static final UUID ID_PERSONAL = UUID.randomUUID();
    private static final UUID ID_EQUIPO = UUID.randomUUID();

    private Contrato contrato;
    private ContratoResponse response;
    private CrearContratoRequest request;
    private PersonalDeportivo personal;
    private Equipo equipo;

    private static final LocalDateTime INICIO = LocalDateTime.of(2024, 1, 1, 0, 0);
    private static final LocalDateTime FIN = LocalDateTime.of(2025, 1, 1, 0, 0);

    @BeforeEach
    void setUp() {
        personal = PersonalDeportivo.builder().idPersonal(ID_PERSONAL).build();
        equipo = Equipo.builder().idEquipo(ID_EQUIPO).nombre("Arsenal").
                tipo(TipoEquipo.CLUB_PROFESIONAL).build();

        request = CrearContratoRequest.builder()
                .idPersonal(ID_PERSONAL)
                .idEquipo(ID_EQUIPO)
                .tipoContrato(TipoContrato.PROFESIONAL)
                .costoFichaje(70_000_000.0)
                .fechaInicio(INICIO)
                .estado(EstadoContrato.ACTIVO)
                .fechaFin(FIN)
                .sueldo(250_000.0)
                .build();

        contrato = Contrato.builder()
                .idContrato(ID_CONTRATO)
                .personal(personal)
                .equipo(equipo)
                .fechaInicio(INICIO)
                .fechaFin(FIN)
                .sueldo(250_000.0)
                .costoFichaje(70_000_000.0)
                .tipoContrato(TipoContrato.PROFESIONAL)
                .estado(EstadoContrato.ACTIVO)
                .build();

        response = ContratoResponse.builder()
                .idContrato(ID_CONTRATO)
                .fechaInicio(INICIO)
                .fechaFin(FIN)
                .sueldo(250_000.0)
                .tipo(TipoContrato.PROFESIONAL)
                .estado(EstadoContrato.ACTIVO)
                .vigente(true)
                .idPersonal(ID_PERSONAL)
                .nombrePersonal("Personal")
                .idEquipo(ID_EQUIPO)
                .nombreEquipo("Arsenal")
                .nombreCortoEquipo("ARS")
                .costoFichaje(70_000_000.0)
                .build();
    }

    @Test
    @DisplayName("crearContrato: debe crear un contrato exitosamente")
    void testCrearContrato() {
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.of(personal));
        when(equipoRepository.findById(ID_EQUIPO)).thenReturn(Optional.of(equipo));
        when(contratoMapper.toEntity(
                any(UUID.class), eq(personal), eq(equipo),
                eq(INICIO), eq(FIN), eq(EstadoContrato.ACTIVO),
                eq(250_000.0), eq(70_000_000.0), eq(TipoContrato.PROFESIONAL)
        )).thenReturn(contrato);
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.crearContrato(request);

        assertThat(result).isNotNull();
        assertThat(result.idContrato()).isEqualTo(ID_CONTRATO);
        verify(contratoRepository).save(any(Contrato.class));
    }

    @Test
    @DisplayName("crearContrato: lanza PersonalNotFoundException si el personal no existe")
    void testCrearContrato_PersonalNoExiste() {
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.crearContrato(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("crearContrato: lanza IllegalArgumentException si el equipo no existe")
    void testCrearContrato_EquipoNoExiste() {
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.of(personal));
        when(equipoRepository.findById(ID_EQUIPO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.crearContrato(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Equipo no encontrado");
    }

    @Test
    @DisplayName("obtenerContratoPorId: retorna el contrato cuando existe")
    void testObtenerContratoPorId() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.obtenerContratoPorId(ID_CONTRATO);

        assertThat(result).isNotNull();
        assertThat(result.idContrato()).isEqualTo(ID_CONTRATO);
        verify(contratoRepository).findById(ID_CONTRATO);
    }

    @Test
    @DisplayName("obtenerContratoPorId: lanza excepción cuando no existe")
    void testObtenerContratoPorId_NoExiste() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.obtenerContratoPorId(ID_CONTRATO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Contrato no encontrado");
    }

    @Test
    @DisplayName("obtenerContratosPorPersonal: retorna todos los contratos de un personal")
    void testObtenerContratosPorPersonal() {
        when(contratoRepository.findByPersonal(ID_PERSONAL)).thenReturn(List.of(contrato));
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        List<ContratoResponse> result = contratoService.obtenerContratosPorPersonal(ID_PERSONAL);

        assertThat(result).hasSize(1);
        verify(contratoRepository).findByPersonal(ID_PERSONAL);
    }

    @Test
    @DisplayName("obtenerContratoVigenteDePersonal: retorna el contrato vigente")
    void testObtenerContratoVigenteDePersonal() {
        when(contratoRepository.findVigenteByPersonal(ID_PERSONAL)).thenReturn(Optional.of(contrato));
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.obtenerContratoVigenteDePersonal(ID_PERSONAL);

        assertThat(result).isNotNull();
        verify(contratoRepository).findVigenteByPersonal(ID_PERSONAL);
    }

    @Test
    @DisplayName("obtenerContratoVigenteDePersonal: lanza excepción si no hay vigente")
    void testObtenerContratoVigenteDePersonal_NoVigente() {
        when(contratoRepository.findVigenteByPersonal(ID_PERSONAL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.obtenerContratoVigenteDePersonal(ID_PERSONAL))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("obtenerContratosVigentesPorEquipo: retorna contratos vigentes de un equipo")
    void testObtenerContratosVigentesPorEquipo() {
        when(contratoRepository.findVigentesByEquipo(ID_EQUIPO)).thenReturn(List.of(contrato));
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        List<ContratoResponse> result = contratoService.obtenerContratosVigentesPorEquipo(ID_EQUIPO);

        assertThat(result).hasSize(1);
        verify(contratoRepository).findVigentesByEquipo(ID_EQUIPO);
    }

    @Test
    @DisplayName("renovarContrato: renueva el contrato exitosamente")
    void testRenovarContrato() {
        LocalDateTime nuevaFecha = LocalDateTime.now().plusYears(2);

        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.renovarContrato(ID_CONTRATO, nuevaFecha);

        assertThat(result).isNotNull();
        assertThat(contrato.getFechaFin()).isEqualTo(nuevaFecha);
        verify(contratoRepository).save(contrato);
    }

    @Test
    @DisplayName("renovarContrato: lanza excepción cuando el contrato no existe")
    void testRenovarContrato_NoExiste() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.renovarContrato(ID_CONTRATO, LocalDateTime.now().plusYears(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("finalizarContrato: finaliza el contrato exitosamente")
    void testFinalizarContrato() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.finalizarContrato(ID_CONTRATO, LocalDateTime.now());

        assertThat(result).isNotNull();
        assertThat(contrato.getEstado()).isEqualTo(EstadoContrato.FINALIZADO);
        verify(contratoRepository).save(contrato);
    }

    @Test
    @DisplayName("finalizarContrato: lanza excepción cuando el contrato no existe")
    void testFinalizarContrato_NoExiste() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.finalizarContrato(ID_CONTRATO, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rescindirContrato: rescinde el contrato exitosamente")
    void testRescindirContrato() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.rescindirContrato(ID_CONTRATO, LocalDateTime.now());

        assertThat(result).isNotNull();
        assertThat(contrato.getEstado()).isEqualTo(EstadoContrato.RESCINDIDO);
        verify(contratoRepository).save(contrato);
    }

    @Test
    @DisplayName("rescindirContrato: lanza excepción cuando el contrato ya está finalizado")
    void testRescindirContrato_YaFinalizado() {
        contrato.setEstado(EstadoContrato.FINALIZADO);
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));

        assertThatThrownBy(() -> contratoService.rescindirContrato(ID_CONTRATO, LocalDateTime.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finalizado");
    }

    @Test
    @DisplayName("rescindirContrato: lanza excepción cuando el contrato ya está rescindido")
    void testRescindirContrato_YaRescindido() {
        contrato.setEstado(EstadoContrato.RESCINDIDO);
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));

        assertThatThrownBy(() -> contratoService.rescindirContrato(ID_CONTRATO, LocalDateTime.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("rescindido");
    }

    @Test
    @DisplayName("eliminarContrato: elimina si existe")
    void testEliminarContrato() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));

        contratoService.eliminarContrato(ID_CONTRATO);

        verify(contratoRepository).deleteById(ID_CONTRATO);
    }

    @Test
    @DisplayName("eliminarContrato: lanza excepción si no existe")
    void testEliminarContrato_NoExiste() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.eliminarContrato(ID_CONTRATO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}