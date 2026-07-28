package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.RegistrarLesionRequest;
import com.futbol.estadisticas.application.port.dto.response.LesionResponse;
import com.futbol.estadisticas.domain.model.enums.Gravedad;

public interface LesionUseCase {
    
    LesionResponse registrarLesion(RegistrarLesionRequest request);

    List<LesionResponse> registrarVariasLesiones(List<RegistrarLesionRequest> requests);

    LesionResponse obtenerLesionPorId(UUID idLesion);
 
    List<LesionResponse> obtenerLesionesPorJugador(UUID idJugador);
 
    List<LesionResponse> obtenerLesionesActivasPorJugador(UUID idJugador);
 
    List<LesionResponse> obtenerLesionesActivasEnSistema();
 
    List<LesionResponse> obtenerLesionesPorGravedad(Gravedad gravedad);
 
    LesionResponse curarLesion(UUID idLesion);
}
