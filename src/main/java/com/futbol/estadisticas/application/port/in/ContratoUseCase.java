package com.futbol.estadisticas.application.port.in;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearContratoRequest;
import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;

public interface ContratoUseCase {
    ContratoResponse crearContrato(CrearContratoRequest request);

    List<ContratoResponse> crearVariosContratos(List<CrearContratoRequest> contratos);

    ContratoResponse obtenerContratoPorId(UUID idContrato);
 
    List<ContratoResponse> obtenerContratosPorPersonal(UUID idPersonal);
 
    ContratoResponse obtenerContratoVigenteDePersonal(UUID idPersonal);
 
    List<ContratoResponse> obtenerContratosVigentesPorClub(UUID idClub);
 
    ContratoResponse renovarContrato(UUID idContrato, int mesesAdicionales);
 
    void finalizarContrato(UUID idContrato);
 
    void rescindirContrato(UUID idContrato);

    void eliminarContrato( UUID idContrato);
}
