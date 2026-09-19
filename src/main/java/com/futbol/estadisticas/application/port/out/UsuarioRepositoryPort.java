package com.futbol.estadisticas.application.port.out;

import com.futbol.estadisticas.domain.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findByUsername(String username);
    Usuario save(Usuario usuario);
    boolean existsByUsername(String username);
    List<Usuario> findAll();
    Optional<Usuario> findById(UUID id);
    void deleteById(UUID id);
}
