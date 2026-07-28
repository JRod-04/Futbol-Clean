package com.futbol.estadisticas.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.CrearClubRequest;
import com.futbol.estadisticas.application.port.dto.response.ClubResponse;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.ClubUseCase;
import com.futbol.estadisticas.application.port.mapper.ClubMapper;
import com.futbol.estadisticas.application.port.mapper.JugadorMapper;
import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService implements ClubUseCase {

    private final ClubRepositoryPort clubRepository;
    private final ClubMapper         clubMapper;
    private final JugadorMapper      jugadorMapper;

    @Override
    public Page<ClubResponse> buscarClubes(String texto, Pageable pageable) {
        if (texto == null || texto.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        Page<Club> page = clubRepository.buscarClubPorNombre(texto.trim(), pageable);
        return page.map(clubMapper::toResponse);
    }

    @Override
    public ClubResponse crearClub(CrearClubRequest request) {
        Club club = clubMapper.toEntity(request);
        return clubMapper.toResponse(clubRepository.save(club));
    }
 
    @Override
    @Transactional(readOnly = true)
    public ClubResponse obtenerClubPorId(UUID idClub) {
        return clubRepository.findById(idClub)
                .map(clubMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<ClubResponse> obtenerTodosLosClubs() {
        return clubRepository.findAll().stream()
                .map(clubMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerJugadoresActivosDeClub(UUID idClub) {
        Club club = clubRepository.findByIdWithContratos(idClub)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
        return club.getJugadoresActivos().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }

    @Override
    public List<JugadorResponse> obtenerTitulares(UUID idClub) {
        Club club = clubRepository.findByIdWithContratos(idClub)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
        return club.getJugadoresTitulares().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerJugadoresDisponiblesDeClub(UUID idClub) {
        Club club = clubRepository.findByIdWithContratos(idClub)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
        return club.getJugadoresDisponibles().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerValorPlantilla(UUID idClub) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
        return club.getValorPlantillaTotal();
    }
 
    @Override
    public void eliminarClub(UUID idClub) {
        if (!clubRepository.existsById(idClub)) {
            throw new IllegalArgumentException("Club no encontrado con id: " + idClub);
        }
        clubRepository.deleteById(idClub);
    }
}
