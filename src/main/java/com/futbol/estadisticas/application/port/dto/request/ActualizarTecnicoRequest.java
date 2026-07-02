package com.futbol.estadisticas.application.port.dto.request;

import lombok.Builder;

@Builder
public record ActualizarTecnicoRequest(
        String nombre,
        String apellido,
        String estiloJuego,
        String alineacionFavorita) {

}
