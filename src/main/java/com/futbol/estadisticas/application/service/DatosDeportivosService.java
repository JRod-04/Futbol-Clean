package com.futbol.estadisticas.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.response.DatosDeportivosResponse;
import com.futbol.estadisticas.application.port.in.DatosDeportivosUseCase;
import com.futbol.estadisticas.application.port.mapper.DatosDeportivosMapper;
import com.futbol.estadisticas.application.port.out.DatosDeportivosRepositoryPort;
import com.futbol.estadisticas.application.port.out.JugadorRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DatosDeportivosService implements DatosDeportivosUseCase {
    
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

        datos.setJugador(jugador);

        DatosDeportivos saved = datosDeportivosRepository.save(datos);
        return datosDeportivosMapper.toResponse(saved, jugador);
    }

    @Override
    public DatosDeportivosResponse cambiarPosicion(UUID idJugador, PosicionJugador nuevaPosicion) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);
        datos.agregarPosicion(nuevaPosicion);
        datos.setJugador(jugador);
        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }

    @Override
    public DatosDeportivosResponse eliminarPosicion(UUID idJugador, PosicionJugador posicionAEliminar) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);

        datos.eliminarPosicion(posicionAEliminar);
        datos.setJugador(jugador);

        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PosicionJugador> obtenerPosiciones(UUID idJugador) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);
        return datos.getPosiciones();
    }

    @Override
    public DatosDeportivosResponse promoverATitular(UUID idJugador) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);

        Club club = jugador.getClubActual();
        validarLimiteTitularesEnClub(club, idJugador);

        datos.promoverATitular();
        datos.setJugador(jugador);

        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }

    private void validarLimiteTitularesEnClub(Club club, UUID idJugadorExcluido) {
        if (club == null) return;

        long titularesEnClub = jugadorRepository.findByClub(club.getIdEquipo()).stream()
                .filter(j -> j.getDatosDeportivos() != null
                        && j.getDatosDeportivos().getEstadoJugador() == EstadoJugador.TITULAR
                        && !j.getIdPersonal().equals(idJugadorExcluido))
                .count();

        if (titularesEnClub >= 11) {
            throw new IllegalStateException(
                    "El club " + club.getNombre() + " ya tiene 11 jugadores titulares");
        }
    }


    @Override
    public DatosDeportivosResponse cambiarASuplente(UUID idJugador) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);

        datos.cambiarASuplente();
        datos.setJugador(jugador);

        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }

    @Override
    public DatosDeportivosResponse actualizarEstado(UUID idJugador, EstadoJugador nuevoEstado) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);

        datos.actualizarEstado(nuevoEstado);
        datos.setJugador(jugador);

        return datosDeportivosMapper.toResponse(datosDeportivosRepository.save(datos), jugador);
    }
    public DatosDeportivosResponse actualizarDorsal(UUID idJugador, Integer nuevoDorsal) {
        Jugador jugador = findJugadorOrThrow(idJugador);
        DatosDeportivos datos = findDatosOrThrow(idJugador);

        validarDorsalUnicoEnClub(jugador, nuevoDorsal);

        datos.actualizarDorsal(nuevoDorsal);

        datos.setJugador(jugador);

        DatosDeportivos saved = datosDeportivosRepository.save(datos);
        return datosDeportivosMapper.toResponse(saved, jugador);
    }
    // ── VALIDACIÓN PRIVADA ──
    
    private void validarDorsalUnicoEnClub(Jugador jugador, Integer dorsal) {
        if (dorsal == null) return;
        
        Club club = jugador.getClubActual();
        if (club == null) return; 
        
        List<Jugador> jugadoresClub = jugadorRepository.findByClub(club.getIdEquipo());
        
        boolean dorsalOcupado = jugadoresClub.stream()
                .filter(j -> !j.getIdPersonal().equals(jugador.getIdPersonal()))
                .anyMatch(j -> j.getDatosDeportivos() != null 
                        && j.getDatosDeportivos().getDorsal() != null
                        && j.getDatosDeportivos().getDorsal().equals(dorsal));
        
        if (dorsalOcupado) {
            throw new IllegalArgumentException(
                    "El dorsal " + dorsal + " ya está asignado a otro jugador del club " + club.getNombre());
        }
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
