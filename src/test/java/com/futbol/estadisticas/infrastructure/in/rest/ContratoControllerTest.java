package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.CrearContratoRequest;
import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import com.futbol.estadisticas.application.port.in.ContratoUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ContratoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ContratoUseCase contratoUseCase;

    @InjectMocks
    private ContratoController contratoController;

    private UUID idContrato;
    private UUID idPersonal;
    private UUID idClub;
    private ContratoResponse response;
    private CrearContratoRequest crearRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(contratoController).build();

        idContrato = UUID.randomUUID();
        idPersonal = UUID.randomUUID();
        idClub = UUID.randomUUID();

        response = ContratoResponse.builder()
                .idContrato(idContrato)
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusMonths(12))
                .sueldo(5000000.0)
                .estado(EstadoContrato.ACTIVO)
                .build();

        crearRequest = new CrearContratoRequest(
                idPersonal,
                idClub,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(12),
                5000000.0
        );
    }

    @Test
    @DisplayName("POST /apifutbol/contratos - debe crear un contrato")
    void testCrear() throws Exception {
        when(contratoUseCase.crearContrato(any(CrearContratoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/apifutbol/contratos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idContrato").value(idContrato.toString()))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(contratoUseCase).crearContrato(any(CrearContratoRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/contratos/{id} - debe obtener contrato por ID")
    void testObtenerPorId() throws Exception {
        when(contratoUseCase.obtenerContratoPorId(idContrato)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/contratos/{id}", idContrato))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idContrato").value(idContrato.toString()))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(contratoUseCase).obtenerContratoPorId(idContrato);
    }

    @Test
    @DisplayName("GET /apifutbol/contratos/personal/{idPersonal} - debe listar contratos por personal")
    void testPorPersonal() throws Exception {
        when(contratoUseCase.obtenerContratosPorPersonal(idPersonal)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/contratos/personal/{idPersonal}", idPersonal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idContrato").value(idContrato.toString()));

        verify(contratoUseCase).obtenerContratosPorPersonal(idPersonal);
    }

    @Test
    @DisplayName("GET /apifutbol/contratos/personal/{idPersonal}/vigente - debe obtener contrato vigente")
    void testVigenteDePersonal() throws Exception {
        when(contratoUseCase.obtenerContratoVigenteDePersonal(idPersonal)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/contratos/personal/{idPersonal}/vigente", idPersonal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idContrato").value(idContrato.toString()));

        verify(contratoUseCase).obtenerContratoVigenteDePersonal(idPersonal);
    }

    @Test
    @DisplayName("GET /apifutbol/contratos/club/{idClub}/vigentes - debe listar contratos vigentes por club")
    void testVigentesPorClub() throws Exception {
        when(contratoUseCase.obtenerContratosVigentesPorClub(idClub)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/contratos/club/{idClub}/vigentes", idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idContrato").value(idContrato.toString()));

        verify(contratoUseCase).obtenerContratosVigentesPorClub(idClub);
    }

    @Test
    @DisplayName("PATCH /apifutbol/contratos/{id}/renovar - debe renovar un contrato")
    void testRenovar() throws Exception {
        int meses = 12;
        when(contratoUseCase.renovarContrato(idContrato, meses)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/contratos/{id}/renovar", idContrato)
                        .param("meses", String.valueOf(meses)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idContrato").value(idContrato.toString()));

        verify(contratoUseCase).renovarContrato(idContrato, meses);
    }

    @Test
    @DisplayName("PATCH /apifutbol/contratos/{id}/finalizar - debe finalizar un contrato")
    void testFinalizar() throws Exception {
        doNothing().when(contratoUseCase).finalizarContrato(idContrato);

        mockMvc.perform(patch("/apifutbol/contratos/{id}/finalizar", idContrato))
                .andExpect(status().isNoContent());

        verify(contratoUseCase).finalizarContrato(idContrato);
    }

    @Test
    @DisplayName("PATCH /apifutbol/contratos/{id}/rescindir - debe rescindir un contrato")
    void testRescindir() throws Exception {
        doNothing().when(contratoUseCase).rescindirContrato(idContrato);

        mockMvc.perform(patch("/apifutbol/contratos/{id}/rescindir", idContrato))
                .andExpect(status().isNoContent());

        verify(contratoUseCase).rescindirContrato(idContrato);
    }
}