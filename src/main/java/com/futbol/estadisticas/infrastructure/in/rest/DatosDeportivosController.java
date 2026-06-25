package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam Double nuevoValor) {
        return ResponseEntity.ok(
                datosDeportivosUseCase.actualizarValorMercado(idJugador, nuevoValor));
    }
 
    @PatchMapping("/posicion")
    public ResponseEntity<DatosDeportivosResponse> cambiarPosicion(
            @PathVariable UUID idJugador,
            @RequestParam PosicionJugador nuevaPosicion) {
        return ResponseEntity.ok(
                datosDeportivosUseCase.cambiarPosicion(idJugador, nuevaPosicion));
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
            @RequestParam EstadoJugador nuevoEstado) {
        return ResponseEntity.ok(
                datosDeportivosUseCase.actualizarEstado(idJugador, nuevoEstado));
    }
}

