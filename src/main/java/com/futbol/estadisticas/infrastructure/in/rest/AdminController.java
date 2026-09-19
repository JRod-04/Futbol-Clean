package com.futbol.estadisticas.infrastructure.in.rest;


import com.futbol.estadisticas.application.port.dto.request.SecurityDTORequest.*;
import com.futbol.estadisticas.application.port.dto.request.SecurityDTORequest.*;
import com.futbol.estadisticas.application.port.dto.response.SecurityDTOResponse.*;
import com.futbol.estadisticas.application.port.in.UsuarioUseCase;
import com.futbol.estadisticas.application.port.mapper.UsuarioWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/apifutbol/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioWebMapper mapper;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(usuarioUseCase.crear(mapper.toCommand(req))));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(
                usuarioUseCase.listar().stream().map(mapper::toResponse).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(usuarioUseCase.obtener(id)));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable UUID id) {
        usuarioUseCase.activar(id);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        usuarioUseCase.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        usuarioUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}