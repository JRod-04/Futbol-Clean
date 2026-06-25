package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.estadisticas.application.port.dto.request.ActualizarTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.application.port.in.TecnicoUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/tecnicos")
@RequiredArgsConstructor
public class TecnicoController {

    private final TecnicoUseCase tecnicoUseCase;
 
    @PostMapping
    public ResponseEntity<TecnicoResponse> crear(@Valid @RequestBody CrearTecnicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tecnicoUseCase.crearTecnico(request));
    }
 
    @GetMapping
    public ResponseEntity<List<TecnicoResponse>> listarTodos() {
        return ResponseEntity.ok(tecnicoUseCase.obtenerTodosTecnicos());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<TecnicoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(tecnicoUseCase.obtenerTecnicoPorId(id));
    }
 
    @GetMapping("/club/{idClub}/actual")
    public ResponseEntity<TecnicoResponse> tecnicoActualDeClub(@PathVariable UUID idClub) {
        return ResponseEntity.ok(tecnicoUseCase.obtenerTecnicoActualDeClub(idClub));
    }
 
    @PatchMapping("/{id}")
    public ResponseEntity<TecnicoResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarTecnicoRequest request) {
        return ResponseEntity.ok(tecnicoUseCase.actualizarTecnico(id, request));
    }
 
    @PutMapping("/{id}/asignar-club/{idClub}")
    public ResponseEntity<TecnicoResponse> asignarAClub(
            @PathVariable UUID id,
            @PathVariable UUID idClub) {
        return ResponseEntity.ok(tecnicoUseCase.asignarTecnicoAClub(id, idClub));
    }
 
    @DeleteMapping("/club/{idClub}/desvincular")
    public ResponseEntity<Void> desvincularDeClub(@PathVariable UUID idClub) {
        tecnicoUseCase.desvincularTecnicoDeClub(idClub);
        return ResponseEntity.noContent().build();
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        tecnicoUseCase.eliminarTecnico(id);
        return ResponseEntity.noContent().build();
    }
}
