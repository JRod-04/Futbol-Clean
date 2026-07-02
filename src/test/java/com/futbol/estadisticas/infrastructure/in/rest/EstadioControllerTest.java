package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.ActualizarEstadioRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearEstadioRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadioResponse;
import com.futbol.estadisticas.application.port.in.EstadioUseCase;
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
class EstadioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EstadioUseCase estadioUseCase;

    @InjectMocks
    private EstadioController estadioController;

    private UUID idEstadio;
    private UUID idClub;
    private EstadioResponse response;
    private CrearEstadioRequest crearRequest;
    private ActualizarEstadioRequest actualizarRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(estadioController).build();

        idEstadio = UUID.randomUUID();
        idClub = UUID.randomUUID();

        response = EstadioResponse.builder()
                .idEstadio(idEstadio)
                .nombre("Camp Nou")
                .direccion("C/ Arístides Maillol, 12")
                .capacidad(99354)
                .fechaFundacion(LocalDate.of(1957, 9, 24))
                .descripcionCompleta("Camp Nou - Capacidad: 99354 - Fundado: 1957")
                .clubPrincipal("FC Barcelona")
                .idClubPrincipal(idClub)
                .build();

        crearRequest = new CrearEstadioRequest(
                "Camp Nou",
                "C/ Arístides Maillol, 12",
                99354,
                LocalDate.of(1957, 9, 24)
        );

        actualizarRequest = new ActualizarEstadioRequest(
                "Camp Nou Actualizado",
                "Calle Nueva 123",
                105000
        );
    }

    @Test
    @DisplayName("POST /apifutbol/estadios - debe crear un estadio")
    void testCrear() throws Exception {
        when(estadioUseCase.crearEstadio(any(CrearEstadioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/apifutbol/estadios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEstadio").value(idEstadio.toString()))
                .andExpect(jsonPath("$.nombre").value("Camp Nou"));

        verify(estadioUseCase).crearEstadio(any(CrearEstadioRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/estadios - debe listar todos los estadios")
    void testListarTodos() throws Exception {
        when(estadioUseCase.obtenerTodosLosEstadios()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/estadios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEstadio").value(idEstadio.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Camp Nou"));

        verify(estadioUseCase).obtenerTodosLosEstadios();
    }

    @Test
    @DisplayName("GET /apifutbol/estadios/{id} - debe obtener estadio por ID")
    void testObtenerPorId() throws Exception {
        when(estadioUseCase.obtenerEstadioPorId(idEstadio)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/estadios/{id}", idEstadio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstadio").value(idEstadio.toString()))
                .andExpect(jsonPath("$.nombre").value("Camp Nou"));

        verify(estadioUseCase).obtenerEstadioPorId(idEstadio);
    }

    @Test
    @DisplayName("PATCH /apifutbol/estadios/{id} - debe actualizar un estadio")
    void testActualizar() throws Exception {
        when(estadioUseCase.actualizarEstadio(eq(idEstadio), any(ActualizarEstadioRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/estadios/{id}", idEstadio)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstadio").value(idEstadio.toString()))
                .andExpect(jsonPath("$.nombre").value("Camp Nou"));

        verify(estadioUseCase).actualizarEstadio(eq(idEstadio), any(ActualizarEstadioRequest.class));
    }

    @Test
    @DisplayName("PUT /apifutbol/estadios/{id}/asignar-club/{idClub} - debe asignar estadio a un club")
    void testAsignarAClub() throws Exception {
        when(estadioUseCase.asignarEstadioAClub(idEstadio, idClub)).thenReturn(response);

        mockMvc.perform(put("/apifutbol/estadios/{id}/asignar-club/{idClub}", idEstadio, idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstadio").value(idEstadio.toString()))
                .andExpect(jsonPath("$.clubPrincipal").value("FC Barcelona"))
                .andExpect(jsonPath("$.idClubPrincipal").value(idClub.toString()));

        verify(estadioUseCase).asignarEstadioAClub(idEstadio, idClub);
    }

    @Test
    @DisplayName("GET /apifutbol/estadios/{id}/ocupacion - debe calcular porcentaje de ocupación")
    void testCalcularOcupacion() throws Exception {
        Integer espectadores = 50000;
        Double ocupacion = 50.33;
        when(estadioUseCase.calcularPorcentajeOcupacion(idEstadio, espectadores)).thenReturn(ocupacion);

        mockMvc.perform(get("/apifutbol/estadios/{id}/ocupacion", idEstadio)
                        .param("espectadores", String.valueOf(espectadores)))
                .andExpect(status().isOk())
                .andExpect(content().string("50.33"));

        verify(estadioUseCase).calcularPorcentajeOcupacion(idEstadio, espectadores);
    }

    @Test
    @DisplayName("DELETE /apifutbol/estadios/{id} - debe eliminar un estadio")
    void testEliminar() throws Exception {
        doNothing().when(estadioUseCase).eliminarEstadio(idEstadio);

        mockMvc.perform(delete("/apifutbol/estadios/{id}", idEstadio))
                .andExpect(status().isNoContent());

        verify(estadioUseCase).eliminarEstadio(idEstadio);
    }
}