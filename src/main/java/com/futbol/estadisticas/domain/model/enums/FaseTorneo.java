package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FaseTorneo {
    LIGA("Liga", "Fase regular de liga"),

    // ============ FASE DE GRUPOS ============

    GRUPO_A("Grupo A", "Fase de Grupos - Grupo A"),
    GRUPO_B("Grupo B", "Fase de Grupos - Grupo B"),
    GRUPO_C("Grupo C", "Fase de Grupos - Grupo C"),
    GRUPO_D("Grupo D", "Fase de Grupos - Grupo D"),
    GRUPO_E("Grupo E", "Fase de Grupos - Grupo E"),
    GRUPO_F("Grupo F", "Fase de Grupos - Grupo F"),
    GRUPO_G("Grupo G", "Fase de Grupos - Grupo G"),
    GRUPO_H("Grupo H", "Fase de Grupos - Grupo H"),
    GRUPO_I("Grupo I", "Fase de Grupos - Grupo I"),
    GRUPO_J("Grupo J", "Fase de Grupos - Grupo J"),
    GRUPO_K("Grupo K", "Fase de Grupos - Grupo K"),
    GRUPO_L("Grupo L", "Fase de Grupos - Grupo L"),


    // ============ ELIMINACIÓN DIRECTA (Mundial/Champions) ============

    DIECISEIS_FINAL("1/16 Final", "Dieciseisavos de Final"),
    OCTAVOS_FINAL("1/8 Final", "Octavos de final"),
    CUARTOS_FINAL("1/4 Final", "Cuartos de final"),
    SEMIFINAL("Semifinal", "Semifinal"),
    FINAL("Final", "Final"),
    TERCER_PUESTO("Tercer Puesto", "Partido por el tercer puesto"),

    // ============ CHAMPIONS LEAGUE (Nuevo formato) ============
    FASE_LIGA("Fase Liga", "Fase de liga de la Champions"),
    PLAYOFF_ELIMINACION("Playoff Eliminación", "Playoff de eliminación"),

    // ============ OTROS ============
    REPECHAJE("Repechaje", "Partido de repechaje");

    private final String displayName;
    private final String descripcion;

    @Override
    public String toString() {
        return getDisplayName();
    }
}
