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
    RETIRADO("Retirado", "Jugador retirado del fútbol profesional", 6),
    CEDIDO("Cedido", "Jugador cedido a otro equipo", 7),
    NO_CONVOCADO("No Convocado", "Jugador no convocado por el entrenador", 8),
    LIBRE("Libre", "Jugador sin contrato", 9),
    EN_RECUPERACION("Retirado", "Jugador recuperándose de una lesión", 10),
    DUDOSO("Retirado", "Jugador en duda de participar en convocatoria del entrenador", 11);


    private final String displayName;
    private final String descripcion;
    private final int orden;

}
