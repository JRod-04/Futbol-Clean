package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.ActualizarTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;

public interface TecnicoUseCase {

    TecnicoResponse crearTecnico(CrearTecnicoRequest request);
 
    TecnicoResponse obtenerTecnicoPorId(UUID idTecnico);
 
    List<TecnicoResponse> obtenerTodosTecnicos();
 
    TecnicoResponse obtenerTecnicoActualDeClub(UUID idClub);
 
    TecnicoResponse actualizarTecnico(UUID idTecnico, ActualizarTecnicoRequest request);
 
    TecnicoResponse asignarTecnicoAClub(UUID idTecnico, UUID idClub);
 
    void desvincularTecnicoDeClub(UUID idClub);
 
    void eliminarTecnico(UUID idTecnico);
}
