package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.RegistrarLesionRequest;
import com.futbol.estadisticas.application.port.dto.response.LesionResponse;
import com.futbol.estadisticas.application.port.in.LesionUseCase;
import com.futbol.estadisticas.domain.model.enums.Gravedad;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LesionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LesionUseCase lesionUseCase;

    @InjectMocks
    private LesionController lesionController;

    private UUID idLesion;
    private UUID idJugador;
    private LesionResponse response;
    private RegistrarLesionRequest registrarRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(lesionController).build();

        idLesion = UUID.randomUUID();
        idJugador = UUID.randomUUID();

        response = LesionResponse.builder()
                .idLesion(idLesion)
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFin(LocalDate.now().plusDays(5))
                .curada(false)
                .idJugador(idJugador)
                .nombreJugador("Lionel Messi")
                .build();

        registrarRequest = RegistrarLesionRequest.builder()
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFinEstimada(LocalDate.now().plusDays(5))
                .build();
    }

    @Test
    @DisplayName("POST /apifutbol/lesiones/jugadores/{idJugador}/lesiones - debe registrar una lesión")
    void testRegistrar() throws Exception {
        when(lesionUseCase.registrarLesion(eq(idJugador), any(RegistrarLesionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/apifutbol/lesiones/jugadores/{idJugador}/lesiones", idJugador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrarRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idLesion").value(idLesion.toString()))
                .andExpect(jsonPath("$.nombreLesion").value("Lesión de tobillo"));

        verify(lesionUseCase).registrarLesion(eq(idJugador), any(RegistrarLesionRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/lesiones/jugadores/{idJugador}/lesiones - debe listar lesiones por jugador")
    void testPorJugador() throws Exception {
        when(lesionUseCase.obtenerLesionesPorJugador(idJugador)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/lesiones/jugadores/{idJugador}/lesiones", idJugador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idLesion").value(idLesion.toString()))
                .andExpect(jsonPath("$[0].nombreLesion").value("Lesión de tobillo"));

        verify(lesionUseCase).obtenerLesionesPorJugador(idJugador);
    }

    @Test
    @DisplayName("GET /apifutbol/lesiones/jugadores/{idJugador}/lesiones/activas - debe listar lesiones activas por jugador")
    void testActivasPorJugador() throws Exception {
        when(lesionUseCase.obtenerLesionesActivasPorJugador(idJugador)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/lesiones/jugadores/{idJugador}/lesiones/activas", idJugador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].curada").value(false));

        verify(lesionUseCase).obtenerLesionesActivasPorJugador(idJugador);
    }

    @Test
    @DisplayName("GET /apifutbol/lesiones/lesiones/{id} - debe obtener lesión por ID")
    void testObtenerPorId() throws Exception {
        when(lesionUseCase.obtenerLesionPorId(idLesion)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/lesiones/lesiones/{id}", idLesion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLesion").value(idLesion.toString()))
                .andExpect(jsonPath("$.nombreLesion").value("Lesión de tobillo"));

        verify(lesionUseCase).obtenerLesionPorId(idLesion);
    }

    @Test
    @DisplayName("GET /apifutbol/lesiones/lesiones/activas - debe listar todas las lesiones activas")
    void testTodasLasActivas() throws Exception {
        when(lesionUseCase.obtenerLesionesActivasEnSistema()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/lesiones/lesiones/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].curada").value(false));

        verify(lesionUseCase).obtenerLesionesActivasEnSistema();
    }

    @Test
    @DisplayName("GET /apifutbol/lesiones/lesiones/gravedad/{gravedad} - debe listar lesiones por gravedad")
    void testPorGravedad() throws Exception {
        when(lesionUseCase.obtenerLesionesPorGravedad(Gravedad.MODERADA)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/lesiones/lesiones/gravedad/{gravedad}", Gravedad.MODERADA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gravedad").value("MODERADA"));

        verify(lesionUseCase).obtenerLesionesPorGravedad(Gravedad.MODERADA);
    }

    @Test
    @DisplayName("PATCH /apifutbol/lesiones/lesiones/{id}/curar - debe curar una lesión")
    void testCurar() throws Exception {
        LesionResponse responseCurada = LesionResponse.builder()
                .idLesion(idLesion)
                .nombreLesion("Lesión de tobillo")
                .gravedad(Gravedad.MODERADA)
                .fechaInicio(LocalDate.now().minusDays(10))
                .fechaFin(LocalDate.now().plusDays(5))
                .curada(true)
                .idJugador(idJugador)
                .nombreJugador("Lionel Messi")
                .build();

        when(lesionUseCase.curarLesion(idLesion)).thenReturn(responseCurada);

        mockMvc.perform(patch("/apifutbol/lesiones/lesiones/{id}/curar", idLesion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.curada").value(true));

        verify(lesionUseCase).curarLesion(idLesion);
    }
}