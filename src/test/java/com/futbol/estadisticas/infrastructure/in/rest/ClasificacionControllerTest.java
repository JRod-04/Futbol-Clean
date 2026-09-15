package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.ClasificacionGruposResponse;
import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.ClasificacionResponse;
import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.EquipoClasificacion;
import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.GrupoClasificacion;
import com.futbol.estadisticas.application.port.dto.response.LideresEstadisticos.LideresEstadisticosResponse;
import com.futbol.estadisticas.application.port.in.ClasificacionUseCase;
import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ClasificacionController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ClasificacionControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ClasificacionUseCase clasificacionUseCase;

    private static final UUID ID_COMPETICION = UUID.randomUUID();
    private static final UUID ID_EQUIPO = UUID.randomUUID();

    // ─── Helpers estáticos ───────────────────────────────────────────────

    private static EquipoClasificacion equipoClasificacion() {
        return new EquipoClasificacion(
                ID_EQUIPO,
                "Arsenal FC",
                "ARS",
                1, 1, 0, 0,
                2, 1, 1, 3
        );
    }

    private static ClasificacionResponse tablaUnicaResponse() {
        return new ClasificacionResponse(
                ID_COMPETICION,
                "Premier League",
                List.of(equipoClasificacion())
        );
    }

    private static ClasificacionGruposResponse tablaConGruposResponse() {
        GrupoClasificacion grupo = new GrupoClasificacion(
                FaseTorneo.GRUPO_A,
                List.of(equipoClasificacion())
        );
        return new ClasificacionGruposResponse(
                ID_COMPETICION,
                "Champions League",
                List.of(grupo)
        );
    }

    private static LideresEstadisticosResponse rankingResponse() {
        return LideresEstadisticosResponse.builder()
                .idCompeticion(ID_COMPETICION)
                .nombreCompeticion("Premier League")
                .estadisticas(List.of())
                .build();
    }

    // ─── Tests ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /apifutbol/clasificacion/{idCompeticion}")
    class ObtenerTabla {

        @Test
        @DisplayName("devuelve 200 con la tabla única")
        void devuelveTablaUnica() throws Exception {
            when(clasificacionUseCase.obtenerTabla(ID_COMPETICION))
                    .thenReturn(tablaUnicaResponse());

            mockMvc.perform(get("/apifutbol/clasificacion/{id}", ID_COMPETICION))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idCompeticion").value(ID_COMPETICION.toString()))
                    .andExpect(jsonPath("$.nombreCompeticion").value("Premier League"))
                    .andExpect(jsonPath("$.tabla").isArray())
                    .andExpect(jsonPath("$.tabla[0].idEquipo").value(ID_EQUIPO.toString()))
                    .andExpect(jsonPath("$.tabla[0].nombreEquipo").value("Arsenal FC"))
                    .andExpect(jsonPath("$.tabla[0].puntos").value(3))
                    .andExpect(jsonPath("$.tabla[0].diferenciaGoles").value(1));
        }

        @Test
        @DisplayName("devuelve 200 con la tabla de grupos")
        void devuelveTablaConGrupos() throws Exception {
            when(clasificacionUseCase.obtenerTabla(ID_COMPETICION))
                    .thenReturn(tablaConGruposResponse());

            mockMvc.perform(get("/apifutbol/clasificacion/{id}", ID_COMPETICION))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idCompeticion").value(ID_COMPETICION.toString()))
                    .andExpect(jsonPath("$.nombreCompeticion").value("Champions League"))
                    .andExpect(jsonPath("$.grupos").isArray())
                    .andExpect(jsonPath("$.grupos[0].nombreGrupo").value("GRUPO_A"))
                    .andExpect(jsonPath("$.grupos[0].tabla[0].nombreEquipo").value("Arsenal FC"));
        }

        @Test
        @DisplayName("devuelve 404 si la competición no existe")
        void noExiste() throws Exception {
            when(clasificacionUseCase.obtenerTabla(ID_COMPETICION))
                    .thenThrow(new IllegalArgumentException(
                            "Competición no encontrada con id: " + ID_COMPETICION));

            mockMvc.perform(get("/apifutbol/clasificacion/{id}", ID_COMPETICION))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/clasificacion/{idCompeticion}/ranking")
    class ObtenerRanking {

        @Test
        @DisplayName("devuelve 200 con el ranking y limit por defecto 10")
        void rankingConLimitPorDefecto() throws Exception {
            when(clasificacionUseCase.obtenerLideresEstadisticos(eq(ID_COMPETICION), eq(10)))
                    .thenReturn(rankingResponse());

            mockMvc.perform(get("/apifutbol/clasificacion/{id}/ranking", ID_COMPETICION))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idCompeticion").value(ID_COMPETICION.toString()))
                    .andExpect(jsonPath("$.nombreCompeticion").value("Premier League"))
                    .andExpect(jsonPath("$.estadisticas").isArray());
        }

        @Test
        @DisplayName("devuelve 200 con el limit especificado")
        void rankingConLimitPersonalizado() throws Exception {
            when(clasificacionUseCase.obtenerLideresEstadisticos(eq(ID_COMPETICION), eq(5)))
                    .thenReturn(rankingResponse());

            mockMvc.perform(get("/apifutbol/clasificacion/{id}/ranking", ID_COMPETICION)
                            .param("limit", "5"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 404 si la competición no existe")
        void noExiste() throws Exception {
            when(clasificacionUseCase.obtenerLideresEstadisticos(eq(ID_COMPETICION), anyInt()))
                    .thenThrow(new IllegalArgumentException(
                            "Competición no encontrada con id: " + ID_COMPETICION));

            mockMvc.perform(get("/apifutbol/clasificacion/{id}/ranking", ID_COMPETICION))
                    .andExpect(status().isBadRequest());
        }
    }
}