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
        return mapper.toDomain(repository.save(entity));
    }
 
    @Override
    public Optional<Contrato> findById(UUID idContrato) {
        return repository.findById(idContrato).map(mapper::toDomain);
    }
 
    @Override
    public List<Contrato> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Contrato> findByPersonal(UUID idPersonal) {
        return repository.findByPersonalIdPersonal(idPersonal).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public List<Contrato> findByClub(UUID idClub) {
        return repository.findByClubIdEquipo(idClub).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public Optional<Contrato> findVigenteByPersonal(UUID idPersonal) {
        return repository.findVigenteByPersonal(idPersonal).map(mapper::toDomain);
    }
 
    @Override
    public List<Contrato> findVigentesByClub(UUID idClub) {
        return repository.findVigentesByClub(idClub).stream()
                .map(mapper::toDomain).toList();
    }
 
    @Override
    public boolean existsById(UUID idContrato) {
        return repository.existsById(idContrato);
    }
 
    @Override
    public void deleteById(UUID idContrato) {
        repository.deleteById(idContrato);
    }
}
