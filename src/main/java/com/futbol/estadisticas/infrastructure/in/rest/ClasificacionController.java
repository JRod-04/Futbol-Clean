// NUEVO Controller
package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.*;
import com.futbol.estadisticas.application.port.in.ClasificacionUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/clasificacion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClasificacionController {

    private final ClasificacionUseCase clasificacionUseCase;

    @GetMapping("/{idCompeticion}")
    public ResponseEntity<ClasificacionResponse> obtenerTabla(
            @PathVariable UUID idCompeticion) {
        return ResponseEntity.ok(clasificacionUseCase.obtenerTabla(idCompeticion));
    }
}