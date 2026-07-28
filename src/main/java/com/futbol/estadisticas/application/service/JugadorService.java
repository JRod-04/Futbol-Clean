package com.futbol.estadisticas.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasJugadorResponse;
import com.futbol.estadisticas.application.port.mapper.EstadisticasJugadorMapper;
import com.futbol.estadisticas.application.port.out.EventosPartidoRepositoryPort;
import com.futbol.estadisticas.domain.model.EventosPartido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.ActualizarJugadorRequest;
import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.application.port.in.JugadoresUseCase;
import com.futbol.estadisticas.application.port.mapper.JugadorMapper;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class JugadorService implements JugadoresUseCase {

    private final EventosPartidoRepositoryPort eventosRepository;
    private final EstadisticasJugadorMapper estadisticasMapper;
    private final JugadorRepositoryPort jugadorRepository;
    private final JugadorMapper         jugadorMapper;

    @Override
    public Page<JugadorResponse> buscarJugadores(String texto, Pageable pageable) {
        if (texto == null || texto.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        Page<Jugador> page = jugadorRepository.buscarJugadorPorTexto(texto.trim(), pageable);
        return page.map(jugadorMapper::toResponse);

    }

    @Override
    public JugadorResponse crearJugador(CrearJugadorRequest request) {
        Jugador jugador = jugadorMapper.toEntity(request);
        return jugadorMapper.toResponse(jugadorRepository.save(jugador));
    }

    @Override
    @Transactional
    public List<JugadorResponse> crearVariosJugadores(List<CrearJugadorRequest> requests) {
        List<Jugador> jugadores = new ArrayList<>();

        for (CrearJugadorRequest request : requests) {
            Jugador jugador = jugadorMapper.toEntity(request);
            jugadores.add(jugador);
        }

        List<Jugador> saved = jugadorRepository.saveAll(jugadores);
        return saved.stream()
                .map(jugadorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EstadisticasJugadorResponse obtenerEstadisticasJugador(UUID idJugador) {
        Jugador jugador = jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Jugador no encontrado con id: " + idJugador));

        List<EventosPartido> eventos = eventosRepository.findByPersonalConCompeticion(idJugador);

        return estadisticasMapper.toResponse(jugador, eventos);

    }

    @Override
    @Transactional(readOnly = true)
    public JugadorResponse obtenerJugadorPorId(UUID idJugador) {
        return jugadorRepository.findById(idJugador)
                .map(jugadorMapper::toResponse)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Jugador no encontrado con id: " + idJugador));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerTodosLosJugadores() {
        return jugadorRepository.findAll().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerJugadoresPorClub(UUID idClub) {
        return jugadorRepository.findByClub(idClub).stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerJugadoresPorPosicion(PosicionJugador posicion) {
        return jugadorRepository.findByPosicion(posicion).stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerJugadoresDisponibles() {
        return jugadorRepository.findDisponibles().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> obtenerJugadoresLesionados() {
        return jugadorRepository.findLesionados().stream()
                .map(jugadorMapper::toResponse)
                .toList();
    }
 
    @Override
    public JugadorResponse actualizarJugador(UUID idJugador, ActualizarJugadorRequest request) {
        Jugador jugador = findJugadorOrThrow(idJugador);
 
        if (request.nombre() != null)    jugador.setNombre(request.nombre());
        if (request.apellido() != null)  jugador.setApellido(request.apellido());
        if (request.pieHabil() != null)  jugador.setPieHabil(request.pieHabil());
        if (request.altura() != null)    jugador.setAltura(request.altura());
        if (request.peso() != null)      jugador.setPeso(request.peso());
 
        if (request.dorsal() != null && jugador.getDatosDeportivos() != null) {
        jugador.getDatosDeportivos().actualizarDorsal(request.dorsal());
        }

        if (request.posicion() != null && jugador.getDatosDeportivos() != null) {
        jugador.getDatosDeportivos().agregarPosicion(request.posicion());
        }
        if (request.valorMercado() != null && jugador.getDatosDeportivos() != null) {
            jugador.getDatosDeportivos().actualizarValorMercado(request.valorMercado());
        }
 
        jugador.setFechaActualizacion(LocalDate.now());
        return jugadorMapper.toResponse(jugadorRepository.save(jugador));
    }
 
    @Override
    public JugadorResponse cambiarEstadoJugador(UUID idJugador, EstadoJugador nuevoEstado) {
        Jugador jugador = findJugadorOrThrow(idJugador);
 
        if (jugador.getDatosDeportivos() == null) {
            throw new IllegalStateException(
                    "El jugador con id: " + idJugador + " no tiene datos deportivos registrados");
        }
        jugador.getDatosDeportivos().actualizarEstado(nuevoEstado);
        return jugadorMapper.toResponse(jugadorRepository.save(jugador));
    }
 
    @Override
    public JugadorResponse actualizarValorMercado(UUID idJugador, Double nuevoValor) {
        Jugador jugador = findJugadorOrThrow(idJugador);
 
        if (jugador.getDatosDeportivos() == null) {
            throw new IllegalStateException(
                    "El jugador con id: " + idJugador + " no tiene datos deportivos registrados");
        }
        jugador.getDatosDeportivos().actualizarValorMercado(nuevoValor);
        return jugadorMapper.toResponse(jugadorRepository.save(jugador));
    }
 
    @Override
    public void eliminarJugador(UUID idJugador) {
        if (!jugadorRepository.existsById(idJugador)) {
            throw new PersonalNotFoundException("Jugador no encontrado con id: " + idJugador);
        }
        jugadorRepository.deleteById(idJugador);
    }
 
 
    private Jugador findJugadorOrThrow(UUID idJugador) {
        return jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Jugador no encontrado con id: " + idJugador));
    }
    
 
}
