package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.request.RegistrarLesionRequest;
import com.futbol.estadisticas.application.port.dto.response.LesionResponse;
import com.futbol.estadisticas.application.port.in.LesionUseCase;
import com.futbol.estadisticas.domain.model.enums.Gravedad;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LesionController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class LesionControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private LesionUseCase lesionUseCase;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ID_LESION = UUID.randomUUID();
    private static final UUID ID_JUGADOR = UUID.randomUUID();


    private static LesionResponse response() {
        return LesionResponse.builder()
                .idLesion(ID_LESION)
                .nombreLesion("Rotura de ligamento cruzado anterior")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusMonths(2))
                .fechaFin(LocalDate.now().plusMonths(2))
                .curada(false)
                .build();
    }

    private static RegistrarLesionRequest registrarRequest() {
        return RegistrarLesionRequest.builder()
                .idJugador(ID_JUGADOR)
                .nombreLesion("Rotura de ligamento cruzado anterior")
                .gravedad(Gravedad.GRAVE)
                .fechaInicio(LocalDate.now().minusMonths(2))
                .fechaFinEstimada(LocalDate.now().plusMonths(2))
                .build();
    }


    @Nested
    @DisplayName("POST /apifutbol/lesiones")
    class Registrar {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(lesionUseCase.registrarLesion(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/lesiones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(registrarRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idLesion").value(ID_LESION.toString()))
                    .andExpect(jsonPath("$.nombreLesion").value("Rotura de ligamento cruzado anterior"))
                    .andExpect(jsonPath("$.gravedad").value("GRAVE"));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/lesiones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 404 si el jugador no existe")
        void jugadorNoExiste() throws Exception {
            when(lesionUseCase.registrarLesion(any()))
                    .thenThrow(new ResourceNotFoundException(
                            "Jugador no encontrado con id: " + ID_JUGADOR));

            mockMvc.perform(post("/apifutbol/lesiones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(registrarRequest())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /apifutbol/lesiones/batch")
    class RegistrarBatch {

        @Test
        @DisplayName("devuelve 201 con la lista")
        void ok() throws Exception {
            when(lesionUseCase.registrarVariasLesiones(any()))
                    .thenReturn(List.of(response()));

            mockMvc.perform(post("/apifutbol/lesiones/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(List.of(registrarRequest()))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$[0].idLesion").value(ID_LESION.toString()));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/lesiones/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[]"))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/lesiones/jugadores/{idJugador}/lesiones")
    class PorJugador {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(lesionUseCase.obtenerLesionesPorJugador(ID_JUGADOR))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/lesiones/jugadores/{idJugador}/lesiones", ID_JUGADOR))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idLesion").value(ID_LESION.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si el jugador no existe")
        void jugadorNoExiste() throws Exception {
            when(lesionUseCase.obtenerLesionesPorJugador(ID_JUGADOR))
                    .thenThrow(new ResourceNotFoundException(
                            "Jugador no encontrado con id: " + ID_JUGADOR));

            mockMvc.perform(get("/apifutbol/lesiones/jugadores/{idJugador}/lesiones", ID_JUGADOR))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/lesiones/jugadores/{idJugador}/lesiones/activas")
    class ActivasPorJugador {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(lesionUseCase.obtenerLesionesActivasPorJugador(ID_JUGADOR))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/lesiones/jugadores/{idJugador}/activas", ID_JUGADOR))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idLesion").value(ID_LESION.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/lesiones/{id}")
    class PorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(lesionUseCase.obtenerLesionPorId(ID_LESION)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/lesiones/{id}", ID_LESION))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idLesion").value(ID_LESION.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(lesionUseCase.obtenerLesionPorId(ID_LESION))
                    .thenThrow(new IllegalArgumentException(
                            "Lesión no encontrada con id: " + ID_LESION));

            mockMvc.perform(get("/apifutbol/lesiones/{id}", ID_LESION))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/lesiones/activas")
    class TodasLasActivas {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(lesionUseCase.obtenerLesionesActivasEnSistema())
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/lesiones/todas-activas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idLesion").value(ID_LESION.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/lesiones/lesiones/gravedad/{gravedad}")
    class PorGravedad {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(lesionUseCase.obtenerLesionesPorGravedad(Gravedad.GRAVE))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/lesiones/gravedad/{gravedad}", "GRAVE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idLesion").value(ID_LESION.toString()));
        }

        @Test
        @DisplayName("devuelve 400 si la gravedad no es válida")
        void gravedadInvalida() throws Exception {
            mockMvc.perform(get("/apifutbol/lesiones/gravedad/{gravedad}", "NO_EXISTE"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/lesiones/lesiones/{id}/curar")
    class Curar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            LesionResponse curada = LesionResponse.builder()
                    .idLesion(ID_LESION)
                    .nombreLesion("Rotura de ligamento cruzado anterior")
                    .gravedad(Gravedad.GRAVE)
                    .curada(true)
                    .build();
            when(lesionUseCase.curarLesion(ID_LESION)).thenReturn(curada);

            mockMvc.perform(patch("/apifutbol/lesiones/{id}/curar", ID_LESION))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idLesion").value(ID_LESION.toString()))
                    .andExpect(jsonPath("$.curada").value(true));
        }

        @Test
        @DisplayName("devuelve 404 si la lesión no existe")
        void noExiste() throws Exception {
            when(lesionUseCase.curarLesion(ID_LESION))
                    .thenThrow(new IllegalArgumentException(
                            "Lesión no encontrada con id: " + ID_LESION));

            mockMvc.perform(patch("/apifutbol/lesiones/{id}/curar", ID_LESION))
                    .andExpect(status().isNotFound());
        }
    }
}