package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.ActualizarJugadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.JugadoresUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JugadorControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private JugadoresUseCase jugadoresUseCase;

    @InjectMocks
    private JugadorController jugadorController;

    private UUID idJugador;
    private UUID idClub;
    private JugadorResponse response;
    private CrearJugadorRequest crearRequest;
    private ActualizarJugadorRequest actualizarRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mockMvc = MockMvcBuilders.standaloneSetup(jugadorController).build();

        idJugador = UUID.randomUUID();
        idClub = UUID.randomUUID();

        response = JugadorResponse.builder()
                .idPersonal(idJugador)
                .nombre("Lionel")
                .apellido("Messi")
                .nombreCompleto("Lionel Messi")
                .fechaNacimiento(LocalDate.of(1987, 6, 24))
                .edad(36)
                .nacionalidad(Nacion.ARGENTINA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(170)
                .peso(72)
                .posicion(PosicionJugador.DELANTERO)
                .dorsal(10)
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(100000000.0)
                .valorMercadoEnMillones(100.0)
                .clubActual("FC Barcelona")
                .idClubActual(idClub)
                .disponible(true)
                .lesionesActivas(0)
                .build();

        crearRequest = CrearJugadorRequest.builder()
                .nombre("Lionel")
                .apellido("Messi")
                .fechaNacimiento(LocalDate.of(1987, 6, 24))
                .nacionalidad(Nacion.ARGENTINA)
                .pieHabil(JuegoPies.ZURDO)
                .altura(170)
                .peso(72)
                .dorsal(10)
                .posicion(PosicionJugador.INTERIOR_DERECHO)
                .build();

        actualizarRequest = ActualizarJugadorRequest.builder()
                .nombre("Lionel Actualizado")
                .apellido("Messi Actualizado")
                .pieHabil(JuegoPies.DERECHO)
                .altura(175)
                .peso(75)
                .dorsal(11)
                .posicion(PosicionJugador.EXTREMO_DERECHO)
                .valorMercado(150000000.0)
                .build();
    }

    @Test
@DisplayName("POST /apifutbol/jugadores - debe crear un jugador")
void testCrear() throws Exception {
    String json = objectMapper.writeValueAsString(crearRequest);
    System.out.println("JSON ENVIADO: " + json);

    when(jugadoresUseCase.crearJugador(any(CrearJugadorRequest.class))).thenReturn(response);

    mockMvc.perform(post("/apifutbol/jugadores")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andDo(result -> System.out.println("RESPONSE: " + result.getResponse().getContentAsString()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idPersonal").value(idJugador.toString()))
            .andExpect(jsonPath("$.nombre").value("Lionel"));

    verify(jugadoresUseCase).crearJugador(any(CrearJugadorRequest.class));
}

    @Test
    @DisplayName("GET /apifutbol/jugadores - debe listar todos los jugadores")
    void testListarTodos() throws Exception {
        when(jugadoresUseCase.obtenerTodosLosJugadores()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/jugadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPersonal").value(idJugador.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Lionel"));

        verify(jugadoresUseCase).obtenerTodosLosJugadores();
    }

    @Test
    @DisplayName("GET /apifutbol/jugadores/{id} - debe obtener jugador por ID")
    void testObtenerPorId() throws Exception {
        when(jugadoresUseCase.obtenerJugadorPorId(idJugador)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/jugadores/{id}", idJugador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(idJugador.toString()))
                .andExpect(jsonPath("$.nombre").value("Lionel"));

        verify(jugadoresUseCase).obtenerJugadorPorId(idJugador);
    }

    @Test
    @DisplayName("GET /apifutbol/jugadores/club/{idClub} - debe listar jugadores por club")
    void testPorClub() throws Exception {
        when(jugadoresUseCase.obtenerJugadoresPorClub(idClub)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/jugadores/club/{idClub}", idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clubActual").value("FC Barcelona"));

        verify(jugadoresUseCase).obtenerJugadoresPorClub(idClub);
    }

    @Test
    @DisplayName("GET /apifutbol/jugadores/posicion/{posicion} - debe listar jugadores por posición")
    void testPorPosicion() throws Exception {
        PosicionJugador posicion = PosicionJugador.DELANTERO;
        when(jugadoresUseCase.obtenerJugadoresPorPosicion(posicion)).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/jugadores/posicion/{posicion}", posicion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].posicion").value("DELANTERO"));

        verify(jugadoresUseCase).obtenerJugadoresPorPosicion(posicion);
    }

    @Test
    @DisplayName("GET /apifutbol/jugadores/disponibles - debe listar jugadores disponibles")
    void testDisponibles() throws Exception {
        when(jugadoresUseCase.obtenerJugadoresDisponibles()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/jugadores/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disponible").value(true));

        verify(jugadoresUseCase).obtenerJugadoresDisponibles();
    }

    @Test
    @DisplayName("GET /apifutbol/jugadores/lesionados - debe listar jugadores lesionados")
    void testLesionados() throws Exception {
        when(jugadoresUseCase.obtenerJugadoresLesionados()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/jugadores/lesionados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lesionesActivas").value(0));

        verify(jugadoresUseCase).obtenerJugadoresLesionados();
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{id} - debe actualizar un jugador")
    void testActualizar() throws Exception {
        when(jugadoresUseCase.actualizarJugador(eq(idJugador), any(ActualizarJugadorRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{id}", idJugador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(idJugador.toString()))
                .andExpect(jsonPath("$.nombre").value("Lionel"));

        verify(jugadoresUseCase).actualizarJugador(eq(idJugador), any(ActualizarJugadorRequest.class));
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{id}/estado - debe cambiar estado del jugador")
    void testCambiarEstado() throws Exception {
        EstadoJugador nuevoEstado = EstadoJugador.SUPLENTE;
        when(jugadoresUseCase.cambiarEstadoJugador(idJugador, nuevoEstado)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{id}/estado", idJugador)
                        .param("nuevoEstado", nuevoEstado.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoJugador").value("TITULAR"));

        verify(jugadoresUseCase).cambiarEstadoJugador(idJugador, nuevoEstado);
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{id}/valor-mercado - debe actualizar valor de mercado")
    void testActualizarValorMercado() throws Exception {
        Double valor = 150000000.0;
        when(jugadoresUseCase.actualizarValorMercado(idJugador, valor)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{id}/valor-mercado", idJugador)
                        .param("valor", String.valueOf(valor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorMercado").value(100000000.0));

        verify(jugadoresUseCase).actualizarValorMercado(idJugador, valor);
    }

    @Test
    @DisplayName("DELETE /apifutbol/jugadores/{id} - debe eliminar un jugador")
    void testEliminar() throws Exception {
        doNothing().when(jugadoresUseCase).eliminarJugador(idJugador);

        mockMvc.perform(delete("/apifutbol/jugadores/{id}", idJugador))
                .andExpect(status().isNoContent());

        verify(jugadoresUseCase).eliminarJugador(idJugador);
    }
}