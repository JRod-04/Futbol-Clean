package com.futbol.estadisticas.infrastructure.in.rest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin(origins = "*")

public class PartidoController {

    private final PartidoUseCase partidoUseCase;
 
    @PostMapping
    public ResponseEntity<PartidoResponse> programar(
            @Valid @RequestBody CrearPartidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partidoUseCase.programarPartido(request));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<PartidoResponse>> programarPartidosBatch(
            @Valid @RequestBody List<CrearPartidoRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partidoUseCase.programarPartidosBatch(requests));
    }

    @PostMapping("/{id}/eventos/batch")
    public ResponseEntity<List<EventoPartidoResponse>> registrarEventosBatch(
            @PathVariable UUID id,
            @Valid @RequestBody List<RegistrarEventoRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partidoUseCase.registrarEventosBatch(id, requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(partidoUseCase.obtenerPartidoPorId(id));
    }

    @GetMapping("/fecha")
    public ResponseEntity<Page<PartidoResponse>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(partidoUseCase.obtenerPartidosPorFecha(fecha, page, size));
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

    @PatchMapping("/{id}/avanzar")
    public ResponseEntity<PartidoResponse> avanzarPartido(@PathVariable UUID id) {
        return ResponseEntity.ok(partidoUseCase.avanzarPartido(id));
    }

    @PatchMapping("/{id}/agregar-tiempo")
    public ResponseEntity<EventoPartidoResponse> agregarTiempoAgregado(
            @PathVariable UUID id,
            @RequestParam int minutos,
            @RequestParam(required = false) String descripcion) {
        return ResponseEntity.ok(partidoUseCase.agregarTiempoAgregado(id, minutos, descripcion));
    }

    @PatchMapping("/{id}/finalizar-tiempo")
    public ResponseEntity<PartidoResponse> finalizarTiempo(
            @PathVariable UUID id,
            @RequestParam LocalTime minutoFin) {
        return ResponseEntity.ok(partidoUseCase.finalizarTiempo(id, minutoFin));
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



    @DeleteMapping("/{idPartido}/eventos/{idEvento}")
    public ResponseEntity<Void> eliminarEvento(
            @PathVariable UUID idPartido,
            @PathVariable UUID idEvento) {
        partidoUseCase.eliminarEvento(idPartido, idEvento);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idPartido}")
    public ResponseEntity<Void> eliminarPartido(
            @PathVariable UUID idPartido) {
        partidoUseCase.eliminarPartido(idPartido);
        return ResponseEntity.noContent().build();
    }


}
