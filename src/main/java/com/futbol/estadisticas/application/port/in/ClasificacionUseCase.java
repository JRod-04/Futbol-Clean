package com.futbol.estadisticas.application.port.in;

import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.*;
import com.futbol.estadisticas.application.port.dto.response.LideresEstadisticos;
import com.futbol.estadisticas.application.port.dto.response.LideresEstadisticos.*;

import java.util.UUID;

public interface ClasificacionUseCase {
    Object obtenerTabla(UUID idCompeticion);
    LideresEstadisticosResponse obtenerLideresEstadisticos(UUID idCompeticion, int limit);
}
