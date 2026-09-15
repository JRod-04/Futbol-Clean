package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.request.CrearArbitroRequest;
import com.futbol.estadisticas.application.port.dto.response.ArbitroResponse;
import com.futbol.estadisticas.application.port.in.ArbitroUseCase;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
import com.futbol.estadisticas.domain.model.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ArbitroController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ArbitroControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ArbitroUseCase arbitroUseCase;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ID = UUID.randomUUID();

    private ArbitroResponse response() {
        return ArbitroResponse.builder()
                .idArbitro(ID)
                .nombre("Michael")
                .apellido("Oliver")
                .build();
    }

    private CrearArbitroRequest crearRequest() {
        return CrearArbitroRequest.builder()
                .nombre("Michael")
                .apellido("Oliver")
                .fechaNacimiento(LocalDate.of(1985, 2, 20))
                .build();
    }

    @Nested
    @DisplayName("POST /apifutbol/arbitros")
    class Crear {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(arbitroUseCase.crearArbitro(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/arbitros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idArbitro").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/arbitros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/arbitros")
    class Listar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(arbitroUseCase.obtenerTodosLosArbitros()).thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/arbitros"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idArbitro").value(ID.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/arbitros/{id}")
    class PorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(arbitroUseCase.obtenerArbitroPorId(ID)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/arbitros/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(arbitroUseCase.obtenerArbitroPorId(ID))
                    .thenThrow(new ResourceNotFoundException("Árbitro no encontrado"));

            mockMvc.perform(get("/apifutbol/arbitros/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/arbitros/buscar")
    class Buscar {

        @Test
        @DisplayName("devuelve 200 con la lista filtrada")
        void ok() throws Exception {
            when(arbitroUseCase.buscarArbitrosPorNombre("Oliver"))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/arbitros/buscar")
                            .param("termino", "Oliver"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].apellido").value("Oliver"));
        }
    }

    @Nested
    @DisplayName("DELETE /apifutbol/arbitros/{id}")
    class Eliminar {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(arbitroUseCase).eliminarArbitro(ID);

            mockMvc.perform(delete("/apifutbol/arbitros/{id}", ID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            doThrow(new ResourceNotFoundException("Árbitro no encontrado"))
                    .when(arbitroUseCase).eliminarArbitro(ID);

            mockMvc.perform(delete("/apifutbol/arbitros/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }
}