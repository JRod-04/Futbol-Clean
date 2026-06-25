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
    MEDIAPUNTA("Mediapunta", "SS", "Delantera"),
    DELANTERO("Delantero", "ST", "Delantera");
    
    private final String displayName;
    private final String abreviatura;
    private final String zona;
  
    

    public boolean esDefensa() {
        return "Defensa".equals(zona);
    }
    
    //Verifica si es una posición de mediocampo
    public boolean esMediocampo() {
        return "Mediocampo".equals(zona);
    }
    
    //Verifica si es una posición de delantera
    public boolean esDelantera() {
        return "Delantera".equals(zona);
    }
    
    //Verifica si es una posición de banda (lateral o extremo)
    public boolean esPosicionDeBanda() {
        return this == LATERAL_DERECHO || this == LATERAL_IZQUIERDO ||
               this == CARRILERO_DERECHO || this == CARRILERO_IZQUIERDO ||
               this == EXTREMO_DERECHO || this == EXTREMO_IZQUIERDO;
    }
    
    //Verifica si es una posición central
    public boolean esPosicionCentral() {
        return this == CENTRAL || this == MEDIOCENTRO_DEFENSIVO || 
               this == MEDIOCENTRO || this == MEDIOCENTRO_OFENSIVO ||
               this == DELANTERO;
    }
    
    //Verifica si es una posición ofensiva
    public boolean esOfensiva() {
        return esDelantera() || this == MEDIOCENTRO_OFENSIVO || 
               this == MEDIAPUNTA || this == EXTREMO_DERECHO || 
               this == EXTREMO_IZQUIERDO;
    }

   
}
