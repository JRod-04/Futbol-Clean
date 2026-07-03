// NUEVO Controller
package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.estadisticas.application.port.dto.response.ClasificacionDTO.*;
import com.futbol.estadisticas.application.port.in.ClasificacionUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/clasificacion")
@RequiredArgsConstructor
public class ClasificacionController {

    private final ClasificacionUseCase clasificacionUseCase;

    @GetMapping("/competicion/{idCompeticion}")
    public ResponseEntity<ClasificacionResponse> obtenerTabla(
            @PathVariable UUID idCompeticion) {
        return ResponseEntity.ok(clasificacionUseCase.obtenerTabla(idCompeticion));
    }
}