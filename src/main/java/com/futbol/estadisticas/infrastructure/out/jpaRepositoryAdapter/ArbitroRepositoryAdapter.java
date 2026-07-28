package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.ArbitroRepositoryPort;
import com.futbol.estadisticas.domain.model.Arbitro;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.ArbitroJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArbitroRepositoryAdapter implements ArbitroRepositoryPort {

    private final ArbitroJPARepository repository;
    private final InfrastructureMapper  mapper;

    @Override
    public Arbitro save(Arbitro arbitro) {
        return mapper.ArbitrotoDomain(repository.save(mapper.toJpa(arbitro)));
    }

    @Override
    public Optional<Arbitro> findById(UUID idArbitro) {
        return repository.findById(idArbitro).map(mapper::ArbitrotoDomain);
    }

    @Override
    public List<Arbitro> findAll() {
        return repository.findAll().stream().map(mapper::ArbitrotoDomain).toList();
    }

    @Override
    public List<Arbitro> findByNombreOrApellido(String termino) {
        return repository.findByNombreOrApellido(termino).stream().map(mapper::ArbitrotoDomain).toList();
    }

    @Override
    public boolean existsById(UUID idArbitro) {
        return repository.existsById(idArbitro);
    }

    @Override
    public void deleteById(UUID idArbitro) {
        repository.deleteById(idArbitro);
    }
}
