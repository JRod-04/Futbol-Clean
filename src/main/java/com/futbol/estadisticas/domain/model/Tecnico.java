package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Tecnico extends PersonalDeportivo {

    private String estiloJuego;
    private String alineacionFavorita;
    private Equipo equipoActualAsignado;

    public Tecnico(UUID idPersonal, String nombre, String apellido,
                   LocalDate fechaNacimiento, Nacion nacionalidad,
                   String estiloJuego, String alineacionFavorita) {
        super(idPersonal, nombre, apellido, fechaNacimiento, nacionalidad, TipoPersonal.TECNICO,
              new java.util.ArrayList<>(), new java.util.ArrayList<>());
        this.estiloJuego = estiloJuego;
        this.alineacionFavorita = alineacionFavorita;
    }

    //Devuelve el contrato Vigente
    public Equipo getClubActual() {
        Contrato contrato = getContratoVigente();
        return contrato != null ? contrato.getEquipo() : equipoActualAsignado;
    }

    //Asigna un club al técnico. 
    public void asignarClub(Equipo club) {
        if (club == null) {
            throw new IllegalArgumentException("El club no puede ser nulo");
        }
        this.equipoActualAsignado = club;
    }

    //Desvincula al técnico de su club actual. 
    public void desvincularClub() {
        this.equipoActualAsignado = null;
    }

    
    public void actualizarEstiloJuego(String nuevoEstilo) {
        if (nuevoEstilo == null || nuevoEstilo.isBlank()) {
            throw new IllegalArgumentException("El estilo de juego no puede ser vacío");
        }
        this.estiloJuego = nuevoEstilo;
    }

    public void actualizarAlineacion(String nuevaAlineacion) {
        if (nuevaAlineacion == null || nuevaAlineacion.isBlank()) {
            throw new IllegalArgumentException("La alineación no puede ser vacía");
        }
        this.alineacionFavorita = nuevaAlineacion;
    }
}