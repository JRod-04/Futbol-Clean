package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.CrearPartidoRequest;
import com.futbol.estadisticas.application.port.dto.request.RealizarSustitucionRequest;
import com.futbol.estadisticas.application.port.dto.request.RegistrarEventoRequest;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoConAlineacionResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.SustitucionResponse;
import com.futbol.estadisticas.application.port.dto.response.TandaPenalesResponse;
import com.futbol.estadisticas.application.port.in.PartidoUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.FaseTorneo;
import com.futbol.estadisticas.domain.model.enums.JornadaPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
import com.futbol.estadisticas.domain.model.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PartidoController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class PartidoControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean
    private PartidoUseCase partidoUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final UUID ID = UUID.randomUUID();

    private PartidoResponse response() {
        return PartidoResponse.builder()
                .idPartido(ID)
                .estado(EstadoPartido.PROGRAMADO)
                .golesLocal(0)
                .golesVisitante(0)
                .build();
    }

    private CrearPartidoRequest crearRequest() {
        return CrearPartidoRequest.builder()
                .idEquipoLocal(UUID.randomUUID())
                .idEquipoVisitante(UUID.randomUUID())
                .idCompeticion(UUID.randomUUID())
                .idArbitro(UUID.randomUUID())
                .fechaYHora(LocalDateTime.now().plusDays(3))
                .jornadaTorneo(JornadaPartido.JORNADA_1)
                .fase(FaseTorneo.LIGA)
                .build();
    }

    // ──────────────────────────── LISTAR ────────────────────────────

    @Nested
    @DisplayName("GET /apifutbol/partidos")
    class Listar {

        @Test
        @DisplayName("devuelve 200 con la página")
        void ok() throws Exception {
            when(partidoUseCase.listarTodos(any()))
                    .thenReturn(new PageImpl<>(List.of(response())));

            mockMvc.perform(get("/apifutbol/partidos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idPartido").value(ID.toString()));
        }
    }

    // ──────────────────────────── PROGRAMAR ────────────────────────────

    @Nested
    @DisplayName("POST /apifutbol/partidos")
    class Programar {

        @Test
        @DisplayName("devuelve 201 con el partido creado")
        void ok() throws Exception {
            when(partidoUseCase.programarPartido(any())).thenReturn(response());

            mockMvc.perform(post("/apifutbol/partidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idPartido").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 400 si faltan campos obligatorios")
        void requestInvalido() throws Exception {
            mockMvc.perform(post("/apifutbol/partidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 400 si el service lanza IllegalArgumentException sin 'no encontrado'")
        void badRequest() throws Exception {
            when(partidoUseCase.programarPartido(any()))
                    .thenThrow(new IllegalArgumentException("Un club no puede jugar contra sí mismo"));

            mockMvc.perform(post("/apifutbol/partidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 404 si el service lanza IllegalArgumentException con 'no encontrado'")
        void notFound() throws Exception {
            when(partidoUseCase.programarPartido(any()))
                    .thenThrow(new ResourceNotFoundException("Club no encontrado con id: " + ID));

            mockMvc.perform(post("/apifutbol/partidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(crearRequest())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /apifutbol/partidos/batch")
    class ProgramarBatch {

        @Test
        @DisplayName("devuelve 201 con la lista")
        void ok() throws Exception {
            when(partidoUseCase.programarPartidosBatch(any()))
                    .thenReturn(List.of(response()));

            mockMvc.perform(post("/apifutbol/partidos/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(List.of(crearRequest()))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$[0].idPartido").value(ID.toString()));
        }
    }

    // ──────────────────────────── CONSULTAS ────────────────────────────

    @Nested
    @DisplayName("GET /apifutbol/partidos/{id}")
    class ObtenerPorId {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(partidoUseCase.obtenerPartidoPorId(ID)).thenReturn(response());

            mockMvc.perform(get("/apifutbol/partidos/{id}", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idPartido").value(ID.toString()));
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void noExiste() throws Exception {
            when(partidoUseCase.obtenerPartidoPorId(ID))
                    .thenThrow(new ResourceNotFoundException("Partido no encontrado con id: " + ID));

            mockMvc.perform(get("/apifutbol/partidos/{id}", ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/partidos/fecha")
    class PorFecha {

        @Test
        @DisplayName("devuelve 200 con la página")
        void ok() throws Exception {
            when(partidoUseCase.obtenerPartidosPorFecha(any(LocalDate.class), anyInt(), anyInt()))
                    .thenReturn(new PageImpl<>(List.of(response())));

            mockMvc.perform(get("/apifutbol/partidos/fecha")
                            .param("fecha", "2024-09-14"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idPartido").value(ID.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/partidos/{id}/penales")
    class Penales {

        @Test
        @DisplayName("devuelve 200 con la tanda")
        void ok() throws Exception {
            TandaPenalesResponse tanda = TandaPenalesResponse.builder().build();
            when(partidoUseCase.obtenerTandaPenales(ID)).thenReturn(tanda);

            mockMvc.perform(get("/apifutbol/partidos/{id}/penales", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 409 si el partido no ha finalizado")
        void conflicto() throws Exception {
            when(partidoUseCase.obtenerTandaPenales(ID))
                    .thenThrow(new IllegalStateException("El partido aún no ha finalizado"));

            mockMvc.perform(get("/apifutbol/partidos/{id}/penales", ID))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/partidos/competicion/{id}")
    class PorCompeticion {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(partidoUseCase.obtenerPartidosPorCompeticion(ID))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/partidos/competicion/{id}", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idPartido").value(ID.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/partidos/por-equipo/{id}")
    class PorEquipo {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            when(partidoUseCase.obtenerPartidosPorEquipo(ID))
                    .thenReturn(List.of(response()));

            mockMvc.perform(get("/apifutbol/partidos/por-equipo/{id}", ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idPartido").value(ID.toString()));
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/partidos/{id}/alineaciones")
    class Alineaciones {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            PartidoConAlineacionResponse response = PartidoConAlineacionResponse.builder().build();
            when(partidoUseCase.obtenerPartidoConAlineacion(ID)).thenReturn(response);

            mockMvc.perform(get("/apifutbol/partidos/{id}/alineaciones", ID))
                    .andExpect(status().isOk());
        }
    }

    // ──────────────────────────── ESTADO ────────────────────────────

    @Nested
    @DisplayName("PATCH /apifutbol/partidos/{id}/iniciar")
    class Iniciar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(partidoUseCase.iniciarPartido(ID)).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/partidos/{id}/iniciar", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("devuelve 409 si no se puede iniciar")
        void conflicto() throws Exception {
            when(partidoUseCase.iniciarPartido(ID))
                    .thenThrow(new IllegalStateException("El partido ya ha sido iniciado"));

            mockMvc.perform(patch("/apifutbol/partidos/{id}/iniciar", ID))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/partidos/{id}/finalizar")
    class Finalizar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(partidoUseCase.finalizarPartido(eq(ID), any(LocalTime.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/partidos/{id}/finalizar", ID)
                            .param("minutoFinal", "01:30:00"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/partidos/{id}/estado")
    class CambiarEstado {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(partidoUseCase.cambiarEstadoPartido(eq(ID), any(EstadoPartido.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/partidos/{id}/estado", ID)
                            .param("nuevoEstado", "SUSPENDIDO"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/partidos/{id}/reanudar")
    class Reanudar {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(partidoUseCase.avanzarPartido(ID)).thenReturn(response());

            mockMvc.perform(patch("/apifutbol/partidos/{id}/reanudar", ID))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/partidos/{id}/agregar-tiempo")
    class AgregarTiempo {

        @Test
        @DisplayName("devuelve 200 con el evento")
        void ok() throws Exception {
            EventoPartidoResponse evento = EventoPartidoResponse.builder()
                    .idEvento(UUID.randomUUID())
                    .tipoEvento(TipoEvento.AGREGADO)
                    .build();
            when(partidoUseCase.agregarTiempoAgregado(eq(ID), anyInt(), any()))
                    .thenReturn(evento);

            mockMvc.perform(patch("/apifutbol/partidos/{id}/agregar-tiempo", ID)
                            .param("minutos", "3")
                            .param("descripcion", "+3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tipoEvento").value("AGREGADO"));
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/partidos/{id}/finalizar-tiempo")
    class FinalizarTiempo {

        @Test
        @DisplayName("devuelve 200")
        void ok() throws Exception {
            when(partidoUseCase.finalizarTiempo(eq(ID), any(LocalTime.class)))
                    .thenReturn(response());

            mockMvc.perform(patch("/apifutbol/partidos/{id}/finalizar-tiempo", ID)
                            .param("minutoFin", "00:45:00"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /apifutbol/partidos/{id}/cancelar")
    class Cancelar {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(partidoUseCase).cancelarPartido(ID);

            mockMvc.perform(patch("/apifutbol/partidos/{id}/cancelar", ID))
                    .andExpect(status().isNoContent());

            verify(partidoUseCase).cancelarPartido(ID);
        }

        @Test
        @DisplayName("devuelve 409 si ya finalizó")
        void conflicto() throws Exception {
            doThrow(new IllegalStateException("No se puede cancelar un partido ya finalizado"))
                    .when(partidoUseCase).cancelarPartido(ID);

            mockMvc.perform(patch("/apifutbol/partidos/{id}/cancelar", ID))
                    .andExpect(status().isConflict());
        }
    }

    // ──────────────────────────── EVENTOS ────────────────────────────

    @Nested
    @DisplayName("POST /apifutbol/partidos/{id}/eventos")
    class RegistrarEvento {

        @Test
        @DisplayName("devuelve 201 con el evento")
        void ok() throws Exception {
            RegistrarEventoRequest req = RegistrarEventoRequest.builder()
                    .tipoEvento(TipoEvento.GOL)
                    .minuto(LocalTime.of(0, 20))
                    .build();
            EventoPartidoResponse evento = EventoPartidoResponse.builder()
                    .idEvento(UUID.randomUUID())
                    .tipoEvento(TipoEvento.GOL)
                    .build();
            when(partidoUseCase.registrarEvento(eq(ID), any())).thenReturn(evento);

            mockMvc.perform(post("/apifutbol/partidos/{id}/eventos", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tipoEvento").value("GOL"));
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void requestInvalido() throws Exception {
            mockMvc.perform(post("/apifutbol/partidos/{id}/eventos", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("devuelve 404 si el personal no existe")
        void personalNoExiste() throws Exception {
            RegistrarEventoRequest req = RegistrarEventoRequest.builder()
                    .tipoEvento(TipoEvento.GOL)
                    .minuto(LocalTime.of(0, 20))
                    .build();
            when(partidoUseCase.registrarEvento(eq(ID), any()))
                    .thenThrow(new ResourceNotFoundException("Personal no encontrado"));

            mockMvc.perform(post("/apifutbol/partidos/{id}/eventos", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /apifutbol/partidos/{id}/eventos/batch")
    class RegistrarEventosBatch {

        @Test
        @DisplayName("devuelve 201 con la lista")
        void ok() throws Exception {
            RegistrarEventoRequest req = RegistrarEventoRequest.builder()
                    .tipoEvento(TipoEvento.GOL)
                    .minuto(LocalTime.of(0, 20))
                    .build();
            EventoPartidoResponse evento = EventoPartidoResponse.builder()
                    .idEvento(UUID.randomUUID())
                    .build();
            when(partidoUseCase.registrarEventosBatch(eq(ID), any()))
                    .thenReturn(List.of(evento));

            mockMvc.perform(post("/apifutbol/partidos/{id}/eventos/batch", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(List.of(req))))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /apifutbol/partidos/{id}/eventos")
    class ObtenerEventos {

        @Test
        @DisplayName("devuelve 200 con la lista")
        void ok() throws Exception {
            EventoPartidoResponse evento = EventoPartidoResponse.builder()
                    .idEvento(UUID.randomUUID())
                    .build();
            when(partidoUseCase.obtenerEventosDePartido(ID)).thenReturn(List.of(evento));

            mockMvc.perform(get("/apifutbol/partidos/{id}/eventos", ID))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /apifutbol/partidos/{id}/eventos/{idEvento}")
    class EliminarEvento {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            UUID idEvento = UUID.randomUUID();
            doNothing().when(partidoUseCase).eliminarEvento(ID, idEvento);

            mockMvc.perform(delete("/apifutbol/partidos/{id}/eventos/{idEvento}", ID, idEvento))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 404 si el evento no existe")
        void eventoNoExiste() throws Exception {
            UUID idEvento = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Evento no encontrado con id: " + idEvento))
                    .when(partidoUseCase).eliminarEvento(ID, idEvento);

            mockMvc.perform(delete("/apifutbol/partidos/{id}/eventos/{idEvento}", ID, idEvento))
                    .andExpect(status().isNotFound());
        }
    }

    // ──────────────────────────── SUSTITUCIÓN ────────────────────────────

    @Nested
    @DisplayName("POST /apifutbol/partidos/{id}/sustituciones")
    class RealizarSustitucion {

        @Test
        @DisplayName("devuelve 201")
        void ok() throws Exception {
            RealizarSustitucionRequest req = RealizarSustitucionRequest.builder()
                    .idJugadorEntrante(UUID.randomUUID())
                    .idJugadorSaliente(UUID.randomUUID())
                    .idEquipo(UUID.randomUUID())
                    .minuto(LocalTime.of(1, 0))
                    .build();
            SustitucionResponse response = SustitucionResponse.builder().build();
            when(partidoUseCase.realizarSustitucion(eq(ID), any())).thenReturn(response);

            mockMvc.perform(post("/apifutbol/partidos/{id}/sustituciones", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("devuelve 400 si el request es inválido")
        void requestInvalido() throws Exception {
            mockMvc.perform(post("/apifutbol/partidos/{id}/sustituciones", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ──────────────────────────── DELETE PARTIDO ────────────────────────────

    @Nested
    @DisplayName("DELETE /apifutbol/partidos/{id}")
    class EliminarPartido {

        @Test
        @DisplayName("devuelve 204")
        void ok() throws Exception {
            doNothing().when(partidoUseCase).eliminarPartido(ID);

            mockMvc.perform(delete("/apifutbol/partidos/{id}", ID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("devuelve 409 si está en curso")
        void enCurso() throws Exception {
            doThrow(new IllegalStateException("No se puede eliminar un partido en curso"))
                    .when(partidoUseCase).eliminarPartido(ID);

            mockMvc.perform(delete("/apifutbol/partidos/{id}", ID))
                    .andExpect(status().isConflict());
        }
    }
}