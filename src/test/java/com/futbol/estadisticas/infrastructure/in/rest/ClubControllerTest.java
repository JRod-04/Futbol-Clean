package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.request.CrearClubRequest;
import com.futbol.estadisticas.application.port.dto.response.ClubResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.ClubUseCase;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClubControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ClubUseCase clubUseCase;

    @InjectMocks
    private ClubController clubController;

    private UUID idClub;
    private ClubResponse response;
    private CrearClubRequest crearRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(clubController).build();

        idClub = UUID.randomUUID();

        response = ClubResponse.builder()
                .idEquipo(idClub)
                .nombre("FC Barcelona")
                .nombreCorto("Barça")
                .fechaFundacion(LocalDate.of(1899, 11, 29))
                .build();

        crearRequest = new CrearClubRequest(
                "FC Barcelona",
                "Barça",
                LocalDate.of(1899, 11, 29)
        );
    }

    @Test
    @DisplayName("POST /apifutbol/clubes - debe crear un club")
    void testCrear() throws Exception {
        when(clubUseCase.crearClub(any(CrearClubRequest.class))).thenReturn(response);

        mockMvc.perform(post("/apifutbol/clubes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEquipo").value(idClub.toString()))
                .andExpect(jsonPath("$.nombre").value("FC Barcelona"));

        verify(clubUseCase).crearClub(any(CrearClubRequest.class));
    }

    @Test
    @DisplayName("GET /apifutbol/clubes - debe listar todos los clubes")
    void testListarTodos() throws Exception {
        when(clubUseCase.obtenerTodosLosClubs()).thenReturn(List.of(response));

        mockMvc.perform(get("/apifutbol/clubes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEquipo").value(idClub.toString()))
                .andExpect(jsonPath("$[0].nombre").value("FC Barcelona"));

        verify(clubUseCase).obtenerTodosLosClubs();
    }

    @Test
    @DisplayName("GET /apifutbol/clubes/{id} - debe obtener club por ID")
    void testObtenerPorId() throws Exception {
        when(clubUseCase.obtenerClubPorId(idClub)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/clubes/{id}", idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEquipo").value(idClub.toString()))
                .andExpect(jsonPath("$.nombre").value("FC Barcelona"));

        verify(clubUseCase).obtenerClubPorId(idClub);
    }

    @Test
    @DisplayName("GET /apifutbol/clubes/{id}/jugadores - debe listar jugadores activos del club")
    void testJugadoresActivos() throws Exception {
        UUID idJugador = UUID.randomUUID();
        JugadorResponse jugadorResponse = JugadorResponse.builder()
                .idPersonal(idJugador)
                .nombre("Lionel")
                .apellido("Messi")
                .build();

        when(clubUseCase.obtenerJugadoresActivosDeClub(idClub)).thenReturn(List.of(jugadorResponse));

        mockMvc.perform(get("/apifutbol/clubes/{id}/jugadores", idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lionel"));

        verify(clubUseCase).obtenerJugadoresActivosDeClub(idClub);
    }

    @Test
    @DisplayName("GET /apifutbol/clubes/{id}/jugadores/disponibles - debe listar jugadores disponibles")
    void testJugadoresDisponibles() throws Exception {
        UUID idJugador = UUID.randomUUID();
        JugadorResponse jugadorResponse = JugadorResponse.builder()
                .idPersonal(idJugador)
                .nombre("Lionel")
                .apellido("Messi")
                .build();

        when(clubUseCase.obtenerJugadoresDisponiblesDeClub(idClub)).thenReturn(List.of(jugadorResponse));

        mockMvc.perform(get("/apifutbol/clubes/{id}/jugadores/disponibles", idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lionel"));

        verify(clubUseCase).obtenerJugadoresDisponiblesDeClub(idClub);
    }

    @Test
    @DisplayName("GET /apifutbol/clubes/{id}/valor-plantilla - debe obtener valor de plantilla")
    void testValorPlantilla() throws Exception {
        Double valor = 500000000.0;
        when(clubUseCase.obtenerValorPlantilla(idClub)).thenReturn(valor);

        mockMvc.perform(get("/apifutbol/clubes/{id}/valor-plantilla", idClub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(500000000.0));

        verify(clubUseCase).obtenerValorPlantilla(idClub);
    }

    @Test
    @DisplayName("DELETE /apifutbol/clubes/{id} - debe eliminar un club")
    void testEliminar() throws Exception {
        doNothing().when(clubUseCase).eliminarClub(idClub);

        mockMvc.perform(delete("/apifutbol/clubes/{id}", idClub))
                .andExpect(status().isNoContent());

        verify(clubUseCase).eliminarClub(idClub);
    }
}