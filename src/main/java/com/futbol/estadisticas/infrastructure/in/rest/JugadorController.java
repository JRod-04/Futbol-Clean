package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasPartidoJugadorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.futbol.estadisticas.application.port.dto.request.ActualizarJugadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.JugadoresUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/apifutbol/jugadores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class JugadorController {

    private final JugadoresUseCase jugadoresUseCase;
 
    @PostMapping
    public ResponseEntity<JugadorResponse> crear(@Valid @RequestBody CrearJugadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jugadoresUseCase.crearJugador(request));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<JugadorResponse>> crearVarios(
            @Valid @RequestBody List<CrearJugadorRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jugadoresUseCase.crearVariosJugadores(requests));
    }

    @GetMapping("/{idJugador}/estadisticas")
    public ResponseEntity<EstadisticasJugadorResponse> obtenerEstadisticas(@PathVariable UUID idJugador) {
        return ResponseEntity.ok(jugadoresUseCase.obtenerEstadisticasJugador(idJugador));
    }

    @GetMapping("/{id}/partidos")
    public ResponseEntity<List<EstadisticasPartidoJugadorResponse>> partidosConEstadisticas(@PathVariable UUID id) {
        return ResponseEntity.ok(jugadoresUseCase.obtenerPartidosConEstadisticas(id));
    }

    @GetMapping
    public ResponseEntity<List<JugadorResponse>> listarTodos() {
        return ResponseEntity.ok(jugadoresUseCase.obtenerTodosLosJugadores());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<JugadorResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(jugadoresUseCase.obtenerJugadorPorId(id));
    }
 
    @GetMapping("/club/{idEquipo}")
    public ResponseEntity<List<JugadorResponse>> porEquipo(@PathVariable UUID idEquipo) {
        return ResponseEntity.ok(jugadoresUseCase.obtenerJugadoresPorEquipo(idEquipo));
    }
 
    @GetMapping("/posicion/{posicion}")
    public ResponseEntity<List<JugadorResponse>> porPosicion(@PathVariable PosicionJugador posicion) {
        return ResponseEntity.ok(jugadoresUseCase.obtenerJugadoresPorPosicion(posicion));
    }
 
    @GetMapping("/disponibles")
    public ResponseEntity<List<JugadorResponse>> disponibles() {
        return ResponseEntity.ok(jugadoresUseCase.obtenerJugadoresDisponibles());
    }
 
    @GetMapping("/lesionados")
    public ResponseEntity<List<JugadorResponse>> lesionados() {
        return ResponseEntity.ok(jugadoresUseCase.obtenerJugadoresLesionados());
    }
 
    @PatchMapping("/{id}")
    public ResponseEntity<JugadorResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarJugadorRequest request) {
        return ResponseEntity.ok(jugadoresUseCase.actualizarJugador(id, request));
    }
 
    @PatchMapping("/{id}/estado")
    public ResponseEntity<JugadorResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoJugador nuevoEstado) {
        return ResponseEntity.ok(jugadoresUseCase.cambiarEstadoJugador(id, nuevoEstado));
    }
 
    @PatchMapping("/{id}/valor-mercado")
    public ResponseEntity<JugadorResponse> actualizarValorMercado(
            @PathVariable UUID id,
            @RequestParam Double valor) {
        return ResponseEntity.ok(jugadoresUseCase.actualizarValorMercado(id, valor));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        jugadoresUseCase.eliminarJugador(id);
        return ResponseEntity.noContent().build();
    }
}
