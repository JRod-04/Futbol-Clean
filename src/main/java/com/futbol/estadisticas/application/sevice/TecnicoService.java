package com.futbol.estadisticas.application.sevice;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.ActualizarTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearTecnicoRequest;
import com.futbol.estadisticas.application.port.dto.response.TecnicoResponse;
import com.futbol.estadisticas.application.port.in.TecnicoUseCase;
import com.futbol.estadisticas.application.port.mapper.TecnicoMapper;
import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.application.port.out.TecnicoRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Tecnico;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;

import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class TecnicoService implements TecnicoUseCase{
    
    private final TecnicoRepositoryPort tecnicoRepository;
    private final ClubRepositoryPort    clubRepository;
    private final TecnicoMapper         tecnicoMapper;
 
    @Override
    public TecnicoResponse crearTecnico(CrearTecnicoRequest request) {
        Tecnico tecnico = tecnicoMapper.toEntity(request);
        return tecnicoMapper.toResponse(tecnicoRepository.save(tecnico));
    }
 
    @Override
    @Transactional(readOnly = true)
    public TecnicoResponse obtenerTecnicoPorId(UUID idTecnico) {
        return tecnicoRepository.findById(idTecnico)
                .map(tecnicoMapper::toResponse)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Técnico no encontrado con id: " + idTecnico));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<TecnicoResponse> obtenerTodosTecnicos() {
        return tecnicoRepository.findAll().stream()
                .map(tecnicoMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public TecnicoResponse obtenerTecnicoActualDeClub(UUID idClub) {
        return tecnicoRepository.findTecnicoActualByClub(idClub)
                .map(tecnicoMapper::toResponse)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "No hay técnico asignado al club con id: " + idClub));
    }
 
    @Override
    public TecnicoResponse actualizarTecnico(UUID idTecnico, ActualizarTecnicoRequest request) {
        Tecnico tecnico = findTecnicoOrThrow(idTecnico);
 
        if (request.nombre() != null)            tecnico.setNombre(request.nombre());
        if (request.apellido() != null)          tecnico.setApellido(request.apellido());
        if (request.estiloJuego() != null)       tecnico.actualizarEstiloJuego(request.estiloJuego());
        if (request.alineacionFavorita() != null) tecnico.actualizarAlineacion(request.alineacionFavorita());
 
        return tecnicoMapper.toResponse(tecnicoRepository.save(tecnico));
    }
 
    @Override
    public TecnicoResponse asignarTecnicoAClub(UUID idTecnico, UUID idClub) {
        Tecnico tecnico = findTecnicoOrThrow(idTecnico);
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
 
        club.asignarTecnico(tecnico);
        clubRepository.save(club);
 
        return tecnicoMapper.toResponse(tecnicoRepository.save(tecnico));
    }
 
    @Override
    public void desvincularTecnicoDeClub(UUID idClub) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
        if (club.getTecnicoActual() == null) {
            throw new IllegalStateException("El club no tiene técnico asignado");
        }
        club.desvincularTecnico();
        clubRepository.save(club);
    }
 
    @Override
    public void eliminarTecnico(UUID idTecnico) {
        if (!tecnicoRepository.existsById(idTecnico)) {
            throw new PersonalNotFoundException("Técnico no encontrado con id: " + idTecnico);
        }
        tecnicoRepository.deleteById(idTecnico);
    }
 
    // ── helper privado ─────────────────────────────────────────────────────────
 
    private Tecnico findTecnicoOrThrow(UUID idTecnico) {
        return tecnicoRepository.findById(idTecnico)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Técnico no encontrado con id: " + idTecnico));
    }
}
