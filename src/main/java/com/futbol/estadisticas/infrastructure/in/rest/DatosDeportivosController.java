package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.futbol.estadisticas.application.port.dto.response.DatosDeportivosResponse;
import com.futbol.estadisticas.application.port.in.DatosDeportivosUseCase;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/jugadores/{idJugador}/datos-deportivos")
@RequiredArgsConstructor
public class DatosDeportivosController {

    private final DatosDeportivosUseCase datosDeportivosUseCase;

    @GetMapping
    public ResponseEntity<DatosDeportivosResponse> obtener(@PathVariable UUID idJugador) {
        return ResponseEntity.ok(datosDeportivosUseCase.obtenerPorJugador(idJugador));
    }

    @PatchMapping("/valor-mercado")
    public ResponseEntity<DatosDeportivosResponse> actualizarValor(
            @PathVariable UUID idJugador,
            @RequestBody Double nuevoValor) {
        return ResponseEntity.ok(
                datosDeportivosUseCase.actualizarValorMercado(idJugador, nuevoValor));
    }

    @GetMapping("/posiciones")
    public ResponseEntity<List<PosicionJugador>> obtenerPosiciones(
            @PathVariable UUID idJugador) {
        return ResponseEntity.ok(
                datosDeportivosUseCase.obtenerPosiciones(idJugador));
    }

    @PostMapping("/posicion")
    public ResponseEntity<DatosDeportivosResponse> agregarPosicion(
            @PathVariable UUID idJugador,
            @RequestBody PosicionJugador nuevaPosicion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(datosDeportivosUseCase.cambiarPosicion(idJugador, nuevaPosicion));
    }

    @DeleteMapping("/posicion")
    public ResponseEntity<DatosDeportivosResponse> eliminarPosicion(
            @PathVariable UUID idJugador,
            @RequestBody PosicionJugador posicionAEliminar) {
        return ResponseEntity.ok(
                datosDeportivosUseCase.eliminarPosicion(idJugador, posicionAEliminar));
    }

    @PatchMapping("/dorsal")
    public ResponseEntity<DatosDeportivosResponse> actualizarDorsal(
            @PathVariable UUID idJugador,
            @RequestBody int nuevoDorsal) {
            return ResponseEntity.ok(
                    datosDeportivosUseCase.actualizarDorsal(idJugador, nuevoDorsal));

    }

    @PatchMapping("/promover-titular")
    public ResponseEntity<DatosDeportivosResponse> promoverATitular(@PathVariable UUID idJugador) {
        return ResponseEntity.ok(datosDeportivosUseCase.promoverATitular(idJugador));
    }

    @PatchMapping("/pasar-suplente")
    public ResponseEntity<DatosDeportivosResponse> pasarASuplente(@PathVariable UUID idJugador) {
        return ResponseEntity.ok(datosDeportivosUseCase.cambiarASuplente(idJugador));
    }

    @PatchMapping("/estado")
    public ResponseEntity<DatosDeportivosResponse> actualizarEstado(
            @PathVariable UUID idJugador,
            @RequestBody EstadoJugador nuevoEstado) {
        return ResponseEntity.ok(
                datosDeportivosUseCase.actualizarEstado(idJugador, nuevoEstado));
    }
}