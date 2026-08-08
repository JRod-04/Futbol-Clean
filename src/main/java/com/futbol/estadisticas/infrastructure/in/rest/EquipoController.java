package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.futbol.estadisticas.application.port.dto.request.CrearEquipoRequest;
import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.EquipoUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/apifutbol/equipos")
@CrossOrigin(origins = "*")
public class EquipoController {

private final EquipoUseCase equipoUseCase;
 
    @PostMapping
    public ResponseEntity<EquipoResponse> crear(@Valid @RequestBody CrearEquipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(equipoUseCase.crearEquipo(request));
    }
 
    @GetMapping
    public ResponseEntity<List<EquipoResponse>> listarTodos() {
        return ResponseEntity.ok(equipoUseCase.obtenerTodosLosEquipos());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<EquipoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(equipoUseCase.obtenerEquipoPorId(id));
    }
 
    @GetMapping("/{id}/jugadores")
    public ResponseEntity<List<JugadorResponse>> jugadoresActivos(@PathVariable UUID id) {
        return ResponseEntity.ok(equipoUseCase.obtenerJugadoresActivosDeEquipo(id));
    }
 
    @GetMapping("/{id}/jugadores/disponibles")
    public ResponseEntity<List<JugadorResponse>> jugadoresDisponibles(@PathVariable UUID id) {
        return ResponseEntity.ok(equipoUseCase.obtenerJugadoresDisponiblesDeEquipo(id));
    }
    @GetMapping("/{id}/jugadores/titulares")
    public ResponseEntity<List<JugadorResponse>> jugadoresTitulares(@PathVariable UUID id) {
        return ResponseEntity.ok(equipoUseCase.obtenerTitulares(id));
    }

    @GetMapping("{idEquipo}/competiciones")
    public ResponseEntity<List<CompeticionResponse>> obtenerCompeticionesDelEquipo(
            @PathVariable UUID idEquipo) {
        return ResponseEntity.ok(equipoUseCase.obtenerCompeticionesPorEquipo(idEquipo));
    }

    @GetMapping("/{id}/valor-plantilla")
    public ResponseEntity<Double> valorPlantilla(@PathVariable UUID id) {
        return ResponseEntity.ok(equipoUseCase.obtenerValorPlantilla(id));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        equipoUseCase.eliminarEquipo(id);
        return ResponseEntity.noContent().build();
    }
}
