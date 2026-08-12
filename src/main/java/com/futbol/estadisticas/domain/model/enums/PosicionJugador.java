package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum PosicionJugador {
    PORTERO("Portero", "GK", "Defensa"),

    CENTRAL("Defensa Central", "CB", "Defensa"),
    CENTRAL_IZQUIERDO("Defensa Central Izquierdo", "LCB", "Defensa"),
    CENTRAL_DERECHO("Defensa Central Derecho", "RCB", "Defensa"),
    LATERAL_DERECHO("Lateral Derecho", "RB", "Defensa"),
    LATERAL_IZQUIERDO("Lateral Izquierdo", "LB", "Defensa"),


    CARRILERO_DERECHO("Carrilero Derecho", "RWB", "Mediocampo"),
    CARRILERO_IZQUIERDO("Carrilero Izquierdo", "LWB", "Mediocampo"),
    MEDIOCENTRO_DEFENSIVO("Mediocentro Defensivo", "CDM", "Mediocampo"),
    MEDIOCENTRO_DEFENSIVO_IZQUIERDO("Mediocentro Defensivo Izquierdo", "LCDM", "Mediocampo"),
    MEDIOCENTRO_DEFENSIVO_DERECHO("Mediocentro Defensivo Derecho", "RCDM", "Mediocampo"),


    MEDIOCENTRO_IZQUIERDO("Mediocentro Izquierdo", "LCM", "Mediocampo"),
    MEDIOCENTRO_DERECHO("Mediocentro Derecho", "RCM", "Mediocampo"),
    MEDIOCENTRO("Mediocentro", "CM", "Mediocampo"),

    MEDIOCENTRO_OFENSIVO("Mediocentro Ofensivo", "CAM", "Delantera"),
    MEDIOCENTRO_OFENSIVO_IZQUIERDO("Mediocentro Ofensivo Izquierdo", "LAM", "Delantera"),
    MEDIOCENTRO_OFENSIVO_DERECHO("Mediocentro Ofensivo Derecho", "RAM", "Delantera"),


    EXTREMO_DERECHO("Extremo Derecho", "RW", "Delantera"),
    EXTREMO_IZQUIERDO("Extremo Izquierdo", "LW", "Delantera"),
    DELANTERO("Delantero", "ST", "Delantera");

    private final String displayName;
    private final String abreviatura;
    private final String zona;

    // En PosicionJugador.java - Método para mapear campos del request
    // En PosicionJugador.java
    public String getNombreCampo() {
        return switch (this) {
            case PORTERO -> "portero";
            case LATERAL_IZQUIERDO -> "lateralIzquierdo";
            case LATERAL_DERECHO -> "lateralDerecho";
            case CENTRAL_IZQUIERDO -> "centralIzquierdo";
            case CENTRAL_DERECHO -> "centralDerecho";
            case CENTRAL -> "central";
            case CARRILERO_IZQUIERDO -> "carrileroIzquierdo";
            case CARRILERO_DERECHO -> "carrileroDerecho";
            case MEDIOCENTRO_DEFENSIVO -> "mediocentroDefensivo";
            case MEDIOCENTRO_DEFENSIVO_IZQUIERDO -> "mediocentroDefensivoIzquierdo";
            case MEDIOCENTRO_DEFENSIVO_DERECHO -> "mediocentroDefensivoDerecho";
            case MEDIOCENTRO_IZQUIERDO -> "mediocentroIzquierdo";
            case MEDIOCENTRO_DERECHO -> "mediocentroDerecho";
            case MEDIOCENTRO -> "mediocentro";
            case MEDIOCENTRO_OFENSIVO -> "mediocentroOfensivo";
            case MEDIOCENTRO_OFENSIVO_IZQUIERDO -> "mediocentroOfensivoIzquierdo";
            case MEDIOCENTRO_OFENSIVO_DERECHO -> "mediocentroOfensivoDerecho";
            case EXTREMO_IZQUIERDO -> "extremoIzquierdo";
            case EXTREMO_DERECHO -> "extremoDerecho";
            case DELANTERO -> "delantero";
        };
    }
    @Override
    public String toString() {
        return getAbreviatura();
    }
   
}
