package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum TipoEquipo {
    // ============ FÚTBOL DE CLUBES ============
    CLUB_PROFESIONAL("Club Profesional", "Equipo profesional de primera o segunda división"),
    CLUB_AMATEUR("Club Amateur", "Equipo de categorías regionales o aficionadas"),
    FILIAL("Equipo Filial", "Equipo secundario"),
    RESERVA("Equipo Reserva","Equipo Juvenil de un club"),

    // ============ FÚTBOL DE SELECCIONES ============
    SELECCION_ABSOLUTA("Selección Absoluta", "Selección nacional mayor / principal"),
    SELECCION_JUVENIL("Selección Juvenil", "Selección nacional por categoría de edad"),

    // ============ EXPOSICIÓN / OTROS ============
    EQUIPO_COMBINADO("Combinado / All-Star", "Equipo de exhibición o selección regional");

    private final String DisplayName;
    private final String descripcion;

    // =========================================================================
    // MÉTODOS DE DOMINIO / VALIDACIÓN
    // =========================================================================

    public boolean esSeleccion() {
        return this == SELECCION_ABSOLUTA || this == SELECCION_JUVENIL;
    }


    public boolean esClub() {
        return this == CLUB_PROFESIONAL || this == CLUB_AMATEUR || this == FILIAL;
    }

    public boolean esFilial() {
        return this == FILIAL;
    }

    public boolean esCombinado() {
        return this == EQUIPO_COMBINADO;
    }

    public boolean esCategoriaInferior() {
        return this == SELECCION_JUVENIL;
    }
    @Override
    public String toString() {
        return getDisplayName();
    }
}
