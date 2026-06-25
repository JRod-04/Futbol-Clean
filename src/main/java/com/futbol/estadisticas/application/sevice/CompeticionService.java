package com.futbol.estadisticas.application.sevice;

import java.util.List;
import java.util.UUID;

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
public class CompeticionService implements CompeticionUseCase{
 private final CompeticionRepositoryPort competicionRepository;
    private final PartidoRepositoryPort     partidoRepository;
    private final CompeticionMapper         competicionMapper;
    private final PartidoMapper             partidoMapper;
 
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
        return competicionRepository.findById(idCompeticion)
                .map(competicionMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Competición no encontrada con id: " + idCompeticion));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<CompeticionResponse> obtenerTodasLasCompeticiones() {
        return competicionRepository.findAll().stream()
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
        if (competicion.estaActiva()) {
            throw new IllegalStateException("No se puede eliminar una competición activa");
        }
        competicionRepository.deleteById(idCompeticion);
    }
}
