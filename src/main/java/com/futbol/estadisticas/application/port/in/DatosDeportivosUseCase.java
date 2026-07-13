package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.response.DatosDeportivosResponse;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

public interface DatosDeportivosUseCase {
    DatosDeportivosResponse obtenerPorJugador(UUID idJugador);
 
    DatosDeportivosResponse actualizarValorMercado(UUID idJugador, Double nuevoValor);
 
    DatosDeportivosResponse cambiarPosicion(UUID idJugador, PosicionJugador nuevaPosicion);

    DatosDeportivosResponse eliminarPosicion(UUID idJugador, PosicionJugador posicionAEliminar);

    List<PosicionJugador> obtenerPosiciones(UUID idJugador);

    DatosDeportivosResponse promoverATitular(UUID idJugador);
 
    DatosDeportivosResponse cambiarASuplente(UUID idJugador);
 
    DatosDeportivosResponse actualizarEstado(UUID idJugador, EstadoJugador nuevoEstado);
    
    DatosDeportivosResponse actualizarDorsal(UUID idJugador, Integer nuevoDorsal);

}
