package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum Alineacion {

    ALINEACION_433("4-3-3",
            "Es el sistema por excelencia para equipos que buscan protagonismo",
            List.of(
                    List.of(PosicionJugador.PORTERO),
                    List.of(
                            PosicionJugador.LATERAL_IZQUIERDO,
                            PosicionJugador.CENTRAL_IZQUIERDO,
                            PosicionJugador.CENTRAL_DERECHO,
                            PosicionJugador.LATERAL_DERECHO
                    ),
                    List.of(
                            PosicionJugador.MEDIOCAMPISTA_IZQUIERDO,
                            PosicionJugador.CENTROCAMPISTA,
                            PosicionJugador.MEDIOCAMPISTA_DERECHO
                    ),
                    List.of(
                            PosicionJugador.EXTREMO_IZQUIERDO,
                            PosicionJugador.DELANTERO_CENTRO,
                            PosicionJugador.EXTREMO_DERECHO
                    )
            )),

    ALINEACION_4231("4-2-3-1",
            "El sistema más equilibrado en el fútbol moderno",
            List.of(
                    List.of(PosicionJugador.PORTERO),
                    List.of(
                            PosicionJugador.LATERAL_IZQUIERDO,
                            PosicionJugador.CENTRAL_IZQUIERDO,
                            PosicionJugador.CENTRAL_DERECHO,
                            PosicionJugador.LATERAL_DERECHO
                    ),
                    List.of(
                            PosicionJugador.MEDIOCAMPISTA_DEFENSIVO_IZQUIERDO,
                            PosicionJugador.MEDIOCAMPISTA_DEFENSIVO_DERECHO
                    ),
                    List.of(
                            PosicionJugador.EXTREMO_IZQUIERDO,
                            PosicionJugador.MEDIOCAMPISTA_OFENSIVO,
                            PosicionJugador.EXTREMO_DERECHO
                    ),
                    List.of(
                            PosicionJugador.DELANTERO_CENTRO
                    )
            )),

    ALINEACION_442("4-4-2",
            "Un clásico. Simplicidad y orden riguroso",
            List.of(
                    List.of(PosicionJugador.PORTERO),
                    List.of(
                            PosicionJugador.LATERAL_IZQUIERDO,
                            PosicionJugador.CENTRAL_IZQUIERDO,
                            PosicionJugador.CENTRAL_DERECHO,
                            PosicionJugador.LATERAL_DERECHO
                    ),
                    List.of(
                            PosicionJugador.MEDIOCAMPISTA_IZQUIERDO,
                            PosicionJugador.MEDIOCENTRO_IZQUIERDO,
                            PosicionJugador.MEDIOCENTRO_DERECHO,
                            PosicionJugador.MEDIOCAMPISTA_DERECHO
                    ),
                    List.of(
                            PosicionJugador.DELANTERO_IZQUIERDO,
                            PosicionJugador.DELANTERO_DERECHO
                    )
            )),

    ALINEACION_352("3-5-2",
            "Domina el centro del campo con densidad de jugadores",
            List.of(
                    List.of(PosicionJugador.PORTERO),
                    List.of(
                            PosicionJugador.CENTRAL_IZQUIERDO,
                            PosicionJugador.DEFENSA_CENTRAL,
                            PosicionJugador.CENTRAL_DERECHO
                    ),
                    List.of(
                            PosicionJugador.CARRILERO_IZQUIERDO,
                            PosicionJugador.MEDIOCENTRO_IZQUIERDO,
                            PosicionJugador.CENTROCAMPISTA,
                            PosicionJugador.MEDIOCENTRO_DERECHO,
                            PosicionJugador.CARRILERO_DERECHO
                    ),
                    List.of(
                            PosicionJugador.DELANTERO_IZQUIERDO,
                            PosicionJugador.DELANTERO_DERECHO
                    )
            )),

    ALINEACION_343("3-4-3",
            "La apuesta más valiente. Posesión y ataque masivo",
            List.of(
                    List.of(PosicionJugador.PORTERO),
                    List.of(
                            PosicionJugador.CENTRAL_IZQUIERDO,
                            PosicionJugador.DEFENSA_CENTRAL,
                            PosicionJugador.CENTRAL_DERECHO
                    ),
                    List.of(
                            PosicionJugador.CARRILERO_IZQUIERDO,
                            PosicionJugador.MEDIOCENTRO_IZQUIERDO,
                            PosicionJugador.MEDIOCENTRO_DERECHO,
                            PosicionJugador.CARRILERO_DERECHO
                    ),
                    List.of(
                            PosicionJugador.EXTREMO_IZQUIERDO,
                            PosicionJugador.DELANTERO_CENTRO,
                            PosicionJugador.EXTREMO_DERECHO
                    )
            )),


    ALINEACION_4141("4-1-4-1",
                           "Buen control del campo. Posesión y ataque masivo",
                   List.of(
                           List.of(PosicionJugador.PORTERO),
                    List.of(
    PosicionJugador.LATERAL_IZQUIERDO,
    PosicionJugador.CENTRAL_IZQUIERDO,
    PosicionJugador.CENTRAL_DERECHO,
    PosicionJugador.LATERAL_DERECHO
                    ),
                           List.of(PosicionJugador.MEDIOCAMPISTA_DEFENSIVO),
                            List.of(
    PosicionJugador.MEDIOCAMPISTA_IZQUIERDO,
    PosicionJugador.MEDIOCENTRO_IZQUIERDO,
    PosicionJugador.MEDIOCENTRO_DERECHO,
    PosicionJugador.MEDIOCAMPISTA_DERECHO
                    ),
                            List.of(
    PosicionJugador.DELANTERO_CENTRO
                    )
                           ));

    private final String nombre;
    private final String descripcion;
    private final List<List<PosicionJugador>> lineas;

    // ─── MÉTODOS AUXILIARES ────────────────────────────────────────────────

    public List<PosicionJugador> getPosicionesAplanadas() {
        return lineas.stream()
                .flatMap(List::stream)
                .toList();
    }
}