package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.request.CrearContratoRequest;
import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import com.futbol.estadisticas.application.port.in.ContratoUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoContrato;
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

@WebMvcTest(controllers = {ContratoController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ContratoControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ContratoUseCase contratoUseCase;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ID_CONTRATO = UUID.randomUUID();
    private static final UUID ID_PERSONAL = UUID.randomUUID();
    private static final UUID ID_EQUIPO = UUID.randomUUID();

    private static final LocalDateTime INICIO = LocalDateTime.of(2024, 1, 1, 0, 0);
    private static final LocalDateTime FIN = LocalDateTime.of(2025, 1, 1, 0, 0);

    // ─── Helpers estáticos ───────────────────────────────────────────────

    private static ContratoResponse response() {
        return ContratoResponse.builder()
                .idContrato(ID_CONTRATO)
                .fechaInicio(INICIO)
                .fechaFin(FIN)
                .sueldo(250_000.0)
                .tipo(TipoContrato.PROFESIONAL)
                .estado(EstadoContrato.ACTIVO)
                .vigente(true)
                .idPersonal(ID_PERSONAL)
                .nombrePersonal("Bukayo Saka")
                .idEquipo(ID_EQUIPO)
                .nombreEquipo("Arsenal FC")
                .nombreCortoEquipo("ARS")
                .costoFichaje(70_000_000.0)
                .build();
    }

    private static CrearContratoRequest crearRequest() {
        return CrearContratoRequest.builder()
                .idPersonal(ID_PERSONAL)
                .idEquipo(ID_EQUIPO)
                .tipoContrato(TipoContrato.PROFESIONAL)
                .costoFichaje(70_000_000.0)
                .fechaInicio(INICIO)
                .estado(EstadoContrato.ACTIVO)
                .fechaFin(FIN)
                .sueldo(250_000.0)
                .build();
    }

    // ─── Tests ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /apifutbol/contratos")
    class Crear {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(contratoUseCase.crearContrato(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/contratos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idContrato").value(ID_CONTRATO.toString()))
                    .andExpect(jsonPath("$.sueldo").value(250_000.0))
                    .andExpect(jsonPath("$.estado").value("ACTIVO"));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/contratos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 404 si el personal no existe")
        void personalNoExiste() throws Exception {
            when(contratoUseCase.crearContrato(any()))
                    .thenThrow(new ResourceNotFoundException(
                            "Personal no encontrado con id: " + ID_PERSONAL));

            mockMvc.perform(post("/apifutbol/contratos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("devuelve 404 si el equipo no existe")
        void equipoNoExiste() throws Exception {
            when(contratoUseCase.crearContrato(any()))
                    .thenThrow(new ResourceNotFoundException(
                            "Equipo no encontrado con id: " + ID_EQUIPO));

            mockMvc.perform(post("/apifutbol/contratos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /apifutbol/contratos/batch")
    class CrearBatch {

        @Test
        @DisplayName("devuelve 201 con la lista")
        void ok() throws Exception {
            when(contratoUseCase.crearVariosContratos(any()))
                    .thenReturn(List.of(response()));

            mockMvc.perform(post("/apifutbol/contratos/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(List.of(crearRequest()))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$[0].idContrato").value(ID_CONTRATO.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/contratos/{id}")
    class PorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(contratoUseCase.obtenerContratoPorId(ID_CONTRATO)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/contratos/{id}", ID_CONTRATO))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idContrato").value(ID_CONTRATO.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(contratoUseCase.obtenerContratoPorId(ID_CONTRATO))
                    .thenThrow(new ResourceNotFoundException(
                            "Contrato no encontrado con id: " + ID_CONTRATO));

            mockMvc.perform(get("/apifutbol/contratos/{id}", ID_CONTRATO))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/contratos/personal/{idPersonal}")
    class PorPersonal {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(contratoUseCase.obtenerContratosPorPersonal(ID_PERSONAL))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/contratos/personal/{id}", ID_PERSONAL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idContrato").value(ID_CONTRATO.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/contratos/personal/{idPersonal}/vigente")
    class VigenteDePersonal {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(contratoUseCase.obtenerContratoVigenteDePersonal(ID_PERSONAL))
                    .thenReturn(response());

            mockMvc.perform(get("/apifutbol/contratos/personal/{id}/vigente", ID_PERSONAL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idContrato").value(ID_CONTRATO.toString()));
        }

        @Test
        @DisplayName("devuelve 409 si no hay vigente")
        void noVigente() throws Exception {
            when(contratoUseCase.obtenerContratoVigenteDePersonal(ID_PERSONAL))
                    .thenThrow(new IllegalStateException(
                            "No hay contrato vigente para el personal con id: " + ID_PERSONAL));

            mockMvc.perform(get("/apifutbol/contratos/personal/{id}/vigente", ID_PERSONAL))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/contratos/equipo/{idEquipo}/vigentes")
    class VigentesPorEquipo {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(contratoUseCase.obtenerContratosVigentesPorEquipo(ID_EQUIPO))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/contratos/equipo/{id}/vigentes", ID_EQUIPO))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idContrato").value(ID_CONTRATO.toString()));
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/contratos/{id}/renovar")
    class Renovar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(contratoUseCase.renovarContrato(eq(ID_CONTRATO), any(LocalDateTime.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/contratos/{id}/renovar", ID_CONTRATO)
                            .param("nuevaFechaFin", "2026-01-01T00:00:00"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(contratoUseCase.renovarContrato(eq(ID_CONTRATO), any(LocalDateTime.class)))
                    .thenThrow(new ResourceNotFoundException(
                            "Contrato no encontrado con id: " + ID_CONTRATO));

            mockMvc.perform(patch("/apifutbol/contratos/{id}/renovar", ID_CONTRATO)
                            .param("nuevaFechaFin", "2026-01-01T00:00:00"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/contratos/{id}/finalizar")
    class Finalizar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(contratoUseCase.finalizarContrato(eq(ID_CONTRATO), any(LocalDateTime.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/contratos/{id}/finalizar", ID_CONTRATO)
                            .param("fechaFin", "2025-12-31T23:59:00"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 409 si ya está finalizado")
        void yaFinalizado() throws Exception {
            when(contratoUseCase.finalizarContrato(eq(ID_CONTRATO), any(LocalDateTime.class)))
                    .thenThrow(new IllegalStateException("El contrato ya está finalizado"));

            mockMvc.perform(patch("/apifutbol/contratos/{id}/finalizar", ID_CONTRATO)
                            .param("fechaFin", "2025-12-31T23:59:00"))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/contratos/{id}/rescindir")
    class Rescindir {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(contratoUseCase.rescindirContrato(eq(ID_CONTRATO), any(LocalDateTime.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/contratos/{id}/rescindir", ID_CONTRATO)
                            .param("fechaRescindido", "2025-06-30T12:00:00"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 409 si ya está finalizado")
        void yaFinalizado() throws Exception {
            when(contratoUseCase.rescindirContrato(eq(ID_CONTRATO), any(LocalDateTime.class)))
                    .thenThrow(new IllegalStateException("El contrato ya está finalizado"));

            mockMvc.perform(patch("/apifutbol/contratos/{id}/rescindir", ID_CONTRATO)
                            .param("fechaRescindido", "2025-06-30T12:00:00"))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /apifutbol/contratos/{id}")
    class Eliminar {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(contratoUseCase).eliminarContrato(ID_CONTRATO);

            mockMvc.perform(delete("/apifutbol/contratos/{id}", ID_CONTRATO))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            doThrow(new ResourceNotFoundException(
                    "Contrato no encontrado con id: " + ID_CONTRATO))
                    .when(contratoUseCase).eliminarContrato(ID_CONTRATO);

            mockMvc.perform(delete("/apifutbol/contratos/{id}", ID_CONTRATO))
                    .andExpect(status().isNotFound());
        }
    }
}