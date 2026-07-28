package com.futbol.estadisticas.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.futbol.estadisticas.application.port.out.TecnicoRepositoryPort;
import com.futbol.estadisticas.domain.model.Tecnico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futbol.estadisticas.application.port.dto.request.CrearContratoRequest;
import com.futbol.estadisticas.application.port.dto.response.ContratoResponse;
import com.futbol.estadisticas.application.port.in.ContratoUseCase;
import com.futbol.estadisticas.application.port.mapper.ContratoMapper;
import com.futbol.estadisticas.application.port.out.ClubRepositoryPort;
import com.futbol.estadisticas.application.port.out.ContratoRepositoryPort;
import com.futbol.estadisticas.application.port.out.PersonalDeportivoRepositoryPort;
import com.futbol.estadisticas.domain.model.Club;
import com.futbol.estadisticas.domain.model.Contrato;
import com.futbol.estadisticas.domain.model.PersonalDeportivo;
import com.futbol.estadisticas.domain.model.enums.EstadoContrato;
import com.futbol.estadisticas.domain.model.exception.PersonalNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratoService implements ContratoUseCase {

    private final ContratoRepositoryPort          contratoRepository;
    private final PersonalDeportivoRepositoryPort personalRepository;
    private final ClubRepositoryPort              clubRepository;
    private final ContratoMapper                  contratoMapper;
    private final TecnicoRepositoryPort           tecnicoRepository;
 
    @Override
    public ContratoResponse crearContrato(CrearContratoRequest request) {
        PersonalDeportivo personal = personalRepository.findById(request.idPersonal())
                .orElseThrow(() -> new PersonalNotFoundException(
                        "Personal no encontrado con id: " + request.idPersonal()));
 
        Club club = clubRepository.findById(request.idClub())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Club no encontrado con id: " + request.idClub()));
 
        contratoRepository.findVigenteByPersonal(request.idPersonal()).ifPresent(c -> {
            throw new IllegalStateException("El personal ya tiene un contrato vigente con: "
                    + c.getClub().getNombre());
        });
 
        Contrato contrato = contratoMapper.toEntity(
                UUID.randomUUID(), personal, club,
                request.fechaInicio(), request.fechaFin(), request.estado(), request.sueldo());
 
        personal.agregarContrato(contrato);
        club.agregarContrato(contrato);

        Contrato saved = contratoRepository.save(contrato);

        if (personal instanceof Tecnico) {
            Tecnico tecnico = (Tecnico) personal;
            club.asignarTecnico(tecnico);
            clubRepository.actualizarTecnicoActual(club.getIdEquipo(), tecnico.getIdPersonal());
            tecnicoRepository.save(tecnico);
        }


        return contratoMapper.toResponse(saved);
    }

    @Override
    public List<ContratoResponse> crearVariosContratos(List<CrearContratoRequest> requests) {
        List<Contrato> contratos = new ArrayList<>();

        for (CrearContratoRequest request : requests) {
            PersonalDeportivo personal = personalRepository.findById(request.idPersonal())
                    .orElseThrow(() -> new PersonalNotFoundException("Personal no encontrado"));

            Club club = clubRepository.findById(request.idClub())
                    .orElseThrow(() -> new IllegalArgumentException("Club no encontrado"));

            contratoRepository.findVigenteByPersonal(request.idPersonal()).ifPresent(c -> {
                throw new IllegalStateException("El personal ya tiene contrato vigente");
            });

            Contrato contrato = contratoMapper.toEntity(
                    UUID.randomUUID(),
                    personal,
                    club,
                    request.fechaInicio(),
                    request.fechaFin(),
                    request.estado(),
                    request.sueldo()
            );

            personal.agregarContrato(contrato);
            club.agregarContrato(contrato);
            contratos.add(contrato);
        }

        List<Contrato> saved = contratoRepository.saveAll(contratos);

        for (Contrato contrato : saved) {
            PersonalDeportivo personal = contrato.getPersonal();
            Club club = contrato.getClub();

            if (personal instanceof Tecnico && club != null) {
                Tecnico tecnico = (Tecnico) personal;
                clubRepository.actualizarTecnicoActual(club.getIdEquipo(), tecnico.getIdPersonal());
                club.asignarTecnico(tecnico);
                tecnicoRepository.save(tecnico);
            }
        }

        return saved.stream()
                .map(contratoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ContratoResponse obtenerContratoPorId(UUID idContrato) {
        return contratoRepository.findById(idContrato)
                .map(contratoMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contrato no encontrado con id: " + idContrato));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<ContratoResponse> obtenerContratosPorPersonal(UUID idPersonal) {
        return contratoRepository.findByPersonal(idPersonal).stream()
                .map(contratoMapper::toResponse)
                .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public ContratoResponse obtenerContratoVigenteDePersonal(UUID idPersonal) {
        return contratoRepository.findVigenteByPersonal(idPersonal)
                .map(contratoMapper::toResponse)
                .orElseThrow(() -> new IllegalStateException(
                        "No hay contrato vigente para el personal con id: " + idPersonal));
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<ContratoResponse> obtenerContratosVigentesPorClub(UUID idClub) {
        return contratoRepository.findVigentesByClub(idClub).stream()
                .map(contratoMapper::toResponse)
                .toList();
    }
 
    @Override
    public ContratoResponse renovarContrato(UUID idContrato, int mesesAdicionales) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contrato no encontrado con id: " + idContrato));
        contrato.renovar(mesesAdicionales);
        return contratoMapper.toResponse(contratoRepository.save(contrato));
    }
 
    @Override
    public void finalizarContrato(UUID idContrato) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contrato no encontrado con id: " + idContrato));


        PersonalDeportivo personal = contrato.getPersonal();
        Club club = contrato.getClub();

        contrato.finalizar();
        contratoRepository.save(contrato);

        if (personal instanceof Tecnico && club != null) {
            if (club.getTecnicoActual() != null &&
                    club.getTecnicoActual().getIdPersonal().equals(personal.getIdPersonal())) {

                clubRepository.actualizarTecnicoActual(club.getIdEquipo(), null);

                club.desvincularTecnico();

                tecnicoRepository.save((Tecnico) personal);
            }
        }
    }
 
    @Override
    public void rescindirContrato(UUID idContrato) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contrato no encontrado con id: " + idContrato));
        if (contrato.getEstado() == EstadoContrato.FINALIZADO
                || contrato.getEstado() == EstadoContrato.RESCINDIDO) {
            throw new IllegalStateException(
                    "El contrato ya está " + contrato.getEstado().name().toLowerCase());
        }
        PersonalDeportivo personal = contrato.getPersonal();
        Club club = contrato.getClub();

        contrato.setEstado(EstadoContrato.RESCINDIDO);
        contratoRepository.save(contrato);

        if (personal instanceof Tecnico && club != null) {
            if (club.getTecnicoActual() != null &&
                    club.getTecnicoActual().getIdPersonal().equals(personal.getIdPersonal())) {

                clubRepository.actualizarTecnicoActual(club.getIdEquipo(), null);

                club.desvincularTecnico();

                tecnicoRepository.save((Tecnico) personal);
            }
        }
    }

    @Override
    public void eliminarContrato(UUID idContrato) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contrato no encontrado con id: " + idContrato));

        PersonalDeportivo personal = contrato.getPersonal();
        Club club = contrato.getClub();

        if (personal != null) {
            personal.getContratos().remove(contrato);
        }

        if (club != null) {
            club.getContratos().remove(contrato);
        }

        contratoRepository.delete(contrato);
    }
}
