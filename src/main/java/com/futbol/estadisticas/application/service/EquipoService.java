package com.futbol.estadisticas.application.service;

import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.response.CompeticionResponse;
import com.futbol.estadisticas.application.port.mapper.CompeticionMapper;
import com.futbol.estadisticas.domain.model.Competicion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.CrearEquipoRequest;
import com.futbol.estadisticas.application.port.dto.response.EquipoResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.EquipoUseCase;
import com.futbol.estadisticas.application.port.mapper.EquipoMapper;
import com.futbol.estadisticas.application.port.mapper.JugadorMapper;
import com.futbol.estadisticas.application.port.out.EquipoRepositoryPort;
import com.futbol.estadisticas.domain.model.Equipo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EquipoService implements EquipoUseCase {

    private final EquipoRepositoryPort equipoRepository;
    private final EquipoMapper equipoMapper;
    private final JugadorMapper      jugadorMapper;
    private final CompeticionMapper competicionMapper;


    @Override
    public Page<EquipoResponse> buscarEquipos(String texto, Pageable pageable) {
        if (texto == null || texto.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        Page<Equipo> page = equipoRepository.buscarEquipoPorNombre(texto.trim(), pageable);
        return page.map(equipoMapper::toResponse);
    }

    @Override
    public EquipoResponse crearEquipo(CrearEquipoRequest request) {
        Equipo club = equipoMapper.toEntity(request);
        return equipoMapper.toResponse(equipoRepository.save(club));
    }

    @Override
    public List<CompeticionResponse> obtenerCompeticionesPorEquipo(UUID idEquipo) {
        if (!equipoRepository.existsById(idEquipo)) {
            throw new IllegalArgumentException("Club no encontrado con id: " + idEquipo);
        }

        List<Competicion> competiciones = equipoRepository.findCompeticionesByEquipo(idEquipo);

        return competiciones.stream()
                .map(competicionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoResponse obtenerEquipoPorId(UUID idEquipo) {
        return equipoRepository.findById(idEquipo)
                .map(equipoMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idEquipo));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<EquipoResponse> obtenerTodosLosEquipos() {
        return equipoRepository.findAll().stream()
                .map(equipoMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerJugadoresActivosDeEquipo(UUID idEquipo) {
        Equipo club = equipoRepository.findByIdWithContratos(idEquipo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idEquipo));
        return club.getJugadoresActivos().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }

    @Override
    public List<JugadorResponse> obtenerTitulares(UUID idEquipo) {
        Equipo club = equipoRepository.findByIdWithContratos(idEquipo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idEquipo));
        return club.getJugadoresTitulares().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerJugadoresDisponiblesDeEquipo(UUID idEquipo) {
        Equipo club = equipoRepository.findByIdWithContratos(idEquipo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idEquipo));
        return club.getJugadoresDisponibles().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerValorPlantilla(UUID idEquipo) {
        Equipo club = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idEquipo));
        return club.getValorPlantillaTotal();
    }
 
    @Override
    public void eliminarEquipo(UUID idEquipo) {
        if (!equipoRepository.existsById(idEquipo)) {
            throw new IllegalArgumentException("Club no encontrado con id: " + idEquipo);
        }
        equipoRepository.deleteById(idEquipo);
    }
}
