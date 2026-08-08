package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.TecnicoRepositoryPort;
import com.futbol.estadisticas.domain.model.Tecnico;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.TecnicoJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TecnicoRepositoryAdapter implements TecnicoRepositoryPort {

    private final TecnicoJPARepository repository;
    private final InfrastructureMapper  mapper;

    @Override
    public Page<Tecnico> buscarTecnicoPorNombre(String texto, Pageable pageable) {
        if (texto == null || texto.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        return repository.buscarTecnicoPorTexto(texto.trim(), pageable)
                .map(mapper::TecnicotoDomain);
    }

    @Override
    public Tecnico save(Tecnico tecnico) {
        return mapper.TecnicotoDomain(repository.save(mapper.toJpa(tecnico)));
    }
 
    @Override
    public Optional<Tecnico> findById(UUID idPersonal) {
        return repository.findById(idPersonal).map(mapper::TecnicotoDomain);
    }
 
    @Override
    public List<Tecnico> findAll() {
        return repository.findAll().stream().map(mapper::TecnicotoDomain).toList();
    }
 
    @Override
    public List<Tecnico> findByEquipo(UUID idEquipo) {
        return repository.findByEquipo(idEquipo).stream().map(mapper::TecnicotoDomain).toList();
    }
 
    @Override
    public Optional<Tecnico> findTecnicoActualByEquipo(UUID idEquipo) {
        return repository.findTecnicoActualByEquipo(idEquipo).map(mapper::TecnicotoDomain);
    }
 
    @Override
    public boolean existsById(UUID idPersonal) {
        return repository.existsById(idPersonal);
    }
 
    @Override
    public void deleteById(UUID idPersonal) {
        repository.deleteById(idPersonal);
    }
}
