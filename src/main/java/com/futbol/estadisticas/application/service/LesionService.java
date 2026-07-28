package com.futbol.estadisticas.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.RegistrarLesionRequest;
import com.futbol.estadisticas.application.port.dto.response.LesionResponse;
import com.futbol.estadisticas.application.port.in.LesionUseCase;
import com.futbol.estadisticas.application.port.mapper.LesionMapper;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.application.port.out.LesionRepositoryPort;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Lesion;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.Gravedad;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LesionService implements LesionUseCase {

    private final LesionRepositoryPort  lesionRepository;
    private final JugadorRepositoryPort jugadorRepository;
    private final LesionMapper          lesionMapper;
 
    @Override
    public LesionResponse registrarLesion(RegistrarLesionRequest request) {
        Jugador jugador = findJugadorOrThrow(request.idJugador());

        Lesion lesion = lesionMapper.toEntity(request, jugador);

        jugador.registrarLesion(lesion);

        lesionRepository.save(lesion);
        jugadorRepository.save(jugador);

        return lesionMapper.toResponse(lesion, jugador);
    }

    @Override
    public List<LesionResponse> registrarVariasLesiones(List<RegistrarLesionRequest> requests) {
        List<Lesion> lesiones = new ArrayList<>();

        for (RegistrarLesionRequest request : requests) {
            Jugador jugador = findJugadorOrThrow(request.idJugador());
            Lesion lesion = lesionMapper.toEntity(request, jugador);
            jugador.registrarLesion(lesion);
            lesiones.add(lesion);
        }

        List<Lesion> saved = lesionRepository.saveAll(lesiones);

        for (Lesion lesion : saved) {
            if (lesion.getJugadorLesionado() != null) {
                jugadorRepository.save(lesion.getJugadorLesionado());
            }
        }

        return saved.stream()
                .map(l -> lesionMapper.toResponse(l, l.getJugadorLesionado()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LesionResponse obtenerLesionPorId(UUID idLesion) {
        Lesion lesion = findLesionOrThrow(idLesion);
        return lesionMapper.toResponse(lesion, null);
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<LesionResponse> obtenerLesionesPorJugador(UUID idJugador) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        return lesionRepository.findByJugador(idJugador).stream()
                .map(l -> lesionMapper.toResponse(l, jugador))
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<LesionResponse> obtenerLesionesActivasPorJugador(UUID idJugador) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        return lesionRepository.findActivasByJugador(idJugador).stream()
                .map(l -> lesionMapper.toResponse(l, jugador))
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<LesionResponse> obtenerLesionesActivasEnSistema() {
        return lesionRepository.findActivas().stream()
                .map(l -> lesionMapper.toResponse(l, null))
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<LesionResponse> obtenerLesionesPorGravedad(Gravedad gravedad) {
        return lesionRepository.findByGravedad(gravedad).stream()
                .map(l -> lesionMapper.toResponse(l, null))
                .toList();
    }
 
    @Override
    public LesionResponse curarLesion(UUID idLesion) {
        Lesion lesion = findLesionOrThrow(idLesion);
        lesion.curar();
 
        Lesion curada = lesionRepository.save(lesion);
 
        if (curada.getIdLesion() != null) {
            lesionRepository.findActivasByJugador(curada.getIdLesion()).stream()
                    .findAny()
                    .ifPresentOrElse(
                            l -> { /* aún hay lesiones activas, no cambiar estado */ },
                            () -> jugadorRepository.findAll().stream()
                                    .filter(j -> j.getLesiones().contains(curada))
                                    .findFirst()
                                    .ifPresent(j -> {
                                        if (j.getDatosDeportivos() != null) {
                                            j.getDatosDeportivos().actualizarEstado(EstadoJugador.SUPLENTE);
                                            jugadorRepository.save(j);
                                        }
                                    })
                    );
        }
 
        return lesionMapper.toResponse(curada, null);
    }
 
    // ── helpers privados ───────────────────────────────────────────────────────
 
    private Jugador findJugadorOrThrow(UUID idJugador) {
        return jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Jugador no encontrado con id: " + idJugador));
    }
 
    private Lesion findLesionOrThrow(UUID idLesion) {
        return lesionRepository.findById(idLesion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lesión no encontrada con id: " + idLesion));
    }
}
