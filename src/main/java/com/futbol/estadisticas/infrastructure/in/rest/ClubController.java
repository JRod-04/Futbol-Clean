package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.futbol.estadisticas.application.port.dto.request.CrearClubRequest;
import com.futbol.estadisticas.application.port.dto.response.ClubResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.ClubUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/apifutbol/clubes")
@CrossOrigin(origins = "*")
public class ClubController {

private final ClubUseCase clubUseCase;
 
    @PostMapping
    public ResponseEntity<ClubResponse> crear(@Valid @RequestBody CrearClubRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clubUseCase.crearClub(request));
    }
 
    @GetMapping
    public ResponseEntity<List<ClubResponse>> listarTodos() {
        return ResponseEntity.ok(clubUseCase.obtenerTodosLosClubs());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<ClubResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(clubUseCase.obtenerClubPorId(id));
    }
 
    @GetMapping("/{id}/jugadores")
    public ResponseEntity<List<JugadorResponse>> jugadoresActivos(@PathVariable UUID id) {
        return ResponseEntity.ok(clubUseCase.obtenerJugadoresActivosDeClub(id));
    }
 
    @GetMapping("/{id}/jugadores/disponibles")
    public ResponseEntity<List<JugadorResponse>> jugadoresDisponibles(@PathVariable UUID id) {
        return ResponseEntity.ok(clubUseCase.obtenerJugadoresDisponiblesDeClub(id));
    }
    @GetMapping("/{id}/jugadores/titulares")
    public ResponseEntity<List<JugadorResponse>> jugadoresTitulares(@PathVariable UUID id) {
        return ResponseEntity.ok(clubUseCase.obtenerTitulares(id));
    }

    @GetMapping("{idClub}/competiciones")
    public ResponseEntity<List<CompeticionResponse>> obtenerCompeticionesDelClub(
            @PathVariable UUID idClub) {
        return ResponseEntity.ok(clubUseCase.obtenerCompeticionesPorClub(idClub));
    }

    @GetMapping("/{id}/valor-plantilla")
    public ResponseEntity<Double> valorPlantilla(@PathVariable UUID id) {
        return ResponseEntity.ok(clubUseCase.obtenerValorPlantilla(id));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        clubUseCase.eliminarClub(id);
        return ResponseEntity.noContent().build();
    }
}
