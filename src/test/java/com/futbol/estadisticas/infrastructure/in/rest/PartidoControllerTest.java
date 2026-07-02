package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.CrearPartidoRequest;
import com.futbol.estadisticas.application.port.dto.request.RegistrarEventoRequest;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.in.PartidoUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PartidoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PartidoUseCase partidoUseCase;

    @InjectMocks
    private PartidoController partidoController;

    private UUID idPartido;
    private UUID idClub;
    private UUID idCompeticion;
    private PartidoResponse response;
    private EventoPartidoResponse eventoResponse;
    private CrearPartidoRequest crearRequest;
    private RegistrarEventoRequest registrarEventoRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(partidoController).build();

        idPartido = UUID.randomUUID();
        idClub = UUID.randomUUID();
        idCompeticion = UUID.randomUUID();

        response = PartidoResponse.builder()
                .idPartido(idPartido)
                .fechaYHora(LocalDateTime.now().plusDays(7))
                .jornada(10)
                .estado(EstadoPartido.PROGRAMADO)
                .estadoDisplayName("Programado")
                .idEquipoLocal(idClub)
                .nombreEquipoLocal("FC Barcelona")
                .idEquipoVisitante(UUID.randomUUID())
                .nombreEquipoVisitante("Real Madrid")
                .golesLocal(0)
                .golesVisitante(0)
                .resultado("0-0")
                .nombreArbitro("Juan Martínez")
                .nombreEstadio("Camp Nou")
                .nombreCompeticion("La Liga")
                .idCompeticion(idCompeticion)
                .enCurso(false)
                .finalizado(false)
                .esFuturo(true)
                .esHoy(false)
                .build();

        eventoResponse = EventoPartidoResponse.builder()
                .idEvento(UUID.randomUUID())
                .tipoEvento(TipoEvento.GOL)
                .minuto(LocalTime.of(0, 30))
                .minutoFormateado("30'")
                .descripcionCompleta("30' - GOL - Lionel Messi")
                .nombreJugador("Lionel Messi")
                .nombreEquipoFavorecido("Barça")
                .esGol(true)
                .esTarjeta(false)
                .esSustitucion(false)
                .esPenalti(false)
                .build();

        crearRequest = new CrearPartidoRequest(
                idClub,
                UUID.randomUUID(),
                idCompeticion,
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(7),
                10,
                UUID.randomUUID()
        );

        registrarEventoRequest = new RegistrarEventoRequest(
                TipoEvento.GOL,
                LocalTime.of(0, 30),
                UUID.randomUUID(),
                idClub,
                "Gol de Messi"
        );
    }

    @Test
    @DisplayName("POST /apifutbol/partidos - debe programar un partido")
    void testProgramar() throws Exception {
        when(partidoUseCase.programarPartido(any(CrearPartidoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/apifutbol/partidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPartido").value(idPartido.toString()))
                .andExpect(jsonPath("$.estado").value("PROGRAMADO"));

        verify(partidoUseCase).programarPartido(any(CrearPartidoRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/partidos/{id} - debe obtener partido por ID")
    void testObtenerPorId() throws Exception {
        when(partidoUseCase.obtenerPartidoPorId(idPartido)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/partidos/{id}", idPartido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPartido").value(idPartido.toString()))
                .andExpect(jsonPath("$.nombreEquipoLocal").value("FC Barcelona"));

        verify(partidoUseCase).obtenerPartidoPorId(idPartido);
    }

    @Test
    @DisplayName("GET /apifutbol/partidos/competicion/{idCompeticion} - debe listar partidos por competición")
    void testPorCompeticion() throws Exception {
        when(partidoUseCase.obtenerPartidosPorCompeticion(idCompeticion)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/partidos/competicion/{idCompeticion}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCompeticion").value("La Liga"));

        verify(partidoUseCase).obtenerPartidosPorCompeticion(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/partidos/club/{idClub} - debe listar partidos por club")
    void testPorClub() throws Exception {
        when(partidoUseCase.obtenerPartidosPorClub(idClub)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/partidos/club/{idClub}", idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreEquipoLocal").value("FC Barcelona"));

        verify(partidoUseCase).obtenerPartidosPorClub(idClub);
    }

    @Test
    @DisplayName("PATCH /apifutbol/partidos/{id}/iniciar - debe iniciar un partido")
    void testIniciar() throws Exception {
        PartidoResponse responseEnCurso = PartidoResponse.builder()
                .idPartido(idPartido)
                .estado(EstadoPartido.PRIMER_TIEMPO)
                .estadoDisplayName("1er Tiempo")
                .enCurso(true)
                .build();

        when(partidoUseCase.iniciarPartido(idPartido)).thenReturn(responseEnCurso);

        mockMvc.perform(patch("/apifutbol/partidos/{id}/iniciar", idPartido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PRIMER_TIEMPO"))
                .andExpect(jsonPath("$.enCurso").value(true));

        verify(partidoUseCase).iniciarPartido(idPartido);
    }

    @Test
    @DisplayName("PATCH /apifutbol/partidos/{id}/finalizar - debe finalizar un partido")
    void testFinalizar() throws Exception {
        PartidoResponse responseFinalizado = PartidoResponse.builder()
                .idPartido(idPartido)
                .estado(EstadoPartido.FINALIZADO)
                .estadoDisplayName("Finalizado")
                .finalizado(true)
                .build();

        when(partidoUseCase.finalizarPartido(idPartido)).thenReturn(responseFinalizado);

        mockMvc.perform(patch("/apifutbol/partidos/{id}/finalizar", idPartido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADO"))
                .andExpect(jsonPath("$.finalizado").value(true));

        verify(partidoUseCase).finalizarPartido(idPartido);
    }

    @Test
    @DisplayName("PATCH /apifutbol/partidos/{id}/estado - debe cambiar estado de un partido")
    void testCambiarEstado() throws Exception {
        EstadoPartido nuevoEstado = EstadoPartido.SUSPENDIDO;
        PartidoResponse responseSuspendido = PartidoResponse.builder()
                .idPartido(idPartido)
                .estado(EstadoPartido.SUSPENDIDO)
                .estadoDisplayName("Suspendido")
                .build();

        when(partidoUseCase.cambiarEstadoPartido(idPartido, nuevoEstado)).thenReturn(responseSuspendido);

        mockMvc.perform(patch("/apifutbol/partidos/{id}/estado", idPartido)
                        .param("nuevoEstado", nuevoEstado.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SUSPENDIDO"));

        verify(partidoUseCase).cambiarEstadoPartido(idPartido, nuevoEstado);
    }

    @Test
    @DisplayName("PATCH /apifutbol/partidos/{id}/cancelar - debe cancelar un partido")
    void testCancelar() throws Exception {
        doNothing().when(partidoUseCase).cancelarPartido(idPartido);

        mockMvc.perform(patch("/apifutbol/partidos/{id}/cancelar", idPartido))
                .andExpect(status().isNoContent());

        verify(partidoUseCase).cancelarPartido(idPartido);
    }

    @Test
    @DisplayName("POST /apifutbol/partidos/{id}/eventos - debe registrar un evento")
    void testRegistrarEvento() throws Exception {
        when(partidoUseCase.registrarEvento(eq(idPartido), any(RegistrarEventoRequest.class))).thenReturn(eventoResponse);

        mockMvc.perform(post("/apifutbol/partidos/{id}/eventos", idPartido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrarEventoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoEvento").value("GOL"))
                .andExpect(jsonPath("$.minutoFormateado").value("30'"));

        verify(partidoUseCase).registrarEvento(eq(idPartido), any(RegistrarEventoRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/partidos/{id}/eventos - debe listar eventos de un partido")
    void testObtenerEventos() throws Exception {
        when(partidoUseCase.obtenerEventosDePartido(idPartido)).thenReturn(List.of(eventoResponse));

        mockMvc.perform(get("/apifutbol/partidos/{id}/eventos", idPartido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoEvento").value("GOL"));

        verify(partidoUseCase).obtenerEventosDePartido(idPartido);
    }
}