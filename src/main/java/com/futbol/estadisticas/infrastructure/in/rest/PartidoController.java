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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futbol.estadisticas.application.port.dto.request.CrearPartidoRequest;
import com.futbol.estadisticas.application.port.dto.request.RegistrarEventoRequest;
import com.futbol.estadisticas.application.port.dto.response.EventoPartidoResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.in.PartidoUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/apifutbol/partidos")
@RequiredArgsConstructor
public class PartidoController {

    private final PartidoUseCase partidoUseCase;
 
    @PostMapping
    public ResponseEntity<PartidoResponse> programar(
            @Valid @RequestBody CrearPartidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partidoUseCase.programarPartido(request));
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<PartidoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(partidoUseCase.obtenerPartidoPorId(id));
    }
 
    @GetMapping("/competicion/{idCompeticion}")
    public ResponseEntity<List<PartidoResponse>> porCompeticion(
            @PathVariable UUID idCompeticion) {
        return ResponseEntity.ok(partidoUseCase.obtenerPartidosPorCompeticion(idCompeticion));
    }
 
    @GetMapping("/club/{idClub}")
    public ResponseEntity<List<PartidoResponse>> porClub(@PathVariable UUID idClub) {
        return ResponseEntity.ok(partidoUseCase.obtenerPartidosPorClub(idClub));
    }
 
    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<PartidoResponse> iniciar(@PathVariable UUID id) {
        return ResponseEntity.ok(partidoUseCase.iniciarPartido(id));
    }
 
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<PartidoResponse> finalizar(@PathVariable UUID id) {
        return ResponseEntity.ok(partidoUseCase.finalizarPartido(id));
    }
 
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PartidoResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoPartido nuevoEstado) {
        return ResponseEntity.ok(partidoUseCase.cambiarEstadoPartido(id, nuevoEstado));
    }
 
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        partidoUseCase.cancelarPartido(id);
        return ResponseEntity.noContent().build();
    }
 
    @PostMapping("/{id}/eventos")
    public ResponseEntity<EventoPartidoResponse> registrarEvento(
            @PathVariable UUID id,
            @Valid @RequestBody RegistrarEventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partidoUseCase.registrarEvento(id, request));
    }
 
    @GetMapping("/{id}/eventos")
    public ResponseEntity<List<EventoPartidoResponse>> obtenerEventos(@PathVariable UUID id) {
        return ResponseEntity.ok(partidoUseCase.obtenerEventosDePartido(id));
    }
}
