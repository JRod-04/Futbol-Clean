package com.futbol.estadisticas.domain.model;

import com.futbol.estadisticas.domain.model.enums.Rol;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Usuario {

    @EqualsAndHashCode.Include
    private UUID idUsuario;

    private String username;
    private String password;
    private Rol rol;
    private boolean activo;

}
