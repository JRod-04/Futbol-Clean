package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.futbol.estadisticas.application.port.dto.request.ActualizarEstadioRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearEstadioRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadioResponse;
import com.futbol.estadisticas.application.port.in.EstadioUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/apifutbol/estadios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class EstadioController {

       private final EstadioUseCase estadioUseCase;
 
    @PostMapping
    public ResponseEntity<EstadioResponse> crear(@Valid @RequestBody CrearEstadioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(estadioUseCase.crearEstadio(request));
    }
 
    @GetMapping
    public ResponseEntity<List<EstadioResponse>> listarTodos() {
        return ResponseEntity.ok(estadioUseCase.obtenerTodosLosEstadios());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<EstadioResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(estadioUseCase.obtenerEstadioPorId(id));
    }
 
    @PatchMapping("/{id}")
    public ResponseEntity<EstadioResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEstadioRequest request) {
        return ResponseEntity.ok(estadioUseCase.actualizarEstadio(id, request));
    }
 
    @PutMapping("/{id}/asignar-equipo/{idEquipo}")
    public ResponseEntity<EstadioResponse> asignarAClub(
            @PathVariable UUID id,
            @PathVariable UUID idEquipo) {
        return ResponseEntity.ok(estadioUseCase.asignarEstadioAEquipo(id, idEquipo));
    }
 
    @GetMapping("/{id}/ocupacion")
    public ResponseEntity<Double> calcularOcupacion(
            @PathVariable UUID id,
            @RequestParam Integer espectadores) {
        return ResponseEntity.ok(estadioUseCase.calcularPorcentajeOcupacion(id, espectadores));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        estadioUseCase.eliminarEstadio(id);
        return ResponseEntity.noContent().build();
    }
}
