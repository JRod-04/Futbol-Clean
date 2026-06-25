package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoPersonal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PersonalDeportivo {

    @EqualsAndHashCode.Include
    private UUID idPersonal;

    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Nacion nacionalidad;
    private TipoPersonal tipoPersonal;

    @Builder.Default
    private List<Contrato> contratos = new ArrayList<>();

    @Builder.Default
    private List<EventosPartido> eventos = new ArrayList<>();


    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
    
    //Devuelve el contrato actualmente vigente (ACTIVO y dentro del rango de fechas).
    public Contrato getContratoVigente() {
        return contratos.stream()
            .filter(Contrato::estaVigente)
            .findFirst()
            .orElse(null);
    }
      public void agregarContrato(Contrato contrato) {
      if (contrato != null && contrato.estaVigente()) {
          this.contratos.add(contrato);
          contrato.setPersonal(this);
      }
  }
}

