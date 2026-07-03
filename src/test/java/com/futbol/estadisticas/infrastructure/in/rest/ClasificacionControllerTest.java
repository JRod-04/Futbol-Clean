package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.ClasificacionResponse;
import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.EquipoClasificacion;
import com.futbol.estadisticas.application.port.in.ClasificacionUseCase;
import com.futbol.estadisticas.domain.model.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClasificacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClasificacionUseCase clasificacionUseCase;

    @InjectMocks
    private ClasificacionController clasificacionController;

    private ObjectMapper objectMapper;
    private UUID idCompeticion;
    private ClasificacionResponse response;

    @BeforeEach
    void setUp() {
        // ✅ IMPORTANTE: Registrar el GlobalExceptionHandler
        mockMvc = MockMvcBuilders
                .standaloneSetup(clasificacionController)
                .setControllerAdvice(new GlobalExceptionHandler())  // 👈 Esto es clave
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        idCompeticion = UUID.randomUUID();

        // Crear datos de prueba para la tabla
        EquipoClasificacion equipo1 = new EquipoClasificacion(
                UUID.randomUUID(),
                "Arsenal FC",
                5,
                4,
                1,
                0,
                12,
                3,
                9,
                13
        );

        EquipoClasificacion equipo2 = new EquipoClasificacion(
                UUID.randomUUID(),
                "Manchester City",
                5,
                3,
                2,
                0,
                10,
                4,
                6,
                11
        );

        EquipoClasificacion equipo3 = new EquipoClasificacion(
                UUID.randomUUID(),
                "Liverpool FC",
                5,
                3,
                1,
                1,
                8,
                5,
                3,
                10
        );

        response = new ClasificacionResponse(
                idCompeticion,
                "Premier League 2025-2026",
                List.of(equipo1, equipo2, equipo3)
        );
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe obtener la tabla de clasificación")
    void testObtenerTabla() throws Exception {
        when(clasificacionUseCase.obtenerTabla(idCompeticion)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompeticion").value(idCompeticion.toString()))
                .andExpect(jsonPath("$.nombreCompeticion").value("Premier League 2025-2026"))
                .andExpect(jsonPath("$.tabla").isArray())
                .andExpect(jsonPath("$.tabla.length()").value(3))
                .andExpect(jsonPath("$.tabla[0].nombreClub").value("Arsenal FC"))
                .andExpect(jsonPath("$.tabla[0].partidosJugados").value(5))
                .andExpect(jsonPath("$.tabla[0].ganados").value(4))
                .andExpect(jsonPath("$.tabla[0].empatados").value(1))
                .andExpect(jsonPath("$.tabla[0].perdidos").value(0))
                .andExpect(jsonPath("$.tabla[0].golesFavor").value(12))
                .andExpect(jsonPath("$.tabla[0].golesContra").value(3))
                .andExpect(jsonPath("$.tabla[0].diferenciaGoles").value(9))
                .andExpect(jsonPath("$.tabla[0].puntos").value(13));

        verify(clasificacionUseCase).obtenerTabla(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe manejar tabla vacía")
    void testObtenerTabla_Vacia() throws Exception {
        ClasificacionResponse responseVacia = new ClasificacionResponse(
                idCompeticion,
                "Premier League 2025-2026",
                List.of()
        );

        when(clasificacionUseCase.obtenerTabla(idCompeticion)).thenReturn(responseVacia);

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompeticion").value(idCompeticion.toString()))
                .andExpect(jsonPath("$.nombreCompeticion").value("Premier League 2025-2026"))
                .andExpect(jsonPath("$.tabla").isArray())
                .andExpect(jsonPath("$.tabla.length()").value(0));

        verify(clasificacionUseCase).obtenerTabla(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe manejar competición con un solo equipo")
    void testObtenerTabla_UnEquipo() throws Exception {
        EquipoClasificacion unEquipo = new EquipoClasificacion(
                UUID.randomUUID(),
                "Chelsea FC",
                3,
                3,
                0,
                0,
                9,
                1,
                8,
                9
        );

        ClasificacionResponse responseUnEquipo = new ClasificacionResponse(
                idCompeticion,
                "Premier League 2025-2026",
                List.of(unEquipo)
        );

        when(clasificacionUseCase.obtenerTabla(idCompeticion)).thenReturn(responseUnEquipo);

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tabla.length()").value(1))
                .andExpect(jsonPath("$.tabla[0].nombreClub").value("Chelsea FC"))
                .andExpect(jsonPath("$.tabla[0].partidosJugados").value(3))
                .andExpect(jsonPath("$.tabla[0].ganados").value(3))
                .andExpect(jsonPath("$.tabla[0].puntos").value(9));

        verify(clasificacionUseCase).obtenerTabla(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe manejar competición con empates")
    void testObtenerTabla_Empates() throws Exception {
        EquipoClasificacion equipo1 = new EquipoClasificacion(
                UUID.randomUUID(),
                "Arsenal FC",
                4,
                2,
                2,
                0,
                6,
                3,
                3,
                8
        );

        EquipoClasificacion equipo2 = new EquipoClasificacion(
                UUID.randomUUID(),
                "Manchester City",
                4,
                2,
                2,
                0,
                5,
                2,
                3,
                8
        );

        ClasificacionResponse responseEmpates = new ClasificacionResponse(
                idCompeticion,
                "Premier League 2025-2026",
                List.of(equipo1, equipo2)
        );

        when(clasificacionUseCase.obtenerTabla(idCompeticion)).thenReturn(responseEmpates);

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tabla[0].nombreClub").value("Arsenal FC"))
                .andExpect(jsonPath("$.tabla[0].empatados").value(2))
                .andExpect(jsonPath("$.tabla[0].puntos").value(8))
                .andExpect(jsonPath("$.tabla[1].nombreClub").value("Manchester City"))
                .andExpect(jsonPath("$.tabla[1].empatados").value(2))
                .andExpect(jsonPath("$.tabla[1].puntos").value(8));

        verify(clasificacionUseCase).obtenerTabla(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe manejar diferencia de goles negativa")
    void testObtenerTabla_DiferenciaNegativa() throws Exception {
        EquipoClasificacion equipo1 = new EquipoClasificacion(
                UUID.randomUUID(),
                "Arsenal FC",
                5,
                2,
                1,
                2,
                5,
                7,
                -2,
                7
        );

        EquipoClasificacion equipo2 = new EquipoClasificacion(
                UUID.randomUUID(),
                "Manchester City",
                5,
                2,
                0,
                3,
                4,
                8,
                -4,
                6
        );

        ClasificacionResponse responseNegativa = new ClasificacionResponse(
                idCompeticion,
                "Premier League 2025-2026",
                List.of(equipo1, equipo2)
        );

        when(clasificacionUseCase.obtenerTabla(idCompeticion)).thenReturn(responseNegativa);

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tabla[0].diferenciaGoles").value(-2))
                .andExpect(jsonPath("$.tabla[1].diferenciaGoles").value(-4));

        verify(clasificacionUseCase).obtenerTabla(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe manejar valores nulos o cero")
    void testObtenerTabla_ValoresCero() throws Exception {
        EquipoClasificacion equipo = new EquipoClasificacion(
                UUID.randomUUID(),
                "Newcastle United",
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );

        ClasificacionResponse responseCero = new ClasificacionResponse(
                idCompeticion,
                "Premier League 2025-2026",
                List.of(equipo)
        );

        when(clasificacionUseCase.obtenerTabla(idCompeticion)).thenReturn(responseCero);

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tabla[0].partidosJugados").value(0))
                .andExpect(jsonPath("$.tabla[0].ganados").value(0))
                .andExpect(jsonPath("$.tabla[0].empatados").value(0))
                .andExpect(jsonPath("$.tabla[0].perdidos").value(0))
                .andExpect(jsonPath("$.tabla[0].golesFavor").value(0))
                .andExpect(jsonPath("$.tabla[0].golesContra").value(0))
                .andExpect(jsonPath("$.tabla[0].diferenciaGoles").value(0))
                .andExpect(jsonPath("$.tabla[0].puntos").value(0));

        verify(clasificacionUseCase).obtenerTabla(idCompeticion);
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe manejar error 404 cuando la competición no existe")
    void testObtenerTabla_CompeticionNoExiste() throws Exception {
        UUID idCompeticionInexistente = UUID.randomUUID();

        when(clasificacionUseCase.obtenerTabla(idCompeticionInexistente))
                .thenThrow(new IllegalArgumentException("Competición no encontrada con id: " + idCompeticionInexistente));

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticionInexistente))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Competición no encontrada con id: " + idCompeticionInexistente))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(clasificacionUseCase).obtenerTabla(idCompeticionInexistente);
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe manejar error 400 cuando el argumento es inválido")
    void testObtenerTabla_ArgumentoInvalido() throws Exception {
        UUID idCompeticionInvalido = UUID.randomUUID();

        when(clasificacionUseCase.obtenerTabla(idCompeticionInvalido))
                .thenThrow(new IllegalArgumentException("ID de competición inválido"));

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticionInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("ID de competición inválido"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(clasificacionUseCase).obtenerTabla(idCompeticionInvalido);
    }

    @Test
    @DisplayName("GET /apifutbol/clasificacion/competicion/{idCompeticion} - debe manejar nombre de competición largo")
    void testObtenerTabla_NombreLargo() throws Exception {
        String nombreLargo = "Premier League 2025-2026 - Temporada Regular - Liga de Fútbol Profesional Inglesa";

        EquipoClasificacion equipo = new EquipoClasificacion(
                UUID.randomUUID(),
                "Arsenal FC",
                10,
                8,
                1,
                1,
                25,
                7,
                18,
                25
        );

        ClasificacionResponse responseLargo = new ClasificacionResponse(
                idCompeticion,
                nombreLargo,
                List.of(equipo)
        );

        when(clasificacionUseCase.obtenerTabla(idCompeticion)).thenReturn(responseLargo);

        mockMvc.perform(get("/apifutbol/clasificacion/competicion/{idCompeticion}", idCompeticion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompeticion").value(nombreLargo))
                .andExpect(jsonPath("$.tabla[0].nombreClub").value("Arsenal FC"));

        verify(clasificacionUseCase).obtenerTabla(idCompeticion);
    }
}