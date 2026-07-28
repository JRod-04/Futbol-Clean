package com.futbol.estadisticas.infrastructure.out.jpaRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.futbol.estadisticas.application.port.out.ContratoRepositoryPort;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.infrastructure.out.InfrastructureMapper;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ClubJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.ContratoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaEntity.PersonalDeportivoJPAEntity;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.ClubJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.ContratoJPARepository;
import com.futbol.estadisticas.infrastructure.out.jpaRepository.PersonalDeportivoJPARepository;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class ContratoRepositoryAdapter implements ContratoRepositoryPort {

    private final ContratoJPARepository          repository;
    private final PersonalDeportivoJPARepository personalRepo;
    private final ClubJPARepository              clubRepo;
    private final InfrastructureMapper           mapper;
 
    @Override
    public Contrato save(Contrato contrato) {
        PersonalDeportivoJPAEntity personalJPA = contrato.getPersonal() != null
                ? personalRepo.findById(contrato.getPersonal().getIdPersonal()).orElse(null) : null;
        ClubJPAEntity clubJPA = contrato.getClub() != null
                ? clubRepo.findById(contrato.getClub().getIdEquipo()).orElse(null) : null;
 
        ContratoJPAEntity entity = mapper.toJpa(contrato, personalJPA, clubJPA);
        return mapper.ContratotoDomain(repository.save(entity));
    }

    @Override
    public List<Contrato> saveAll(List<Contrato> contratos) {
        List<ContratoJPAEntity> entities = contratos.stream()
                .map(c -> {
                    PersonalDeportivoJPAEntity personalJPA = personalRepo.findById(c.getPersonal().getIdPersonal()).orElseThrow();
                    ClubJPAEntity clubJPA = clubRepo.findById(c.getClub().getIdEquipo()).orElseThrow();
                    return mapper.toJpa(c, personalJPA, clubJPA);
                })
                .toList();
        List<ContratoJPAEntity> saved = repository.saveAll(entities);
        return saved.stream().map(mapper::ContratotoDomain).toList();
    }

    @Override
    public Optional<Contrato> findById(UUID idContrato) {
        return repository.findByIdWithRelations(idContrato).map(mapper::ContratotoDomain);
    }
 
    @Override
    public List<Contrato> findAll() {
        return repository.findAll().stream().map(mapper::ContratotoDomain).toList();
    }
 
    @Override
    public List<Contrato> findByPersonal(UUID idPersonal) {
        return repository.findByPersonalIdPersonal(idPersonal).stream()
                .map(mapper::ContratotoDomain).toList();
    }
 
    @Override
    public List<Contrato> findByClub(UUID idClub) {
        return repository.findByClubIdEquipo(idClub).stream()
                .map(mapper::ContratotoDomain).toList();
    }
 
    @Override
    public Optional<Contrato> findVigenteByPersonal(UUID idPersonal) {
        return repository.findVigenteByPersonal(idPersonal).map(mapper::ContratotoDomain);
    }
 
    @Override
    public List<Contrato> findVigentesByClub(UUID idClub) {
        return repository.findVigentesByClub(idClub).stream()
                .map(mapper::ContratotoDomain).toList();
    }
 
    @Override
    public boolean existsById(UUID idContrato) {
        return repository.existsById(idContrato);
    }
 
    @Override
    public void deleteById(UUID idContrato) {
        repository.deleteById(idContrato);
    }

    @Override
    public void delete(Contrato contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException("El contrato no puede ser nulo");
        }
        ContratoJPAEntity entity = repository.findById(contrato.getIdContrato())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contrato no encontrado con id: " + contrato.getIdContrato()));

        repository.delete(entity);
    }
}
