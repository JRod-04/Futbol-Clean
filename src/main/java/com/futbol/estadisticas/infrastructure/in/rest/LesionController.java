package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.estadisticas.application.port.dto.request.RegistrarLesionRequest;
import com.futbol.estadisticas.application.port.dto.response.LesionResponse;
import com.futbol.estadisticas.application.port.in.LesionUseCase;
import com.futbol.estadisticas.domain.model.enums.Gravedad;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/lesiones")
@RequiredArgsConstructor
public class LesionController {

    private final LesionUseCase lesionUseCase;
 
    @PostMapping("/jugadores/{idJugador}/lesiones")
    public ResponseEntity<LesionResponse> registrar(
            @PathVariable UUID idJugador,
            @Valid @RequestBody RegistrarLesionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lesionUseCase.registrarLesion(idJugador, request));
    }
 
    @GetMapping("/jugadores/{idJugador}/lesiones")
    public ResponseEntity<List<LesionResponse>> porJugador(@PathVariable UUID idJugador) {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionesPorJugador(idJugador));
    }
 
    @GetMapping("/jugadores/{idJugador}/lesiones/activas")
    public ResponseEntity<List<LesionResponse>> activasPorJugador(@PathVariable UUID idJugador) {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionesActivasPorJugador(idJugador));
    }
 
    @GetMapping("/lesiones/{id}")
    public ResponseEntity<LesionResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionPorId(id));
    }
 
    @GetMapping("/lesiones/activas")
    public ResponseEntity<List<LesionResponse>> todasLasActivas() {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionesActivasEnSistema());
    }
 
    @GetMapping("/lesiones/gravedad/{gravedad}")
    public ResponseEntity<List<LesionResponse>> porGravedad(@PathVariable Gravedad gravedad) {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionesPorGravedad(gravedad));
    }
 
    @PatchMapping("/lesiones/{id}/curar")
    public ResponseEntity<LesionResponse> curar(@PathVariable UUID id) {
        return ResponseEntity.ok(lesionUseCase.curarLesion(id));
    }
}
