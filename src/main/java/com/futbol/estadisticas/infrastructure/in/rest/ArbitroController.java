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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.estadisticas.application.port.dto.request.CrearArbitroRequest;
import com.futbol.estadisticas.application.port.dto.response.ArbitroResponse;
import com.futbol.estadisticas.application.port.in.ArbitroUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/arbitros")
@RequiredArgsConstructor
public class ArbitroController {

    private final ArbitroUseCase arbitroUseCase;
 
    @PostMapping
    public ResponseEntity<ArbitroResponse> crear(@Valid @RequestBody CrearArbitroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(arbitroUseCase.crearArbitro(request));
    }
 
    @GetMapping
    public ResponseEntity<List<ArbitroResponse>> listarTodos() {
        return ResponseEntity.ok(arbitroUseCase.obtenerTodosLosArbitros());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<ArbitroResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(arbitroUseCase.obtenerArbitroPorId(id));
    }
 
    @GetMapping("/buscar")
    public ResponseEntity<List<ArbitroResponse>> buscarPorNombre(@RequestParam String termino) {
        return ResponseEntity.ok(arbitroUseCase.buscarArbitrosPorNombre(termino));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        arbitroUseCase.eliminarArbitro(id);
        return ResponseEntity.noContent().build();
    }
}
