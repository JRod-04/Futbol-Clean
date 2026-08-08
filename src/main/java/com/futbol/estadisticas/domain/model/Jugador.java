package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoJugador;
import com.futbol.estadisticas.domain.model.enums.JuegoPies;
import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Jugador extends PersonalDeportivo {

    private JuegoPies pieHabil;
    private Integer altura;
    private Integer peso;
    private LocalDate fechaActualizacion;
    private DatosDeportivos datosDeportivos;

    @Builder.Default
    private List<Lesion> lesiones = new ArrayList<>();

    public Jugador(UUID idPersonal, String nombre, String apellido,
                   LocalDate fechaNacimiento, Nacion nacionalidad,
                   JuegoPies pieHabil, Integer altura, Integer peso) {
        super(idPersonal, nombre, apellido, fechaNacimiento, nacionalidad, TipoPersonal.JUGADOR,
              new ArrayList<>(), new ArrayList<>());
        this.pieHabil = pieHabil;
        this.altura = altura;
        this.peso = peso;
        this.fechaActualizacion = LocalDate.now();
        this.lesiones = new ArrayList<>();
    }


    //Devuelve el club con el que el jugador tiene contrato vigente, o null si no tiene.
    public Equipo getEquipoActual() {
        Contrato contrato = getContratoVigente();
        return contrato != null ? contrato.getEquipo() : null;
    }

    //Registra una lesión en el jugador y actualiza su estado a LESIONADO.
    public void registrarLesion(Lesion lesion) {
        if (lesion == null) {
            throw new IllegalArgumentException("La lesión no puede ser nula");
        }
        if (lesion.isCurada()) {
            throw new IllegalArgumentException("No se puede registrar una lesión que ya está curada");
        }
        this.lesiones.add(lesion);
        if (this.datosDeportivos != null) {
            this.datosDeportivos.setEstadoJugador(EstadoJugador.LESIONADO);
        }
        lesion.setJugadorLesionado(this);

    }

    //Indica si el jugador tiene al menos una lesión activa actualmente.
    public boolean estaLesionado() {
        return lesiones.stream().anyMatch(Lesion::esActiva);
    }


    //Indica si el jugador puede ser convocado: sin lesiones activas,
    public boolean estaDisponible() {
        boolean noLesionado = !estaLesionado();
        boolean estadoValido = datosDeportivos != null &&
            datosDeportivos.getEstadoJugador() != EstadoJugador.SUSPENDIDO &&
            datosDeportivos.getEstadoJugador() != EstadoJugador.RETIRADO &&
            datosDeportivos.getEstadoJugador() != EstadoJugador.APARTADO;
        return noLesionado && estadoValido;
    }
}