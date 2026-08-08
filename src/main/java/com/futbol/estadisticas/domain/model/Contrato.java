package com.futbol.estadisticas.domain.model;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoContrato;

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
public class Contrato {

    @EqualsAndHashCode.Include
    private UUID idContrato;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Double sueldo;
    private Equipo equipo;
    private PersonalDeportivo personal;
    private TipoContrato tipoContrato;
    private Double costoFichaje;

    @Builder.Default
    private EstadoContrato estado = EstadoContrato.ACTIVO;   

    
    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return estado == EstadoContrato.ACTIVO && 
               fechaInicio != null && 
               fechaFin != null &&
               ahora.isAfter(fechaInicio) && 
               ahora.isBefore(fechaFin);
    }

    public boolean esCompatibleConTipoEquipo(TipoEquipo tipoEquipo) {
        if (tipoEquipo == null || this.tipoContrato == null) return false;

        return switch (tipoEquipo) {
            case CLUB_PROFESIONAL ->
                    this.tipoContrato == TipoContrato.PROFESIONAL ||
                            this.tipoContrato == TipoContrato.CESION;

            case CLUB_AMATEUR ->
                    this.tipoContrato == TipoContrato.AMATEUR;

            case FILIAL, RESERVA ->
                    this.tipoContrato == TipoContrato.JUVENIL ||
                            this.tipoContrato == TipoContrato.PROFESIONAL;

            case SELECCION_ABSOLUTA, SELECCION_JUVENIL ->
                    this.tipoContrato == TipoContrato.CONVOCATORIA;

            case EQUIPO_COMBINADO ->
                    this.tipoContrato == TipoContrato.CESION;

            default -> false;
        };
    }

    public void validarContratoConEquipo(Equipo equipo) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser nulo");
        }

        TipoEquipo tipoEquipo = equipo.getTipo();

        if (!esCompatibleConTipoEquipo(tipoEquipo)) {
            throw new IllegalStateException(
                    String.format("El tipo de contrato '%s' no es compatible con el tipo de equipo '%s'.",
                            this.tipoContrato.getDisplayName(),
                            tipoEquipo.getDisplayName())
            );
        }

        validarContratoPorTipo(tipoEquipo);
    }

    private void validarContratoPorTipo(TipoEquipo tipoEquipo) {
        if (this.tipoContrato == null || tipoEquipo == null) return;

        switch (this.tipoContrato) {
            case JUVENIL -> {
                if (tipoEquipo != TipoEquipo.FILIAL && tipoEquipo != TipoEquipo.RESERVA) {
                    throw new IllegalStateException(
                            "Los contratos JUVENIL solo pueden registrarse en equipos FILIAL o RESERVA."
                    );
                }
                if (personal != null && personal.getEdad() > 18) {
                    throw new IllegalStateException(
                            "Los contratos JUVENIL solo pueden ser para jugadores menores de 18 años."
                    );
                }
            }

            case CONVOCATORIA -> {
                if (tipoEquipo != TipoEquipo.SELECCION_ABSOLUTA &&
                        tipoEquipo != TipoEquipo.SELECCION_JUVENIL) {
                    throw new IllegalStateException(
                            "Los contratos de CONVOCATORIA solo pueden registrarse en una Selección o Seleccion Juvenil."
                    );
                }
                if (personal != null && equipo != null) {
                    if (!personal.getNacionalidad().equals(equipo.getPais())) {
                        throw new IllegalStateException(
                                "El jugador debe tener la nacionalidad del país de la selección."
                        );
                    }
                }
            }

            case AMATEUR -> {
                if (tipoEquipo != TipoEquipo.CLUB_AMATEUR) {
                    throw new IllegalStateException(
                            "Los contratos AMATEUR solo pueden registrarse en un Club Amateur."
                    );
                }
                if (sueldo != null && sueldo > 0) {
                    throw new IllegalStateException(
                            "Los contratos AMATEUR no pueden tener sueldo."
                    );
                }
            }

            case CESION -> {
                if (tipoEquipo != TipoEquipo.CLUB_PROFESIONAL) {
                    throw new IllegalStateException(
                            "Los contratos de Cesión solo pueden registrarse en un Club Profesional."
                    );
                }
                if (personal != null) {
                    boolean tieneProfesional = personal.getContratos().stream()
                            .anyMatch(Contrato::esProfesional);

                    if (!tieneProfesional) {
                        throw new IllegalStateException(
                                "No se puede registrar un contrato de CESION porque el personal " +
                                        "no tiene un contrato PROFESIONAL previo."
                        );
                    }
                }
            }



            case PROFESIONAL -> {
                if (tipoEquipo != TipoEquipo.CLUB_PROFESIONAL &&
                        tipoEquipo != TipoEquipo.FILIAL &&
                        tipoEquipo != TipoEquipo.RESERVA) {
                    throw new IllegalStateException(
                            "Los contratos PROFESIONALES solo pueden registrarse en CLUB PROFESIONAL, FILIAL o RESERVA."
                    );
                }
            }
        }
    }


    public void validarContratoConPersonal(PersonalDeportivo personal) {
        if (personal == null) {
            throw new IllegalArgumentException("El personal no puede ser nulo");
        }

        // Validar que no tenga contrato vigente
        Contrato vigente = personal.getContratoVigente();
        if (vigente != null) {
            throw new IllegalStateException(
                    String.format("El personal ya tiene un contrato vigente con %s",
                            vigente.getEquipo() != null ? vigente.getEquipo().getNombre() : "equipo desconocido")
            );
        }

        if (this.tipoContrato == TipoContrato.PROFESIONAL) {
            boolean tieneProfesional = personal.getContratos().stream()
                    .filter(Contrato::estaVigente)
                    .anyMatch(Contrato::esProfesional);

            if (tieneProfesional) {
                throw new IllegalStateException(
                        "El personal ya tiene un contrato PROFESIONAL vigente. " +
                                "No puede tener más de un contrato profesional."
                );
            }
        }

        if (this.tipoContrato == TipoContrato.CONVOCATORIA) {
            boolean tieneConvocatoria = personal.getContratos().stream()
                    .filter(Contrato::estaVigente)
                    .anyMatch(Contrato::esConvocatoria);

            if (tieneConvocatoria) {
                throw new IllegalStateException(
                        "El personal ya tiene un contrato de CONVOCATORIA vigente. " +
                                "No puede tener más de una convocatoria a selección."
                );
            }
        }

        if (this.tipoContrato == TipoContrato.CONVOCATORIA) {
            boolean tieneCesion = personal.getContratos().stream()
                    .filter(Contrato::estaVigente)
                    .anyMatch(Contrato::esCesion);

            if (tieneCesion) {
                throw new IllegalStateException(
                        "El personal ya tiene un contrato de CESION vigente. " +
                                "No puede tener más de una cesión ACTIVA."
                );
            }
        }

        validarCompatibilidadTiposEquipo(personal);
    }

    private void validarCompatibilidadTiposEquipo(PersonalDeportivo personal) {
        if (equipo == null || personal == null) return;

        List<Contrato> contratosVigentes = personal.getContratos().stream()
                .filter(Contrato::estaVigente)
                .filter(c -> !c.getIdContrato().equals(this.idContrato))
                .toList();

        for (Contrato contrato : contratosVigentes) {
            Equipo equipoExistente = contrato.getEquipo();
            if (equipoExistente == null) continue;

            TipoEquipo tipoExistente = equipoExistente.getTipo();
            TipoEquipo tipoNuevo = equipo.getTipo();

            // CLUB_PROFESIONAL + SELECCION = COMPATIBLE
            boolean esClubYSeleccion =
                    (tipoExistente == TipoEquipo.CLUB_PROFESIONAL &&
                            (tipoNuevo == TipoEquipo.SELECCION_ABSOLUTA || tipoNuevo == TipoEquipo.SELECCION_JUVENIL)) ||
                            ((tipoExistente == TipoEquipo.SELECCION_ABSOLUTA || tipoExistente == TipoEquipo.SELECCION_JUVENIL) &&
                                    tipoNuevo == TipoEquipo.CLUB_PROFESIONAL);

            // CLUB_PROFESIONAL + CLUB_AMATEUR = INCOMPATIBLE
            boolean esClubYClub =
                    (tipoExistente == TipoEquipo.CLUB_PROFESIONAL && tipoNuevo == TipoEquipo.CLUB_AMATEUR) ||
                            (tipoExistente == TipoEquipo.CLUB_AMATEUR && tipoNuevo == TipoEquipo.CLUB_PROFESIONAL);

            // SELECCION_ABSOLUTA + SELECCION_JUVENIL = INCOMPATIBLE
            boolean esSeleccionYSeleccion =
                    (tipoExistente == TipoEquipo.SELECCION_ABSOLUTA && tipoNuevo == TipoEquipo.SELECCION_JUVENIL) ||
                            (tipoExistente == TipoEquipo.SELECCION_JUVENIL && tipoNuevo == TipoEquipo.SELECCION_ABSOLUTA);

            if (esClubYClub) {
                throw new IllegalStateException(
                        String.format("El personal ya tiene un contrato con %s (%s). " +
                                        "No puede tener contratos con dos clubes diferentes.",
                                equipoExistente.getNombre(),
                                tipoExistente.getDisplayName())
                );
            }

            if (esSeleccionYSeleccion) {
                throw new IllegalStateException(
                        String.format("El personal ya tiene un contrato con %s (%s). " +
                                        "No puede tener contratos con dos selecciones diferentes.",
                                equipoExistente.getNombre(),
                                tipoExistente.getDisplayName())
                );
            }
        }
    }




    public boolean esProfesional() {
        return this.tipoContrato == TipoContrato.PROFESIONAL;
    }

    public boolean esConvocatoria() {
        return this.tipoContrato == TipoContrato.CONVOCATORIA;
    }

    public boolean esCesion() {
        return this.tipoContrato == TipoContrato.CESION;
    }

    public boolean esJuvenil() {
        return this.tipoContrato == TipoContrato.JUVENIL;
    }

    public boolean esAmateur() {
        return this.tipoContrato == TipoContrato.AMATEUR;
    }

    public void finalizar() {
        if (this.estado == EstadoContrato.FINALIZADO) {
            throw new IllegalStateException("El contrato ya está finalizado");
        }
        this.estado = EstadoContrato.FINALIZADO;
        this.fechaFin = LocalDateTime.now();
    }
    
    public void renovar(int mesesAdicionales) {
        if (mesesAdicionales <= 0) {
            throw new IllegalArgumentException("Los meses deben ser positivos");
        }
        if (this.estado != EstadoContrato.ACTIVO) {
            throw new IllegalStateException("No se puede renovar un contrato no activo");
        }
        this.fechaFin = this.fechaFin.plusMonths(mesesAdicionales);
    }


        public void rescindir() {
        if (this.estado == EstadoContrato.FINALIZADO) {
            throw new IllegalStateException("No se puede Rescindir un contrato Finalizado");
        }
        this.estado = EstadoContrato.RESCINDIDO;
        this.fechaFin = LocalDateTime.now();
   }
}