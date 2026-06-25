package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
public class Arbitro {
    @EqualsAndHashCode.Include
    private UUID idArbitro;
    
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;

     @Builder.Default
    private List<Partido> partidosArbitrados = new ArrayList<>();
    
    //Obtiene el nombre completo del árbitro
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    //Calcula la edad del árbitro
    public int getEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
    
    //Agrega un partido arbitrado
    public void agregarPartido(Partido partido) {
        if (partido != null) {
            this.partidosArbitrados.add(partido);
            partido.setArbitro(this);
        }
    }
    
    //Obtiene la cantidad de partidos arbitrados
    public int getCantidadPartidos() {
        return partidosArbitrados.size();
    }
    
    //Obtiene los partidos arbitrados en una temporada específica
    public List<Partido> getPartidosPorTemporada(int año) {
        return partidosArbitrados.stream()
            .filter(p -> p.getFechaYHora() != null && 
                        p.getFechaYHora().getYear() == año)
            .toList();
    }

}
