package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.PersonalDeportivoRepositoryPort;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.PersonalDeportivoJPARepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonalDeportivoRepositoryAdapter implements PersonalDeportivoRepositoryPort{

    private final PersonalDeportivoJPARepository repository;
    private final InfrastructureMapper           mapper;
 
    @Override
    public PersonalDeportivo save(PersonalDeportivo personal) {
        // No se usa directamente; guardado se delega a JugadorAdapter / TecnicoAdapter
        throw new UnsupportedOperationException(
                "Usa JugadorRepositoryPort o TecnicoRepositoryPort para guardar personal específico");
    }
 
    @Override
    public Optional<PersonalDeportivo> findById(UUID idPersonal) {
        return repository.findById(idPersonal).map(mapper::toDomain);
    }
 
    @Override
    public List<PersonalDeportivo> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<PersonalDeportivo> findByTipo(TipoPersonal tipoPersonal) {
        return repository.findByTipoPersonal(tipoPersonal).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<PersonalDeportivo> findByNombreOrApellido(String termino) {
        return repository.findByNombreOrApellido(termino).stream()
                .map(mapper::toDomain).toList();
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
