package com.futbol.estadisticas.application.service;

import com.futbol.estadisticas.application.port.dto.request.CrearContratoRequest;
import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import com.futbol.estadisticas.application.port.mapper.ContratoMapper;
import com.futbol.estadisticas.application.port.out.EquipoRepositoryPort;
import com.futbol.estadisticas.application.port.out.ContratoRepositoryPort;
import com.futbol.estadisticas.application.port.out.PersonalDeportivoRepositoryPort;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {

    @Mock private ContratoRepositoryPort contratoRepository;
    @Mock private PersonalDeportivoRepositoryPort personalRepository;
    @Mock private EquipoRepositoryPort clubRepository;
    @Mock private ContratoMapper contratoMapper;
    @InjectMocks private ContratoService contratoService;

    private static final UUID ID_CONTRATO = UUID.randomUUID();
    private static final UUID ID_PERSONAL = UUID.randomUUID();
    private static final UUID ID_CLUB = UUID.randomUUID();
    private Contrato contrato;
    private ContratoResponse response;
    private CrearContratoRequest request;
    private PersonalDeportivo personal;
    private Equipo club;

    @BeforeEach
    void setUp() {
        personal = PersonalDeportivo.builder().idPersonal(ID_PERSONAL).build();
        club = Equipo.builder().idEquipo(ID_CLUB).build();

        request = new CrearContratoRequest(
                ID_PERSONAL, ID_CLUB,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().plusMonths(11),
                250000.0
        );

        contrato = Contrato.builder()
                .idContrato(ID_CONTRATO)
                .personal(personal)
                .equipo(club)
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFin())
                .sueldo(request.sueldo())
                .estado(EstadoContrato.ACTIVO)
                .build();

        response = new ContratoResponse(
                ID_CONTRATO, request.fechaInicio(), request.fechaFin(),
                request.sueldo(), EstadoContrato.ACTIVO, true,
                ID_PERSONAL, "Personal", ID_CLUB, "Club"
        );
    }

    @Test
    @DisplayName("crearContrato: debe crear un contrato exitosamente")
    void testCrearContrato() {
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.of(personal));
        when(clubRepository.findById(ID_CLUB)).thenReturn(Optional.of(club));
        when(contratoRepository.findVigenteByPersonal(ID_PERSONAL)).thenReturn(Optional.empty());
        when(contratoMapper.toEntity(any(), any(), any(), any(), any(), any())).thenReturn(contrato);
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.crearContrato(request);

        assertThat(result).isNotNull();
        assertThat(result.idContrato()).isEqualTo(ID_CONTRATO);
        verify(contratoRepository).save(any(Contrato.class));
    }

    @Test
    @DisplayName("crearContrato: debe lanzar excepción cuando el personal no existe")
    void testCrearContrato_PersonalNoExiste() {
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.crearContrato(request))
                .isInstanceOf(PersonalNotFoundException.class);
    }

    @Test
    @DisplayName("crearContrato: debe lanzar excepción cuando el club no existe")
    void testCrearContrato_ClubNoExiste() {
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.of(personal));
        when(clubRepository.findById(ID_CLUB)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.crearContrato(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("crearContrato: debe lanzar excepción cuando el personal ya tiene contrato vigente")
    void testCrearContrato_ContratoVigenteExistente() {
        when(personalRepository.findById(ID_PERSONAL)).thenReturn(Optional.of(personal));
        when(clubRepository.findById(ID_CLUB)).thenReturn(Optional.of(club));
        when(contratoRepository.findVigenteByPersonal(ID_PERSONAL)).thenReturn(Optional.of(contrato));

        assertThatThrownBy(() -> contratoService.crearContrato(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("obtenerContratoPorId: debe retornar el contrato cuando existe")
    void testObtenerContratoPorId() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.obtenerContratoPorId(ID_CONTRATO);

        assertThat(result).isNotNull();
        assertThat(result.idContrato()).isEqualTo(ID_CONTRATO);
        verify(contratoRepository).findById(ID_CONTRATO);
    }

    @Test
    @DisplayName("obtenerContratoPorId: debe lanzar excepción cuando no existe")
    void testObtenerContratoPorId_NoExiste() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.obtenerContratoPorId(ID_CONTRATO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("obtenerContratosPorPersonal: debe retornar todos los contratos de un personal")
    void testObtenerContratosPorPersonal() {
        when(contratoRepository.findByPersonal(ID_PERSONAL)).thenReturn(List.of(contrato));
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        List<ContratoResponse> result = contratoService.obtenerContratosPorPersonal(ID_PERSONAL);

        assertThat(result).hasSize(1);
        verify(contratoRepository).findByPersonal(ID_PERSONAL);
    }

    @Test
    @DisplayName("obtenerContratoVigenteDePersonal: debe retornar el contrato vigente")
    void testObtenerContratoVigenteDePersonal() {
        when(contratoRepository.findVigenteByPersonal(ID_PERSONAL)).thenReturn(Optional.of(contrato));
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.obtenerContratoVigenteDePersonal(ID_PERSONAL);

        assertThat(result).isNotNull();
        verify(contratoRepository).findVigenteByPersonal(ID_PERSONAL);
    }

    @Test
    @DisplayName("obtenerContratoVigenteDePersonal: debe lanzar excepción cuando no hay contrato vigente")
    void testObtenerContratoVigenteDePersonal_NoVigente() {
        when(contratoRepository.findVigenteByPersonal(ID_PERSONAL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.obtenerContratoVigenteDePersonal(ID_PERSONAL))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("obtenerContratosVigentesPorClub: debe retornar contratos vigentes de un club")
    void testObtenerContratosVigentesPorEquipo() {
        when(contratoRepository.findVigentesByEquipo(ID_CLUB)).thenReturn(List.of(contrato));
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        List<ContratoResponse> result = contratoService.obtenerContratosVigentesPorEquipo(ID_CLUB);

        assertThat(result).hasSize(1);
        verify(contratoRepository).findVigentesByEquipo(ID_CLUB);
    }

    @Test
    @DisplayName("renovarContrato: debe renovar el contrato exitosamente")
    void testRenovarContrato() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(contratoMapper.toResponse(contrato)).thenReturn(response);

        ContratoResponse result = contratoService.renovarContrato(ID_CONTRATO, 12);

        assertThat(result).isNotNull();
        verify(contratoRepository).save(contrato);
    }

    @Test
    @DisplayName("renovarContrato: debe lanzar excepción cuando el contrato no existe")
    void testRenovarContrato_NoExiste() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.renovarContrato(ID_CONTRATO, 12))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("finalizarContrato: debe finalizar el contrato exitosamente")
    void testFinalizarContrato() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);

        contratoService.finalizarContrato(ID_CONTRATO);

        verify(contratoRepository).save(contrato);
    }

    @Test
    @DisplayName("finalizarContrato: debe lanzar excepción cuando el contrato no existe")
    void testFinalizarContrato_NoExiste() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.finalizarContrato(ID_CONTRATO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rescindirContrato: debe rescindir el contrato exitosamente")
    void testRescindirContrato() {
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);

        contratoService.rescindirContrato(ID_CONTRATO);

        verify(contratoRepository).save(contrato);
    }

    @Test
    @DisplayName("rescindirContrato: debe lanzar excepción cuando el contrato ya está finalizado")
    void testRescindirContrato_YaFinalizado() {
        contrato.setEstado(EstadoContrato.FINALIZADO);
        when(contratoRepository.findById(ID_CONTRATO)).thenReturn(Optional.of(contrato));

        assertThatThrownBy(() -> contratoService.rescindirContrato(ID_CONTRATO))
                .isInstanceOf(IllegalStateException.class);
    }
}