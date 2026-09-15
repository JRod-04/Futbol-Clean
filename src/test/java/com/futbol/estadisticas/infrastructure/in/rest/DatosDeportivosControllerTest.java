package com.futbol.estadisticas.infrastructure.in.rest;

import com.futbol.estadisticas.application.port.dto.response.DatosDeportivosResponse;
import com.futbol.estadisticas.application.port.in.DatosDeportivosUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {DatosDeportivosController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class DatosDeportivosControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private DatosDeportivosUseCase datosDeportivosUseCase;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ID_JUGADOR = UUID.randomUUID();

    // ─── Helpers estáticos ───────────────────────────────────────────────

    private static DatosDeportivosResponse response() {
        return new DatosDeportivosResponse(
                UUID.randomUUID(),                             // idHistorialDeportivo
                PosicionJugador.EXTREMO_DERECHO,               // posicion
                7,                                             // dorsal
                EstadoJugador.TITULAR,                         // estadoJugador
                85_000_000.0,                                  // valorMercado
                85.0,                                          // valorMercadoEnMillones
                LocalDate.now(),                               // fechaActualizacion
                true,                                          // esTitular
                false,                                         // esSuplente
                true,                                          // estaDisponible
                false,                                         // estaLesionado
                ID_JUGADOR,                                    // idJugador
                "Bukayo Saka"                                  // nombreJugador
        );
    }

    // ─── Tests ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /apifutbol/jugadores/{id}/datos-deportivos")
    class Obtener {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(datosDeportivosUseCase.obtenerPorJugador(ID_JUGADOR)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/jugadores/{id}/datos-deportivos", ID_JUGADOR))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dorsal").value(7))
                    .andExpect(jsonPath("$.posicion").value("RW"))
                    .andExpect(jsonPath("$.estadoJugador").value("TITULAR"));
        }

        @Test
        @DisplayName("devuelve 404 si el jugador no existe")
        void jugadorNoExiste() throws Exception {
            when(datosDeportivosUseCase.obtenerPorJugador(ID_JUGADOR))
                    .thenThrow(new ResourceNotFoundException("Jugador no encontrado"));

            mockMvc.perform(get("/apifutbol/jugadores/{id}/datos-deportivos", ID_JUGADOR))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("devuelve 409 si no tiene datos deportivos")
        void sinDatos() throws Exception {
            when(datosDeportivosUseCase.obtenerPorJugador(ID_JUGADOR))
                    .thenThrow(new IllegalStateException(
                            "El jugador con id: " + ID_JUGADOR + " no tiene datos deportivos registrados"));

            mockMvc.perform(get("/apifutbol/jugadores/{id}/datos-deportivos", ID_JUGADOR))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/jugadores/{id}/datos-deportivos/valor-mercado")
    class ActualizarValorMercado {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(datosDeportivosUseCase.actualizarValorMercado(eq(ID_JUGADOR), any(Double.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/valor-mercado", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("100000000.0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dorsal").value(7));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/jugadores/{id}/datos-deportivos/posiciones")
    class ObtenerPosiciones {

        @Test
        @DisplayName("devuelve 200 con la lista de posiciones")
        void ok() throws Exception {
            Deque<PosicionJugador> posiciones = new ArrayDeque<>();
            posiciones.add(PosicionJugador.EXTREMO_DERECHO);
            posiciones.add(PosicionJugador.DELANTERO_DERECHO);

            when(datosDeportivosUseCase.obtenerPosiciones(ID_JUGADOR)).thenReturn(posiciones);

            mockMvc.perform(get("/apifutbol/jugadores/{id}/datos-deportivos/posiciones", ID_JUGADOR))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0]").value("RW"))
                    .andExpect(jsonPath("$[1]").value("RST"));
        }
    }

    @Nested
    @DisplayName("POST /apifutbol/jugadores/{id}/datos-deportivos/posicion")
    class AgregarPosicion {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            when(datosDeportivosUseCase.cambiarPosicion(eq(ID_JUGADOR), any(PosicionJugador.class)))
                    .thenReturn(response());

            mockMvc.perform(post("/apifutbol/jugadores/{id}/datos-deportivos/posicion", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"ST\""))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.dorsal").value(7));
        }

        @Test
        @DisplayName("devuelve 400 si la posición no es válida")
        void posicionInvalida() throws Exception {
            mockMvc.perform(post("/apifutbol/jugadores/{id}/datos-deportivos/posicion", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"NO_EXISTE\""))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /apifutbol/jugadores/{id}/datos-deportivos/posicion")
    class EliminarPosicion {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(datosDeportivosUseCase.eliminarPosicion(eq(ID_JUGADOR), any(PosicionJugador.class)))
                    .thenReturn(response());

            mockMvc.perform(delete("/apifutbol/jugadores/{id}/datos-deportivos/posicion", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"ST\""))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/jugadores/{id}/datos-deportivos/dorsal")
    class ActualizarDorsal {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(datosDeportivosUseCase.actualizarDorsal(eq(ID_JUGADOR), any(Integer.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/dorsal", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dorsal").value(7));
        }

        @Test
        @DisplayName("devuelve 400 si el dorsal está ocupado")
        void dorsalOcupado() throws Exception {
            when(datosDeportivosUseCase.actualizarDorsal(eq(ID_JUGADOR), any(Integer.class)))
                    .thenThrow(new IllegalArgumentException(
                            "El dorsal 10 ya está asignado a otro jugador del club Arsenal FC"));

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/dorsal", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("10"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 400 si el dorsal es nulo")
        void dorsalNulo() throws Exception {
            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/dorsal", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("null"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/jugadores/{id}/datos-deportivos/promover-titular")
    class PromoverATitular {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(datosDeportivosUseCase.promoverATitular(ID_JUGADOR)).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/promover-titular", ID_JUGADOR))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 404 si el jugador no existe")
        void noExiste() throws Exception {
            when(datosDeportivosUseCase.promoverATitular(ID_JUGADOR))
                    .thenThrow(new ResourceNotFoundException("Jugador no encontrado"));

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/promover-titular", ID_JUGADOR))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/jugadores/{id}/datos-deportivos/pasar-suplente")
    class PasarASuplente {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(datosDeportivosUseCase.cambiarASuplente(ID_JUGADOR)).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/pasar-suplente", ID_JUGADOR))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/jugadores/{id}/datos-deportivos/estado")
    class ActualizarEstado {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(datosDeportivosUseCase.actualizarEstado(eq(ID_JUGADOR), any(EstadoJugador.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/estado", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"LESIONADO\""))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 400 si el estado no es válido")
        void estadoInvalido() throws Exception {
            mockMvc.perform(patch("/apifutbol/jugadores/{id}/datos-deportivos/estado", ID_JUGADOR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"NO_EXISTE\""))
                    .andExpect(status().isBadRequest());
        }
    }
}