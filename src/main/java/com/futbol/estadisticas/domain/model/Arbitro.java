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
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    public int getEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
    
    public void agregarPartido(Partido partido) {
        if (partido != null) {
            this.partidosArbitrados.add(partido);
            partido.setArbitro(this);
        }
    }
    
    public int getCantidadPartidos() {
        return partidosArbitrados.size();
    }
    

}
