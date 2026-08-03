package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.PosicionJugador;

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
public class Club {

    @EqualsAndHashCode.Include
    private UUID idEquipo;

    private String nombre;
    private String nombreCorto;
    private LocalDate fechaFundacion;
    private Nacion pais;

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


    //Agrega un contrato al club y establece la referencia bidireccional.
    public void agregarContrato(Contrato contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException("El contrato no puede ser nulo");
        }
        contrato.setClub(this);
        this.contratos.add(contrato);
    }

    //Devuelve los jugadores con contrato vigente en el club.
    public List<Jugador> getJugadoresActivos() {
        return contratos.stream()
            .filter(Contrato::estaVigente)
            .map(Contrato::getPersonal)
            .filter(p -> p instanceof Jugador)
            .map(p -> (Jugador) p)
            .collect(Collectors.toList());
    }

    //Devuelve los jugadores titulares (estado TITULAR).
    public List<Jugador> getJugadoresTitulares() {
        return getJugadoresActivos().stream()
            .filter(j -> j.getDatosDeportivos() != null &&
                         j.getDatosDeportivos().esTitular())
            .collect(Collectors.toList());
    }

    //Devuelve los jugadores con al menos una lesión activa.
    public List<Jugador> getJugadoresLesionados() {
        return getJugadoresActivos().stream()
            .filter(Jugador::estaLesionado)
            .collect(Collectors.toList());
    }

    //Devuelve los jugadores disponibles para jugar (sin lesión, sin suspensión, con contrato).
    public List<Jugador> getJugadoresDisponibles() {
        return getJugadoresActivos().stream()
            .filter(Jugador::estaDisponible)
            .collect(Collectors.toList());
    }

    //Devuelve el valor de mercado total de la plantilla activa.
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

   
    

    //Agrega un partido como local y establece la referencia bidireccional.
    public void agregarPartidoLocal(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido no puede ser nulo");
        }
        this.partidosLocal.add(partido);
        partido.setEquipoLocal(this);
    }

    //Agrega un partido como visitante y establece la referencia bidireccional.
    public void agregarPartidoVisitante(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido no puede ser nulo");
        }
        this.partidosVisitante.add(partido);
        partido.setEquipoVisitante(this);
    }

    //Devuelve todos los partidos programados del club (local + visitante).
    public List<Partido> getTodosLosPartidos() {
        List<Partido> todos = new ArrayList<>();
        todos.addAll(partidosLocal);
        todos.addAll(partidosVisitante);
        return todos;
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