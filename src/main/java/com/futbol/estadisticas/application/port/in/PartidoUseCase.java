package com.futbol.estadisticas.application.port.in;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearPartidoRequest;
import com.futbol.estadisticas.application.port.dto.request.RealizarSustitucionRequest;
import com.futbol.estadisticas.application.port.dto.request.RegistrarEventoRequest;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.SustitucionResponse;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartidoUseCase {
    
    PartidoResponse programarPartido(CrearPartidoRequest request);

    List<PartidoResponse> programarPartidosBatch(List<CrearPartidoRequest> requests);

    Page<PartidoResponse> obtenerPartidosPorFecha(LocalDate fecha, int page, int size);

    PartidoResponse obtenerPartidoPorId(UUID idPartido);
 
    List<PartidoResponse> obtenerPartidosPorCompeticion(UUID idCompeticion);
 
    List<PartidoResponse> obtenerPartidosPorClub(UUID idClub);

    SustitucionResponse realizarSustitucion(UUID idPartido, RealizarSustitucionRequest request);

    PartidoResponse iniciarPartido(UUID idPartido);
 
    PartidoResponse cambiarEstadoPartido(UUID idPartido, EstadoPartido nuevoEstado);

    PartidoResponse avanzarPartido(UUID idPartido);

    EventoPartidoResponse agregarTiempoAgregado(UUID idPartido, int minutos, String descripcion);

    PartidoResponse finalizarTiempo(UUID idPartido, LocalTime minutoFin);

    PartidoResponse finalizarPartido(UUID idPartido);

    EventoPartidoResponse registrarEvento(UUID idPartido, RegistrarEventoRequest request);

    List<EventoPartidoResponse> registrarEventosBatch(UUID idPartido, List<RegistrarEventoRequest> requests);

    List<EventoPartidoResponse> obtenerEventosDePartido(UUID idPartido);
 
    void cancelarPartido(UUID idPartido);

    void eliminarPartido(UUID idPartido);

    void eliminarEvento(UUID idPartido, UUID idEvento);


}
