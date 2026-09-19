package com.futbol.estadisticas.infrastructure.out.jpaRepository;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.UsuarioJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJPARepository extends JpaRepository<UsuarioJPAEntity, UUID> {
    Optional<UsuarioJPAEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
