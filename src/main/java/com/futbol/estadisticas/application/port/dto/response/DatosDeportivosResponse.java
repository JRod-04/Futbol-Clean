package com.futbol.estadisticas.application.port.dto.response;
import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

public record DatosDeportivosResponse (
        UUID idHistorialDeportivo,
        PosicionJugador posicion,
        String posicionDisplayName,
        EstadoJugador estadoJugador,
        Double valorMercado,
        double valorMercadoEnMillones,
        LocalDate fechaActualizacion,
        boolean esTitular,
        boolean esSuplente,
        boolean estaDisponible,
        boolean estaLesionado,
 
        UUID idJugador,
        String nombreJugador
){}
