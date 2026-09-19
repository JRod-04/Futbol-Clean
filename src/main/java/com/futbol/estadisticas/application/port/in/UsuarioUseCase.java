// src/main/java/com/tuapp/usuario/domain/usecase/UsuarioUseCase.java
package com.futbol.estadisticas.application.port.in;

import com.futbol.estadisticas.application.port.dto.request.SecurityDTORequest;
import com.futbol.estadisticas.domain.model.Usuario;

import java.util.List;
import java.util.UUID;

public interface UsuarioUseCase {

    Usuario crear(SecurityDTORequest.CrearUsuarioRequest command);

    List<Usuario> listar();

    Usuario obtener(UUID id);

    void activar(UUID id);

    void desactivar(UUID id);

    void eliminar(UUID id);

    boolean existeUsername(String username);
}