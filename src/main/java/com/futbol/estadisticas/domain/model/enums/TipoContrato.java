package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum TipoContrato {
    PROFESIONAL("Profesional", "Contrato profesional estándar"),
    JUVENIL("Juvenil", "Contrato para jugadores en formación"),
    CESION("Cesión", "Jugador cedido desde otro equipo"),
    AMATEUR("Amateur", "Contrato amateur"),
    CONVOCATORIA("Convocatoria Internacional","Convocatoria a Selecciones, Absolutas o con rango de edad");

    private final String displayName;
    private final String descripcion;
}
