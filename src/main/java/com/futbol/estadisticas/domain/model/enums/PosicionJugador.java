package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum PosicionJugador {
    PORTERO("Portero", "GK", "Defensa"),

    //DEFENSAS
    DEFENSA_CENTRAL("Defensa Central", "CB", "Defensa"),
    CENTRAL_IZQUIERDO("Defensa Central Izquierdo", "LCB", "Defensa"),
    CENTRAL_DERECHO("Defensa Central Derecho", "RCB", "Defensa"),
    LATERAL_DERECHO("Lateral Derecho", "RB", "Defensa"),
    LATERAL_IZQUIERDO("Lateral Izquierdo", "LB", "Defensa"),

    //MEDIOCAMPISTAS DEFENSIVOS
    CARRILERO_DERECHO("Carrilero Derecho", "RWB", "Mediocampo"),
    CARRILERO_IZQUIERDO("Carrilero Izquierdo", "LWB", "Mediocampo"),
    MEDIOCAMPISTA_DEFENSIVO("Mediocentro Defensivo", "CDM", "Mediocampo"),
    MEDIOCAMPISTA_DEFENSIVO_IZQUIERDO("Mediocentro Defensivo Izquierdo", "LCDM", "Mediocampo"),
    MEDIOCAMPISTA_DEFENSIVO_DERECHO("Mediocentro Defensivo Derecho", "RCDM", "Mediocampo"),


    //MEDIOCAMPISTAS
    MEDIOCENTRO_IZQUIERDO("Mediocentro Izquierdo", "LCM", "Mediocampo"),
    MEDIOCENTRO_DERECHO("Mediocentro Derecho", "RCM", "Mediocampo"),
    CENTROCAMPISTA("Mediocentro", "CM", "Mediocampo"),
    MEDIOCAMPISTA_IZQUIERDO("Centrocampista Izquierdo", "LM", "Mediocampo"),
    MEDIOCAMPISTA_DERECHO("Centrocampista Derecho", "RM", "Mediocampo"),

    //MEDIOCAMPISTAS DEFENSIVOS
    MEDIOCAMPISTA_OFENSIVO("Mediocentro Ofensivo", "CAM", "Delantera"),
    MEDIOCAMPISTA_OFENSIVO_IZQUIERDO("Mediocentro Ofensivo Izquierdo", "LAM", "Delantera"),
    MEDIOCAMPISTA_OFENSIVO_DERECHO("Mediocentro Ofensivo Derecho", "RAM", "Delantera"),


    //DELANTEROS
    EXTREMO_DERECHO("Extremo Derecho", "RW", "Delantera"),
    EXTREMO_IZQUIERDO("Extremo Izquierdo", "LW", "Delantera"),
    DELANTERO_CENTRO("Delantero", "ST", "Delantera"),
    DELANTERO_IZQUIERDO("Delantero Izquierdo", "LST", "Delantera"),
    DELANTERO_DERECHO("Delantero Derecho", "RST", "Delantera");

    private final String displayName;
    private final String abreviatura;
    private final String zona;

    public String getNombreCampo() {
        return switch (this) {
            case PORTERO -> "portero";
            case LATERAL_IZQUIERDO -> "lateralIzquierdo";
            case LATERAL_DERECHO -> "lateralDerecho";
            case CENTRAL_IZQUIERDO -> "centralIzquierdo";
            case CENTRAL_DERECHO -> "centralDerecho";
            case DEFENSA_CENTRAL -> "DefensaCentral";
            case CARRILERO_IZQUIERDO -> "carrileroIzquierdo";
            case CARRILERO_DERECHO -> "carrileroDerecho";
            case MEDIOCAMPISTA_DEFENSIVO -> "centroCampistaDefensivo";
            case MEDIOCAMPISTA_DEFENSIVO_IZQUIERDO -> "centroCampistaDefensivoIzquierdo";
            case MEDIOCAMPISTA_DEFENSIVO_DERECHO -> "centroCampistaDefensivoDerecho";
            case MEDIOCENTRO_IZQUIERDO -> "mediocentroIzquierdo";
            case MEDIOCENTRO_DERECHO -> "mediocentroDerecho";
            case CENTROCAMPISTA -> "centroCampista";
            case MEDIOCAMPISTA_DERECHO ->  "centroCampistaDerecho";
            case MEDIOCAMPISTA_IZQUIERDO ->  "centroCampistaIzquierdo";
            case MEDIOCAMPISTA_OFENSIVO -> "centroCampistaOfensivo";
            case MEDIOCAMPISTA_OFENSIVO_IZQUIERDO -> "centroCampistaOfensivoIzquierdo";
            case MEDIOCAMPISTA_OFENSIVO_DERECHO -> "centroCampistaOfensivoDerecho";
            case EXTREMO_IZQUIERDO -> "extremoIzquierdo";
            case EXTREMO_DERECHO -> "extremoDerecho";
            case DELANTERO_CENTRO -> "delanteroCentro";
            case DELANTERO_IZQUIERDO -> "delanteroIzquierdo";
            case DELANTERO_DERECHO -> "delanteroDerecho";

        };
    }
    @Override
    public String toString() {
        return getAbreviatura();
    }
   
}
