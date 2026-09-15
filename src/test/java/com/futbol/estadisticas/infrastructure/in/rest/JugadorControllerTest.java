package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.request.ActualizarJugadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.JugadoresUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
import com.futbol.estadisticas.domain.model.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {JugadorController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class JugadorControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private JugadoresUseCase jugadoresUseCase;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ID = UUID.randomUUID();

    private JugadorResponse response() {
        return JugadorResponse.builder()
                .idPersonal(ID).nombre("Bukayo").apellido("Saka")
                .nombreCompleto("Bukayo Saka").edad(24)
                .nacionalidad(Nacion.INGLATERRA).pieHabil(JuegoPies.ZURDO)
                .altura(178).peso(70).dorsal(7)
                .estadoJugador(EstadoJugador.TITULAR)
                .posiciones(List.of(PosicionJugador.EXTREMO_DERECHO))
                .build();
    }

    private CrearJugadorRequest crearRequest() {
        return CrearJugadorRequest.builder()
                .nombre("Bukayo").apellido("Saka")
                .fechaNacimiento(LocalDate.of(2001, 9, 5))
                .nacionalidad(Nacion.INGLATERRA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(178).peso(70).dorsal(7)
                .posiciones(List.of(PosicionJugador.EXTREMO_DERECHO))
                .valorMercado(85_000_000.0)
                .build();
    }

    // ──────────────────────────── CREAR ────────────────────────────

    @Nested
    @DisplayName("POST /apifutbol/jugadores")
    class Crear {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(jugadoresUseCase.crearJugador(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/jugadores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idPersonal").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 400 si faltan campos obligatorios")
        void invalido() throws Exception {
            mockMvc.perform(post("/apifutbol/jugadores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /apifutbol/jugadores/batch")
    class CrearBatch {

        @Test
        @DisplayName("devuelve 201 con la lista")
        void ok() throws Exception {
            when(jugadoresUseCase.crearVariosJugadores(any())).thenReturn(List.of(response()));

            mockMvc.perform(post("/apifutbol/jugadores/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(List.of(crearRequest()))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$[0].idPersonal").value(ID.toString()));
        }
    }

    // ──────────────────────────── ESTADÍSTICAS ────────────────────────────

    @Nested
    @DisplayName("GET /apifutbol/jugadores/{id}/estadisticas")
    class Estadisticas {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            EstadisticasJugadorResponse r = EstadisticasJugadorResponse.builder()
                    .idJugador(ID).nombreJugador("Bukayo Saka").build();
            when(jugadoresUseCase.obtenerEstadisticasJugador(ID)).thenReturn(r);

            mockMvc.perform(get("/apifutbol/jugadores/{id}/estadisticas", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(jugadoresUseCase.obtenerEstadisticasJugador(ID))
                    .thenThrow(new ResourceNotFoundException("Jugador no encontrado"));

            mockMvc.perform(get("/apifutbol/jugadores/{id}/estadisticas", ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/jugadores/{id}/partidos")
    class Partidos {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(jugadoresUseCase.obtenerPartidosConEstadisticas(ID))
                    .thenReturn(List.of());

            mockMvc.perform(get("/apifutbol/jugadores/{id}/partidos", ID))
                    .andExpect(status().isOk());
        }
    }

    // ──────────────────────────── LISTAR ────────────────────────────

    @Nested
    @DisplayName("GET /apifutbol/jugadores")
    class Listar {

        @Test
        @DisplayName("devuelve 200 con la página")
        void ok() throws Exception {
            when(jugadoresUseCase.obtenerTodosLosJugadores(any()))
                    .thenReturn(new PageImpl<>(List.of(response())));

            mockMvc.perform(get("/apifutbol/jugadores"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idPersonal").value(ID.toString()));
        }
    }

    // ──────────────────────────── POR ID ────────────────────────────

    @Nested
    @DisplayName("GET /apifutbol/jugadores/{id}")
    class PorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(jugadoresUseCase.obtenerJugadorPorId(ID)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/jugadores/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 404")
        void noExiste() throws Exception {
            when(jugadoresUseCase.obtenerJugadorPorId(ID))
                    .thenThrow(new ResourceNotFoundException("Jugador no encontrado"));

            mockMvc.perform(get("/apifutbol/jugadores/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }

    // ──────────────────────────── FILTROS ────────────────────────────

    @Nested
    @DisplayName("GET /apifutbol/jugadores/club/{id}")
    class PorEquipo {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(jugadoresUseCase.obtenerJugadoresPorEquipo(ID)).thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/jugadores/club/{id}", ID))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/jugadores/posicion/{posicion}")
    class PorPosicion {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(jugadoresUseCase.obtenerJugadoresPorPosicion(PosicionJugador.EXTREMO_DERECHO))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/jugadores/posicion/EXTREMO_DERECHO"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/jugadores/disponibles y lesionados")
    class Filtros {

        @Test
        @DisplayName("disponibles 200")
        void disponibles() throws Exception {
            when(jugadoresUseCase.obtenerJugadoresDisponibles()).thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/jugadores/disponibles"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("lesionados 200")
        void lesionados() throws Exception {
            when(jugadoresUseCase.obtenerJugadoresLesionados()).thenReturn(List.of());

            mockMvc.perform(get("/apifutbol/jugadores/lesionados"))
                    .andExpect(status().isOk());
        }
    }

    // ──────────────────────────── ACTUALIZAR ────────────────────────────

    @Nested
    @DisplayName("PATCH /apifutbol/jugadores/{id}")
    class Actualizar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            ActualizarJugadorRequest req = ActualizarJugadorRequest.builder()
                    .nombre("Nuevo").build();
            when(jugadoresUseCase.actualizarJugador(eq(ID), any())).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/jugadores/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/jugadores/{id}/estado")
    class CambiarEstado {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(jugadoresUseCase.cambiarEstadoJugador(eq(ID), any())).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/estado", ID)
                            .param("nuevoEstado", "LESIONADO"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/jugadores/{id}/valor-mercado")
    class ValorMercado {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(jugadoresUseCase.actualizarValorMercado(eq(ID), any())).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/valor-mercado", ID)
                            .param("valor", "90000000"))
                    .andExpect(status().isOk());
        }
    }

    // ──────────────────────────── ELIMINAR ────────────────────────────

    @Nested
    @DisplayName("DELETE /apifutbol/jugadores/{id}")
    class Eliminar {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(jugadoresUseCase).eliminarJugador(ID);

            mockMvc.perform(delete("/apifutbol/jugadores/{id}", ID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 404")
        void noExiste() throws Exception {
            doThrow(new ResourceNotFoundException("Jugador no encontrado"))
                    .when(jugadoresUseCase).eliminarJugador(ID);

            mockMvc.perform(delete("/apifutbol/jugadores/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }
}