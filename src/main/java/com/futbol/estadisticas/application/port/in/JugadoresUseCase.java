package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.ActualizarJugadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

public interface JugadoresUseCase {
    
    JugadorResponse crearJugador(CrearJugadorRequest request);
 
    JugadorResponse obtenerJugadorPorId(UUID idJugador);
 
    List<JugadorResponse> obtenerTodosLosJugadores();
 
    List<JugadorResponse> obtenerJugadoresPorClub(UUID idClub);
 
    List<JugadorResponse> obtenerJugadoresPorPosicion(PosicionJugador posicion);
 
    List<JugadorResponse> obtenerJugadoresDisponibles();
 
    List<JugadorResponse> obtenerJugadoresLesionados();
 
    JugadorResponse actualizarJugador(UUID idJugador, ActualizarJugadorRequest request);
 
    JugadorResponse cambiarEstadoJugador(UUID idJugador, EstadoJugador nuevoEstado);
 
    JugadorResponse actualizarValorMercado(UUID idJugador, Double nuevoValor);
 
    void eliminarJugador(UUID idJugador);
}
