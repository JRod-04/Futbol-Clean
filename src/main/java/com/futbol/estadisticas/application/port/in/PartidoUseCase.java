package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearPartidoRequest;
import com.futbol.estadisticas.application.port.dto.request.RegistrarEventoRequest;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;

public interface PartidoUseCase {
    
    PartidoResponse programarPartido(CrearPartidoRequest request);
 
    PartidoResponse obtenerPartidoPorId(UUID idPartido);
 
    List<PartidoResponse> obtenerPartidosPorCompeticion(UUID idCompeticion);
 
    List<PartidoResponse> obtenerPartidosPorClub(UUID idClub);
 
    PartidoResponse iniciarPartido(UUID idPartido);
 
    PartidoResponse cambiarEstadoPartido(UUID idPartido, EstadoPartido nuevoEstado);
 
    PartidoResponse finalizarPartido(UUID idPartido);
 
    EventoPartidoResponse registrarEvento(UUID idPartido, RegistrarEventoRequest request);
 
    List<EventoPartidoResponse> obtenerEventosDePartido(UUID idPartido);
 
    void cancelarPartido(UUID idPartido);
}
