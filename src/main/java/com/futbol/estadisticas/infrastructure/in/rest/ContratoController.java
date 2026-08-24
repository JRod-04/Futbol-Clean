package com.futbol.estadisticas.infrastructure.in.rest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.futbol.estadisticas.application.port.dto.request.CrearContratoRequest;
import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import com.futbol.estadisticas.application.port.in.ContratoUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/contratos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContratoController {

    private final ContratoUseCase contratoUseCase;
 
    @PostMapping
    public ResponseEntity<ContratoResponse> crear(@Valid @RequestBody CrearContratoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contratoUseCase.crearContrato(request));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ContratoResponse>> crearContratosBatch(
            @Valid @RequestBody List<CrearContratoRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contratoUseCase.crearVariosContratos(requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(contratoUseCase.obtenerContratoPorId(id));
    }
 
    @GetMapping("/personal/{idPersonal}")
    public ResponseEntity<List<ContratoResponse>> porPersonal(@PathVariable UUID idPersonal) {
        return ResponseEntity.ok(contratoUseCase.obtenerContratosPorPersonal(idPersonal));
    }
 
    @GetMapping("/personal/{idPersonal}/vigente")
    public ResponseEntity<ContratoResponse> vigenteDePersonal(@PathVariable UUID idPersonal) {
        return ResponseEntity.ok(contratoUseCase.obtenerContratoVigenteDePersonal(idPersonal));
    }
 
    @GetMapping("/equipo/{idEquipo}/vigentes")
    public ResponseEntity<List<ContratoResponse>> vigentesPorEquipo(@PathVariable UUID idEquipo) {
        return ResponseEntity.ok(contratoUseCase.obtenerContratosVigentesPorEquipo(idEquipo));
    }
 
    @PatchMapping("/{id}/renovar")
    public ResponseEntity<ContratoResponse> renovar(
            @PathVariable UUID id,
            @RequestParam int meses) {
        return ResponseEntity.ok(contratoUseCase.renovarContrato(id, meses));
    }
 
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ContratoResponse> finalizar(@PathVariable UUID id, @RequestParam LocalDateTime fechaFin) {
        return ResponseEntity.ok(contratoUseCase.finalizarContrato(id, fechaFin));
    }
 
    @PatchMapping("/{id}/rescindir")
    public ResponseEntity<ContratoResponse> rescindir(@PathVariable UUID id, @RequestParam LocalDateTime fechaRescindido) {
        contratoUseCase.rescindirContrato(id, fechaRescindido);
        return ResponseEntity.ok(contratoUseCase.rescindirContrato(id, fechaRescindido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        contratoUseCase.eliminarContrato(id);
        return ResponseEntity.noContent().build();
    }

}
