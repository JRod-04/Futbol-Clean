package com.futbol.estadisticas.application.service;

import java.time.LocalTime;
import java.util.*;

import com.futbol.estadisticas.application.port.dto.request.AlineacionRequest;
import com.futbol.estadisticas.application.port.dto.response.*;
import com.futbol.estadisticas.application.port.in.DatosDeportivosUseCase;
import com.futbol.estadisticas.application.port.mapper.AlineacionMapper;
import com.futbol.estadisticas.application.port.mapper.CompeticionMapper;
import com.futbol.estadisticas.application.port.out.*;
import com.futbol.estadisticas.domain.model.*;
import com.futbol.estadisticas.domain.model.enums.Alineacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.CrearEquipoRequest;
import com.futbol.estadisticas.application.port.in.EquipoUseCase;
import com.futbol.estadisticas.application.port.mapper.EquipoMapper;
import com.futbol.estadisticas.application.port.mapper.JugadorMapper;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EquipoService implements EquipoUseCase {

    private final EquipoRepositoryPort equipoRepository;
    private final EquipoMapper equipoMapper;
    private final AlineacionMapper alineacionMapper;
    private final PartidoRepositoryPort partidoRepository;
    private final EventosPartidoRepositoryPort eventosPartidoRepository;
    private final DatosDeportivosUseCase datosDeportivosUseCase;
    private final DatosDeportivosRepositoryPort datosDeportivosRepository;
    private final JugadorMapper jugadorMapper;
    private final CompeticionMapper competicionMapper;


    @Override
    public AlineacionResponse establecerAlineacionTitular(UUID idEquipo, AlineacionRequest request) {
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con id: " + idEquipo));

        Partido partido = partidoRepository.findById(request.idPartido())
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado con id: " + request.idPartido()));

        if (!partido.getEquipoLocal().getIdEquipo().equals(idEquipo) &&
                !partido.getEquipoVisitante().getIdEquipo().equals(idEquipo)) {
            throw new IllegalArgumentException("El equipo no participa en este partido");
        }

        Alineacion alineacion = request.alineacion();
        Map<String, UUID> mapaCampos = request.toMap();

        List<JugadorPosicionNotificacionDTO> jugadoresConPosicion = equipo.asignarPosiciones(alineacion, mapaCampos);

        List<Jugador> onceTitulares = jugadoresConPosicion.stream()
                .map(JugadorPosicionNotificacionDTO::jugador)
                .toList();

        List<UUID> idsTitulares = onceTitulares.stream()
                .map(Jugador::getIdPersonal)
                .toList();

        Map<UUID, DatosDeportivos> datosDeportivosMap = new HashMap<>();

        for (JugadorPosicionNotificacionDTO dto : jugadoresConPosicion) {
            Jugador jugador = dto.jugador();
            if (jugador.getDatosDeportivos() != null) {
                DatosDeportivos datos = jugador.getDatosDeportivos();
                datos.promoverATitular();
                datosDeportivosMap.put(datos.getIdHistorialDeportivo(), datos);
            }
        }

        for (Jugador jugador : equipo.getJugadoresActivos()) {
            if (!idsTitulares.contains(jugador.getIdPersonal()) && !jugador.estaLesionado()) {
                try {
                    DatosDeportivos datos = jugador.getDatosDeportivos();
                    if (datos != null) {
                        datos.cambiarASuplente();
                        datosDeportivosMap.put(datos.getIdHistorialDeportivo(), datos);
                    }
                } catch (IllegalStateException ignored) {
                }
            }
        }

        List<DatosDeportivos> datosDeportivosActualizados = new ArrayList<>(datosDeportivosMap.values());

        if (!datosDeportivosActualizados.isEmpty()) {
            for (DatosDeportivos datos : datosDeportivosActualizados) {
                try {
                    if (datos.getJugador() != null) {
                        datosDeportivosRepository.save(datos);
                    }
                } catch (DuplicateKeyException ignored) {
                }
            }
        }

        partido.getEventos().removeIf(e ->
                e.getTipoEvento() == TipoEvento.TITULAR &&
                        e.getEquipoFavorecido() != null &&
                        e.getEquipoFavorecido().getIdEquipo().equals(equipo.getIdEquipo())
        );

        boolean esLocal = partido.getEquipoLocal().getIdEquipo().equals(idEquipo);
        if (esLocal) {
            partido.setAlineacionLocal(alineacion);
        } else {
            partido.setAlineacionVisitante(alineacion);
        }
        partidoRepository.save(partido);

        return alineacionMapper.toResponse(equipo, alineacion, jugadoresConPosicion);
}

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
