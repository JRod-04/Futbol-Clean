package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.ActualizarJugadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasPartidoJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JugadoresUseCase {

    Page<JugadorResponse> buscarJugadores(String texto, Pageable pageable);

    JugadorResponse crearJugador(CrearJugadorRequest request);

    List<JugadorResponse> crearVariosJugadores(List<CrearJugadorRequest> requests);

    EstadisticasJugadorResponse obtenerEstadisticasJugador(UUID idJugador);

    List<EstadisticasPartidoJugadorResponse> obtenerPartidosConEstadisticas(UUID idJugador);

    JugadorResponse obtenerJugadorPorId(UUID idJugador);
 
    List<JugadorResponse> obtenerTodosLosJugadores();
 
    List<JugadorResponse> obtenerJugadoresPorEquipo(UUID idEquipo);
 
    List<JugadorResponse> obtenerJugadoresPorPosicion(PosicionJugador posicion);
 
    List<JugadorResponse> obtenerJugadoresDisponibles();
 
    List<JugadorResponse> obtenerJugadoresLesionados();
 
    JugadorResponse actualizarJugador(UUID idJugador, ActualizarJugadorRequest request);
 
    JugadorResponse cambiarEstadoJugador(UUID idJugador, EstadoJugador nuevoEstado);
 
    JugadorResponse actualizarValorMercado(UUID idJugador, Double nuevoValor);
 
    void eliminarJugador(UUID idJugador);
}
