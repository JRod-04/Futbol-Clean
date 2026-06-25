package com.futbol.estadisticas.application.sevice;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.CrearArbitroRequest;
import com.futbol.estadisticas.application.port.dto.response.ArbitroResponse;
import com.futbol.estadisticas.application.port.in.ArbitroUseCase;
import com.futbol.estadisticas.application.port.mapper.ArbitroMapper;
import com.futbol.estadisticas.application.port.out.ArbitroRepositoryPort;
import com.futbol.estadisticas.domain.model.Arbitro;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class ArbitroService implements ArbitroUseCase{
private final ArbitroRepositoryPort arbitroRepository;
    private final ArbitroMapper         arbitroMapper;
 
    @Override
    public ArbitroResponse crearArbitro(CrearArbitroRequest request) {
        Arbitro arbitro = arbitroMapper.toEntity(request);
        return arbitroMapper.toResponse(arbitroRepository.save(arbitro));
    }
 
    @Override
    @Transactional(readOnly = true)
    public ArbitroResponse obtenerArbitroPorId(UUID idArbitro) {
        return arbitroRepository.findById(idArbitro)
                .map(arbitroMapper::toResponse)
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Árbitro no encontrado con id: " + idArbitro));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<ArbitroResponse> obtenerTodosLosArbitros() {
        return arbitroRepository.findAll().stream()
                .map(arbitroMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<ArbitroResponse> buscarArbitrosPorNombre(String termino) {
        return arbitroRepository.findByNombreOrApellido(termino).stream()
                .map(arbitroMapper::toResponse)
                .toList();
    }
 
    @Override
    public void eliminarArbitro(UUID idArbitro) {
        if (!arbitroRepository.existsById(idArbitro)) {
            throw new PersonalNotFoundException("Árbitro no encontrado con id: " + idArbitro);
        }
        arbitroRepository.deleteById(idArbitro);
    }
}
