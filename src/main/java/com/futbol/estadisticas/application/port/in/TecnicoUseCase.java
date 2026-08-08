package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.ActualizarTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TecnicoUseCase {

    Page<TecnicoResponse> buscarTecnicos(String texto, Pageable pageable);

    TecnicoResponse crearTecnico(CrearTecnicoRequest request);
 
    TecnicoResponse obtenerTecnicoPorId(UUID idTecnico);
 
    List<TecnicoResponse> obtenerTodosTecnicos();
 
    TecnicoResponse obtenerTecnicoActualDeEquipo(UUID idEquipo);
 
    TecnicoResponse actualizarTecnico(UUID idTecnico, ActualizarTecnicoRequest request);

    void eliminarTecnico(UUID idTecnico);
}
