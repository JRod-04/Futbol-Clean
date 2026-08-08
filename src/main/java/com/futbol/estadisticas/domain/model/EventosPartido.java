package com.futbol.estadisticas.domain.model;

import java.time.LocalTime;
import java.util.UUID;

import com.futbol.estadisticas.domain.model.enums.EstadoPartido;
import com.futbol.estadisticas.domain.model.enums.TipoEvento;

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
public class EventosPartido {

    @EqualsAndHashCode.Include
    private UUID idEvento;


    private LocalTime minuto;
    private String descripcion;
    private TipoEvento tipoEvento;
    private Partido partido;
    private PersonalDeportivo personal;
    private Equipo equipoFavorecido;
    private EstadoPartido estadoEvento;


    public String getMinutoFormateado() {
        if (minuto == null || estadoEvento == null) {
            return "0'";
        }

        int totalMinutos = (minuto.getHour()*60) + minuto.getMinute() + (minuto.getSecond() > 0 ? 1 : 0);

        if (estadoEvento == EstadoPartido.ENTRETIEMPO ||
                estadoEvento == EstadoPartido.ENTRETIEMPO_PRORROGA) {
            return "Descanso";
        }

        if (estadoEvento == EstadoPartido.ESPERANDO_PRORROGA) {
            return "Esperando prórroga";
        }

        if (estadoEvento == EstadoPartido.PENALTIS) {
            return "Penaltis";
        }

        if (estadoEvento == EstadoPartido.CANCELADO ||
                estadoEvento == EstadoPartido.FINALIZADO) {
            if (totalMinutos > 90) {
                return "90+" + (totalMinutos - 90) + "'";
            }
            return totalMinutos + "'";
        }

        if (estadoEvento == EstadoPartido.PROGRAMADO) {
            return "Programado";
        }

        return switch (estadoEvento) {
            case PRIMER_TIEMPO -> formatearPrimerTiempo(totalMinutos);
            case AGREGADO_PRIMER_TIEMPO -> formatearAgregadoPrimerTiempo(totalMinutos);
            case SEGUNDO_TIEMPO -> formatearSegundoTiempo(totalMinutos);
            case AGREGADO_SEGUNDO_TIEMPO -> formatearAgregadoSegundoTiempo(totalMinutos);
            case PRIMER_TIEMPO_PRORROGA -> formatearProrroga(totalMinutos);
            case AGREGADO_PRORROGA_PRIMER -> formatearAgregadoProrroga(totalMinutos, 105);
            case SEGUNDO_TIEMPO_PRORROGA -> formatearProrroga(totalMinutos);
            case AGREGADO_PRORROGA_SEGUNDO -> formatearAgregadoProrroga(totalMinutos, 120);
            default -> totalMinutos + "'";
        };
    }
    private String formatearPrimerTiempo(int totalMinutos) {
        return totalMinutos + "'";
    }

    private String formatearAgregadoPrimerTiempo(int totalMinutos) {
        return "45+" + (totalMinutos - 45) + "'";
    }

    private String formatearSegundoTiempo(int totalMinutos) {
        return totalMinutos + "'";
    }

    private String formatearAgregadoSegundoTiempo(int totalMinutos) {
        return "90+" + (totalMinutos - 90) + "'";
    }

    private String formatearProrroga(int totalMinutos) {
        return totalMinutos + "'";
    }

    private String formatearAgregadoProrroga(int totalMinutos, int inicio) {
        if (totalMinutos <= inicio) return inicio + "+" + (totalMinutos - inicio) + "'";
        return inicio + "+" + (totalMinutos - inicio) + "'";
    }

    //Verifica si el evento es un gol
    public boolean esGol() {
        return tipoEvento == TipoEvento.GOL || 
               tipoEvento == TipoEvento.AUTOGOL || 
               tipoEvento == TipoEvento.PENALTI_ANOTADO;
    }
    

