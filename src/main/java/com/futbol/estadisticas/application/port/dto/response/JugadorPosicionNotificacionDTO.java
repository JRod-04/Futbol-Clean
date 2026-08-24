package com.futbol.estadisticas.application.port.dto.response;

import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

public record JugadorPosicionNotificacionDTO(
        Jugador jugador,
        PosicionJugador posicionNueva,
        boolean posicionCambiada
) {}