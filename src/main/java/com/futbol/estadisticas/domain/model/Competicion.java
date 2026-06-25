package com.futbol.estadisticas.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

     @Builder.Default
    private List<Partido> partidos = new ArrayList<>();
    

    //Agrega un partido a la competición
    public void agregarPartido(Partido partido) {
        if (partido != null) {
            this.partidos.add(partido);
            partido.setCompeticion(this);
        }
    }
    
    //Verifica si la competición está activa
    public boolean estaActiva() {
        LocalDateTime ahora = LocalDateTime.now();
        return fechaInicio != null && 
               fechaFin != null &&
               ahora.isAfter(fechaInicio) && 
               ahora.isBefore(fechaFin);
    }
    
    //Verifica si la competición ha finalizado
    public boolean haFinalizado() {
        return fechaFin != null && LocalDateTime.now().isAfter(fechaFin);
    }
    
    //Verifica si la competición aún no ha comenzado
    public boolean noHaComenzado() {
        return fechaInicio != null && LocalDateTime.now().isBefore(fechaInicio);
    }
    

    //Obtiene los partidos jugados (finalizados) de la competición
    public List<Partido> getPartidosJugados() {
        return partidos.stream()
            .filter(Partido::haFinalizado)
            .toList();
    }
    

    //Obtiene los partidos pendientes de la competición
    public List<Partido> getPartidosPendientes() {
        return partidos.stream()
            .filter(p -> !p.haFinalizado() && !p.esFuturo())
            .toList();
    }
    
    //Obtiene los partidos futuros de la competición
    public List<Partido> getPartidosFuturos() {
        return partidos.stream()
            .filter(Partido::esFuturo)
            .toList();
    }
    
    
    //Obtiene el porcentaje de partidos jugados
    public double getPorcentajePartidosJugados() {
        if (partidos.isEmpty()) {
            return 0;
        }
        long jugados = partidos.stream().filter(Partido::haFinalizado).count();
        return (double) jugados / partidos.size() * 100;
    }
}
