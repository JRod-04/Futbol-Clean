package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.LesionRepositoryPort;
import com.futbol.estadisticas.domain.model.Lesion;
import com.futbol.estadisticas.domain.model.enums.Gravedad;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.JugadorJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.LesionJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LesionRepositoryAdapter implements LesionRepositoryPort{

    private final LesionJPARepository  repository;
    private final InfrastructureMapper mapper;
 
    @Override
    public Lesion save(Lesion lesion) {
        // Buscar el jugador JPA desde la lesión de dominio si tiene referencia
        JugadorJPAEntity jugadorJPA = null;
        if (lesion.getIdLesion() != null) {
            // Intento de recuperar jugador vinculado desde BD
            jugadorJPA = repository.findById(lesion.getIdLesion())
                    .map(e -> e.getJugador())
                    .orElse(null);
        }
        return mapper.toDomain(repository.save(mapper.toJpa(lesion, jugadorJPA)));
    }
 
    @Override
    public Optional<Lesion> findById(UUID idLesion) {
        return repository.findById(idLesion).map(mapper::toDomain);
    }
 
    @Override
    public List<Lesion> findByJugador(UUID idJugador) {
        return repository.findByJugadorIdPersonal(idJugador).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Lesion> findActivasByJugador(UUID idJugador) {
        return repository.findActivasByJugador(idJugador, LocalDate.now()).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Lesion> findByGravedad(Gravedad gravedad) {
        return repository.findByGravedad(gravedad).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Lesion> findActivas() {
        return repository.findActivas(LocalDate.now()).stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public boolean existsById(UUID idLesion) {
        return repository.existsById(idLesion);
    }
 
    @Override
    public void deleteById(UUID idLesion) {
        repository.deleteById(idLesion);
    }
}
