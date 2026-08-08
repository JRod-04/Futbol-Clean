package com.futbol.estadisticas.application.service;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.mapper.EquipoMapper;
import com.futbol.estadisticas.application.port.out.EquipoRepositoryPort;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.Partido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.CrearCompeticionRequest;
import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.dto.response.PartidoResponse;
import com.futbol.estadisticas.application.port.in.CompeticionUseCase;
import com.futbol.estadisticas.application.port.mapper.CompeticionMapper;
import com.futbol.estadisticas.application.port.mapper.PartidoMapper;
import com.futbol.estadisticas.application.port.out.CompeticionRepositoryPort;
import com.futbol.estadisticas.application.port.out.PartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.enums.EstadoPartido;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CompeticionService implements CompeticionUseCase {

    private final EquipoRepositoryPort clubRepository;
    private final CompeticionRepositoryPort competicionRepository;
    private final PartidoRepositoryPort     partidoRepository;
    private final CompeticionMapper         competicionMapper;
    private final PartidoMapper             partidoMapper;
    private final EquipoMapper equipoMapper;

    @Override
    public Page<CompeticionResponse> buscarCompeticiones(String texto, Pageable pageable) {
        if (texto == null || texto.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        Page<Competicion> page = competicionRepository.buscarCompeticionesPorNombre(texto.trim(), pageable);
        return page.map(competicionMapper::toResponse);
    }

    @Override
    public CompeticionResponse crearCompeticion(CrearCompeticionRequest request) {
        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        Competicion competicion = competicionMapper.toEntity(request);
        return competicionMapper.toResponse(competicionRepository.save(competicion));
    }
 
    @Override
    @Transactional(readOnly = true)
    public CompeticionResponse obtenerCompeticionPorId(UUID idCompeticion) {
        Competicion competicion = competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));

        List<Partido> partidos = partidoRepository.findByCompeticion(idCompeticion);

        competicion.setPartidos(partidos);

        partidos.forEach(p -> p.setCompeticion(competicion));

        return competicionMapper.toResponse(competicion);
    }

    @Override
    public List<EquipoResponse> obtenerEquiposParticipantes(UUID idCompeticion) {

        Competicion competicion = competicionRepository.findByIdWithPartidosAndEquipos(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException("Competición no encontrada"));

        List<Equipo> clubes = competicion.getClubesParticipantes();


        if (clubes.isEmpty()) {
            throw new IllegalStateException("La competición no tiene partidos registrados");
        }

        return  clubes.stream()
                .map(equipoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompeticionResponse> obtenerTodasLasCompeticiones() {
        List<Competicion> competiciones = competicionRepository.findActivas();

        for (Competicion competicion : competiciones) {
            List<Partido> partidos = partidoRepository.findByCompeticion(competicion.getIdCompeticion());
            competicion.setPartidos(partidos);
            partidos.forEach(p -> p.setCompeticion(competicion));
        }

        return competiciones.stream()
                .map(competicionMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<CompeticionResponse> obtenerCompeticionesActivas() {
        return competicionRepository.findActivas().stream()
                .map(competicionMapper::toResponse)
                .toList();
    }

    @Override
    public CompeticionResponse actualizarEquipoGanador(UUID idCompeticion, UUID idEquipoGanador) {
        Competicion competicion = findCompeticionOrThrow(idCompeticion);

        List<Partido> partidos = partidoRepository.findByCompeticion(idCompeticion);

        if (partidos.isEmpty()) {
            throw new IllegalStateException("La competición no tiene partidos registrados");
        }

        boolean hayPartidosNoFinalizados = partidos.stream()
                .anyMatch(p -> !p.haFinalizado());

        if (hayPartidosNoFinalizados) {
            throw new IllegalStateException(
                    "No se puede asignar un ganador porque hay partidos que aún no han finalizado"
            );
        }

        if (idEquipoGanador == null) {
            competicion.setEquipoGanador(null);
            return competicionMapper.toResponse(competicionRepository.save(competicion));
        }

        Equipo ganador = clubRepository.findById(idEquipoGanador)
                .orElseThrow(() -> new IllegalArgumentException("Club no encontrado con id: " + idEquipoGanador));

        boolean participa = partidos.stream()
                .anyMatch(p ->
                        (p.getEquipoLocal() != null && p.getEquipoLocal().getIdEquipo().equals(idEquipoGanador)) ||
                                (p.getEquipoVisitante() != null && p.getEquipoVisitante().getIdEquipo().equals(idEquipoGanador))
                );

        if (!participa) {
            throw new IllegalStateException(
                    "El club no participa en esta competición: " + ganador.getNombre()
            );
        }

        competicion.setEquipoGanador(ganador);
        return competicionMapper.toResponse(competicionRepository.save(competicion));
    }

    private Competicion findCompeticionOrThrow(UUID idCompeticion) {
        return competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> obtenerPartidosPorCompeticion(UUID idCompeticion) {
        competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));
        return partidoRepository.findByCompeticion(idCompeticion).stream()
                .map(partidoMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> obtenerPartidosPendientesPorCompeticion(UUID idCompeticion) {
        competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));
        return partidoRepository.findByCompeticion(idCompeticion).stream()
                .filter(p -> p.getEstado() == EstadoPartido.PROGRAMADO)
                .map(partidoMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public Double obtenerPorcentajeAvance(UUID idCompeticion) {
        return competicionRepository.findById(idCompeticion)
                .map(Competicion::getPorcentajePartidosJugados)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));
    }
 
    @Override
    public void eliminarCompeticion(UUID idCompeticion) {
        Competicion competicion = competicionRepository.findById(idCompeticion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));

        competicionRepository.deleteById(idCompeticion);
    }
}
