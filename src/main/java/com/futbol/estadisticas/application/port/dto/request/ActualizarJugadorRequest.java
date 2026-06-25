package com.futbol.estadisticas.application.port.dto.request;

import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

import jakarta.validation.constraints.Positive;

public record ActualizarJugadorRequest(String nombre,
        String apellido,
        JuegoPies pieHabil,
        Integer altura,
        Integer peso,
        PosicionJugador posicion,
 
        @Positive(message = "El valor de mercado debe ser positivo")
        Double valorMercado) {

}
