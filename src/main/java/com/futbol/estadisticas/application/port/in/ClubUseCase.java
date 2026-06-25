package com.futbol.estadisticas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearClubRequest;
import com.futbol.estadisticas.application.port.dto.response.ClubResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;

public interface ClubUseCase {
    ClubResponse crearClub(CrearClubRequest request);
 
    ClubResponse obtenerClubPorId(UUID idClub);
 
    List<ClubResponse> obtenerTodosLosClubs();
 
    List<JugadorResponse> obtenerJugadoresActivosDeClub(UUID idClub);
 
    List<JugadorResponse> obtenerJugadoresDisponiblesDeClub(UUID idClub);
 
    Double obtenerValorPlantilla(UUID idClub);
 
    void eliminarClub(UUID idClub);
}
