package com.futbol.estadisticas.application.port.dto.request;

import java.time.LocalDate;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record CrearEquipoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
 
        @NotBlank(message = "El nombre corto es obligatorio")
        @Size(max = 10, message = "El nombre corto no puede superar 10 caracteres")
        String nombreCorto,

        @NotBlank(message = "El país es obligatorio")
        Nacion paisEquipo,

        @Past(message = "La fecha de fundación debe ser en el pasado")
        LocalDate fechaFundacion,

        TipoEquipo tipo
) {

}
