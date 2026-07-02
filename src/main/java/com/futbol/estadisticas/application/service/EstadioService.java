package com.futbol.estadisticas.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.ActualizarEstadioRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearEstadioRequest;
import com.futbol.estadisticas.application.port.dto.response.EstadioResponse;
import com.futbol.estadisticas.application.port.in.EstadioUseCase;
import com.futbol.estadisticas.application.port.mapper.EstadioMapper;
import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.application.port.out.EstadioRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Estadio;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class EstadioService implements EstadioUseCase{

private final EstadioRepositoryPort estadioRepository;
    private final ClubRepositoryPort    clubRepository;
    private final EstadioMapper         estadioMapper;
 
    @Override
    public EstadioResponse crearEstadio(CrearEstadioRequest request) {
        Estadio estadio = estadioMapper.toEntity(request);
        return estadioMapper.toResponse(estadioRepository.save(estadio));
    }
 
    @Override
    @Transactional(readOnly = true)
    public EstadioResponse obtenerEstadioPorId(UUID idEstadio) {
        return estadioRepository.findById(idEstadio)
                .map(estadioMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estadio no encontrado con id: " + idEstadio));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<EstadioResponse> obtenerTodosLosEstadios() {
        return estadioRepository.findAll().stream()
                .map(estadioMapper::toResponse)
                .toList();
    }
 
    @Override
    public EstadioResponse actualizarEstadio(UUID idEstadio, ActualizarEstadioRequest request) {
        Estadio estadio = estadioRepository.findById(idEstadio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estadio no encontrado con id: " + idEstadio));
 
        if (request.nombre() != null)    estadio.setNombre(request.nombre());
        if (request.direccion() != null) estadio.setDireccion(request.direccion());
        if (request.capacidad() != null) estadio.setCapacidad(request.capacidad());
 
        return estadioMapper.toResponse(estadioRepository.save(estadio));
    }
 
    @Override
    public EstadioResponse asignarEstadioAClub(UUID idEstadio, UUID idClub) {
        Estadio estadio = estadioRepository.findById(idEstadio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estadio no encontrado con id: " + idEstadio));
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + idClub));
 
        estadio.setClubPrincipal(club);
        club.setEstadio(estadio);
 
        clubRepository.save(club);
        return estadioMapper.toResponse(estadioRepository.save(estadio));
    }
 
    @Override
    @Transactional(readOnly = true)
    public double calcularPorcentajeOcupacion(UUID idEstadio, Integer espectadores) {
        Estadio estadio = estadioRepository.findById(idEstadio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estadio no encontrado con id: " + idEstadio));
        return estadio.getPorcentajeOcupacion(espectadores);
    }
 
    @Override
    public void eliminarEstadio(UUID idEstadio) {
        if (!estadioRepository.existsById(idEstadio)) {
            throw new IllegalArgumentException("Estadio no encontrado con id: " + idEstadio);
        }
        estadioRepository.deleteById(idEstadio);
    }
}
