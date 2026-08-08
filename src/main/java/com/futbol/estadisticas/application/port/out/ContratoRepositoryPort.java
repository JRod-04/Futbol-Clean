package com.futbol.estadisticas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.Contrato;

public interface ContratoRepositoryPort {
    
    Contrato save(Contrato contrato);

    List<Contrato> saveAll(List<Contrato> contratos);

    Optional<Contrato> findById(UUID idContrato);
 
    List<Contrato> findAll();
 
    List<Contrato> findByPersonal(UUID idPersonal);
 
    List<Contrato> findByEquipo(UUID idEquipo);
 
    Optional<Contrato> findVigenteByPersonal(UUID idPersonal);
 
    List<Contrato> findVigentesByEquipo(UUID idEquipo);
 
    boolean existsById(UUID idContrato);
 
    void deleteById(UUID idContrato);

    void delete(Contrato contrato);
}
