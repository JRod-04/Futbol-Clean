package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.futbol.estadisticas.infrastructure.out.jpaEntity.LesionJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.JugadorJPARepository;
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
public class LesionRepositoryAdapter implements LesionRepositoryPort {

    private final JugadorJPARepository jugadorRepo;
    private final LesionJPARepository  repository;
    private final InfrastructureMapper mapper;
 
    @Override
    public Lesion save(Lesion lesion) {
        JugadorJPAEntity jugadorJPA = null;
        if (lesion.getJugadorLesionado() != null) {
            jugadorJPA = jugadorRepo.findById(lesion.getJugadorLesionado().getIdPersonal()).orElse(null);
        }
        LesionJPAEntity entity = mapper.LesiontoJpa(lesion, jugadorJPA);
        return mapper.LesiontoDomain(repository.save(entity));
    }

    @Override
    public List<Lesion> saveAll(List<Lesion> lesiones) {
        List<LesionJPAEntity> entities = lesiones.stream()
                .map(l -> {
                    JugadorJPAEntity jugadorJPA = null;
                    if (l.getJugadorLesionado() != null) {
                        jugadorJPA = jugadorRepo.findById(l.getJugadorLesionado().getIdPersonal()).orElse(null);
                    }
                    return mapper.LesiontoJpa(l, jugadorJPA);
                })
                .toList();
        return repository.saveAll(entities).stream()
                .map(mapper::LesiontoDomain)
                .toList();
    }

    @Override
    public Optional<Lesion> findById(UUID idLesion) {
            return repository.findByIdWithJugador(idLesion)
                    .map(mapper::LesiontoDomain);
        }
 
    @Override
    public List<Lesion> findByJugador(UUID idJugador) {
        return repository.findByJugadorIdPersonalWithJugador(idJugador).stream()
                .map(mapper::LesiontoDomain)
                .collect(Collectors.toList());
    }
 
    @Override
    public List<Lesion> findActivasByJugador(UUID idJugador) {
        return repository.findActivasByJugador(idJugador, LocalDate.now()).stream()
                .map(mapper::LesiontoDomain).toList();
    }
 
    @Override
    public List<Lesion> findByGravedad(Gravedad gravedad) {
        return repository.findByGravedad(gravedad).stream().map(mapper::LesiontoDomain).toList();
    }
 
    @Override
    public List<Lesion> findActivas() {
        return repository.findActivas(LocalDate.now()).stream().map(mapper::LesiontoDomain).toList();
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
