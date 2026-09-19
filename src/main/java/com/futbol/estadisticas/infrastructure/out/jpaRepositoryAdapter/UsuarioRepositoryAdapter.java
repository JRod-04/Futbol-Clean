package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import com.futbol.estadisticas.application.port.out.UsuarioRepositoryPort;
import com.futbol.estadisticas.domain.model.Usuario;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.UsuarioJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {
    private final UsuarioJPARepository repository;
    private final InfrastructureMapper mapper;

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return repository.findByUsername(username).map(mapper::UsuariotoDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return mapper.UsuariotoDomain(repository.save(mapper.UsuariotoJpa(usuario)));
    }

    @Override
    public List<Usuario> findAll() {
        return repository.findAll().stream().map(mapper::UsuariotoDomain).toList();
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return repository.findById(id).map(mapper::UsuariotoDomain);

    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
