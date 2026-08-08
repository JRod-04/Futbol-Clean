package com.futbol.estadisticas.application.port.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.dto.request.CrearJugadorRequest;
import com.futbol.estadisticas.application.port.dto.response.JugadorResponse;
import com.futbol.estadisticas.domain.model.Equipo;
import com.futbol.estadisticas.domain.model.DatosDeportivos;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;

@Component
public class JugadorMapper {
      public Jugador toEntity(CrearJugadorRequest request) {
        UUID idPersonal = UUID.randomUUID();
 

        DatosDeportivos datosDeportivos = DatosDeportivos.builder()
                .idHistorialDeportivo(UUID.randomUUID())
                .posiciones(request.posiciones() != null
                        ? request.posiciones()
                        : new ArrayList<>())
                .estadoJugador(EstadoJugador.SUPLENTE)
                .valorMercado(request.valorMercado())
                .dorsal(request.dorsal())
                .fechaActualizacion(LocalDate.now())
                .build();
 
        Jugador jugador = Jugador.builder()
                .idPersonal(idPersonal)
                .nombre(request.nombre())
                .apellido(request.apellido())
                .fechaNacimiento(request.fechaNacimiento())
                .nacionalidad(request.nacionalidad())
                .tipoPersonal(TipoPersonal.JUGADOR)
                .pieHabil(request.pieHabil())
                .altura(request.altura())
                .peso(request.peso())
                .fechaActualizacion(LocalDate.now())
                .datosDeportivos(datosDeportivos)
                .build();
 
        datosDeportivos.setJugador(jugador);
        return jugador;
    }
 
    public JugadorResponse toResponse(Jugador jugador) {
        DatosDeportivos datos = jugador.getDatosDeportivos();
        Equipo club = jugador.getEquipoActual();
        long lesionesActivas = jugador.getLesiones().stream()
                .filter(l -> l.esActiva())
                .count();
 
        return new JugadorResponse(
                jugador.getIdPersonal(),
                jugador.getNombre(),
                jugador.getApellido(),
                jugador.getNombreCompleto(),
                jugador.getFechaNacimiento(),
                jugador.getEdad(),
                jugador.getNacionalidad(),
                jugador.getPieHabil(),
                jugador.getAltura(),
                jugador.getPeso(),
                datos != null ? datos.getPosiciones() : new ArrayList<>(),
                datos != null ? datos.getDorsal() : null,
                datos != null ? datos.getEstadoJugador() : null,
                datos != null ? datos.getValorMercado() : null,
                datos != null ? datos.getValorMercadoEnMillones() : 0.0,
                club != null ? club.getNombre() : null,
                club != null ? club.getIdEquipo() : null,
                jugador.estaDisponible(),
                (int) lesionesActivas
        );
    }
}
