package com.futbol.estadisticas.infrastructure.in.rest;

import java.util.HashMap;
import java.util.Map;

import com.futbol.estadisticas.application.port.in.JugadoresUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.application.port.in.EquipoUseCase;
import com.futbol.estadisticas.application.port.in.CompeticionUseCase;
import com.futbol.estadisticas.application.port.in.TecnicoUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apifutbol/buscar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusquedaController {

    private final JugadoresUseCase jugadorUseCase;
    private final TecnicoUseCase tecnicoUseCase;
    private final EquipoUseCase equipoUseCase;
    private final CompeticionUseCase competicionUseCase;

    // ──────────────── JUGADORES ────────────────

    @GetMapping("/jugadores")
    public ResponseEntity<Page<JugadorResponse>> buscarJugadores(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty(PageRequest.of(page, size)));
        }

        return ResponseEntity.ok(jugadorUseCase.buscarJugadores(q.trim(), PageRequest.of(page, size)));
    }

    // ──────────────── TÉCNICOS ────────────────

    @GetMapping("/tecnicos")
    public ResponseEntity<Page<TecnicoResponse>> buscarTecnicos(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty(PageRequest.of(page, size)));
        }

        return ResponseEntity.ok(tecnicoUseCase.buscarTecnicos(q.trim(), PageRequest.of(page, size)));
    }

    // ──────────────── CLUBES ────────────────

    @GetMapping("/clubes")
    public ResponseEntity<Page<EquipoResponse>> buscarClubes(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty(PageRequest.of(page, size)));
        }

        return ResponseEntity.ok(equipoUseCase.buscarEquipos(q.trim(), PageRequest.of(page, size)));
    }

    // ──────────────── COMPETICIONES ────────────────

    @GetMapping("/competiciones")
    public ResponseEntity<Page<CompeticionResponse>> buscarCompeticiones(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty(PageRequest.of(page, size)));
        }

        return ResponseEntity.ok(competicionUseCase.buscarCompeticiones(q.trim(), PageRequest.of(page, size)));
    }

    // ──────────────── BÚSQUEDA GLOBAL ────────────────

    @GetMapping("/global")
    public ResponseEntity<Map<String, Page<?>>> busquedaGlobal(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Pageable pageable = PageRequest.of(page, size);

        if (q == null || q.trim().isEmpty()) {
            Map<String, Page<?>> vacio = new HashMap<>();
            vacio.put("jugadores", Page.empty(pageable));
            vacio.put("tecnicos", Page.empty(pageable));
            vacio.put("clubes", Page.empty(pageable));
            vacio.put("competiciones", Page.empty(pageable));
            return ResponseEntity.ok(vacio);
        }

        String texto = q.trim();

        Map<String, Page<?>> resultados = new HashMap<>();
        resultados.put("jugadores", jugadorUseCase.buscarJugadores(texto, pageable));
        resultados.put("tecnicos", tecnicoUseCase.buscarTecnicos(texto, pageable));
        resultados.put("clubes", equipoUseCase.buscarEquipos(texto, pageable));
        resultados.put("competiciones", competicionUseCase.buscarCompeticiones(texto, pageable));

        return ResponseEntity.ok(resultados);
    }
}