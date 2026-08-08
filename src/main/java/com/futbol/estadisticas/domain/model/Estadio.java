package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
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
public class Estadio {

    @EqualsAndHashCode.Include
    private UUID idEstadio;


    private String nombre;
    private String direccion;
    private Integer capacidad;
    private LocalDate fechaFundacion;
    private Equipo equipoPrincipal;



    
    //Calcula el porcentaje de ocupación del estadio
    public double getPorcentajeOcupacion(Integer espectadores) {
        if (capacidad == null || capacidad == 0 || espectadores == null) {
            return 0;
        }
        return (double) espectadores / capacidad * 100;
    }
  


    
    //Obtiene la descripción completa del estadio
    public String getDescripcionCompleta() {
        return String.format("%s - Capacidad: %d espectadores - Fundado: %d",
            nombre,
            capacidad != null ? capacidad : 0,
            fechaFundacion != null ? fechaFundacion.getYear() : 0
        );
    }
}
