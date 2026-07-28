package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoPartido {
    PROGRAMADO("Programado", "Partido programado para jugarse", false, false),

    PRIMER_TIEMPO("1er Tiempo", "Jugando el primer tiempo", true, false),
    AGREGADO_PRIMER_TIEMPO("Tiempo Agregado 1er Tiempo", "Tiempo agregado sobre el primer tiempo", true, false),

    ENTRETIEMPO("Entretiempo", "Descanso entre tiempos", true, false),

    SEGUNDO_TIEMPO("2do Tiempo", "Jugando el segundo tiempo", true, false),
    AGREGADO_SEGUNDO_TIEMPO("Tiempo Agregado 2do Tiempo", "Tiempo agregado sobre el segundo tiempo", true, false),

    ESPERANDO_PRORROGA("Esperando a prorroga", "Esperando primer tiempo de prórroga", true, false),

    PRIMER_TIEMPO_PRORROGA("1er Tiempo Prórroga", "Primer tiempo de prórroga", true, false),
    AGREGADO_PRORROGA_PRIMER("Tiempo Agregado 1er Prórroga", "Tiempo agregado sobre la primera prórroga", true, false),

    ENTRETIEMPO_PRORROGA("Entretiempo Prórroga", "Descanso de prórroga", true, false),

    SEGUNDO_TIEMPO_PRORROGA("2do Tiempo Prórroga", "Segundo tiempo de prórroga", true, false),
    AGREGADO_PRORROGA_SEGUNDO("Tiempo Agregado 2do Prórroga", "Tiempo agregado sobre la segunda prórroga", true, false),

    ESPERANDO_PENALTIS("Esperando Definicion por penales", "Se espera definir por menales", true, false),

    PENALTIS("Penaltis", "Definiendo por penaltis", true, false),
    FINALIZADO("Finalizado", "Partido terminado", false, true),
    SUSPENDIDO("Suspendido", "Partido suspendido", false, true),
    CANCELADO("Cancelado", "Partido cancelado", false, true);

    private final String displayName;
    private final String descripcion;
    private final boolean enCurso;
    private final boolean finalizado;

    /**
     * Verifica si el partido puede iniciarse desde este estado
     */
    public boolean puedeIniciarse() {
        return this == PROGRAMADO;
    }

    /**
     * Verifica si el partido puede finalizarse desde este estado
     */
    public boolean puedeFinalizarse() {
        return this == PRIMER_TIEMPO || this == SEGUNDO_TIEMPO ||
                this == PRIMER_TIEMPO_PRORROGA || this == SEGUNDO_TIEMPO_PRORROGA ||
                this == PENALTIS || this == ENTRETIEMPO ||
                this == AGREGADO_PRIMER_TIEMPO || this == AGREGADO_SEGUNDO_TIEMPO ||
                this == AGREGADO_PRORROGA_PRIMER || this == AGREGADO_PRORROGA_SEGUNDO;
    }

    /**
     * Verifica si el partido puede suspenderse
     */
    public boolean puedeSuspenderse() {
        return this != FINALIZADO && this != CANCELADO && this != SUSPENDIDO;
    }

    /**
     * Obtiene el próximo estado en el flujo normal del partido
     */
    public EstadoPartido getSiguienteEstado() {
        return switch (this) {
            case PROGRAMADO -> PRIMER_TIEMPO;
            case PRIMER_TIEMPO -> AGREGADO_PRIMER_TIEMPO;
            case AGREGADO_PRIMER_TIEMPO -> ENTRETIEMPO;
            case ENTRETIEMPO -> SEGUNDO_TIEMPO;
            case SEGUNDO_TIEMPO -> AGREGADO_SEGUNDO_TIEMPO;
            case AGREGADO_SEGUNDO_TIEMPO -> ESPERANDO_PRORROGA;
            case ESPERANDO_PRORROGA -> PRIMER_TIEMPO_PRORROGA;
            case PRIMER_TIEMPO_PRORROGA -> AGREGADO_PRORROGA_PRIMER;
            case AGREGADO_PRORROGA_PRIMER -> ENTRETIEMPO_PRORROGA;
            case ENTRETIEMPO_PRORROGA -> SEGUNDO_TIEMPO_PRORROGA;
            case SEGUNDO_TIEMPO_PRORROGA -> AGREGADO_PRORROGA_SEGUNDO;
            case AGREGADO_PRORROGA_SEGUNDO -> ESPERANDO_PENALTIS;
            case ESPERANDO_PENALTIS -> PENALTIS;
            case PENALTIS -> FINALIZADO;
            default -> this;
        };
    }

    /**
     * Obtiene el estado previo en el flujo normal del partido
     */
    public EstadoPartido getEstadoPrevio() {
        return switch (this) {
            case PRIMER_TIEMPO -> PROGRAMADO;
            case AGREGADO_PRIMER_TIEMPO -> PRIMER_TIEMPO;
            case ENTRETIEMPO -> AGREGADO_PRIMER_TIEMPO;
            case SEGUNDO_TIEMPO -> ENTRETIEMPO;
            case AGREGADO_SEGUNDO_TIEMPO -> SEGUNDO_TIEMPO;
            case PRIMER_TIEMPO_PRORROGA -> AGREGADO_SEGUNDO_TIEMPO;
            case AGREGADO_PRORROGA_PRIMER -> PRIMER_TIEMPO_PRORROGA;
            case ENTRETIEMPO_PRORROGA -> AGREGADO_PRORROGA_PRIMER;
            case SEGUNDO_TIEMPO_PRORROGA -> ENTRETIEMPO_PRORROGA;
            case AGREGADO_PRORROGA_SEGUNDO -> SEGUNDO_TIEMPO_PRORROGA;
            case PENALTIS -> AGREGADO_PRORROGA_SEGUNDO;
            default -> this;
        };
    }

    /**
     * Obtiene el minuto límite para este estado
     */
    public int getMinutoLimite() {
        return switch (this) {
            case PRIMER_TIEMPO, AGREGADO_PRIMER_TIEMPO -> 45;
            case SEGUNDO_TIEMPO, AGREGADO_SEGUNDO_TIEMPO -> 90;
            case PRIMER_TIEMPO_PRORROGA, AGREGADO_PRORROGA_PRIMER -> 105;
            case SEGUNDO_TIEMPO_PRORROGA, AGREGADO_PRORROGA_SEGUNDO -> 120;
            default -> 0;
        };
    }

    /**
     * Verifica si está en tiempo agregado
     */
    public boolean esTiempoValido() {
        return this == AGREGADO_PRIMER_TIEMPO ||
                this == SEGUNDO_TIEMPO ||
                this == AGREGADO_SEGUNDO_TIEMPO ||
                this == ESPERANDO_PRORROGA ||
                this == AGREGADO_PRORROGA_PRIMER ||
                this == SEGUNDO_TIEMPO_PRORROGA ||
                this == AGREGADO_PRORROGA_SEGUNDO;
    }

    /**
     * Obtiene el estado base (sin agregado)
     */
    public EstadoPartido getEstadoBase() {
        return switch (this) {
            case AGREGADO_PRIMER_TIEMPO -> PRIMER_TIEMPO;
            case AGREGADO_SEGUNDO_TIEMPO -> SEGUNDO_TIEMPO;
            case AGREGADO_PRORROGA_PRIMER -> PRIMER_TIEMPO_PRORROGA;
            case AGREGADO_PRORROGA_SEGUNDO -> SEGUNDO_TIEMPO_PRORROGA;
            default -> this;
        };
    }
}