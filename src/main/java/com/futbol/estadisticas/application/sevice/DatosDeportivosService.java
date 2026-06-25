package com.futbol.estadisticas.application.sevice;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.response.DatosDeportivosResponse;
import com.futbol.estadisticas.application.port.in.DatosDeportivosUseCase;
import com.futbol.estadisticas.application.port.mapper.DatosDeportivosMapper;
import com.futbol.estadisticas.application.port.out.DatosDeportivosRepositoryPort;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DatosDeportivosService implements DatosDeportivosUseCase{
    
    private final DatosDeportivosRepositoryPort datosDeportivosRepository;
    private final JugadorRepositoryPort         jugadorRepository;
    private final DatosDeportivosMapper         datosDeportivosMapper;
 
    @Override
    @Transactional(readOnly = true)
    public DatosDeportivosResponse obtenerPorJugador(UUID idJugador) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);
        return datosDeportivosMapper.toResponse(datos, jugador);
    }
 
    @Override
    public DatosDeportivosResponse actualizarValorMercado(UUID idJugador, Double nuevoValor) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);
        datos.actualizarValorMercado(nuevoValor);
        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }
 
    @Override
    public DatosDeportivosResponse cambiarPosicion(UUID idJugador, PosicionJugador nuevaPosicion) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);
        datos.agregarPosicion(nuevaPosicion);
        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }
 
    @Override
    public DatosDeportivosResponse promoverATitular(UUID idJugador) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);
        datos.promoverATitular();
        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }
 
    @Override
    public DatosDeportivosResponse cambiarASuplente(UUID idJugador) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);
        datos.cambiarASuplente();
        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }
 
    @Override
    public DatosDeportivosResponse actualizarEstado(UUID idJugador, EstadoJugador nuevoEstado) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);
        datos.actualizarEstado(nuevoEstado);
        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }
 
    // ── helpers privados ───────────────────────────────────────────────────────
 
    private Jugador findJugadorOrThrow(UUID idJugador) {
        return jugadorRepository.findById(idJugador)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Jugador no encontrado con id: " + idJugador));
    }
 
    private DatosDeportivos findDatosOrThrow(UUID idJugador) {
        return datosDeportivosRepository.findByJugador(idJugador)
                .orElseThrow(() -> new IllegalStateException(
                        "El jugador con id: " + idJugador + " no tiene datos deportivos registrados"));
    }
}
