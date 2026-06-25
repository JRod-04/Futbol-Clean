package com.futbol.estadisticas.application.port.dto.request;

public record ActualizarTecnicoRequest(
        String nombre,
        String apellido,
        String estiloJuego,
        String alineacionFavorita) {

}
