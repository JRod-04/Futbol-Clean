package com.futbol.estadisticas.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.futbol.estadisticas.domain.model.enums.EstadoCompeticion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Competicion {

    @EqualsAndHashCode.Include
    private UUID idCompeticion;

    
    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Equipo equipoGanador;
    private EstadoCompeticion estado;

     @Builder.Default
    private List<Partido> partidos = new ArrayList<>();
    

    public void agregarPartido(Partido partido) {
        if (partido != null) {
            this.partidos.add(partido);
            partido.setCompeticion(this);
        }
    }
    
    public boolean estaActiva() {
        return this.estado == EstadoCompeticion.EN_CURSO;
    }
    
    public boolean haFinalizado() {
        return this.estado == EstadoCompeticion.FINALIZADA;
    }
    
    public boolean noHaComenzado() {
        return this.estado == EstadoCompeticion.POR_INICIAR;

    }
    

    public List<Partido> getPartidosJugados() {
        return partidos.stream()
            .filter(Partido::haFinalizado)
            .toList();
    }

    public double getPorcentajePartidosJugados() {
        if (partidos.isEmpty()) {
            return 0;
        }
        long jugados = partidos.stream().filter(Partido::haFinalizado).count();
        return (double) jugados / partidos.size() * 100;
    }

    public List<Equipo> getClubesParticipantes() {
        return this.partidos.stream()
                .flatMap(p -> Stream.of(p.getEquipoLocal(), p.getEquipoVisitante()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public void finalizarCompeticion() {
        if (this.estado == EstadoCompeticion.FINALIZADA) {
            throw new IllegalStateException("La competición ya está finalizada");
        }
        this.estado = EstadoCompeticion.FINALIZADA;
    }

    public void iniciarCompeticion() {
        if (this.estado == EstadoCompeticion.FINALIZADA) {
            throw new IllegalStateException("No se puede activar una competición finalizada");
        }
        this.estado = EstadoCompeticion.EN_CURSO;
    }

    public void suspender() {
        if (this.estado == EstadoCompeticion.FINALIZADA) {
            throw new IllegalStateException("No se puede suspender una competición finalizada");
        }
        this.estado = EstadoCompeticion.SUSPENDIDA;
    }

    public void reanudar() {
        if (this.estado == EstadoCompeticion.FINALIZADA) {
            throw new IllegalStateException("No se puede reanudar una competición finalizada");
        }
        this.estado = EstadoCompeticion.EN_CURSO;
    }




}
