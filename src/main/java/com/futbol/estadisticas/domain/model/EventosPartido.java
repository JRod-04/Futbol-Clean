package com.futbol.estadisticas.domain.model;

import java.time.LocalTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.TipoEvento;

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
public class EventosPartido {

    @EqualsAndHashCode.Include
    private UUID idEvento;


    private LocalTime minuto;
    private String descripcion;
    private TipoEvento tipoEvento;
    private Partido partido;
    private PersonalDeportivo personal;
    private Club equipoFavorecido;


    //Obtiene el minuto del evento en formato MM:SS
    public String getMinutoFormateado() {
        if (minuto == null) {
            return "0:00";
        }
        return String.format("%d'", minuto.getMinute());
    }
    
    //Verifica si el evento es un gol
    public boolean esGol() {
        return tipoEvento == TipoEvento.GOL || 
               tipoEvento == TipoEvento.AUTOGOL || 
               tipoEvento == TipoEvento.PENALTI_ANOTADO;
    }
    

    //Verifica si el evento es una tarjeta disciplinaria
    public boolean esTarjeta() {
        return tipoEvento == TipoEvento.AMARILLA || 
               tipoEvento == TipoEvento.ROJA;
    }
    
    //Verifica si el evento es una sustitución
    public boolean esSustitucion() {
        return tipoEvento == TipoEvento.SUB_IN || 
               tipoEvento == TipoEvento.SUB_OUT;
    }
    

    //Obtiene el color de la tarjeta si es una tarjeta
    public String getColorTarjeta() {
        if (tipoEvento == TipoEvento.AMARILLA) {
            return "Amarilla";
        } else if (tipoEvento == TipoEvento.ROJA) {
            return "Roja";
        }
        return null;
    }
    
    //Verifica si el evento es un penalti
    public boolean esPenalti() {
        return tipoEvento == TipoEvento.PENALTI_CONCEDIDO ||
               tipoEvento == TipoEvento.PENALTI_ANOTADO ||
               tipoEvento == TipoEvento.PENALTI_FALLADO;
    }
    

    //Obtiene el nombre del jugador relacionado con el evento
    public String getNombreJugador() {
        if (personal == null) {
            return "Desconocido";
        }
        return personal.getNombreCompleto();
    }
    
    //Obtiene el nombre del equipo favorecido
    public String getNombreEquipoFavorecido() {
        if (equipoFavorecido == null) {
            return "Ninguno";
        }
        return equipoFavorecido.getNombreCorto();
    }
    

    //Obtiene una descripción completa del evento
    public String getDescripcionCompleta() {
        if (descripcion != null && !descripcion.isEmpty()) {
            return descripcion;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(minuto != null ? getMinutoFormateado() : "0:00");
        sb.append(" - ");
        sb.append(tipoEvento != null ? tipoEvento.name().replace("_", " ") : "Evento");
        
        if (personal != null) {
            sb.append(" - ").append(personal.getNombreCompleto());
        }
        
        if (equipoFavorecido != null && esGol()) {
            sb.append(" (").append(equipoFavorecido.getNombreCorto()).append(")");
        }
        
        return sb.toString();
    }
    

    //Verifica si el evento es relevante para estadísticas
    public boolean esEstadisticable() {
        return tipoEvento != null && 
               tipoEvento != TipoEvento.INICIO_PARTIDO &&
               tipoEvento != TipoEvento.FIN_PARTIDO &&
               tipoEvento != TipoEvento.AGREGADO;
    }
}
