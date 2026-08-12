package com.futbol.estadisticas.application.port.mapper;

import com.futbol.estadisticas.application.port.dto.response.EstadisticasCompeticionDTO;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasJugadorResponse;
import com.futbol.estadisticas.application.port.dto.response.EstadisticasPartidoJugadorResponse;
import com.futbol.estadisticas.domain.model.Competicion;
import com.futbol.estadisticas.domain.model.EventosPartido;
import com.futbol.estadisticas.domain.model.Jugador;
import com.futbol.estadisticas.domain.model.Partido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class EstadisticasCompeticionMapper {

    private final EstadisticasPartidoMapper estadisticasPartidoMapper;

    public EstadisticasCompeticionMapper(EstadisticasPartidoMapper estadisticasPartidoMapper) {
        this.estadisticasPartidoMapper = estadisticasPartidoMapper;
    }

    public EstadisticasCompeticionDTO toDTO(Competicion competicion, List<EventosPartido> eventos) {
        Map<UUID, List<EventosPartido>> eventosPorPartido = eventos.stream()
                .filter(e -> e.getPartido() != null)
                .collect(Collectors.groupingBy(e -> e.getPartido().getIdPartido()));

        int partidosJugados = eventosPorPartido.size();

        int minutosJugados = eventosPorPartido.values().stream()
                .mapToInt(eventosDelPartido -> {
                    Partido partido = eventosDelPartido.get(0).getPartido();
                    return estadisticasPartidoMapper
                            .toResponse(partido, eventosDelPartido)
                            .minutosJugados();
                })
                .sum();

        int totalGoles = 0;
        int totalGolesPenal = 0;
        int totalPenalesFallados = 0;
        int totalAutogoles = 0;
        int totalAsistencias = 0;
        int totalAmarillas = 0;
        int totalRojas = 0;

        for (List<EventosPartido> eventosDelPartido : eventosPorPartido.values()) {
            Partido partido = eventosDelPartido.get(0).getPartido();
            var statsPartido = estadisticasPartidoMapper.toResponse(partido, eventosDelPartido);

            totalGoles += statsPartido.goles();
            totalGolesPenal += statsPartido.golesPenal();
            totalPenalesFallados += statsPartido.penalesFallados();
            totalAutogoles += statsPartido.autogoles();
            totalAsistencias += statsPartido.asistencias();
            totalAmarillas += statsPartido.tarjetasAmarillas();
            totalRojas += statsPartido.tarjetasRojas();
        }

        return EstadisticasCompeticionDTO.builder()
                .idCompeticion(competicion.getIdCompeticion())
                .nombreCompeticion(competicion.getNombre())
                .partidosJugados(partidosJugados)
                .minutosJugados(minutosJugados)
                .goles(totalGoles)
                .golesPenal(totalGolesPenal)
                .penalesFallados(totalPenalesFallados)
                .autogoles(totalAutogoles)
                .asistencias(totalAsistencias)
                .tarjetasAmarillas(totalAmarillas)
                .tarjetasRojas(totalRojas)
                .build();
    }
}
