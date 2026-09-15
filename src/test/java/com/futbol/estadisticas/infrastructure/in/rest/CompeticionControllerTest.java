package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.request.ActualizarGanadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.in.CompeticionUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoCompeticion;
import com.futbol.estadisticas.domain.model.enums.Temporada;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
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

import java.time.LocalDateTime;
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

@WebMvcTest(controllers = {CompeticionController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class CompeticionControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private CompeticionUseCase competicionUseCase;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ID = UUID.randomUUID();

    // ─── Helpers estáticos ───────────────────────────────────────────────

    private static CompeticionResponse response() {
        return CompeticionResponse.builder()
                .idCompeticion(ID)
                .nombre("Premier League")
                .nombreCompleto("Premier League 2024-25")
                .estado(EstadoCompeticion.POR_INICIAR)
                .activa(false)
                .finalizada(false)
                .noHaComenzado(true)
                .totalPartidos(0)
                .partidosJugados(0)
                .partidosPendientes(0)
                .porcentajeAvance(0.0)
                .build();
    }

    private static CrearCompeticionRequest crearRequest() {
        return CrearCompeticionRequest.builder()
                .nombre("Premier League")
                .temporada(Temporada.T2024_25)
                .fechaInicio(LocalDateTime.now().plusDays(1))
                .fechaFin(LocalDateTime.now().plusMonths(6))
                .build();
    }

    // ─── Tests ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /apifutbol/competiciones")
    class Crear {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(competicionUseCase.crearCompeticion(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/competiciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idCompeticion").value(ID.toString()))
                    .andExpect(jsonPath("$.nombre").value("Premier League"));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/competiciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 400 si fechaFin es anterior a fechaInicio")
        void fechaInvalida() throws Exception {
            when(competicionUseCase.crearCompeticion(any()))
                    .thenThrow(new IllegalArgumentException(
                            "La fecha de fin no puede ser anterior a la fecha de inicio"));

            mockMvc.perform(post("/apifutbol/competiciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/competiciones")
    class Listar {

        @Test
        @DisplayName("todas devuelve 200")
        void todas() throws Exception {
            when(competicionUseCase.obtenerTodasLasCompeticiones())
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/competiciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idCompeticion").value(ID.toString()));
        }

        @Test
        @DisplayName("activas devuelve 200")
        void activas() throws Exception {
            when(competicionUseCase.obtenerCompeticionesActivas())
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/competiciones/activas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idCompeticion").value(ID.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/competiciones/{id}")
    class PorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(competicionUseCase.obtenerCompeticionPorId(ID)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/competiciones/{id}", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idCompeticion").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(competicionUseCase.obtenerCompeticionPorId(ID))
                    .thenThrow(new IllegalArgumentException(
                            "Competición no encontrada con id: " + ID));

            mockMvc.perform(get("/apifutbol/competiciones/{id}", ID))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/competiciones/{id}/equipos")
    class Equipos {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            EquipoResponse equipo = EquipoResponse.builder()
                    .idEquipo(UUID.randomUUID())
                    .nombre("Arsenal")
                    .build();
            when(competicionUseCase.obtenerEquiposParticipantes(ID))
                    .thenReturn(List.of(equipo));

            mockMvc.perform(get("/apifutbol/competiciones/{id}/equipos", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("Arsenal"));
        }

        @Test
        @DisplayName("devuelve 409 si no tiene partidos")
        void sinPartidos() throws Exception {
            when(competicionUseCase.obtenerEquiposParticipantes(ID))
                    .thenThrow(new IllegalStateException(
                            "La competición no tiene partidos registrados"));

            mockMvc.perform(get("/apifutbol/competiciones/{id}/equipos", ID))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/competiciones/{id}/partidos")
    class Partidos {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            PartidoResponse partido = PartidoResponse.builder()
                    .idPartido(UUID.randomUUID())
                    .build();
            when(competicionUseCase.obtenerPartidosPorCompeticion(ID))
                    .thenReturn(List.of(partido));

            mockMvc.perform(get("/apifutbol/competiciones/{id}/partidos", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idPartido").value(partido.idPartido().toString()));
        }

        @Test
        @DisplayName("pendientes devuelve 200")
        void pendientes() throws Exception {
            when(competicionUseCase.obtenerPartidosPendientesPorCompeticion(ID))
                    .thenReturn(List.of());

            mockMvc.perform(get("/apifutbol/competiciones/{id}/partidos/pendientes", ID))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/competiciones/{id}/avance")
    class Avance {

        @Test
        @DisplayName("devuelve 200 con el porcentaje")
        void ok() throws Exception {
            when(competicionUseCase.obtenerPorcentajeAvance(ID)).thenReturn(75.0);

            mockMvc.perform(get("/apifutbol/competiciones/{id}/avance", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(75.0));
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/competiciones/{id}/estado")
    class CambiosEstado {

        @Test
        @DisplayName("iniciar 200")
        void iniciar() throws Exception {
            when(competicionUseCase.iniciarCompeticion(ID)).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/competiciones/{id}/iniciar", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("finalizar 200")
        void finalizar() throws Exception {
            when(competicionUseCase.finalizarCompeticion(ID)).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/competiciones/{id}/finalizar", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("finalizar devuelve 409 si ya está finalizada")
        void finalizarConflicto() throws Exception {
            when(competicionUseCase.finalizarCompeticion(ID))
                    .thenThrow(new IllegalStateException("La competición ya está finalizada"));

            mockMvc.perform(patch("/apifutbol/competiciones/{id}/finalizar", ID))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("suspender 200")
        void suspender() throws Exception {
            when(competicionUseCase.suspenderCompeticion(ID)).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/competiciones/{id}/suspender", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("reanudar 200")
        void reanudar() throws Exception {
            when(competicionUseCase.reanudarCompeticion(ID)).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/competiciones/{id}/reanudar", ID))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/competiciones/{id}/ganador")
    class Ganador {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            ActualizarGanadorRequest req = ActualizarGanadorRequest.builder()
                    .idEquipoGanador(UUID.randomUUID())
                    .build();
            when(competicionUseCase.actualizarEquipoGanador(eq(ID), any()))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/competiciones/{id}/ganador", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 409 si hay partidos sin finalizar")
        void partidosSinFinalizar() throws Exception {
            ActualizarGanadorRequest req = ActualizarGanadorRequest.builder()
                    .idEquipoGanador(UUID.randomUUID())
                    .build();
            when(competicionUseCase.actualizarEquipoGanador(eq(ID), any()))
                    .thenThrow(new IllegalStateException(
                            "No se puede asignar un ganador porque hay partidos que aún no han finalizado"));

            mockMvc.perform(patch("/apifutbol/competiciones/{id}/ganador", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(req)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /apifutbol/competiciones/{id}")
    class Eliminar {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(competicionUseCase).eliminarCompeticion(ID);

            mockMvc.perform(delete("/apifutbol/competiciones/{id}", ID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            doThrow(new IllegalArgumentException(
                    "Competición no encontrada con id: " + ID))
                    .when(competicionUseCase).eliminarCompeticion(ID);

            mockMvc.perform(delete("/apifutbol/competiciones/{id}", ID))
                    .andExpect(status().isBadRequest());
        }
    }
}