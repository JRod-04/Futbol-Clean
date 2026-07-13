package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Contrato;

public interface ContratoRepositoryPort {
    
    Contrato save(Contrato contrato);
 
    Optional<Contrato> findById(UUID idContrato);
 
    List<Contrato> findAll();
 
    List<Contrato> findByPersonal(UUID idPersonal);
 
    List<Contrato> findByClub(UUID idClub);
 
    Optional<Contrato> findVigenteByPersonal(UUID idPersonal);
 
    List<Contrato> findVigentesByClub(UUID idClub);
 
    boolean existsById(UUID idContrato);
 
    void deleteById(UUID idContrato);

    void delete(Contrato contrato);
}
