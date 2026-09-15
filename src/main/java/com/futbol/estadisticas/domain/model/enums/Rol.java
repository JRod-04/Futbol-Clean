package com.futbol.estadisticas.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum Rol {
    ADMIN("Adminstrador del sistema"), USUARIO("Usuario de la app");

    private String descripcion;

}
