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
 
    // POST /api/v1/jugadores/{idJugador}/lesiones
    @PostMapping("/jugadores/{idJugador}/lesiones")
    public ResponseEntity<LesionResponse> registrar(
            @PathVariable UUID idJugador,
            @Valid @RequestBody RegistrarLesionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lesionUseCase.registrarLesion(idJugador, request));
    }
 
    // GET /api/v1/jugadores/{idJugador}/lesiones
    @GetMapping("/jugadores/{idJugador}/lesiones")
    public ResponseEntity<List<LesionResponse>> porJugador(@PathVariable UUID idJugador) {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionesPorJugador(idJugador));
    }
 
    // GET /api/v1/jugadores/{idJugador}/lesiones/activas
    @GetMapping("/jugadores/{idJugador}/lesiones/activas")
    public ResponseEntity<List<LesionResponse>> activasPorJugador(@PathVariable UUID idJugador) {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionesActivasPorJugador(idJugador));
    }
 
    // GET /api/v1/lesiones/{id}
    @GetMapping("/lesiones/{id}")
    public ResponseEntity<LesionResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionPorId(id));
    }
 
    // GET /api/v1/lesiones/activas
    @GetMapping("/lesiones/activas")
    public ResponseEntity<List<LesionResponse>> todasLasActivas() {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionesActivasEnSistema());
    }
 
    // GET /api/v1/lesiones/gravedad/{gravedad}
    @GetMapping("/lesiones/gravedad/{gravedad}")
    public ResponseEntity<List<LesionResponse>> porGravedad(@PathVariable Gravedad gravedad) {
        return ResponseEntity.ok(lesionUseCase.obtenerLesionesPorGravedad(gravedad));
    }
 
    // PATCH /api/v1/lesiones/{id}/curar
    @PatchMapping("/lesiones/{id}/curar")
    public ResponseEntity<LesionResponse> curar(@PathVariable UUID id) {
        return ResponseEntity.ok(lesionUseCase.curarLesion(id));
    }
}
