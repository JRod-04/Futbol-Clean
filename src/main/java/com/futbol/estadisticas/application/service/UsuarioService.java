// src/main/java/com/tuapp/usuario/domain/service/UsuarioService.java
package com.futbol.estadisticas.application.service;


import com.futbol.estadisticas.application.port.dto.request.SecurityDTORequest.*;
import com.futbol.estadisticas.application.port.in.UsuarioUseCase;
import com.futbol.estadisticas.application.port.mapper.UsuarioWebMapper;
import com.futbol.estadisticas.application.port.out.UsuarioRepositoryPort;
import com.futbol.estadisticas.domain.model.Usuario;
import com.futbol.estadisticas.domain.model.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioWebMapper mapper;

    @Override
    @Transactional
    public Usuario crear(CrearUsuarioRequest crearUsuarioRequest) {
       String username = crearUsuarioRequest.username();
       String password = crearUsuarioRequest.password();
       String rol = crearUsuarioRequest.rol();
       Rol rolEnum = Rol.valueOf(rol.toUpperCase());

        Usuario usuario = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .username(username.trim())
                .password(passwordEncoder.encode(password))
                .rol(rolEnum != null ? rolEnum : Rol.USUARIO)
                .activo(true)
                .build();

        return usuarioRepository.save(usuario);
    }



    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtener(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Override
    @Transactional
    public void activar(UUID id) {
        Usuario usuario = obtener(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void desactivar(UUID id) {
        Usuario usuario = obtener(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(UUID id) {
        if (usuarioRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }
}