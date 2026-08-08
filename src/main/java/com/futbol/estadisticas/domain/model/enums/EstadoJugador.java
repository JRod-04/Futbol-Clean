package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoJugador {
    TITULAR("Titular", "Jugador en el once inicial", 1),
    SUPLENTE("Suplente", "Jugador en el banquillo", 2),
    APARTADO("Apartado", "Jugador separado del equipo", 3),
    LESIONADO("Lesionado", "Jugador con lesión", 4),
    SUSPENDIDO("Suspendido", "Jugador sancionado", 5),
    RETIRADO("Retirado", "Jugador retirado del fútbol profesional", 6);
    
    private final String displayName;
    private final String descripcion;
    private final int orden;
    
    public boolean estaDisponible() {
        return this == TITULAR || this == SUPLENTE;
    }
    
    public boolean estaInactivo() {
        return this == LESIONADO || this == SUSPENDIDO || this == RETIRADO || this == APARTADO;
    }
    
    public boolean puedeSerConvocado() {
        return this == TITULAR || this == SUPLENTE;
    }
    

    public EstadoJugador getEstadoBase() {
        if (this == LESIONADO || this == SUSPENDIDO || this == APARTADO) {
            return SUPLENTE; 
        }
        return this;
    }
    
    public boolean puedeTransicionarA(EstadoJugador nuevoEstado) {
        if (this == RETIRADO) {
            return false;
        }
        
        if (this == SUSPENDIDO && nuevoEstado != SUPLENTE && nuevoEstado != TITULAR) {
            return false;
        }
        
        if (this == LESIONADO && nuevoEstado != SUPLENTE && nuevoEstado != TITULAR) {
            return false;
        }
        
        return true;
    }

}
