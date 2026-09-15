package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.Rol;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @EqualsAndHashCode.Include
    private UUID id;

    private String nombre;
    private String constraseña;
    private Rol rol;
    private boolean activo;
    
}
