// src/main/java/com/tuapp/usuario/adapter/in/web/mapper/UsuarioWebMapper.java
package com.futbol.estadisticas.application.port.mapper;


import com.futbol.estadisticas.application.port.dto.request.SecurityDTORequest.*;
import com.futbol.estadisticas.application.port.dto.response.SecurityDTOResponse.*;
import com.futbol.estadisticas.domain.model.Usuario;
import com.futbol.estadisticas.domain.model.enums.Rol;
import org.springframework.stereotype.Component;

@Component
public class UsuarioWebMapper {

    public CrearUsuarioRequest toCommand(CrearUsuarioRequest req) {
        return new CrearUsuarioRequest(
                req.username(),
                req.password(),
                req.rol()
        );
    }

    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getIdUsuario(),
                u.getUsername(),
                u.getRol().name(),
                u.isActivo()
        );
    }
}