package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoCompeticion {
    POR_INICIAR("Por iniciar", "La competición aún no ha comenzado"),
    EN_CURSO("En curso", "La competición se está disputando actualmente"),
    FINALIZADA("Finalizada", "La competición ha terminado"),
    SUSPENDIDA("Suspendida", "La competición ha sido suspendida"),
    CANCELADA("Cancelada", "La competición ha sido cancelada");

    private final String displayName;
    private final String descripcion;
}