package com.futbol.estadisticas.application.port.mapper;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.response.DatosDeportivosResponse;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;

@Component
public class DatosDeportivosMapper {
    
     public DatosDeportivosResponse toResponse(DatosDeportivos datos, Jugador jugador) {
        return new DatosDeportivosResponse(
                datos.getIdHistorialDeportivo(),
                datos.getPosicion(),
                datos.getPosicion() != null ? datos.getPosicion().getDisplayName() : null,
                datos.getEstadoJugador(),
                datos.getValorMercado(),
                datos.getValorMercadoEnMillones(),
                datos.getFechaActualizacion(),
                datos.esTitular(),
                datos.esSuplente(),
                datos.estaDisponible(),
                datos.estaLesionado(),
                jugador != null ? jugador.getIdPersonal() : null,
                jugador != null ? jugador.getNombreCompleto() : null
        );
    }
}
