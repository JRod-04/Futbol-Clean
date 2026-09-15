package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.request.ActualizarEstadioRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearEstadioRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadioResponse;
import com.futbol.estadisticas.application.port.in.EstadioUseCase;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.DeserializationFeature;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {EstadioController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class EstadioControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JsonMapper jsonMapper;
    @MockitoBean private EstadioUseCase estadioUseCase;

    private static final UUID ID_ESTADIO = UUID.randomUUID();
    private static final UUID ID_EQUIPO = UUID.randomUUID();


    @TestConfiguration
    static class JacksonConfig {
        @Bean
        @Primary
        public JsonMapper jsonMapper() {
            return JsonMapper.builder()
                    .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                    .build();
        }
    }


    private static EstadioResponse response() {
        return EstadioResponse.builder()
                .idEstadio(ID_ESTADIO)
                .nombre("Emirates Stadium")
                .direccion("Highbury, Londres")
                .capacidad(60704)
                .fechaFundacion(LocalDate.of(2006, 7, 22))
                .build();
    }

    private static CrearEstadioRequest crearRequest() {
        return CrearEstadioRequest.builder()
                .nombre("Emirates Stadium")
                .direccion("Highbury, Londres")
                .capacidad(60704)
                .fechaFundacion(LocalDate.of(2006, 7, 22))
                .build();
    }

    private static ActualizarEstadioRequest actualizarRequest() {
        return ActualizarEstadioRequest.builder()
                .nombre("Emirates Stadium Renovado")
                .capacidad(65000)
                .build();
    }


    @Nested
    @DisplayName("POST /apifutbol/estadios")
    class Crear {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(estadioUseCase.crearEstadio(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/estadios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idEstadio").value(ID_ESTADIO.toString()))
                    .andExpect(jsonPath("$.nombre").value("Emirates Stadium"))
                    .andExpect(jsonPath("$.capacidad").value(60704));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/estadios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/estadios")
    class Listar {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(estadioUseCase.obtenerTodosLosEstadios()).thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/estadios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idEstadio").value(ID_ESTADIO.toString()))
                    .andExpect(jsonPath("$[0].nombre").value("Emirates Stadium"));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/estadios/{id}")
    class PorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(estadioUseCase.obtenerEstadioPorId(ID_ESTADIO)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/estadios/{id}", ID_ESTADIO))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idEstadio").value(ID_ESTADIO.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(estadioUseCase.obtenerEstadioPorId(ID_ESTADIO))
                    .thenThrow(new IllegalArgumentException(
                            "Estadio no encontrado con id: " + ID_ESTADIO));

            mockMvc.perform(get("/apifutbol/estadios/{id}", ID_ESTADIO))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/estadios/{id}")
    class Actualizar {

        @Test
        @DisplayName("devuelve 200 con body vacío (todos los campos opcionales)")
        void bodyVacioOk() throws Exception {
            when(estadioUseCase.actualizarEstadio(eq(ID_ESTADIO), any()))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/estadios/{id}", ID_ESTADIO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idEstadio").value(ID_ESTADIO.toString()));
        }

        @Test
        @DisplayName("devuelve 200 con campos válidos")
        void actualizacionParcialOk() throws Exception {
            when(estadioUseCase.actualizarEstadio(eq(ID_ESTADIO), any()))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/estadios/{id}", ID_ESTADIO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(actualizarRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idEstadio").value(ID_ESTADIO.toString()));
        }

        @Test
        @DisplayName("devuelve 400 si capacidad es un string")
        void capacidadStringBadRequest() throws Exception {
            mockMvc.perform(patch("/apifutbol/estadios/{id}", ID_ESTADIO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"capacidad\": \"no-es-un-numero\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 400 si capacidad es un double")
        void capacidadDoubleBadRequest() throws Exception {
            mockMvc.perform(patch("/apifutbol/estadios/{id}", ID_ESTADIO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"capacidad\": 50000.5}"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 404 si el estadio no existe")
        void noExiste() throws Exception {
            when(estadioUseCase.actualizarEstadio(eq(ID_ESTADIO), any()))
                    .thenThrow(new IllegalArgumentException(
                            "Estadio no encontrado con id: " + ID_ESTADIO));

            mockMvc.perform(patch("/apifutbol/estadios/{id}", ID_ESTADIO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(actualizarRequest())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /apifutbol/estadios/{id}/asignar-equipo/{idEquipo}")
    class AsignarAClub {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(estadioUseCase.asignarEstadioAEquipo(ID_ESTADIO, ID_EQUIPO))
                    .thenReturn(response());

            mockMvc.perform(put("/apifutbol/estadios/{id}/asignar-equipo/{idEquipo}",
                            ID_ESTADIO, ID_EQUIPO))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idEstadio").value(ID_ESTADIO.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si el estadio no existe")
        void estadioNoExiste() throws Exception {
            when(estadioUseCase.asignarEstadioAEquipo(ID_ESTADIO, ID_EQUIPO))
                    .thenThrow(new IllegalArgumentException(
                            "Estadio no encontrado con id: " + ID_ESTADIO));

            mockMvc.perform(put("/apifutbol/estadios/{id}/asignar-equipo/{idEquipo}",
                            ID_ESTADIO, ID_EQUIPO))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("devuelve 404 si el equipo no existe")
        void equipoNoExiste() throws Exception {
            when(estadioUseCase.asignarEstadioAEquipo(ID_ESTADIO, ID_EQUIPO))
                    .thenThrow(new IllegalArgumentException(
                            "Equipo no encontrado con id: " + ID_EQUIPO));

            mockMvc.perform(put("/apifutbol/estadios/{id}/asignar-equipo/{idEquipo}",
                            ID_ESTADIO, ID_EQUIPO))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/estadios/{id}/ocupacion")
    class CalcularOcupacion {

        @Test
        @DisplayName("devuelve 200 con el porcentaje")
        void ok() throws Exception {
            when(estadioUseCase.calcularPorcentajeOcupacion(ID_ESTADIO, 30352))
                    .thenReturn(50.0);

            mockMvc.perform(get("/apifutbol/estadios/{id}/ocupacion", ID_ESTADIO)
                            .param("espectadores", "30352"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(50.0));
        }

        @Test
        @DisplayName("devuelve 400 si falta el parámetro espectadores")
        void sinParametro() throws Exception {
            mockMvc.perform(get("/apifutbol/estadios/{id}/ocupacion", ID_ESTADIO))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 400 si espectadores no es un entero")
        void espectadoresNoEntero() throws Exception {
            mockMvc.perform(get("/apifutbol/estadios/{id}/ocupacion", ID_ESTADIO)
                            .param("espectadores", "muchos"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 404 si el estadio no existe")
        void noExiste() throws Exception {
            when(estadioUseCase.calcularPorcentajeOcupacion(ID_ESTADIO, 30352))
                    .thenThrow(new IllegalArgumentException(
                            "Estadio no encontrado con id: " + ID_ESTADIO));

            mockMvc.perform(get("/apifutbol/estadios/{id}/ocupacion", ID_ESTADIO)
                            .param("espectadores", "30352"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /apifutbol/estadios/{id}")
    class Eliminar {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(estadioUseCase).eliminarEstadio(ID_ESTADIO);

            mockMvc.perform(delete("/apifutbol/estadios/{id}", ID_ESTADIO))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            doThrow(new IllegalArgumentException(
                    "Estadio no encontrado con id: " + ID_ESTADIO))
                    .when(estadioUseCase).eliminarEstadio(ID_ESTADIO);

            mockMvc.perform(delete("/apifutbol/estadios/{id}", ID_ESTADIO))
                    .andExpect(status().isNotFound());
        }
    }
}