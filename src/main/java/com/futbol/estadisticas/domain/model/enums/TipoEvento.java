package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoEvento {
    // Eventos de gol
    GOL("Gol", "G"),
    AUTOGOL("Autogol", "AG"),
    PENALTI_ANOTADO("Penalti anotado", "PA"),
    PENALTI_FALLADO("Penalti fallado", "PF"),
    PENALTI_CONCEDIDO("Penalti concedido", "PC"),
    
    // Asistencias y tiros
    ASISTENCIA("Asistencia", "A"),
    TIRO_A_PUERTA("Tiro a puerta", "TP"),
    TIRO_FUERA("Tiro fuera", "TF"),
    PARADA("Parada", "P"),
    
    // Faltas y tarjetas
    FALTA_COMETIDA("Falta cometida", "FC"),
    FALTA_RECIBIDA("Falta recibida", "FR"),
    AMARILLA("Tarjeta Amarilla", "TA"),
    ROJA("Tarjeta Roja", "TR"),
    
    // Sustituciones
    SUB_IN("Entra al campo", "IN"),
    SUB_OUT("Sale del campo", "OUT"),
    
    // Inicio y fin
    INICIO_PARTIDO("Inicio del partido", "IP"),
    FIN_PARTIDO("Fin del partido", "FP"),
    AGREGADO("Tiempo agregado", "TA"),
    
    // Jugadas de estrategia
    CORNER("Córner", "C"),
    LATERAL("Lateral", "L"),
    SAQUE_META("Saque de meta", "SM"),
    TIRO_LIBRE("Tiro libre", "TL"),
    FUERA_DE_JUEGO("Fuera de juego", "FJ");
    
    private final String displayName;
    private final String abreviatura;
    
    //Verifica si es un gol (incluye autogoles y penaltis anotados)
    
    public boolean esGol() {
        return this == GOL || this == AUTOGOL || this == PENALTI_ANOTADO;
    }
    
    //Verifica si es una tarjeta disciplinaria
    public boolean esTarjeta() {
        return this == AMARILLA || this == ROJA;
    }
    
    //Verifica si es una sustitución
    public boolean esSustitucion() {
        return this == SUB_IN || this == SUB_OUT;
    }
    
    //Verifica si es un penalti
    public boolean esPenalti() {
        return this == PENALTI_ANOTADO || this == PENALTI_FALLADO || 
               this == PENALTI_CONCEDIDO;
    }
    
    //Verifica si es un evento de tiro
    public boolean esTiro() {
        return this == TIRO_A_PUERTA || this == TIRO_FUERA || 
               this == GOL || this == PENALTI_ANOTADO || this == PENALTI_FALLADO;
    }
    
    //Verifica si es un evento de falta
    public boolean esFalta() {
        return this == FALTA_COMETIDA || this == FALTA_RECIBIDA;
    }
    
    //Verifica si es un evento de juego de estrategia
    public boolean esJugadaEstrategia() {
        return this == CORNER || this == LATERAL || this == SAQUE_META || 
               this == TIRO_LIBRE || this == FUERA_DE_JUEGO;
    }
}