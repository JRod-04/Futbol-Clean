package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.request.AlineacionRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearEquipoRequest;
import com.futbol.estadisticas.application.port.dto.response.AlineacionResponse;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.EquipoUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoCompeticion;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {EquipoController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class EquipoControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private EquipoUseCase equipoUseCase;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ID = UUID.randomUUID();
    private static final UUID ID_PARTIDO = UUID.randomUUID();

    // ─── Helpers estáticos ───────────────────────────────────────────────

    private static EquipoResponse response() {
        return EquipoResponse.builder()
                .idEquipo(ID)
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .build();
    }

    private static CrearEquipoRequest crearRequest() {
        return CrearEquipoRequest.builder()
                .nombre("Arsenal FC")
                .nombreCorto("ARS")
                .build();
    }

    private static AlineacionRequest alineacionRequest() {
        return AlineacionRequest.builder()
                .idPartido(ID_PARTIDO)
                .alineacion(com.futbol.estadisticas.domain.model.enums.Alineacion.ALINEACION_433)
                .portero(UUID.randomUUID())
                .lateralIzquierdo(UUID.randomUUID())
                .lateralDerecho(UUID.randomUUID())
                .centralIzquierdo(UUID.randomUUID())
                .centralDerecho(UUID.randomUUID())
                .centroCampistaIzquierdo(UUID.randomUUID())
                .centroCampistaDerecho(UUID.randomUUID())
                .centroCampista(UUID.randomUUID())
                .extremoIzquierdo(UUID.randomUUID())
                .extremoDerecho(UUID.randomUUID())
                .delanteroCentro(UUID.randomUUID())
                .build();
    }

    // ─── Tests ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /apifutbol/equipos")
    class Crear {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(equipoUseCase.crearEquipo(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/equipos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idEquipo").value(ID.toString()))
                    .andExpect(jsonPath("$.nombre").value("Arsenal FC"))
                    .andExpect(jsonPath("$.nombreCorto").value("ARS"));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/equipos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/equipos")
    class Listar {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(equipoUseCase.obtenerTodosLosEquipos()).thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/equipos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idEquipo").value(ID.toString()))
                    .andExpect(jsonPath("$[0].nombre").value("Arsenal FC"));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/equipos/{id}")
    class PorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(equipoUseCase.obtenerEquipoPorId(ID)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/equipos/{id}", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idEquipo").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(equipoUseCase.obtenerEquipoPorId(ID))
                    .thenThrow(new ResourceNotFoundException(
                            "Equipo no encontrado con id: " + ID));

            mockMvc.perform(get("/apifutbol/equipos/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/equipos/{id}/alineacion")
    class EstablecerAlineacion {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            AlineacionResponse resp = AlineacionResponse.builder().build();
            when(equipoUseCase.establecerAlineacionTitular(eq(ID), any()))
                    .thenReturn(resp);

            mockMvc.perform(patch("/apifutbol/equipos/{id}/alineacion", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(alineacionRequest())))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(patch("/apifutbol/equipos/{id}/alineacion", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 404 si el equipo no existe")
        void equipoNoExiste() throws Exception {
            when(equipoUseCase.establecerAlineacionTitular(eq(ID), any()))
                    .thenThrow(new ResourceNotFoundException(
                            "Equipo no encontrado con id: " + ID));

            mockMvc.perform(patch("/apifutbol/equipos/{id}/alineacion", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(alineacionRequest())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET jugadores")
    class Jugadores {

        @Test
        @DisplayName("activos 200")
        void activos() throws Exception {
            JugadorResponse j = JugadorResponse.builder()
                    .idPersonal(UUID.randomUUID())
                    .nombre("Bukayo")
                    .build();
            when(equipoUseCase.obtenerJugadoresActivosDeEquipo(ID))
                    .thenReturn(List.of(j));

            mockMvc.perform(get("/apifutbol/equipos/{id}/jugadores", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("Bukayo"));
        }

        @Test
        @DisplayName("disponibles 200")
        void disponibles() throws Exception {
            when(equipoUseCase.obtenerJugadoresDisponiblesDeEquipo(ID))
                    .thenReturn(List.of());

            mockMvc.perform(get("/apifutbol/equipos/{id}/disponibles", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("titulares 200")
        void titulares() throws Exception {
            when(equipoUseCase.obtenerTitulares(ID)).thenReturn(List.of());

            mockMvc.perform(get("/apifutbol/equipos/{id}/jugadores/titulares", ID))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/equipos/{idEquipo}/competiciones")
    class Competiciones {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            CompeticionResponse comp = CompeticionResponse.builder()
                    .idCompeticion(UUID.randomUUID())
                    .nombre("Premier League")
                    .estado(EstadoCompeticion.EN_CURSO)
                    .build();
            when(equipoUseCase.obtenerCompeticionesPorEquipo(ID))
                    .thenReturn(List.of(comp));

            mockMvc.perform(get("/apifutbol/equipos/{id}/competiciones", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("Premier League"));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/equipos/{id}/valor-plantilla")
    class ValorPlantilla {

        @Test
        @DisplayName("devuelve 200 con el valor")
        void ok() throws Exception {
            when(equipoUseCase.obtenerValorPlantilla(ID)).thenReturn(100_000_000.0);

            mockMvc.perform(get("/apifutbol/equipos/{id}/valor-plantilla", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(100_000_000.0));
        }
    }

    @Nested
    @DisplayName("DELETE /apifutbol/equipos/{id}")
    class Eliminar {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(equipoUseCase).eliminarEquipo(ID);

            mockMvc.perform(delete("/apifutbol/equipos/{id}", ID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            doThrow(new ResourceNotFoundException(
                    "Equipo no encontrado con id: " + ID))
                    .when(equipoUseCase).eliminarEquipo(ID);

            mockMvc.perform(delete("/apifutbol/equipos/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }
}