package com.futbol.estadisticas.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.Nacion;
import com.futbol.estadisticas.domain.model.enums.TipoContrato;
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
    
    public Contrato getContratoVigente() {
        return contratos.stream()
            .filter(Contrato::estaVigente)
            .findFirst()
            .orElse(null);
    }


          public void agregarContrato(Contrato contrato) {
              if (contrato == null) {
                  throw new IllegalArgumentException("El contrato no puede ser nulo");
              }

              contrato.validarContratoConPersonal(this);

              contrato.validarContratoConEquipo(contrato.getEquipo());

              this.contratos.add(contrato);
              contrato.setPersonal(this);
          }

          public boolean puedeRegistrarseEnEquipo(Equipo equipo) {
              if (equipo == null) return false;

              Contrato contratoTemp = Contrato.builder()
                      .tipoContrato(TipoContrato.PROFESIONAL)
                      .equipo(equipo)
                      .build();

              try {
                  contratoTemp.validarContratoConPersonal(this);
                  return true;
              } catch (IllegalStateException e) {
                  return false;
              }
          }


          public List<Contrato> getContratosPorTipo(TipoContrato tipo) {
              return contratos.stream()
                      .filter(c -> c.getTipoContrato() == tipo)
                      .toList();
          }


          public boolean tieneContratoProfesionalVigente() {
              return contratos.stream()
                      .filter(Contrato::estaVigente)
                      .anyMatch(Contrato::esProfesional);
          }


          public boolean tieneContratoConvocatoriaVigente() {
              return contratos.stream()
                      .filter(Contrato::estaVigente)
                      .anyMatch(Contrato::esConvocatoria);
          }


          public Equipo getEquipoActual() {
              Contrato vigente = getContratoVigente();
              return vigente != null ? vigente.getEquipo() : null;
          }
  }


