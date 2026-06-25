package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;

public interface CompeticionUseCase {
    
    CompeticionResponse crearCompeticion(CrearCompeticionRequest request);
 
    CompeticionResponse obtenerCompeticionPorId(UUID idCompeticion);
 
    List<CompeticionResponse> obtenerTodasLasCompeticiones();
 
    List<CompeticionResponse> obtenerCompeticionesActivas();
 
    List<PartidoResponse> obtenerPartidosPorCompeticion(UUID idCompeticion);
 
    List<PartidoResponse> obtenerPartidosPendientesPorCompeticion(UUID idCompeticion);
 
    Double obtenerPorcentajeAvance(UUID idCompeticion);
 
    void eliminarCompeticion(UUID idCompeticion);
}
