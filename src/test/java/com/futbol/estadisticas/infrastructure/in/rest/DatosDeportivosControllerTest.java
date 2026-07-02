package com.futbol.estadisticas.infrastructure.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.futbol.estadisticas.application.port.dto.response.DatosDeportivosResponse;
import com.futbol.estadisticas.application.port.in.DatosDeportivosUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DatosDeportivosControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DatosDeportivosUseCase datosDeportivosUseCase;

    @InjectMocks
    private DatosDeportivosController datosDeportivosController;

    private UUID idJugador;
    private DatosDeportivosResponse response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(datosDeportivosController).build();

        idJugador = UUID.randomUUID();

        response = DatosDeportivosResponse.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .posicion(PosicionJugador.DELANTERO)
                .dorsal(10)
                .estadoJugador(EstadoJugador.TITULAR)
                .valorMercado(100000000.0)
                .valorMercadoEnMillones(100.0)
                .fechaActualizacion(LocalDate.now())
                .esTitular(true)
                .esSuplente(false)
                .estaDisponible(true)
                .estaLesionado(false)
                .idJugador(idJugador)
                .nombreJugador("Lionel Messi")
                .build();
    }

    @Test
    @DisplayName("GET /apifutbol/jugadores/{idJugador}/datos-deportivos - debe obtener datos deportivos")
    void testObtener() throws Exception {
        when(datosDeportivosUseCase.obtenerPorJugador(idJugador)).thenReturn(response);

        mockMvc.perform(get("/apifutbol/jugadores/{idJugador}/datos-deportivos", idJugador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idJugador").value(idJugador.toString()))
                .andExpect(jsonPath("$.posicion").value("DELANTERO"))
                .andExpect(jsonPath("$.dorsal").value(10))
                .andExpect(jsonPath("$.estadoJugador").value("TITULAR"));

        verify(datosDeportivosUseCase).obtenerPorJugador(idJugador);
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{idJugador}/datos-deportivos/valor-mercado - debe actualizar valor de mercado")
    void testActualizarValor() throws Exception {
        Double nuevoValor = 150000000.0;
        when(datosDeportivosUseCase.actualizarValorMercado(idJugador, nuevoValor)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{idJugador}/datos-deportivos/valor-mercado", idJugador)
                        .param("nuevoValor", String.valueOf(nuevoValor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorMercado").value(100000000.0));

        verify(datosDeportivosUseCase).actualizarValorMercado(idJugador, nuevoValor);
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{idJugador}/datos-deportivos/posicion - debe cambiar posición")
    void testCambiarPosicion() throws Exception {
        PosicionJugador nuevaPosicion = PosicionJugador.EXTREMO_DERECHO;
        when(datosDeportivosUseCase.cambiarPosicion(idJugador, nuevaPosicion)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{idJugador}/datos-deportivos/posicion", idJugador)
                        .param("nuevaPosicion", nuevaPosicion.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posicion").value("DELANTERO"));

        verify(datosDeportivosUseCase).cambiarPosicion(idJugador, nuevaPosicion);
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{idJugador}/datos-deportivos/dorsal - debe actualizar dorsal")
    void testActualizarDorsal() throws Exception {
        Integer nuevoDorsal = 11;
        when(datosDeportivosUseCase.actualizarDorsal(idJugador, nuevoDorsal)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{idJugador}/datos-deportivos/dorsal", idJugador)
                        .param("nuevoDorsal", String.valueOf(nuevoDorsal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dorsal").value(10));

        verify(datosDeportivosUseCase).actualizarDorsal(idJugador, nuevoDorsal);
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{idJugador}/datos-deportivos/promover-titular - debe promover a titular")
    void testPromoverATitular() throws Exception {
        when(datosDeportivosUseCase.promoverATitular(idJugador)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{idJugador}/datos-deportivos/promover-titular", idJugador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esTitular").value(true));

        verify(datosDeportivosUseCase).promoverATitular(idJugador);
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{idJugador}/datos-deportivos/pasar-suplente - debe pasar a suplente")
    void testPasarASuplente() throws Exception {
        when(datosDeportivosUseCase.cambiarASuplente(idJugador)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{idJugador}/datos-deportivos/pasar-suplente", idJugador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esTitular").value(true));

        verify(datosDeportivosUseCase).cambiarASuplente(idJugador);
    }

    @Test
    @DisplayName("PATCH /apifutbol/jugadores/{idJugador}/datos-deportivos/estado - debe actualizar estado")
    void testActualizarEstado() throws Exception {
        EstadoJugador nuevoEstado = EstadoJugador.LESIONADO;
        when(datosDeportivosUseCase.actualizarEstado(idJugador, nuevoEstado)).thenReturn(response);

        mockMvc.perform(patch("/apifutbol/jugadores/{idJugador}/datos-deportivos/estado", idJugador)
                        .param("nuevoEstado", nuevoEstado.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoJugador").value("TITULAR"));

        verify(datosDeportivosUseCase).actualizarEstado(idJugador, nuevoEstado);
    }
}