package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.in.CompeticionUseCase;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CompeticionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CompeticionUseCase competicionUseCase;

    @InjectMocks
    private CompeticionController competicionController;

    private UUID idCompeticion;
    private CompeticionResponse response;
    private CrearCompeticionRequest crearRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(competicionController).build();

        idCompeticion = UUID.randomUUID();

        response = CompeticionResponse.builder()
                .idCompeticion(idCompeticion)
                .nombre("La Liga")
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .build();

        crearRequest = new CrearCompeticionRequest(
                "La Liga",
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(6)
        );
    }

    @Test
    @DisplayName("POST /apifutbol/competiciones - debe crear una competición")
    void testCrear() throws Exception {
        when(competicionUseCase.crearCompeticion(any(CrearCompeticionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/apifutbol/competiciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCompeticion").value(idCompeticion.toString()))
                .andExpect(jsonPath("$.nombre").value("La Liga"));

        verify(competicionUseCase).crearCompeticion(any(CrearCompeticionRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/competiciones - debe listar todas las competiciones")
    void testListarTodas() throws Exception {
        when(competicionUseCase.obtenerTodasLasCompeticiones()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/competiciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCompeticion").value(idCompeticion.toString()))
                .andExpect(jsonPath("$[0].nombre").value("La Liga"));

        verify(competicionUseCase).obtenerTodasLasCompeticiones();
    }

    @Test
    @DisplayName("GET /apifutbol/competiciones/activas - debe listar competiciones activas")
    void testActivas() throws Exception {
        when(competicionUseCase.obtenerCompeticionesActivas()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/competiciones/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("La Liga"));

        verify(competicionUseCase).obtenerCompeticionesActivas();
    }

    @Test
    @DisplayName("GET /apifutbol/competiciones/{id} - debe obtener competición por ID")
    void testObtenerPorId() throws Exception {
        when(competicionUseCase.obtenerCompeticionPorId(idCompeticion)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/competiciones/{id}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompeticion").value(idCompeticion.toString()))
                .andExpect(jsonPath("$.nombre").value("La Liga"));

        verify(competicionUseCase).obtenerCompeticionPorId(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/competiciones/{id}/partidos - debe listar partidos de la competición")
    void testPartidos() throws Exception {
        PartidoResponse partidoResponse = PartidoResponse.builder()
                .idPartido(UUID.randomUUID())
                .nombreCompeticion("La Liga")
                .build();

        when(competicionUseCase.obtenerPartidosPorCompeticion(idCompeticion)).thenReturn(List.of(partidoResponse));

        mockMvc.perform(get("/apifutbol/competiciones/{id}/partidos", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCompeticion").value("La Liga"));

        verify(competicionUseCase).obtenerPartidosPorCompeticion(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/competiciones/{id}/partidos/pendientes - debe listar partidos pendientes")
    void testPendientes() throws Exception {
        PartidoResponse partidoResponse = PartidoResponse.builder()
                .idPartido(UUID.randomUUID())
                .nombreCompeticion("La Liga")
                .build();

        when(competicionUseCase.obtenerPartidosPendientesPorCompeticion(idCompeticion)).thenReturn(List.of(partidoResponse));

        mockMvc.perform(get("/apifutbol/competiciones/{id}/partidos/pendientes", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCompeticion").value("La Liga"));

        verify(competicionUseCase).obtenerPartidosPendientesPorCompeticion(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/competiciones/{id}/avance - debe obtener porcentaje de avance")
    void testPorcentajeAvance() throws Exception {
        Double avance = 50.0;
        when(competicionUseCase.obtenerPorcentajeAvance(idCompeticion)).thenReturn(avance);

        mockMvc.perform(get("/apifutbol/competiciones/{id}/avance", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(content().string("50.0"));

        verify(competicionUseCase).obtenerPorcentajeAvance(idCompeticion);
    }

    @Test
    @DisplayName("DELETE /apifutbol/competiciones/{id} - debe eliminar una competición")
    void testEliminar() throws Exception {
        doNothing().when(competicionUseCase).eliminarCompeticion(idCompeticion);

        mockMvc.perform(delete("/apifutbol/competiciones/{id}", idCompeticion))
                .andExpect(status().isNoContent());

        verify(competicionUseCase).eliminarCompeticion(idCompeticion);
    }
}