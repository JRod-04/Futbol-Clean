package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.CrearArbitroRequest;
import com.futbol.estadisticas.application.port.dto.response.ArbitroResponse;
import com.futbol.estadisticas.application.port.in.ArbitroUseCase;
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
class ArbitroControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ArbitroUseCase arbitroUseCase;

    @InjectMocks
    private ArbitroController arbitroController;

    private UUID idArbitro;
    private ArbitroResponse response;
    private CrearArbitroRequest crearRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(arbitroController).build();

        idArbitro = UUID.randomUUID();

        response = ArbitroResponse.builder()
                .idArbitro(idArbitro)
                .nombre("Juan")
                .apellido("Martínez")
                .fechaNacimiento(LocalDate.of(1980, 5, 15))
                .build();

        crearRequest = new CrearArbitroRequest(
                "Juan",
                "Martínez",
                LocalDate.of(1980, 5, 15)
        );
    }

    @Test
    @DisplayName("POST /apifutbol/arbitros - debe crear un árbitro")
    void testCrear() throws Exception {
        when(arbitroUseCase.crearArbitro(any(CrearArbitroRequest.class))).thenReturn(response);

        mockMvc.perform(post("/apifutbol/arbitros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idArbitro").value(idArbitro.toString()))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(arbitroUseCase).crearArbitro(any(CrearArbitroRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/arbitros - debe listar todos los árbitros")
    void testListarTodos() throws Exception {
        when(arbitroUseCase.obtenerTodosLosArbitros()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/arbitros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idArbitro").value(idArbitro.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));

        verify(arbitroUseCase).obtenerTodosLosArbitros();
    }

    @Test
    @DisplayName("GET /apifutbol/arbitros/{id} - debe obtener árbitro por ID")
    void testObtenerPorId() throws Exception {
        when(arbitroUseCase.obtenerArbitroPorId(idArbitro)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/arbitros/{id}", idArbitro))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idArbitro").value(idArbitro.toString()))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(arbitroUseCase).obtenerArbitroPorId(idArbitro);
    }

    @Test
    @DisplayName("GET /apifutbol/arbitros/buscar - debe buscar árbitros por nombre")
    void testBuscarPorNombre() throws Exception {
        String termino = "Juan";
        when(arbitroUseCase.buscarArbitrosPorNombre(termino)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/arbitros/buscar")
                        .param("termino", termino))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));

        verify(arbitroUseCase).buscarArbitrosPorNombre(termino);
    }

    @Test
    @DisplayName("DELETE /apifutbol/arbitros/{id} - debe eliminar un árbitro")
    void testEliminar() throws Exception {
        doNothing().when(arbitroUseCase).eliminarArbitro(idArbitro);

        mockMvc.perform(delete("/apifutbol/arbitros/{id}", idArbitro))
                .andExpect(status().isNoContent());

        verify(arbitroUseCase).eliminarArbitro(idArbitro);
    }
}