    //Verifica si el evento es una tarjeta disciplinaria
    public boolean esTarjeta() {
        return tipoEvento == TipoEvento.AMARILLA || 
               tipoEvento == TipoEvento.ROJA;
    }
    
    //Verifica si el evento es una sustitución
    public boolean esSustitucion() {
        return tipoEvento == TipoEvento.SUB_IN || 
               tipoEvento == TipoEvento.SUB_OUT;
    }
    

    //Obtiene el color de la tarjeta si es una tarjeta
    public String getColorTarjeta() {
        if (tipoEvento == TipoEvento.AMARILLA) {
            return "Amarilla";
        } else if (tipoEvento == TipoEvento.ROJA) {
            return "Roja";
        }
        return null;
    }
    
    //Verifica si el evento es un penalti
    public boolean esPenalti() {
        return tipoEvento == TipoEvento.PENALTI_CONCEDIDO ||
               tipoEvento == TipoEvento.PENALTI_ANOTADO ||
               tipoEvento == TipoEvento.PENALTI_FALLADO;
    }
    

    //Obtiene el nombre del jugador relacionado con el evento
    public String getNombreJugador() {
        if (personal == null) {
            return "Desconocido";
        }
        return personal.getNombreCompleto();
    }
    
    //Obtiene el nombre del equipo favorecido
    public String getNombreEquipoFavorecido() {
        if (equipoFavorecido == null) {
            return "Ninguno";
        }
        return equipoFavorecido.getNombreCorto();
    }
    

    public String getDescripcionCompleta() {
        if (descripcion != null && !descripcion.isEmpty()) {
            return descripcion;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(minuto != null ? getMinutoFormateado() : "0:00");
        sb.append(" - ");
        sb.append(tipoEvento != null ? tipoEvento.name().replace("_", " ") : "Evento");
        
        if (personal != null) {
            sb.append(" - ").append(personal.getNombreCompleto());
        }
        
        if (equipoFavorecido != null && esGol()) {
            sb.append(" (").append(equipoFavorecido.getNombreCorto()).append(")");
        }
        
        return sb.toString();
    }

    public void validarMinutoCreate(Partido partido) {
        if (minuto == null || partido == null) {
            return;
        }

        EstadoPartido estado = partido.getEstado();
        int totalMinutos = (minuto.getHour()*60) + minuto.getMinute() + (minuto.getSecond() > 0 ? 1 : 0);

        if (estado == EstadoPartido.ENTRETIEMPO ||
                estado == EstadoPartido.ENTRETIEMPO_PRORROGA ||
                estado == EstadoPartido.ESPERANDO_PRORROGA) {
            throw new IllegalStateException("No se pueden registrar eventos durante el " + estado.getDisplayName());
        }

        if (estado == EstadoPartido.FINALIZADO ||
                estado == EstadoPartido.CANCELADO ||
                estado == EstadoPartido.SUSPENDIDO) {
            throw new IllegalStateException("No se pueden registrar eventos en un partido finalizado");
        }

        if (estado == EstadoPartido.PROGRAMADO) {
            throw new IllegalStateException("No se pueden registrar eventos en un partido programado");
        }

        if (estado.esTiempoValido()) {
            if (totalMinutos > 120) {
                throw new IllegalStateException(
                        "El minuto " + totalMinutos + "' excede el límite máximo permitido de 120'");
            }
            return;
        }

        int limite = estado.getMinutoLimite();
        if (totalMinutos > limite) {
            throw new IllegalStateException(
                    "No se puede registrar un minuto mayor a " + limite +
                            "' en " + estado.getDisplayName() + " sin tiempo agregado");
        }
    }

    public boolean esEstadisticable() {
        if (tipoEvento == null) {
            return false;
        }
        return tipoEvento.esGol() || tipoEvento == TipoEvento.ASISTENCIA || tipoEvento.esTarjeta();
    }

}
