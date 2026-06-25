package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearArbitroRequest;
import com.futbol.estadisticas.application.port.dto.response.ArbitroResponse;

public interface ArbitroUseCase {
    
    ArbitroResponse crearArbitro(CrearArbitroRequest request);
 
    ArbitroResponse obtenerArbitroPorId(UUID idArbitro);
 
    List<ArbitroResponse> obtenerTodosLosArbitros();
 
    List<ArbitroResponse> buscarArbitrosPorNombre(String termino);
 
    void eliminarArbitro(UUID idArbitro);
}
