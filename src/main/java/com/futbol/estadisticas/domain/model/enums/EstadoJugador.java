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
    
    //Verifica si el jugador está disponible para jugar 
    public boolean estaDisponible() {
        return this == TITULAR || this == SUPLENTE;
    }
    
    //Verifica si el jugador está inactivo
    public boolean estaInactivo() {
        return this == LESIONADO || this == SUSPENDIDO || this == RETIRADO || this == APARTADO;
    }
    
    //Verifica si el jugador puede ser convocado
    public boolean puedeSerConvocado() {
        return this == TITULAR || this == SUPLENTE;
    }
    
    //Obtiene el estado base (sin considerar lesiones o suspensiones)
    
    public EstadoJugador getEstadoBase() {
        if (this == LESIONADO || this == SUSPENDIDO || this == APARTADO) {
            return SUPLENTE; 
        }
        return this;
    }
    
    //Transición de estado válida
    public boolean puedeTransicionarA(EstadoJugador nuevoEstado) {
        // Un jugador retirado no puede cambiar de estado
        if (this == RETIRADO) {
            return false;
        }
        
        // Un jugador suspendido solo puede pasar a SUPLENTE o TITULAR
        if (this == SUSPENDIDO && nuevoEstado != SUPLENTE && nuevoEstado != TITULAR) {
            return false;
        }
        
        // Un jugador lesionado solo puede pasar a SUPLENTE o TITULAR (cuando se recupere)
        if (this == LESIONADO && nuevoEstado != SUPLENTE && nuevoEstado != TITULAR) {
            return false;
        }
        
        return true;
    }

}
