package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.ActualizarEstadioRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearEstadioRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadioResponse;

public interface EstadioUseCase {
    EstadioResponse crearEstadio(CrearEstadioRequest request);
 
    EstadioResponse obtenerEstadioPorId(UUID idEstadio);
 
    List<EstadioResponse> obtenerTodosLosEstadios();
 
    EstadioResponse actualizarEstadio(UUID idEstadio, ActualizarEstadioRequest request);
 
    EstadioResponse asignarEstadioAClub(UUID idEstadio, UUID idClub);
 
    double calcularPorcentajeOcupacion(UUID idEstadio, Integer espectadores);
 
    void eliminarEstadio(UUID idEstadio);
}
