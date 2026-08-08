package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompeticionUseCase {

    Page<CompeticionResponse> buscarCompeticiones(String texto, Pageable pageable);

    CompeticionResponse crearCompeticion(CrearCompeticionRequest request);
 
    CompeticionResponse obtenerCompeticionPorId(UUID idCompeticion);

    List<EquipoResponse> obtenerEquiposParticipantes(UUID idCompeticion);

    List<CompeticionResponse> obtenerTodasLasCompeticiones();
 
    List<CompeticionResponse> obtenerCompeticionesActivas();

    CompeticionResponse actualizarEquipoGanador(UUID idCompeticion, UUID idEquipoGanador);

    List<PartidoResponse> obtenerPartidosPorCompeticion(UUID idCompeticion);
 
    List<PartidoResponse> obtenerPartidosPendientesPorCompeticion(UUID idCompeticion);
 
    Double obtenerPorcentajeAvance(UUID idCompeticion);
 
    void eliminarCompeticion(UUID idCompeticion);
}
