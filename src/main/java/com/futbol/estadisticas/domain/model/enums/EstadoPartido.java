package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoPartido {
    PROGRAMADO("Programado", "Partido programado para jugarse", false, false),
    PRIMER_TIEMPO("1er Tiempo", "Jugando el primer tiempo", true, false),
    ENTRETIEMPO("Entretiempo", "Descanso entre tiempos", true, false),
    SEGUNDO_TIEMPO("2do Tiempo", "Jugando el segundo tiempo", true, false),
    PRORROGA("Prórroga", "Tiempo extra", true, false),
    PENALTIS("Penaltis", "Definiendo por penaltis", true, false),
    FINALIZADO("Finalizado", "Partido terminado", false, true),
    SUSPENDIDO("Suspendido", "Partido suspendido", false, true),
    CANCELADO("Cancelado", "Partido cancelado", false, true);
    
    private final String displayName;
    private final String descripcion;
    private final boolean enCurso;
    private final boolean finalizado;
    
    //Verifica si el partido puede iniciarse desde este estado
    
    public boolean puedeIniciarse() {
        return this == PROGRAMADO;
    }
    
    //Verifica si el partido puede finalizarse desde este estado
     
    public boolean puedeFinalizarse() {
        return this == PRIMER_TIEMPO || this == SEGUNDO_TIEMPO || 
               this == PRORROGA || this == PENALTIS || this == ENTRETIEMPO;
    }
    
    //Verifica si el partido puede suspenderse
    
    public boolean puedeSuspenderse() {
        return this != FINALIZADO && this != CANCELADO && this != SUSPENDIDO;
    }
    
    //Obtiene el próximo estado en el flujo normal del partido
    public EstadoPartido getSiguienteEstado() {
        return switch (this) {
            case PROGRAMADO -> PRIMER_TIEMPO;
            case PRIMER_TIEMPO -> ENTRETIEMPO;
            case ENTRETIEMPO -> SEGUNDO_TIEMPO;
            case SEGUNDO_TIEMPO -> FINALIZADO;
            case PRORROGA -> PENALTIS;
            case PENALTIS -> FINALIZADO;
            default -> this;
        };
    }
    
    //Obtiene el estado previo en el flujo normal del partido
    public EstadoPartido getEstadoPrevio() {
        return switch (this) {
            case PRIMER_TIEMPO -> PROGRAMADO;
            case ENTRETIEMPO -> PRIMER_TIEMPO;
            case SEGUNDO_TIEMPO -> ENTRETIEMPO;
            case PRORROGA -> SEGUNDO_TIEMPO;
            case PENALTIS -> PRORROGA;
            default -> this;
        };
    }
}