package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

public record CrearJugadorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
 
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,
 
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message="La fecha de nacimiento debe ser en el pasado")
        LocalDate fechaNacimiento,
 
        @NotNull(message = "La nacionalidad es obligatoria")
        Nacion nacionalidad,
 
        @NotNull(message = "El pie hábil es obligatorio")
        JuegoPies pieHabil,
 
        Integer altura,
 
        Integer peso,
 
        @NotNull(message = "La posición es obligatoria")
        PosicionJugador posicion,
 
        @Positive(message = "El valor de mercado debe ser positivo")
        Double valorMercado
) {

}
