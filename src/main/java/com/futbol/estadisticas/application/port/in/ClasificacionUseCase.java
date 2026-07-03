package com.futbol.estadisticas.application.port.in;

import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.*;

import java.util.UUID;

public interface ClasificacionUseCase {
    ClasificacionResponse obtenerTabla(UUID idCompeticion);
}
