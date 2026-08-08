package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearEquipoRequest;
import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipoUseCase {

    Page<EquipoResponse> buscarEquipos(String texto, Pageable pageable);

    EquipoResponse crearEquipo(CrearEquipoRequest request);

    List<CompeticionResponse> obtenerCompeticionesPorEquipo(UUID idEquipo);

    EquipoResponse obtenerEquipoPorId(UUID idEquipo);
 
    List<EquipoResponse> obtenerTodosLosEquipos();
 
    List<JugadorResponse> obtenerJugadoresActivosDeEquipo(UUID idEquipo);

    List<JugadorResponse> obtenerTitulares(UUID idEquipo);

    List<JugadorResponse> obtenerJugadoresDisponiblesDeEquipo(UUID idEquipo);
 
    Double obtenerValorPlantilla(UUID idEquipo);
 
    void eliminarEquipo(UUID idEquipo);
}
