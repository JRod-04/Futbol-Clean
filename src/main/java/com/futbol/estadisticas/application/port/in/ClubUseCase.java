package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearClubRequest;
import com.futbol.estadisticas.application.port.dto.response.ClubResponse;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClubUseCase {

    Page<ClubResponse> buscarClubes(String texto, Pageable pageable);

    ClubResponse crearClub(CrearClubRequest request);

    List<CompeticionResponse> obtenerCompeticionesPorClub(UUID idClub);

    ClubResponse obtenerClubPorId(UUID idClub);
 
    List<ClubResponse> obtenerTodosLosClubs();
 
    List<JugadorResponse> obtenerJugadoresActivosDeClub(UUID idClub);

    List<JugadorResponse> obtenerTitulares(UUID idClub);

    List<JugadorResponse> obtenerJugadoresDisponiblesDeClub(UUID idClub);
 
    Double obtenerValorPlantilla(UUID idClub);
 
    void eliminarClub(UUID idClub);
}
