package com.futbol.estadisticas.application.port.in;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.application.port.dto.request.CrearContratoRequest;
import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import org.springframework.cglib.core.Local;

public interface ContratoUseCase {
    ContratoResponse crearContrato(CrearContratoRequest request);

    List<ContratoResponse> crearVariosContratos(List<CrearContratoRequest> contratos);

    ContratoResponse obtenerContratoPorId(UUID idContrato);
 
    List<ContratoResponse> obtenerContratosPorPersonal(UUID idPersonal);
 
    ContratoResponse obtenerContratoVigenteDePersonal(UUID idPersonal);
 
    List<ContratoResponse> obtenerContratosVigentesPorEquipo(UUID idEquipo);
 
    ContratoResponse renovarContrato(UUID idContrato, int mesesAdicionales);
 
    ContratoResponse finalizarContrato(UUID idContrato, LocalDateTime fechaFin);
 
    ContratoResponse rescindirContrato(UUID idContrato, LocalDateTime fechaRescindido);

    void eliminarContrato( UUID idContrato);
}
