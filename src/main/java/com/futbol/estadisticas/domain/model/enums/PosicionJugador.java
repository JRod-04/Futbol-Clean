package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PosicionJugador {
    PORTERO("Portero", "GK", "Defensa"),
    CENTRAL("Defensa Central", "CB", "Defensa"),
    LATERAL_DERECHO("Lateral Derecho", "RB", "Defensa"),
    LATERAL_IZQUIERDO("Lateral Izquierdo", "LB", "Defensa"),
    CARRILERO_DERECHO("Carrilero Derecho", "RWB", "Mediocampo"),
    CARRILERO_IZQUIERDO("Carrilero Izquierdo", "LWB", "Mediocampo"),
    MEDIOCENTRO_DEFENSIVO("Mediocentro Defensivo", "CDM", "Mediocampo"),
    MEDIOCENTRO("Mediocentro", "CM", "Mediocampo"),
    MEDIOCENTRO_OFENSIVO("Mediocentro Ofensivo", "CAM", "Mediocampo"),
    INTERIOR_DERECHO("Interior Derecho", "RM", "Mediocampo"),
    INTERIOR_IZQUIERDO("Interior Izquierdo", "LM", "Mediocampo"),
    EXTREMO_DERECHO("Extremo Derecho", "RW", "Delantera"),
    EXTREMO_IZQUIERDO("Extremo Izquierdo", "LW", "Delantera"),
    MEDIAPUNTA("Mediapunta", "AM", "Delantera"),
    DELANTERO("Delantero", "ST", "Delantera");
    
    private final String displayName;
    private final String abreviatura;
    private final String zona;

    @Override
    public String toString() {
        return getAbreviatura();
    }
   
}
