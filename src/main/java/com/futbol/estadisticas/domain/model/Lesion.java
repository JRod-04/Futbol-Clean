package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Gravedad;

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
public class Lesion {

    @EqualsAndHashCode.Include
    private UUID idLesion;


    private String nombreLesion;
    private Gravedad gravedad; 
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private Jugador jugadorLesionado;

     @Builder.Default
    private boolean curada = false;
    
    //Verifica si la lesión está activa actualmente
    public boolean esActiva() {
        LocalDate hoy = LocalDate.now();
        return !curada && 
               fechaInicio != null && 
               fechaInicio.isBefore(hoy) && 
               (fechaFin == null || hoy.isBefore(fechaFin));
    }
    
    //Cura la lesión
    public void curar() {
        if (this.curada) {
            throw new IllegalStateException("La lesión ya está curada");
        }
        this.curada = true;
        this.fechaFin = LocalDate.now();
    }
    
    //Verifica si la lesión es grave (GRAVE o CRITICA)
    public boolean esGrave() {
        return gravedad == Gravedad.GRAVE || gravedad == Gravedad.CRITICA;
    }
    
    //Obtiene la duración de la lesión en días
    public long getDuracionDias() {
        if (fechaInicio == null) {
            return 0;
        }
        LocalDate fechaFinCalculo = fechaFin != null ? fechaFin : LocalDate.now();
        return ChronoUnit.DAYS.between(fechaInicio, fechaFinCalculo);
    }

    
    //Verifica si la lesión necesita atención médica urgente
    public boolean necesitaAtencionUrgente() {
        return gravedad == Gravedad.GRAVE || gravedad == Gravedad.CRITICA;
    }
    
    //Obtiene el estado de la lesión como texto
    
    public String getEstadoLesion() {
        if (curada) {
            return "Curada";
        }
        if (esActiva()) {
            return "Activa";
        }
        return "Inactiva";
    }
    
    //Calcula los días restantes de recuperación
    public long getDiasRestantesRecuperacion() {
        if (fechaFin == null || curada || !esActiva()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);
    }
}
