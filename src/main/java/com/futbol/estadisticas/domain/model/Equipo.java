package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.futbol.estadisticas.domain.model.enums.Nacion;

import com.futbol.estadisticas.domain.model.enums.TipoContrato;
import com.futbol.estadisticas.domain.model.enums.TipoEquipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Equipo {

    @EqualsAndHashCode.Include
    private UUID idEquipo;

    private String nombre;
    private String nombreCorto;
    private LocalDate fechaFundacion;
    private Nacion pais;
    private TipoEquipo tipo;

    @Builder.Default
    private List<Contrato> contratos = new ArrayList<>();

    private Estadio estadio;

    @Builder.Default
    private List<Partido> partidosLocal = new ArrayList<>();

    @Builder.Default
    private List<Partido> partidosVisitante = new ArrayList<>();
     
    @Builder.Default
    private List<Tecnico> tecnicos = new ArrayList<>();

    private Tecnico tecnicoActual;


    public List<Jugador> getJugadoresActivos() {
        return contratos.stream()
            .filter(Contrato::estaVigente)
            .map(Contrato::getPersonal)
            .filter(p -> p instanceof Jugador)
            .map(p -> (Jugador) p)
            .collect(Collectors.toList());
    }

    public List<Jugador> getJugadoresTitulares() {
        return getJugadoresActivos().stream()
            .filter(j -> j.getDatosDeportivos() != null &&
                         j.getDatosDeportivos().esTitular())
            .collect(Collectors.toList());
    }

    public List<Jugador> getJugadoresLesionados() {
        return getJugadoresActivos().stream()
            .filter(Jugador::estaLesionado)
            .collect(Collectors.toList());
    }

    public List<Jugador> getJugadoresDisponibles() {
        return getJugadoresActivos().stream()
            .filter(Jugador::estaDisponible)
            .collect(Collectors.toList());
    }

    public double getValorPlantillaTotal() {
        return getJugadoresActivos().stream()
            .mapToDouble(j -> {
                if (j.getDatosDeportivos() == null ||
                    j.getDatosDeportivos().getValorMercado() == null) {
                    return 0;
                }
                return j.getDatosDeportivos().getValorMercado();
            })
            .sum();
    }

    public void agregarContrato(Contrato contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException("El contrato no puede ser nulo");
        }

        contrato.validarContratoConEquipo(this);

        contrato.setEquipo(this);
        this.contratos.add(contrato);
    }

    public boolean puedeTenerContratoDeTipo(TipoContrato tipoContrato) {
        if (tipoContrato == null || this.tipo == null) return false;

        return switch (this.tipo) {
            case CLUB_PROFESIONAL ->
                    tipoContrato == TipoContrato.PROFESIONAL ||
                            tipoContrato == TipoContrato.CESION;

            case CLUB_AMATEUR ->
                    tipoContrato == TipoContrato.AMATEUR;

            case FILIAL, RESERVA ->
                    tipoContrato == TipoContrato.JUVENIL ||
                            tipoContrato == TipoContrato.PROFESIONAL;

            case SELECCION_ABSOLUTA, SELECCION_JUVENIL, EQUIPO_COMBINADO ->
                    tipoContrato == TipoContrato.CONVOCATORIA;

            default -> false;
        };
    }

    public List<Contrato> getContratosPorTipo(TipoContrato tipo) {
        return contratos.stream()
                .filter(c -> c.getTipoContrato() == tipo)
                .toList();
    }

    public List<Jugador> getJugadoresProfesionales() {
        return contratos.stream()
                .filter(Contrato::estaVigente)
                .filter(Contrato::esProfesional)
                .map(Contrato::getPersonal)
                .filter(p -> p instanceof Jugador)
                .map(p -> (Jugador) p)
                .toList();
    }


    public List<Jugador> getJugadoresJuveniles() {
        return contratos.stream()
                .filter(Contrato::estaVigente)
                .filter(Contrato::esJuvenil)
                .map(Contrato::getPersonal)
                .filter(p -> p instanceof Jugador)
                .map(p -> (Jugador) p)
                .toList();
    }


    // ============ GESTIÓN DEL TÉCNICO ============


    public void asignarTecnico(Tecnico tecnico) {
        if (tecnico == null) {
            throw new IllegalArgumentException("El técnico no puede ser nulo");
        }
        if (this.tecnicoActual != null && !this.tecnicoActual.equals(tecnico)) {
            this.tecnicoActual.desvincularClub();
        }
        this.tecnicoActual = tecnico;
        tecnico.asignarClub(this);
        if (!tecnicos.contains(tecnico)) {
            this.tecnicos.add(tecnico);
        }
    }

    public void desvincularTecnico() {
        if (this.tecnicoActual == null) {
            throw new IllegalStateException("El club no tiene técnico asignado actualmente");
        }
        this.tecnicoActual.desvincularClub();
        this.tecnicoActual = null;
    }
}