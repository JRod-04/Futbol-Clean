package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.in.CompeticionUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/competiciones")
@RequiredArgsConstructor
public class CompeticionController {

       private final CompeticionUseCase competicionUseCase;
 
    @PostMapping
    public ResponseEntity<CompeticionResponse> crear(
            @Valid @RequestBody CrearCompeticionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(competicionUseCase.crearCompeticion(request));
    }
 
    @GetMapping
    public ResponseEntity<List<CompeticionResponse>> listarTodas() {
        return ResponseEntity.ok(competicionUseCase.obtenerTodasLasCompeticiones());
    }
 
    @GetMapping("/activas")
    public ResponseEntity<List<CompeticionResponse>> activas() {
        return ResponseEntity.ok(competicionUseCase.obtenerCompeticionesActivas());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<CompeticionResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(competicionUseCase.obtenerCompeticionPorId(id));
    }
 
    @GetMapping("/{id}/partidos")
    public ResponseEntity<List<PartidoResponse>> partidos(@PathVariable UUID id) {
        return ResponseEntity.ok(competicionUseCase.obtenerPartidosPorCompeticion(id));
    }
 
    @GetMapping("/{id}/partidos/pendientes")
    public ResponseEntity<List<PartidoResponse>> pendientes(@PathVariable UUID id) {
        return ResponseEntity.ok(competicionUseCase.obtenerPartidosPendientesPorCompeticion(id));
    }
 
    @GetMapping("/{id}/avance")
    public ResponseEntity<Double> porcentajeAvance(@PathVariable UUID id) {
        return ResponseEntity.ok(competicionUseCase.obtenerPorcentajeAvance(id));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        competicionUseCase.eliminarCompeticion(id);
        return ResponseEntity.noContent().build();
    }
}
